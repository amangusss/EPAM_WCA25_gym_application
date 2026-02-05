package com.github.amangusss.gym_application.metrics;

import com.github.amangusss.gym_application.exception.TraineeNotFoundException;
import com.github.amangusss.gym_application.exception.ValidationException;

import io.micrometer.core.instrument.Timer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MetricsExecutor Tests")
class MetricsExecutorTest {

    private static final String OPERATION = "test_operation";
    private static final String ENDPOINT = "/api/test";
    private static final String METHOD = "GET";

    @Mock
    private ApiPerformanceMetrics apiMetrics;

    @InjectMocks
    private MetricsExecutor metricsExecutor;

    private MetricsExecutor.MetricsContext metricsContext;
    private Timer.Sample mockSample;

    @BeforeEach
    void setUp() {
        metricsContext = MetricsExecutor.MetricsContext.builder()
                .operation(OPERATION)
                .endpoint(ENDPOINT)
                .method(METHOD)
                .build();

        mockSample = mock(Timer.Sample.class);
        when(apiMetrics.startTimer()).thenReturn(mockSample);
        doNothing().when(apiMetrics).recordRequest(anyString(), anyString());
        doNothing().when(apiMetrics).recordResponse(anyString(), anyString(), anyInt());
    }

    @Nested
    @DisplayName("executeWithMetrics Tests")
    class ExecuteWithMetricsTests {

        @Test
        @DisplayName("Should execute successfully and record metrics")
        void shouldExecuteSuccessfullyAndRecordMetrics() {
            String expectedResult = "success";
            AtomicBoolean onSuccessCalled = new AtomicBoolean(false);

            String result = metricsExecutor.executeWithMetrics(
                    metricsContext,
                    () -> expectedResult,
                    r -> onSuccessCalled.set(true),
                    e -> {}
            );

            assertThat(result).isEqualTo(expectedResult);
            assertThat(onSuccessCalled.get()).isTrue();

            verify(apiMetrics).startTimer();
            verify(apiMetrics).recordRequest(ENDPOINT, METHOD);
            verify(apiMetrics).stopTimerSuccess(eq(mockSample), eq(OPERATION), eq(ENDPOINT), eq(METHOD));
            verify(apiMetrics).recordResponse(ENDPOINT, METHOD, 200);
        }

        @Test
        @DisplayName("Should handle exception and record failure metrics")
        void shouldHandleExceptionAndRecordFailureMetrics() {
            RuntimeException expectedException = new RuntimeException("Test error");
            AtomicBoolean onFailureCalled = new AtomicBoolean(false);

            assertThatThrownBy(() -> metricsExecutor.executeWithMetrics(
                    metricsContext,
                    () -> { throw expectedException; },
                    r -> {},
                    e -> onFailureCalled.set(true)
            )).isEqualTo(expectedException);

            assertThat(onFailureCalled.get()).isTrue();

            verify(apiMetrics).stopTimerFailure(eq(mockSample), eq(OPERATION), eq(ENDPOINT), eq(METHOD));
            verify(apiMetrics).recordResponse(ENDPOINT, METHOD, 500);
        }

        @Test
        @DisplayName("Should return 404 for TraineeNotFoundException")
        void shouldReturn404ForTraineeNotFoundException() {
            TraineeNotFoundException exception = new TraineeNotFoundException("test");

            assertThatThrownBy(() -> metricsExecutor.executeWithMetrics(
                    metricsContext,
                    () -> { throw exception; },
                    r -> {},
                    e -> {}
            )).isInstanceOf(TraineeNotFoundException.class);

            verify(apiMetrics).recordResponse(ENDPOINT, METHOD, 404);
        }

        @Test
        @DisplayName("Should return 400 for ValidationException")
        void shouldReturn400ForValidationException() {
            ValidationException exception = new ValidationException("Validation error");

            assertThatThrownBy(() -> metricsExecutor.executeWithMetrics(
                    metricsContext,
                    () -> { throw exception; },
                    r -> {},
                    e -> {}
            )).isInstanceOf(ValidationException.class);

            verify(apiMetrics).recordResponse(ENDPOINT, METHOD, 400);
        }
    }

    @Nested
    @DisplayName("executeVoidWithMetrics Tests")
    class ExecuteVoidWithMetricsTests {

        @Test
        @DisplayName("Should execute void operation successfully")
        void shouldExecuteVoidOperationSuccessfully() {
            AtomicBoolean executed = new AtomicBoolean(false);
            AtomicBoolean onSuccessCalled = new AtomicBoolean(false);

            metricsExecutor.executeVoidWithMetrics(
                    metricsContext,
                    () -> executed.set(true),
                    () -> onSuccessCalled.set(true),
                    e -> {}
            );

            assertThat(executed.get()).isTrue();
            assertThat(onSuccessCalled.get()).isTrue();

            verify(apiMetrics).stopTimerSuccess(eq(mockSample), eq(OPERATION), eq(ENDPOINT), eq(METHOD));
            verify(apiMetrics).recordResponse(ENDPOINT, METHOD, 200);
        }

        @Test
        @DisplayName("Should handle exception in void operation")
        void shouldHandleExceptionInVoidOperation() {
            RuntimeException expectedException = new RuntimeException("Test error");
            AtomicBoolean onFailureCalled = new AtomicBoolean(false);

            assertThatThrownBy(() -> metricsExecutor.executeVoidWithMetrics(
                    metricsContext,
                    () -> { throw expectedException; },
                    () -> {},
                    e -> onFailureCalled.set(true)
            )).isEqualTo(expectedException);

            assertThat(onFailureCalled.get()).isTrue();

            verify(apiMetrics).stopTimerFailure(eq(mockSample), eq(OPERATION), eq(ENDPOINT), eq(METHOD));
        }
    }

    @Nested
    @DisplayName("MetricsContext Tests")
    class MetricsContextTests {

        @Test
        @DisplayName("Should build MetricsContext correctly")
        void shouldBuildMetricsContextCorrectly() {
            MetricsExecutor.MetricsContext context = MetricsExecutor.MetricsContext.builder()
                    .operation("test_op")
                    .endpoint("/api/test")
                    .method("POST")
                    .build();

            assertThat(context.operation()).isEqualTo("test_op");
            assertThat(context.endpoint()).isEqualTo("/api/test");
            assertThat(context.method()).isEqualTo("POST");
        }
    }
}

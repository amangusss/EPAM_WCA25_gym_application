package com.github.amangusss.gym_application.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiPerformanceMetrics Tests")
class ApiPerformanceMetricsTest {

    private MeterRegistry meterRegistry;
    private ApiPerformanceMetrics apiPerformanceMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        apiPerformanceMetrics = new ApiPerformanceMetrics(meterRegistry);
    }

    @Test
    @DisplayName("Should initialize API performance metrics counters")
    void shouldInitializeApiPerformanceMetricsCounters() {
        assertThat(meterRegistry.find("api.requests.total").counter()).isNotNull();
        assertThat(meterRegistry.find("api.requests.success").counter()).isNotNull();
        assertThat(meterRegistry.find("api.requests.failure").counter()).isNotNull();
    }

    @Test
    @DisplayName("Should increment request counter when starting timer")
    void shouldIncrementRequestCounterWhenStartingTimer() {
        Counter requestCounter = meterRegistry.find("api.requests.total").counter();
        Assertions.assertNotNull(requestCounter);
        double initialCount = requestCounter.count();

        apiPerformanceMetrics.startTimer();

        assertThat(requestCounter.count()).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should record operation duration with success isActive")
    void shouldRecordOperationDurationWithSuccessStatus() {
        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        String operation = "create_training";
        String endpoint = "/api/trainings";
        String method = "POST";

        apiPerformanceMetrics.stopTimerSuccess(sample, operation, endpoint, method);

        Timer timer = meterRegistry.find("api.operation.duration")
                .tag("operation", operation)
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("isActive", "success")
                .timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should record operation duration with failure isActive")
    void shouldRecordOperationDurationWithFailureStatus() {
        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        String operation = "create_training";
        String endpoint = "/api/trainings";
        String method = "POST";

        apiPerformanceMetrics.stopTimerFailure(sample, operation, endpoint, method);

        Timer timer = meterRegistry.find("api.operation.duration")
                .tag("operation", operation)
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("isActive", "failure")
                .timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should increment success counter on successful operation")
    void shouldIncrementSuccessCounterOnSuccessfulOperation() {
        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        Counter successCounter = meterRegistry.find("api.requests.success").counter();
        Assertions.assertNotNull(successCounter);
        double initialCount = successCounter.count();

        apiPerformanceMetrics.stopTimerSuccess(sample, "test_operation", "/test", "GET");

        assertThat(successCounter.count()).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should increment failure counter on failed operation")
    void shouldIncrementFailureCounterOnFailedOperation() {
        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        Counter failureCounter = meterRegistry.find("api.requests.failure").counter();
        Assertions.assertNotNull(failureCounter);
        double initialCount = failureCounter.count();

        apiPerformanceMetrics.stopTimerFailure(sample, "test_operation", "/test", "GET");

        assertThat(failureCounter.count()).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should record request per endpoint")
    void shouldRecordRequestPerEndpoint() {
        String endpoint = "/api/trainees";
        String method = "POST";

        apiPerformanceMetrics.recordRequest(endpoint, method);

        Counter counter = meterRegistry.find("api.endpoint.requests")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record response per endpoint with isActive code")
    void shouldRecordResponsePerEndpointWithStatusCode() {
        String endpoint = "/api/trainees";
        String method = "POST";
        int statusCode = 200;

        apiPerformanceMetrics.recordResponse(endpoint, method, statusCode);

        Counter counter = meterRegistry.find("api.endpoint.responses")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("status_code", String.valueOf(statusCode))
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record multiple requests to same endpoint")
    void shouldRecordMultipleRequestsToSameEndpoint() {
        String endpoint = "/api/trainers";
        String method = "GET";

        apiPerformanceMetrics.recordRequest(endpoint, method);
        apiPerformanceMetrics.recordRequest(endpoint, method);
        apiPerformanceMetrics.recordRequest(endpoint, method);

        Counter counter = meterRegistry.find("api.endpoint.requests")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(3.0);
    }

    @Test
    @DisplayName("Should differentiate between different HTTP methods")
    void shouldDifferentiateBetweenDifferentHttpMethods() {
        String endpoint = "/api/trainers";
        Timer.Sample getSample = apiPerformanceMetrics.startTimer();
        Timer.Sample postSample = apiPerformanceMetrics.startTimer();

        apiPerformanceMetrics.stopTimerSuccess(getSample, "get_trainer", endpoint, "GET");
        apiPerformanceMetrics.stopTimerSuccess(postSample, "create_trainer", endpoint, "POST");

        Timer getTimer = meterRegistry.find("api.operation.duration")
                .tag("method", "GET")
                .timer();
        Timer postTimer = meterRegistry.find("api.operation.duration")
                .tag("method", "POST")
                .timer();

        assertThat(getTimer).isNotNull();
        assertThat(postTimer).isNotNull();
        assertThat(getTimer.count()).isEqualTo(1L);
        assertThat(postTimer.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should record different isActive codes separately")
    void shouldRecordDifferentStatusCodesSeparately() {
        String endpoint = "/api/trainings";
        String method = "POST";

        apiPerformanceMetrics.recordResponse(endpoint, method, 200);
        apiPerformanceMetrics.recordResponse(endpoint, method, 400);
        apiPerformanceMetrics.recordResponse(endpoint, method, 500);

        Counter counter200 = meterRegistry.find("api.endpoint.responses")
                .tag("status_code", "200")
                .counter();
        Counter counter400 = meterRegistry.find("api.endpoint.responses")
                .tag("status_code", "400")
                .counter();
        Counter counter500 = meterRegistry.find("api.endpoint.responses")
                .tag("status_code", "500")
                .counter();

        Assertions.assertNotNull(counter200);
        assertThat(counter200.count()).isEqualTo(1.0);
        Assertions.assertNotNull(counter400);
        assertThat(counter400.count()).isEqualTo(1.0);
        Assertions.assertNotNull(counter500);
        assertThat(counter500.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should measure operation duration")
    void shouldMeasureOperationDuration() throws InterruptedException {
        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        Thread.sleep(10);

        apiPerformanceMetrics.stopTimerSuccess(sample, "test_op", "/test", "GET");

        Timer timer = meterRegistry.find("api.operation.duration")
                .tag("operation", "test_op")
                .timer();
        assertThat(timer).isNotNull();
        assertThat(timer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS)).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should use stopTimer with explicit isActive")
    void shouldUseStopTimerWithExplicitStatus() {
        Timer.Sample sample = apiPerformanceMetrics.startTimer();

        apiPerformanceMetrics.stopTimer(sample, "custom_op", "/custom", "PUT", "success");

        Timer timer = meterRegistry.find("api.operation.duration")
                .tag("isActive", "success")
                .timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isGreaterThan(0);
    }
}

package com.github.amangusss.gym_application.metrics;

import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsExecutor {

    private final ApiPerformanceMetrics apiMetrics;

    public <T> T executeWithMetrics(
            MetricsContext context,
            Supplier<T> businessLogic,
            Consumer<T> onSuccess,
            Consumer<Exception> onFailure) {

        Timer.Sample sample = apiMetrics.startTimer();
        apiMetrics.recordRequest(context.endpoint(), context.method());

        try {
            T result = businessLogic.get();

            if (onSuccess != null) {
                onSuccess.accept(result);
            }

            apiMetrics.stopTimerSuccess(sample, context.operation(), context.endpoint(), context.method());
            apiMetrics.recordResponse(context.endpoint(), context.method(), 200);

            log.debug("Metrics recorded successfully for operation: {}", context.operation());
            return result;

        } catch (Exception e) {
            if (onFailure != null) {
                onFailure.accept(e);
            }

            apiMetrics.stopTimerFailure(sample, context.operation(), context.endpoint(), context.method());
            apiMetrics.recordResponse(context.endpoint(), context.method(), determineStatusCode(e));

            log.error("Operation {} failed: {}", context.operation(), e.getMessage());
            throw e;
        }
    }

    public void executeVoidWithMetrics(
            MetricsContext context,
            Runnable businessLogic,
            Runnable onSuccess,
            Consumer<Exception> onFailure) {

        Timer.Sample sample = apiMetrics.startTimer();
        apiMetrics.recordRequest(context.endpoint(), context.method());

        try {
            businessLogic.run();

            if (onSuccess != null) {
                onSuccess.run();
            }

            apiMetrics.stopTimerSuccess(sample, context.operation(), context.endpoint(), context.method());
            apiMetrics.recordResponse(context.endpoint(), context.method(), 200);

        } catch (Exception e) {
            if (onFailure != null) {
                onFailure.accept(e);
            }

            apiMetrics.stopTimerFailure(sample, context.operation(), context.endpoint(), context.method());
            apiMetrics.recordResponse(context.endpoint(), context.method(), determineStatusCode(e));

            throw e;
        }
    }

    private int determineStatusCode(Exception e) {
        String exceptionName = e.getClass().getSimpleName();
        return switch (exceptionName) {
            case "TraineeNotFoundException", "TrainerNotFoundException", "TrainingTypeNotFoundException" -> 404;
            case "ValidationException" -> 400;
            case "AccessDeniedException" -> 403;
            case "AuthenticationException", "BadCredentialsException" -> 401;
            default -> 500;
        };
    }

    public record MetricsContext(
            String operation,
            String endpoint,
            String method
    ) {
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String operation;
            private String endpoint;
            private String method;

            public Builder operation(String operation) {
                this.operation = operation;
                return this;
            }

            public Builder endpoint(String endpoint) {
                this.endpoint = endpoint;
                return this;
            }

            public Builder method(String method) {
                this.method = method;
                return this;
            }

            public MetricsContext build() {
                return new MetricsContext(operation, endpoint, method);
            }
        }
    }
}

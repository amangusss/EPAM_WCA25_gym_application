package com.github.amangusss.gym_application.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApiPerformanceMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter requestCounter;
    private final Counter successCounter;
    private final Counter failureCounter;

    public ApiPerformanceMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.requestCounter = Counter.builder("api.requests.total")
                .description("Total number of API requests")
                .register(meterRegistry);

        this.successCounter = Counter.builder("api.requests.success")
                .description("Total number of successful API requests")
                .register(meterRegistry);

        this.failureCounter = Counter.builder("api.requests.failure")
                .description("Total number of failed API requests")
                .register(meterRegistry);

        log.info("API Performance metrics initialized");
    }

    public Timer.Sample startTimer() {
        requestCounter.increment();
        return Timer.start(meterRegistry);
    }

    public void stopTimer(Timer.Sample sample, String operation, String endpoint,
                          String method, String status) {
        Timer timer = Timer.builder("api.operation.duration")
                .description("Duration of API operations")
                .tag("operation", operation)
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("status", status)
                .register(meterRegistry);

        sample.stop(timer);

        if ("success".equals(status)) {
            successCounter.increment();
        } else if ("failure".equals(status)) {
            failureCounter.increment();
        }

        log.debug("Recorded {} operation: endpoint={}, method={}, status={}",
                operation, endpoint, method, status);
    }

    public void stopTimerSuccess(Timer.Sample sample, String operation,
                                 String endpoint, String method) {
        stopTimer(sample, operation, endpoint, method, "success");
    }

    public void stopTimerFailure(Timer.Sample sample, String operation,
                                 String endpoint, String method) {
        stopTimer(sample, operation, endpoint, method, "failure");
    }

    public void recordRequest(String endpoint, String method) {
        Counter.builder("api.endpoint.requests")
                .description("Requests per endpoint")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .register(meterRegistry)
                .increment();
    }

    public void recordResponse(String endpoint, String method, int statusCode) {
        Counter.builder("api.endpoint.responses")
                .description("Responses per endpoint")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("status_code", String.valueOf(statusCode))
                .register(meterRegistry)
                .increment();
    }
}

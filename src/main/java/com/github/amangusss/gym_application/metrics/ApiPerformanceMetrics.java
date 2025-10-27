package com.github.amangusss.gym_application.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
@Component
public class ApiPerformanceMetrics {

    private final Timer trainingCreationTimer;

    public ApiPerformanceMetrics(MeterRegistry meterRegistry) {
        this.trainingCreationTimer = Timer.builder("api.training.creation.duration")
                .description("Time taken to create a training")
                .tag("endpoint", "/api/trainings")
                .tag("method", "POST")
                .register(meterRegistry);

        log.info("API performance metrics initialized");
    }

    public void recordTrainingCreationTime(Duration duration) {
        trainingCreationTimer.record(duration);
        log.debug("Training creation time recorded: {} ms", duration.toMillis());
    }
}

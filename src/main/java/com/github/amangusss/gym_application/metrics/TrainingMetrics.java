package com.github.amangusss.gym_application.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TrainingMetrics {

    private final Counter trainingCreatedCounter;
    private final Counter trainingFailedCounter;

    public TrainingMetrics(MeterRegistry meterRegistry) {
        this.trainingCreatedCounter = Counter.builder("training.created.total")
                .description("Total number of trainings created successfully")
                .tag("type", "training")
                .register(meterRegistry);

        this.trainingFailedCounter = Counter.builder("training.failed.total")
                .description("Total number of failed training creation attempts")
                .tag("type", "training")
                .register(meterRegistry);

        log.info("Training metrics initialized");
    }

    public void incrementTrainingCreated() {
        trainingCreatedCounter.increment();
        log.debug("Training created counter incremented");
    }

    public void incrementTrainingFailed() {
        trainingFailedCounter.increment();
        log.debug("Training failed counter incremented");
    }
}

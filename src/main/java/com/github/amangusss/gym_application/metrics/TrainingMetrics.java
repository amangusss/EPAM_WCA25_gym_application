package com.github.amangusss.gym_application.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrainingMetrics {

    MeterRegistry meterRegistry;
    Counter trainingCreatedCounter;
    Counter trainingDeletedCounter;
    Counter trainingFailedCounter;
    Counter totalOperationsCounter;

    public TrainingMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.trainingCreatedCounter = Counter.builder("training.operations")
                .description("Training operations counter")
                .tag("type", "training")
                .tag("isActive", "success")
                .tag("operation", "create")
                .register(meterRegistry);

        this.trainingDeletedCounter = Counter.builder("training.operations")
                .description("Training operations counter")
                .tag("type", "training")
                .tag("isActive", "success")
                .tag("operation", "delete")
                .register(meterRegistry);

        this.trainingFailedCounter = Counter.builder("training.operations")
                .description("Training operations counter")
                .tag("type", "training")
                .tag("isActive", "failure")
                .tag("operation", "create")
                .register(meterRegistry);

        this.totalOperationsCounter = Counter.builder("training.operations.total")
                .description("Total training operations counter (success + failure)")
                .register(meterRegistry);

        log.info("Training metrics initialized");
    }

    public void incrementTrainingCreated() {
        trainingCreatedCounter.increment();
        totalOperationsCounter.increment();
        log.debug("Training created counter incremented");
    }

    public void incrementTrainingDeleted() {
        trainingDeletedCounter.increment();
        totalOperationsCounter.increment();
        log.debug("Training deleted counter incremented");
    }

    public void incrementTrainingFailed() {
        trainingFailedCounter.increment();
        totalOperationsCounter.increment();
        log.debug("Training failed counter incremented");
    }

    public void incrementTrainingFailedByReason(String reason) {
        Counter.builder("training.failed.by_reason")
                .description("Training failures by reason")
                .tag("reason", reason)
                .tag("isActive", "failure")
                .register(meterRegistry)
                .increment();
    }

    public void recordTrainingDuration(double durationMinutes, String trainingType) {
        Timer.builder("training.duration")
                .description("Training session duration")
                .tag("training_type", trainingType)
                .register(meterRegistry)
                .record(java.time.Duration.ofMinutes((long) durationMinutes));
    }

    public void recordTrainingByTrainer(String trainerUsername) {
        Counter.builder("training.by_trainer")
                .description("Trainings per trainer")
                .tag("trainer", trainerUsername)
                .register(meterRegistry)
                .increment();
    }

    public void recordTrainingByTrainee(String traineeUsername) {
        Counter.builder("training.by_trainee")
                .description("Trainings per trainee")
                .tag("trainee", traineeUsername)
                .register(meterRegistry)
                .increment();
    }
}

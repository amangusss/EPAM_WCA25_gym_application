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
public class TraineeMetrics {

    MeterRegistry meterRegistry;
    Counter traineeRegisteredCounter;
    Counter traineeUpdatedCounter;
    Counter traineeDeletedCounter;
    Counter totalOperationsCounter;

    public TraineeMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.traineeRegisteredCounter = Counter.builder("trainee.operations")
                .description("Trainee operations counter")
                .tag("type", "trainee")
                .tag("status", "success")
                .tag("operation", "register")
                .register(meterRegistry);

        this.traineeUpdatedCounter = Counter.builder("trainee.operations")
                .description("Trainee operations counter")
                .tag("type", "trainee")
                .tag("status", "success")
                .tag("operation", "update")
                .register(meterRegistry);

        this.traineeDeletedCounter = Counter.builder("trainee.operations")
                .description("Trainee operations counter")
                .tag("type", "trainee")
                .tag("status", "success")
                .tag("operation", "delete")
                .register(meterRegistry);

        this.totalOperationsCounter = Counter.builder("trainee.operations.total")
                .description("Total trainee operations counter (success + failure)")
                .register(meterRegistry);

        log.info("Trainee metrics initialized");
    }

    public void incrementTraineeRegistered() {
        traineeRegisteredCounter.increment();
        totalOperationsCounter.increment();
        log.debug("Trainee registered counter incremented");
    }

    public void incrementTraineeUpdated() {
        traineeUpdatedCounter.increment();
        totalOperationsCounter.increment();
        log.debug("Trainee updated counter incremented");
    }

    public void incrementTraineeDeleted() {
        traineeDeletedCounter.increment();
        totalOperationsCounter.increment();
        log.debug("Trainee deleted counter incremented");
    }

    public void incrementTraineeOperationFailed(String operation) {
        Counter.builder("trainee.operations")
                .description("Trainee operations counter")
                .tag("type", "trainee")
                .tag("status", "failure")
                .tag("operation", operation)
                .register(meterRegistry)
                .increment();
        totalOperationsCounter.increment();

        log.debug("Trainee {} operation failed counter incremented", operation);
    }

    public void recordTraineeProfileView(String username) {
        Counter.builder("trainee.profile.views")
                .description("Trainee profile views")
                .tag("trainee", username)
                .register(meterRegistry)
                .increment();
    }

    public void recordTraineeActivation(boolean isActive) {
        Counter.builder("trainee.status.changes")
                .description("Trainee status changes")
                .tag("status", isActive ? "activated" : "deactivated")
                .register(meterRegistry)
                .increment();
    }

    public void recordTraineeTrainersUpdate(String username, int trainersCount) {
        Counter.builder("trainee.trainers.updates")
                .description("Trainee trainers list updates")
                .tag("trainee", username)
                .register(meterRegistry)
                .increment();

        Timer.builder("trainee.trainers.count")
                .description("Number of trainers assigned to trainee")
                .tag("trainee", username)
                .register(meterRegistry)
                .record(java.time.Duration.ofSeconds(trainersCount));
    }

    public void recordTraineeTrainingsQuery(String username) {
        Counter.builder("trainee.trainings.queries")
                .description("Trainee trainings list queries")
                .tag("trainee", username)
                .register(meterRegistry)
                .increment();
    }

    public void recordUnassignedTrainersQuery(String username) {
        Counter.builder("trainee.unassigned_trainers.queries")
                .description("Unassigned trainers list queries")
                .tag("trainee", username)
                .register(meterRegistry)
                .increment();
    }
}

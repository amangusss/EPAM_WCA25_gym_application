package com.github.amangusss.gym_application.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrainerMetrics {

    MeterRegistry meterRegistry;
    Counter trainerRegisteredCounter;
    Counter trainerUpdatedCounter;

    public TrainerMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.trainerRegisteredCounter = Counter.builder("trainer.operations")
                .description("Trainer operations counter")
                .tag("type", "trainer")
                .tag("status", "success")
                .tag("operation", "register")
                .register(meterRegistry);

        this.trainerUpdatedCounter = Counter.builder("trainer.operations")
                .description("Trainer operations counter")
                .tag("type", "trainer")
                .tag("status", "success")
                .tag("operation", "update")
                .register(meterRegistry);

        log.info("Trainer metrics initialized");
    }

    public void incrementTrainerRegistered() {
        trainerRegisteredCounter.increment();
        log.debug("Trainer registered counter incremented");
    }

    public void incrementTrainerUpdated() {
        trainerUpdatedCounter.increment();
        log.debug("Trainer updated counter incremented");
    }

    public void incrementTrainerOperationFailed(String operation) {
        Counter.builder("trainer.operations")
                .description("Trainer operations counter")
                .tag("type", "trainer")
                .tag("status", "failure")
                .tag("operation", operation)
                .register(meterRegistry)
                .increment();

        log.debug("Trainer {} operation failed counter incremented", operation);
    }

    public void recordTrainerProfileView(String username) {
        Counter.builder("trainer.profile.views")
                .description("Trainer profile views")
                .tag("trainer", username)
                .register(meterRegistry)
                .increment();
    }

    public void recordTrainerActivation(boolean isActive) {
        Counter.builder("trainer.status.changes")
                .description("Trainer status changes")
                .tag("status", isActive ? "activated" : "deactivated")
                .register(meterRegistry)
                .increment();
    }

    public void recordTrainerBySpecialization(Long specialization) {
        Counter.builder("trainer.by_specialization")
                .description("Trainers per specialization")
                .tag("specialization", specialization.toString())
                .register(meterRegistry)
                .increment();
    }

    public void recordTrainerTrainingsQuery(String username) {
        Counter.builder("trainer.trainings.queries")
                .description("Trainer trainings list queries")
                .tag("trainer", username)
                .register(meterRegistry)
                .increment();
    }
}

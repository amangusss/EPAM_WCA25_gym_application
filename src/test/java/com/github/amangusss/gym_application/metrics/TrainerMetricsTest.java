package com.github.amangusss.gym_application.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TrainerMetrics Tests")
class TrainerMetricsTest {

    private MeterRegistry meterRegistry;
    private TrainerMetrics trainerMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        trainerMetrics = new TrainerMetrics(meterRegistry);
    }

    @Test
    @DisplayName("Should initialize trainer metrics counters")
    void shouldInitializeTrainerMetricsCounters() {
        assertThat(meterRegistry.find("trainer.operations").counters()).isNotEmpty();
        assertThat(meterRegistry.find("trainer.operations.total").counter()).isNotNull();
    }

    @Test
    @DisplayName("Should increment trainer registered counter")
    void shouldIncrementTrainerRegisteredCounter() {
        Counter operationsCounter = meterRegistry.find("trainer.operations.total").counter();
        Assertions.assertNotNull(operationsCounter);
        double initialCount = operationsCounter.count();

        trainerMetrics.incrementTrainerRegistered();

        assertThat(operationsCounter.count()).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should increment trainer updated counter")
    void shouldIncrementTrainerUpdatedCounter() {
        Counter operationsCounter = meterRegistry.find("trainer.operations.total").counter();
        Assertions.assertNotNull(operationsCounter);
        double initialCount = operationsCounter.count();

        trainerMetrics.incrementTrainerUpdated();

        assertThat(operationsCounter.count()).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should increment trainer operation failed counter")
    void shouldIncrementTrainerOperationFailedCounter() {
        String operation = "register";
        Counter operationsCounter = meterRegistry.find("trainer.operations.total").counter();
        Assertions.assertNotNull(operationsCounter);
        double initialCount = operationsCounter.count();

        trainerMetrics.incrementTrainerOperationFailed(operation);

        assertThat(operationsCounter.count()).isEqualTo(initialCount + 1);
        Counter failedCounter = meterRegistry.find("trainer.operations")
                .tag("operation", operation)
                .tag("status", "failure")
                .counter();
        assertThat(failedCounter).isNotNull();
        assertThat(failedCounter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record trainer profile view")
    void shouldRecordTrainerProfileView() {
        String username = "Jane.Smith";

        trainerMetrics.recordTrainerProfileView(username);

        Counter counter = meterRegistry.find("trainer.profile.views")
                .tag("trainer", username)
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record trainer activation")
    void shouldRecordTrainerActivation() {
        trainerMetrics.recordTrainerActivation(true);

        Counter counter = meterRegistry.find("trainer.status.changes")
                .tag("status", "activated")
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record trainer deactivation")
    void shouldRecordTrainerDeactivation() {
        trainerMetrics.recordTrainerActivation(false);

        Counter counter = meterRegistry.find("trainer.status.changes")
                .tag("status", "deactivated")
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record trainer by specialization")
    void shouldRecordTrainerBySpecialization() {
        Long specializationId = 1L;

        trainerMetrics.recordTrainerBySpecialization(specializationId);

        Counter counter = meterRegistry.find("trainer.by_specialization")
                .tag("specialization", specializationId.toString())
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record trainer trainings query")
    void shouldRecordTrainerTrainingsQuery() {
        String username = "Jane.Smith";

        trainerMetrics.recordTrainerTrainingsQuery(username);

        Counter counter = meterRegistry.find("trainer.trainings.queries")
                .tag("trainer", username)
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should increment multiple trainer operations")
    void shouldIncrementMultipleTrainerOperations() {
        Counter operationsCounter = meterRegistry.find("trainer.operations.total").counter();
        Assertions.assertNotNull(operationsCounter);
        double initialCount = operationsCounter.count();

        trainerMetrics.incrementTrainerRegistered();
        trainerMetrics.incrementTrainerUpdated();
        trainerMetrics.incrementTrainerOperationFailed("delete");

        assertThat(operationsCounter.count()).isEqualTo(initialCount + 3);
    }

    @Test
    @DisplayName("Should record trainer operations with correct tags")
    void shouldRecordTrainerOperationsWithCorrectTags() {
        trainerMetrics.incrementTrainerRegistered();

        Counter counter = meterRegistry.find("trainer.operations")
                .tag("type", "trainer")
                .tag("status", "success")
                .tag("operation", "register")
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should record multiple profile views for same trainer")
    void shouldRecordMultipleProfileViewsForSameTrainer() {
        String username = "Jane.Smith";

        trainerMetrics.recordTrainerProfileView(username);
        trainerMetrics.recordTrainerProfileView(username);
        trainerMetrics.recordTrainerProfileView(username);

        Counter counter = meterRegistry.find("trainer.profile.views")
                .tag("trainer", username)
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(3.0);
    }
}

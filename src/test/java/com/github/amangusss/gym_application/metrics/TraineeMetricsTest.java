package com.github.amangusss.gym_application.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TraineeMetrics Tests")
class TraineeMetricsTest {

    private MeterRegistry meterRegistry;
    private TraineeMetrics traineeMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        traineeMetrics = new TraineeMetrics(meterRegistry);
    }

    @Test
    @DisplayName("Should initialize trainee metrics counters")
    void shouldInitializeTraineeMetricsCounters() {
        assertThat(meterRegistry.find("trainee.operations").counters()).isNotEmpty();
        assertThat(meterRegistry.find("trainee.operations.total").counter()).isNotNull();
    }

    @Test
    @DisplayName("Should increment trainee registered counter")
    void shouldIncrementTraineeRegisteredCounter() {
        Counter operationsCounter = meterRegistry.find("trainee.operations.total").counter();
        Assertions.assertNotNull(operationsCounter);
        double initialCount = operationsCounter.count();

        traineeMetrics.incrementTraineeRegistered();

        assertThat(operationsCounter.count()).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should increment trainee updated counter")
    void shouldIncrementTraineeUpdatedCounter() {
        Counter operationsCounter = meterRegistry.find("trainee.operations.total").counter();
        Assertions.assertNotNull(operationsCounter);
        double initialCount = operationsCounter.count();

        traineeMetrics.incrementTraineeUpdated();

        assertThat(operationsCounter.count()).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should increment trainee deleted counter")
    void shouldIncrementTraineeDeletedCounter() {
        Counter operationsCounter = meterRegistry.find("trainee.operations.total").counter();
        Assertions.assertNotNull(operationsCounter);
        double initialCount = operationsCounter.count();

        traineeMetrics.incrementTraineeDeleted();

        assertThat(operationsCounter.count()).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should increment trainee operation failed counter")
    void shouldIncrementTraineeOperationFailedCounter() {
        String operation = "register";
        Counter operationsCounter = meterRegistry.find("trainee.operations.total").counter();
        Assertions.assertNotNull(operationsCounter);
        double initialCount = operationsCounter.count();

        traineeMetrics.incrementTraineeOperationFailed(operation);

        assertThat(operationsCounter.count()).isEqualTo(initialCount + 1);
        Counter failedCounter = meterRegistry.find("trainee.operations")
                .tag("operation", operation)
                .tag("isActive", "failure")
                .counter();
        assertThat(failedCounter).isNotNull();
        assertThat(failedCounter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record trainee profile view")
    void shouldRecordTraineeProfileView() {
        String username = "John.Doe";

        traineeMetrics.recordTraineeProfileView(username);

        Counter counter = meterRegistry.find("trainee.profile.views")
                .tag("trainee", username)
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record trainee activation")
    void shouldRecordTraineeActivation() {
        traineeMetrics.recordTraineeActivation(true);

        Counter counter = meterRegistry.find("trainee.isActive.changes")
                .tag("isActive", "activated")
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record trainee deactivation")
    void shouldRecordTraineeDeactivation() {
        traineeMetrics.recordTraineeActivation(false);

        Counter counter = meterRegistry.find("trainee.isActive.changes")
                .tag("isActive", "deactivated")
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record trainee trainers update")
    void shouldRecordTraineeTrainersUpdate() {
        String username = "John.Doe";
        int trainersCount = 3;

        traineeMetrics.recordTraineeTrainersUpdate(username, trainersCount);

        Counter counter = meterRegistry.find("trainee.trainers.updates")
                .tag("trainee", username)
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record trainee trainings query")
    void shouldRecordTraineeTrainingsQuery() {
        String username = "John.Doe";

        traineeMetrics.recordTraineeTrainingsQuery(username);

        Counter counter = meterRegistry.find("trainee.trainings.queries")
                .tag("trainee", username)
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record unassigned trainers query")
    void shouldRecordUnassignedTrainersQuery() {
        String username = "John.Doe";

        traineeMetrics.recordUnassignedTrainersQuery(username);

        Counter counter = meterRegistry.find("trainee.unassigned_trainers.queries")
                .tag("trainee", username)
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should increment multiple trainee operations")
    void shouldIncrementMultipleTraineeOperations() {
        Counter operationsCounter = meterRegistry.find("trainee.operations.total").counter();
        Assertions.assertNotNull(operationsCounter);
        double initialCount = operationsCounter.count();

        traineeMetrics.incrementTraineeRegistered();
        traineeMetrics.incrementTraineeUpdated();
        traineeMetrics.incrementTraineeDeleted();

        assertThat(operationsCounter.count()).isEqualTo(initialCount + 3);
    }

    @Test
    @DisplayName("Should record trainee operations with correct tags")
    void shouldRecordTraineeOperationsWithCorrectTags() {
        traineeMetrics.incrementTraineeRegistered();

        Counter counter = meterRegistry.find("trainee.operations")
                .tag("type", "trainee")
                .tag("isActive", "success")
                .tag("operation", "register")
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isGreaterThan(0);
    }
}

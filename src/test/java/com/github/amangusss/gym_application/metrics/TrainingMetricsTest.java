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

@DisplayName("TrainingMetrics Tests")
class TrainingMetricsTest {

    private MeterRegistry meterRegistry;
    private TrainingMetrics trainingMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        trainingMetrics = new TrainingMetrics(meterRegistry);
    }

    @Test
    @DisplayName("Should initialize training metrics counters")
    void shouldInitializeTrainingMetricsCounters() {
        assertThat(meterRegistry.find("training.operations").counters()).isNotEmpty();
        assertThat(meterRegistry.find("training.operations.total").counter()).isNotNull();
    }

    @Test
    @DisplayName("Should increment training created counter")
    void shouldIncrementTrainingCreatedCounter() {
        Counter operationsCounter = meterRegistry.find("training.operations.total").counter();
        Assertions.assertNotNull(operationsCounter);
        double initialCount = operationsCounter.count();

        trainingMetrics.incrementTrainingCreated();

        assertThat(operationsCounter.count()).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should increment training failed counter")
    void shouldIncrementTrainingFailedCounter() {
        Counter operationsCounter = meterRegistry.find("training.operations.total").counter();
        Assertions.assertNotNull(operationsCounter);
        double initialCount = operationsCounter.count();

        trainingMetrics.incrementTrainingFailed();

        assertThat(operationsCounter.count()).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should record training failed by reason")
    void shouldRecordTrainingFailedByReason() {
        String reason = "TraineeNotFoundException";

        trainingMetrics.incrementTrainingFailedByReason(reason);

        Counter counter = meterRegistry.find("training.failed.by_reason")
                .tag("reason", reason)
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record training duration")
    void shouldRecordTrainingDuration() {
        long durationMinutes = 60L;
        String trainingType = "Cardio";

        trainingMetrics.recordTrainingDuration(durationMinutes, trainingType);

        Timer timer = meterRegistry.find("training.duration")
                .tag("training_type", trainingType)
                .timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should record training by trainer")
    void shouldRecordTrainingByTrainer() {
        String trainerUsername = "John.Doe";

        trainingMetrics.recordTrainingByTrainer(trainerUsername);

        Counter counter = meterRegistry.find("training.by_trainer")
                .tag("trainer", trainerUsername)
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record training by trainee")
    void shouldRecordTrainingByTrainee() {
        String traineeUsername = "Jane.Smith";

        trainingMetrics.recordTrainingByTrainee(traineeUsername);

        Counter counter = meterRegistry.find("training.by_trainee")
                .tag("trainee", traineeUsername)
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should increment multiple training operations")
    void shouldIncrementMultipleTrainingOperations() {
        Counter operationsCounter = meterRegistry.find("training.operations.total").counter();
        Assertions.assertNotNull(operationsCounter);
        double initialCount = operationsCounter.count();

        trainingMetrics.incrementTrainingCreated();
        trainingMetrics.incrementTrainingCreated();
        trainingMetrics.incrementTrainingFailed();

        assertThat(operationsCounter.count()).isEqualTo(initialCount + 3);
    }

    @Test
    @DisplayName("Should record training with isActive success tag")
    void shouldRecordTrainingWithStatusSuccessTag() {
        trainingMetrics.incrementTrainingCreated();

        Counter counter = meterRegistry.find("training.operations")
                .tag("type", "training")
                .tag("isActive", "success")
                .tag("operation", "create")
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should record training with isActive failure tag")
    void shouldRecordTrainingWithStatusFailureTag() {
        trainingMetrics.incrementTrainingFailed();

        Counter counter = meterRegistry.find("training.operations")
                .tag("type", "training")
                .tag("isActive", "failure")
                .tag("operation", "create")
                .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isGreaterThan(0);
    }
}

package com.github.amangusss.gym_application.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TrainingTypeMetrics Tests")
class TrainingTypeMetricsTest {

    private MeterRegistry meterRegistry;
    private TrainingTypeMetrics trainingTypeMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        trainingTypeMetrics = new TrainingTypeMetrics(meterRegistry);
    }

    @Test
    @DisplayName("Should initialize training type metrics counters")
    void shouldInitializeTrainingTypeMetricsCounters() {
        assertThat(meterRegistry.find("training_type.queries.total").counter()).isNotNull();
    }

    @Test
    @DisplayName("Should increment training type query counter")
    void shouldIncrementTrainingTypeQueryCounter() {
        Counter queryCounter = meterRegistry.find("training_type.queries.total").counter();
        Assertions.assertNotNull(queryCounter);
        double initialCount = queryCounter.count();

        trainingTypeMetrics.incrementTrainingTypeQuery();

        assertThat(queryCounter.count()).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should increment multiple training type queries")
    void shouldIncrementMultipleTrainingTypeQueries() {
        Counter queryCounter = meterRegistry.find("training_type.queries.total").counter();
        Assertions.assertNotNull(queryCounter);
        double initialCount = queryCounter.count();

        trainingTypeMetrics.incrementTrainingTypeQuery();
        trainingTypeMetrics.incrementTrainingTypeQuery();
        trainingTypeMetrics.incrementTrainingTypeQuery();

        assertThat(queryCounter.count()).isEqualTo(initialCount + 3);
    }
}

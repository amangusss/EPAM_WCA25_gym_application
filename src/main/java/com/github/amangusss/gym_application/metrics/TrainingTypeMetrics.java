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
public class TrainingTypeMetrics {

    MeterRegistry meterRegistry;
    Counter trainingTypeQueriesCounter;

    public TrainingTypeMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.trainingTypeQueriesCounter = Counter.builder("training_type.queries.total")
                .description("Total number of training type queries")
                .tag("type", "training_type")
                .register(meterRegistry);

        log.info("Training Type metrics initialized");
    }

    public void incrementTrainingTypeQuery() {
        trainingTypeQueriesCounter.increment();
        log.debug("Training type query counter incremented");
    }

}

package com.github.amangusss.gym_application.entity;

import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.entity.training.TrainingBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainingBuilderTest {

    private TrainingBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new TrainingBuilder();
    }

    @Test
    void build_WithAllFields_ShouldCreateTrainingWithAllValues() {
        Long id = 1L;
        Long trainerId = 2L;
        Long traineeId = 3L;
        String trainingName = "Morning Workout";
        TrainingType trainingType = TrainingType.FITNESS;
        LocalDate trainingDate = LocalDate.of(2024, 1, 15);
        Integer trainingDuration = 60;

        Training training = builder
                .setId(id)
                .trainerId(trainerId)
                .traineeId(traineeId)
                .trainingName(trainingName)
                .trainingType(trainingType)
                .trainingDate(trainingDate)
                .trainingDuration(trainingDuration)
                .build();

        assertNotNull(training);
        assertEquals(id, training.getId());
        assertEquals(trainerId, training.getTrainerId());
        assertEquals(traineeId, training.getTraineeId());
        assertEquals(trainingName, training.getTrainingName());
        assertEquals(trainingType, training.getTrainingType());
        assertEquals(trainingDate, training.getTrainingDate());
        assertEquals(trainingDuration, training.getTrainingDuration());
    }

    @Test
    void build_WithMinimalFields_ShouldCreateTrainingWithNulls() {
        Long trainerId = 2L;
        Long traineeId = 3L;
        String trainingName = "Morning Workout";
        TrainingType trainingType = TrainingType.FITNESS;
        LocalDate trainingDate = LocalDate.of(2024, 1, 15);
        Integer trainingDuration = 60;

        Training training = builder
                .trainerId(trainerId)
                .traineeId(traineeId)
                .trainingName(trainingName)
                .trainingType(trainingType)
                .trainingDate(trainingDate)
                .trainingDuration(trainingDuration)
                .build();

        assertNotNull(training);
        assertNull(training.getId());
        assertEquals(trainerId, training.getTrainerId());
        assertEquals(traineeId, training.getTraineeId());
        assertEquals(trainingName, training.getTrainingName());
        assertEquals(trainingType, training.getTrainingType());
        assertEquals(trainingDate, training.getTrainingDate());
        assertEquals(trainingDuration, training.getTrainingDuration());
    }

    @Test
    void build_WithNullValues_ShouldCreateTrainingWithNulls() {
        Training training = builder.build();

        assertNotNull(training);
        assertNull(training.getId());
        assertNull(training.getTrainerId());
        assertNull(training.getTraineeId());
        assertNull(training.getTrainingName());
        assertNull(training.getTrainingType());
        assertNull(training.getTrainingDate());
        assertNull(training.getTrainingDuration());
    }

    @Test
    void builder_StaticFactoryMethod_ShouldCreateNewBuilder() {
        TrainingBuilder newBuilder = TrainingBuilder.builder();

        assertNotNull(newBuilder);
        assertNotSame(builder, newBuilder);
    }

    @Test
    void build_MultipleCalls_ShouldCreateDifferentInstances() {
        Long trainerId = 2L;
        Long traineeId = 3L;
        String trainingName = "Morning Workout";
        TrainingType trainingType = TrainingType.FITNESS;
        LocalDate trainingDate = LocalDate.of(2024, 1, 15);
        Integer trainingDuration = 60;

        Training training1 = builder
                .trainerId(trainerId)
                .traineeId(traineeId)
                .trainingName(trainingName)
                .trainingType(trainingType)
                .trainingDate(trainingDate)
                .trainingDuration(trainingDuration)
                .build();

        Training training2 = builder
                .trainerId(trainerId)
                .traineeId(traineeId)
                .trainingName(trainingName)
                .trainingType(trainingType)
                .trainingDate(trainingDate)
                .trainingDuration(trainingDuration)
                .build();

        assertNotNull(training1);
        assertNotNull(training2);
        assertNotSame(training1, training2);
        assertEquals(training1.getTrainerId(), training2.getTrainerId());
        assertEquals(training1.getTraineeId(), training2.getTraineeId());
        assertEquals(training1.getTrainingName(), training2.getTrainingName());
        assertEquals(training1.getTrainingType(), training2.getTrainingType());
        assertEquals(training1.getTrainingDate(), training2.getTrainingDate());
        assertEquals(training1.getTrainingDuration(), training2.getTrainingDuration());
    }
}
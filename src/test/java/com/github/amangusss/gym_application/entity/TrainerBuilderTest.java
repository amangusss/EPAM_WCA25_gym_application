package com.github.amangusss.gym_application.entity;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.trainer.TrainerBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TrainerBuilderTest {

    private TrainerBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new TrainerBuilder();
    }

    @Test
    void build_WithAllFields_ShouldCreateTrainerWithAllValues() {
        String firstName = "Jane";
        String lastName = "Smith";
        TrainingType specialization = TrainingType.FITNESS;
        Set<Trainee> trainees = new HashSet<>();
        boolean isActive = true;
        String username = "jane.smith";
        String password = "password123";

        Trainer trainer = builder
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization)
                .traineeId(trainees)
                .isActive(isActive)
                .username(username)
                .password(password)
                .build();

        assertNotNull(trainer);
        assertEquals(firstName, trainer.getFirstName());
        assertEquals(lastName, trainer.getLastName());
        assertEquals(specialization, trainer.getSpecialization());
        assertEquals(trainees, trainer.getTraineeId());
        assertEquals(isActive, trainer.isActive());
        assertEquals(username, trainer.getUsername());
        assertEquals(password, trainer.getPassword());
    }

    @Test
    void build_WithMinimalFields_ShouldCreateTrainerWithDefaults() {
        String firstName = "John";
        String lastName = "Doe";
        TrainingType specialization = TrainingType.YOGA;

        Trainer trainer = builder
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization)
                .build();

        assertNotNull(trainer);
        assertEquals(firstName, trainer.getFirstName());
        assertEquals(lastName, trainer.getLastName());
        assertEquals(specialization, trainer.getSpecialization());
        assertNotNull(trainer.getTraineeId());
        assertTrue(trainer.getTraineeId().isEmpty());
        assertTrue(trainer.isActive());
        assertNull(trainer.getUsername());
        assertNull(trainer.getPassword());
    }

    @Test
    void build_WithNullTraineeId_ShouldCreateEmptySet() {
        String firstName = "John";
        String lastName = "Doe";
        TrainingType specialization = TrainingType.YOGA;

        Trainer trainer = builder
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization)
                .traineeId(null)
                .build();

        assertNotNull(trainer);
        assertNotNull(trainer.getTraineeId());
        assertTrue(trainer.getTraineeId().isEmpty());
    }

    @Test
    void addTrainee_ShouldAddToExistingSet() {
        String firstName = "John";
        String lastName = "Doe";
        TrainingType specialization = TrainingType.YOGA;
        Trainee trainee1 = new Trainee("Alice", "Johnson");
        trainee1.setId(1L);
        trainee1.setUsername("alice.johnson");
        Trainee trainee2 = new Trainee("Bob", "Wilson");
        trainee2.setId(2L);
        trainee2.setUsername("bob.wilson");

        Trainer trainer = builder
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization)
                .addTrainee(trainee1)
                .addTrainee(trainee2)
                .build();

        assertNotNull(trainer);
        assertEquals(2, trainer.getTraineeId().size());
        assertTrue(trainer.getTraineeId().contains(trainee1));
        assertTrue(trainer.getTraineeId().contains(trainee2));
    }

    @Test
    void builder_StaticFactoryMethod_ShouldCreateNewBuilder() {
        TrainerBuilder newBuilder = TrainerBuilder.builder();

        assertNotNull(newBuilder);
        assertNotSame(builder, newBuilder);
    }

    @Test
    void build_MultipleCalls_ShouldCreateDifferentInstances() {
        String firstName = "John";
        String lastName = "Doe";
        TrainingType specialization = TrainingType.YOGA;

        Trainer trainer1 = builder
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization)
                .build();

        Trainer trainer2 = builder
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization)
                .build();

        assertNotNull(trainer1);
        assertNotNull(trainer2);
        assertNotSame(trainer1, trainer2);
        assertEquals(trainer1.getFirstName(), trainer2.getFirstName());
        assertEquals(trainer1.getLastName(), trainer2.getLastName());
        assertEquals(trainer1.getSpecialization(), trainer2.getSpecialization());
    }
}
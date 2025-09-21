package com.github.amangusss.gym_application.entity;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainee.TraineeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TraineeBuilderTest {

    private TraineeBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new TraineeBuilder();
    }

    @Test
    void build_WithAllFields_ShouldCreateTraineeWithAllValues() {
        String firstName = "John";
        String lastName = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 5, 15);
        String address = "123 Main St";
        Set<Long> trainerIds = Set.of(1L, 2L);
        boolean isActive = true;
        String username = "john.doe";
        String password = "password123";

        Trainee trainee = builder
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .address(address)
                .trainerIds(trainerIds)
                .isActive(isActive)
                .username(username)
                .password(password)
                .build();

        assertNotNull(trainee);
        assertEquals(firstName, trainee.getFirstName());
        assertEquals(lastName, trainee.getLastName());
        assertEquals(dateOfBirth, trainee.getDateOfBirth());
        assertEquals(address, trainee.getAddress());
        assertEquals(trainerIds, trainee.getTrainerIds());
        assertEquals(isActive, trainee.isActive());
        assertEquals(username, trainee.getUsername());
        assertEquals(password, trainee.getPassword());
    }

    @Test
    void build_WithMinimalFields_ShouldCreateTraineeWithDefaults() {
        String firstName = "Jane";
        String lastName = "Smith";

        Trainee trainee = builder
                .firstName(firstName)
                .lastName(lastName)
                .build();

        assertNotNull(trainee);
        assertEquals(firstName, trainee.getFirstName());
        assertEquals(lastName, trainee.getLastName());
        assertNull(trainee.getDateOfBirth());
        assertNull(trainee.getAddress());
        assertNotNull(trainee.getTrainerIds());
        assertTrue(trainee.getTrainerIds().isEmpty());
        assertTrue(trainee.isActive());
        assertNull(trainee.getUsername());
        assertNull(trainee.getPassword());
    }

    @Test
    void build_WithNullTrainerIds_ShouldCreateEmptySet() {
        String firstName = "John";
        String lastName = "Doe";

        Trainee trainee = builder
                .firstName(firstName)
                .lastName(lastName)
                .trainerIds(null)
                .build();

        assertNotNull(trainee);
        assertNotNull(trainee.getTrainerIds());
        assertTrue(trainee.getTrainerIds().isEmpty());
    }

    @Test
    void addTrainerId_ShouldAddToExistingSet() {
        String firstName = "John";
        String lastName = "Doe";
        Long trainerId1 = 1L;
        Long trainerId2 = 2L;

        Trainee trainee = builder
                .firstName(firstName)
                .lastName(lastName)
                .addTrainerId(trainerId1)
                .addTrainerId(trainerId2)
                .build();

        assertNotNull(trainee);
        assertEquals(2, trainee.getTrainerIds().size());
        assertTrue(trainee.getTrainerIds().contains(trainerId1));
        assertTrue(trainee.getTrainerIds().contains(trainerId2));
    }

    @Test
    void builder_StaticFactoryMethod_ShouldCreateNewBuilder() {
        TraineeBuilder newBuilder = TraineeBuilder.builder();

        assertNotNull(newBuilder);
        assertNotSame(builder, newBuilder);
    }

    @Test
    void build_MultipleCalls_ShouldCreateDifferentInstances() {
        String firstName = "John";
        String lastName = "Doe";

        Trainee trainee1 = builder
                .firstName(firstName)
                .lastName(lastName)
                .build();

        Trainee trainee2 = builder
                .firstName(firstName)
                .lastName(lastName)
                .build();

        assertNotNull(trainee1);
        assertNotNull(trainee2);
        assertNotSame(trainee1, trainee2);
        assertEquals(trainee1.getFirstName(), trainee2.getFirstName());
        assertEquals(trainee1.getLastName(), trainee2.getLastName());
    }
}
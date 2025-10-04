package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.config.GymApplicationConfig;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.exception.TraineeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GymApplicationConfig.class)
@Transactional
class TraineeRepositoryTest {

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    private Trainee testTrainee;

    @BeforeEach
    void setUp() {
        testTrainee = Trainee.builder()
                .firstName("Aman")
                .lastName("Nazarkulov")
                .username("Aman.Nazarkulov")
                .password("password123")
                .isActive(true)
                .dateOfBirth(LocalDate.of(2004, 2, 14))
                .address("Isakeev st, 18/10 Block 15")
                .build();
    }

    @Test
    void save_ShouldPersistTrainee() {
        Trainee saved = traineeRepository.save(testTrainee);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Aman.Nazarkulov", saved.getUsername());
        assertEquals("Isakeev st, 18/10 Block 15", saved.getAddress());
    }

    @Test
    void findByUsername_ShouldReturnTrainee() {
        traineeRepository.save(testTrainee);

        Trainee found = traineeRepository.findByUsername("Aman.Nazarkulov");

        assertNotNull(found);
        assertEquals("Aman.Nazarkulov", found.getUsername());
        assertEquals("Aman", found.getFirstName());
    }

    @Test
    void findByUsername_WithNonExistentUsername_ShouldThrowException() {
        assertThrows(TraineeNotFoundException.class,
            () -> traineeRepository.findByUsername("NonExistent"));
    }

    @Test
    void existsByUsernameAndPassword_ShouldReturnTrue() {
        traineeRepository.save(testTrainee);

        boolean exists = traineeRepository.existsByUsernameAndPassword("Aman.Nazarkulov", "password123");

        assertTrue(exists);
    }

    @Test
    void existsByUsernameAndPassword_WithWrongPassword_ShouldReturnFalse() {
        traineeRepository.save(testTrainee);

        boolean exists = traineeRepository.existsByUsernameAndPassword("Aman.Nazarkulov", "wrongPassword");

        assertFalse(exists);
    }

    @Test
    void update_ShouldUpdateTrainee() {
        Trainee saved = traineeRepository.save(testTrainee);
        saved.setAddress("Erkindik boulevard, 8");

        Trainee updated = traineeRepository.update(saved);

        assertEquals("Erkindik boulevard, 8", updated.getAddress());
    }

    @Test
    void deleteByUsername_ShouldRemoveTrainee() {
        traineeRepository.save(testTrainee);

        traineeRepository.deleteByUsername("Aman.Nazarkulov");

        assertThrows(TraineeNotFoundException.class,
            () -> traineeRepository.findByUsername("Aman.Nazarkulov"));
    }

    @Test
    void updatePasswordByUsername_ShouldUpdatePassword() {
        traineeRepository.save(testTrainee);

        Trainee updated = traineeRepository.updatePasswordByUsername("Aman.Nazarkulov", "password123", "newPassword");

        assertTrue(traineeRepository.existsByUsernameAndPassword(updated.getUsername(), updated.getPassword()));
        assertFalse(traineeRepository.existsByUsernameAndPassword(updated.getUsername(), "password123"));
    }

    @Test
    void updateActiveStatusByUsername_ShouldUpdateStatus() {
        traineeRepository.save(testTrainee);

        Trainee updated = traineeRepository.updateActiveStatusByUsername("Aman.Nazarkulov", false);

        assertFalse(updated.isActive());
    }

    @Test
    void findTrainersNotAssignedOnTraineeByUsername_ShouldReturnUnassignedTrainers() {
        traineeRepository.save(testTrainee);

        Trainer trainer1 = Trainer.builder()
                .firstName("Dastan")
                .lastName("Ibraimov")
                .username("Dastan.Ibraimov")
                .password("pass123")
                .isActive(true)
                .specialization(TrainingType.YOGA)
                .build();
        trainerRepository.save(trainer1);

        List<Trainer> unassigned = traineeRepository.findTrainersNotAssignedOnTraineeByUsername("Aman.Nazarkulov");

        assertFalse(unassigned.isEmpty());
        assertTrue(unassigned.stream().anyMatch(t -> t.getUsername().equals("Dastan.Ibraimov")));
    }

    @Test
    void updateTrainersListByUsername_ShouldUpdateTrainersList() {
        traineeRepository.save(testTrainee);

        Trainer trainer = Trainer.builder()
                .firstName("Dastan")
                .lastName("Ibraimov")
                .username("Dastan.Ibraimov")
                .password("pass123")
                .isActive(true)
                .specialization(TrainingType.FITNESS)
                .build();
        trainerRepository.save(trainer);

        Set<Trainer> trainers = new HashSet<>();
        trainers.add(trainer);

        Trainee updated = traineeRepository.updateTrainersListByUsername("Aman.Nazarkulov", trainers);

        assertNotNull(updated.getTrainers());
        assertEquals(1, updated.getTrainers().size());
    }

    @Test
    void findTrainingsByUsername_ShouldReturnFilteredTrainings() {
        traineeRepository.save(testTrainee);

        List<com.github.amangusss.gym_application.entity.training.Training> trainings =
            traineeRepository.findTrainingsByUsername("Aman.Nazarkulov", null, null, null, null);

        assertNotNull(trainings);
    }
}
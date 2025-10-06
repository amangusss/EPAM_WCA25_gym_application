package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.config.GymApplicationConfig;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GymApplicationConfig.class)
@Transactional
class TrainerRepositoryTest {

    @Autowired
    private TrainerRepository trainerRepository;

    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        testTrainer = Trainer.builder()
                .firstName("Dastan")
                .lastName("Ibraimov")
                .username("Dastan.Ibraimov")
                .password("password123")
                .isActive(true)
                .specialization(TrainingType.YOGA)
                .build();
    }

    @Test
    void save_ShouldPersistTrainer() {
        Trainer saved = trainerRepository.save(testTrainer);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Dastan.Ibraimov", saved.getUsername());
        assertEquals(TrainingType.YOGA, saved.getSpecialization());
    }

    @Test
    void findByUsername_ShouldReturnTrainer() {
        trainerRepository.save(testTrainer);

        Trainer found = trainerRepository.findByUsername("Dastan.Ibraimov");

        assertNotNull(found);
        assertEquals("Dastan.Ibraimov", found.getUsername());
        assertEquals("Dastan", found.getFirstName());
        assertEquals(TrainingType.YOGA, found.getSpecialization());
    }

    @Test
    void findByUsername_WithNonExistentUsername_ShouldThrowException() {
        assertThrows(TrainerNotFoundException.class,
                () -> trainerRepository.findByUsername("NonExistent"));
    }

    @Test
    void existsByUsernameAndPassword_ShouldReturnTrue() {
        trainerRepository.save(testTrainer);

        boolean exists = trainerRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "password123");

        assertTrue(exists);
    }

    @Test
    void existsByUsernameAndPassword_WithWrongPassword_ShouldReturnFalse() {
        trainerRepository.save(testTrainer);

        boolean exists = trainerRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "wrongPassword");

        assertFalse(exists);
    }

    @Test
    void update_ShouldUpdateTrainer() {
        Trainer saved = trainerRepository.save(testTrainer);
        saved.setSpecialization(TrainingType.FITNESS);

        Trainer updated = trainerRepository.update(saved);

        assertEquals(TrainingType.FITNESS, updated.getSpecialization());
    }

    @Test
    void updatePasswordByUsername_ShouldUpdatePassword() {
        trainerRepository.save(testTrainer);

        Trainer updated = trainerRepository.updatePasswordByUsername("Dastan.Ibraimov", "password123", "newPassword");

        assertTrue(trainerRepository.existsByUsernameAndPassword(updated.getUsername(), updated.getPassword()));
        assertFalse(trainerRepository.existsByUsernameAndPassword(updated.getUsername(), "password123"));
    }

    @Test
    void updateActiveStatusByUsername_ShouldUpdateStatus() {
        trainerRepository.save(testTrainer);

        Trainer updated = trainerRepository.updateActiveStatusByUsername("Dastan.Ibraimov", false);

        assertFalse(updated.isActive());
    }

    @Test
    void findTrainingsByUsername_ShouldReturnFilteredTrainings() {
        trainerRepository.save(testTrainer);

        List<com.github.amangusss.gym_application.entity.training.Training> trainings =
                trainerRepository.findTrainingsByUsername("Dastan.Ibraimov", null, null, null);

        assertNotNull(trainings);
    }

    @Test
    void findTrainingsByUsername_WithDateRange_ShouldFilterByDates() {
        trainerRepository.save(testTrainer);

        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(30);

        List<com.github.amangusss.gym_application.entity.training.Training> trainings =
                trainerRepository.findTrainingsByUsername("Dastan.Ibraimov", fromDate, toDate, null);

        assertNotNull(trainings);
    }

    @Test
    void update_WithNullTrainer_ShouldThrowException() {
        assertThrows(Exception.class, () -> trainerRepository.update(null));
    }
}


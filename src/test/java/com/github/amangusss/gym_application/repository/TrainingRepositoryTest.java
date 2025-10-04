package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.config.GymApplicationConfig;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GymApplicationConfig.class)
@Transactional
class TrainingRepositoryTest {

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    private Trainee testTrainee;
    private Trainer testTrainer;
    private Training testTraining;

    @BeforeEach
    void setUp() {
        testTrainee = Trainee.builder()
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe")
                .password("password123")
                .isActive(true)
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St")
                .build();
        traineeRepository.save(testTrainee);

        testTrainer = Trainer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .username("Jane.Smith")
                .password("password123")
                .isActive(true)
                .specialization(TrainingType.YOGA)
                .build();
        trainerRepository.save(testTrainer);

        testTraining = Training.builder()
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingName("Morning Yoga")
                .trainingType(TrainingType.YOGA)
                .trainingDate(LocalDate.now().plusDays(1))
                .trainingDuration(60)
                .build();
    }

    @Test
    void save_ShouldPersistTraining() {
        Training saved = trainingRepository.save(testTraining);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Morning Yoga", saved.getTrainingName());
        assertEquals(60, saved.getTrainingDuration());
        assertEquals(TrainingType.YOGA, saved.getTrainingType());
    }

    @Test
    void save_WithNullTrainee_ShouldThrowException() {
        testTraining.setTrainee(null);

        assertThrows(Exception.class, () -> trainingRepository.save(testTraining));
    }

    @Test
    void save_WithNullTrainer_ShouldThrowException() {
        testTraining.setTrainer(null);

        assertThrows(Exception.class, () -> trainingRepository.save(testTraining));
    }

    @Test
    void save_WithNullTrainingName_ShouldThrowException() {
        testTraining.setTrainingName(null);

        assertThrows(Exception.class, () -> trainingRepository.save(testTraining));
    }

    @Test
    void save_WithNullTrainingDate_ShouldThrowException() {
        testTraining.setTrainingDate(null);

        assertThrows(Exception.class, () -> trainingRepository.save(testTraining));
    }

    @Test
    void save_WithZeroDuration_ShouldPersist() {
        testTraining.setTrainingDuration(0);

        Training saved = trainingRepository.save(testTraining);

        assertNotNull(saved);
        assertEquals(0, saved.getTrainingDuration());
    }

    @Test
    void save_MultipleTrainings_ShouldPersistAll() {
        Training training1 = trainingRepository.save(testTraining);

        Training training2 = Training.builder()
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingName("Evening Yoga")
                .trainingType(TrainingType.YOGA)
                .trainingDate(LocalDate.now().plusDays(2))
                .trainingDuration(90)
                .build();
        Training saved2 = trainingRepository.save(training2);

        assertNotNull(training1.getId());
        assertNotNull(saved2.getId());
        assertNotEquals(training1.getId(), saved2.getId());
    }
}


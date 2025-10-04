package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.repository.TrainingRepository;
import com.github.amangusss.gym_application.service.impl.TrainingServiceImpl;
import com.github.amangusss.gym_application.util.validation.service.training.TrainingServiceValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingServiceValidation trainingServiceValidation;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training testTraining;
    private Trainee testTrainee;
    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        testTrainee = Trainee.builder()
                .id(1L)
                .firstName("Aman")
                .lastName("Nazarkulov")
                .username("Aman.Nazarkulov")
                .password("password123")
                .isActive(true)
                .dateOfBirth(LocalDate.of(2004, 2, 14))
                .address("Isakeev st, 18/10 Block 15")
                .build();

        testTrainer = Trainer.builder()
                .id(2L)
                .firstName("Dastan")
                .lastName("Ibraimov")
                .username("Dastan.Ibraimov")
                .password("password123")
                .isActive(true)
                .specialization(TrainingType.YOGA)
                .build();

        testTraining = Training.builder()
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingName("Morning Yoga Session")
                .trainingType(TrainingType.YOGA)
                .trainingDate(LocalDate.now().plusDays(1))
                .trainingDuration(60)
                .build();
    }

    @Test
    void addTraining_ShouldValidateAndSaveTraining() {
        doNothing().when(trainingServiceValidation).validateTrainingForAddition(any());
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        Training result = trainingService.addTraining(testTraining);

        assertNotNull(result);
        assertEquals("Morning Yoga Session", result.getTrainingName());
        assertEquals(60, result.getTrainingDuration());
        assertEquals(TrainingType.YOGA, result.getTrainingType());

        verify(trainingServiceValidation).validateTrainingForAddition(testTraining);
        verify(trainingRepository).save(testTraining);
    }

    @Test
    void addTraining_WithNullTraining_ShouldValidateAndThrowException() {
        doThrow(new IllegalArgumentException("Training cannot be null"))
                .when(trainingServiceValidation).validateTrainingForAddition(null);

        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(null));

        verify(trainingServiceValidation).validateTrainingForAddition(null);
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void addTraining_WithInvalidTrainingData_ShouldThrowException() {
        testTraining.setTrainingName(null);

        doThrow(new IllegalArgumentException("Training name cannot be null"))
                .when(trainingServiceValidation).validateTrainingForAddition(testTraining);

        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(testTraining));

        verify(trainingServiceValidation).validateTrainingForAddition(testTraining);
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void addTraining_WithNullTrainee_ShouldThrowException() {
        testTraining.setTrainee(null);

        doThrow(new IllegalArgumentException("Trainee cannot be null"))
                .when(trainingServiceValidation).validateTrainingForAddition(testTraining);

        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(testTraining));

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void addTraining_WithNullTrainer_ShouldThrowException() {
        testTraining.setTrainer(null);

        doThrow(new IllegalArgumentException("Trainer cannot be null"))
                .when(trainingServiceValidation).validateTrainingForAddition(testTraining);

        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(testTraining));

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void addTraining_WithPastDate_ShouldStillSave() {
        testTraining.setTrainingDate(LocalDate.now().minusDays(1));

        doNothing().when(trainingServiceValidation).validateTrainingForAddition(any());
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        Training result = trainingService.addTraining(testTraining);

        assertNotNull(result);
        verify(trainingRepository).save(testTraining);
    }

    @Test
    void addTraining_WithZeroDuration_ShouldStillSave() {
        testTraining.setTrainingDuration(0);

        doNothing().when(trainingServiceValidation).validateTrainingForAddition(any());
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        Training result = trainingService.addTraining(testTraining);

        assertNotNull(result);
        assertEquals(0, result.getTrainingDuration());
        verify(trainingRepository).save(testTraining);
    }

    @Test
    void addTraining_MultipleTrainings_ShouldSaveAllIndependently() {
        Training training1 = testTraining;
        Training training2 = Training.builder()
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingName("Evening Yoga Session")
                .trainingType(TrainingType.YOGA)
                .trainingDate(LocalDate.now().plusDays(2))
                .trainingDuration(90)
                .build();

        doNothing().when(trainingServiceValidation).validateTrainingForAddition(any());
        when(trainingRepository.save(training1)).thenReturn(training1);
        when(trainingRepository.save(training2)).thenReturn(training2);

        Training result1 = trainingService.addTraining(training1);
        Training result2 = trainingService.addTraining(training2);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1.getTrainingName(), result2.getTrainingName());

        verify(trainingRepository, times(2)).save(any(Training.class));
    }
}
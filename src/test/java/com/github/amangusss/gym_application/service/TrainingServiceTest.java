package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.repository.TrainingRepository;
import com.github.amangusss.gym_application.service.impl.TrainingServiceImpl;
import com.github.amangusss.gym_application.validation.training.TrainingEntityValidation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingService Tests")
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingEntityValidation trainingEntityValidation;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training testTraining;
    private TrainingType testTrainingType;

    @BeforeEach
    void setUp() {
        testTrainingType = TrainingType.builder()
                .id(1L)
                .typeName("Yoga")
                .build();

        User traineeUser = User.builder()
                .id(1L)
                .firstName("Aman")
                .lastName("Nazarkulov")
                .username("Aman.Nazarkulov")
                .password("password123")
                .isActive(true)
                .build();

        Trainee testTrainee = Trainee.builder()
                .id(1L)
                .user(traineeUser)
                .dateOfBirth(LocalDate.of(2004, 2, 14))
                .address("Isakeev st, 18/10 Block 15")
                .build();

        User trainerUser = User.builder()
                .id(2L)
                .firstName("Dastan")
                .lastName("Ibraimov")
                .username("Dastan.Ibraimov")
                .password("password123")
                .isActive(true)
                .build();

        Trainer testTrainer = Trainer.builder()
                .id(1L)
                .user(trainerUser)
                .specialization(testTrainingType)
                .build();

        testTraining = Training.builder()
                .id(1L)
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingName("Morning Yoga")
                .trainingType(testTrainingType)
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();
    }

    @Test
    @DisplayName("Should add training successfully")
    void addTraining_ShouldPersistTraining() {
        doNothing().when(trainingEntityValidation).validateTrainingForAddition(any());
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        Training result = trainingService.addTraining(testTraining);

        assertThat(result).isNotNull();
        assertThat(result.getTrainingName()).isEqualTo("Morning Yoga");
        assertThat(result.getTrainingDuration()).isEqualTo(60);
        assertThat(result.getTrainingType()).isEqualTo(testTrainingType);

        verify(trainingEntityValidation).validateTrainingForAddition(testTraining);
        verify(trainingRepository).save(testTraining);
    }

    @Test
    @DisplayName("Should throw exception when training is null")
    void addTraining_WithNullTraining_ShouldThrowException() {
        doThrow(new ValidationException("Training cannot be null"))
                .when(trainingEntityValidation).validateTrainingForAddition(null);

        assertThatThrownBy(() -> trainingService.addTraining(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Training cannot be null");

        verify(trainingEntityValidation).validateTrainingForAddition(null);
    }

    @Test
    @DisplayName("Should throw exception when training data is invalid")
    void addTraining_WithInvalidTrainingData_ShouldThrowException() {
        testTraining.setTrainingName(null);
        doThrow(new ValidationException("Training name cannot be null"))
                .when(trainingEntityValidation).validateTrainingForAddition(testTraining);

        assertThatThrownBy(() -> trainingService.addTraining(testTraining))
                .isInstanceOf(ValidationException.class);

        verify(trainingEntityValidation).validateTrainingForAddition(testTraining);
    }

    @Test
    @DisplayName("Should add training with correct duration")
    void addTraining_WithValidDuration_ShouldSave() {
        testTraining.setTrainingDuration(120);
        doNothing().when(trainingEntityValidation).validateTrainingForAddition(any());
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        Training result = trainingService.addTraining(testTraining);

        assertThat(result.getTrainingDuration()).isEqualTo(120);
        verify(trainingRepository).save(testTraining);
    }
}

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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingService Tests")
class TrainingServiceTest {

    private static final Long TRAINEE_USER_ID = 1L;
    private static final Long TRAINEE_ID = 1L;
    private static final Long TRAINER_USER_ID = 2L;
    private static final Long TRAINER_ID = 1L;
    private static final Long TRAINING_ID = 1L;
    private static final Long TRAINING_TYPE_ID = 1L;
    private static final String TRAINEE_FIRST_NAME = "Aman";
    private static final String TRAINEE_LAST_NAME = "Nazarkulov";
    private static final String TRAINEE_USERNAME = "Aman.Nazarkulov";
    private static final String TRAINER_FIRST_NAME = "Dastan";
    private static final String TRAINER_LAST_NAME = "Ibraimov";
    private static final String TRAINER_USERNAME = "Dastan.Ibraimov";
    private static final String VALID_PASSWORD = "password123";
    private static final String TRAINEE_ADDRESS = "Isakeev st, 18/10 Block 15";
    private static final LocalDate TRAINEE_BIRTH_DATE = LocalDate.of(2004, 2, 14);
    private static final String TRAINING_TYPE_NAME = "Yoga";
    private static final String TRAINING_NAME = "Morning Yoga";
    private static final int TRAINING_DURATION = 60;
    private static final int UPDATED_TRAINING_DURATION = 120;
    private static final boolean IS_ACTIVE = true;
    private static final String NULL_TRAINING_ERROR = "Training cannot be null";
    private static final String NULL_TRAINING_NAME_ERROR = "Training name cannot be null";

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
        Mockito.reset(trainingRepository, trainingEntityValidation);

        testTrainingType = TrainingType.builder()
                .id(TRAINING_TYPE_ID)
                .typeName(TRAINING_TYPE_NAME)
                .build();

        User traineeUser = User.builder()
                .id(TRAINEE_USER_ID)
                .firstName(TRAINEE_FIRST_NAME)
                .lastName(TRAINEE_LAST_NAME)
                .username(TRAINEE_USERNAME)
                .password(VALID_PASSWORD)
                .isActive(IS_ACTIVE)
                .build();

        Trainee testTrainee = Trainee.builder()
                .id(TRAINEE_ID)
                .user(traineeUser)
                .dateOfBirth(TRAINEE_BIRTH_DATE)
                .address(TRAINEE_ADDRESS)
                .build();

        User trainerUser = User.builder()
                .id(TRAINER_USER_ID)
                .firstName(TRAINER_FIRST_NAME)
                .lastName(TRAINER_LAST_NAME)
                .username(TRAINER_USERNAME)
                .password(VALID_PASSWORD)
                .isActive(IS_ACTIVE)
                .build();

        Trainer testTrainer = Trainer.builder()
                .id(TRAINER_ID)
                .user(trainerUser)
                .specialization(testTrainingType)
                .build();

        testTraining = Training.builder()
                .id(TRAINING_ID)
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingName(TRAINING_NAME)
                .trainingType(testTrainingType)
                .trainingDate(LocalDate.now())
                .trainingDuration(TRAINING_DURATION)
                .build();
    }

    @Test
    @DisplayName("Should add training successfully when training data is valid")
    void shouldAddTrainingSuccessfullyWhenTrainingDataIsValid() {
        Mockito.doNothing().when(trainingEntityValidation).validateTrainingForAddition(ArgumentMatchers.any());
        Mockito.when(trainingRepository.save(ArgumentMatchers.any(Training.class))).thenReturn(testTraining);

        Training result = trainingService.addTraining(testTraining);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTrainingName()).isEqualTo(TRAINING_NAME);
        Assertions.assertThat(result.getTrainingDuration()).isEqualTo(TRAINING_DURATION);
        Assertions.assertThat(result.getTrainingType()).isEqualTo(testTrainingType);

        Mockito.verify(trainingEntityValidation, Mockito.times(1)).validateTrainingForAddition(testTraining);
        Mockito.verify(trainingRepository, Mockito.times(1)).save(testTraining);
    }

    @Test
    @DisplayName("Should throw ValidationException when training is null")
    void shouldThrowValidationExceptionWhenTrainingIsNull() {
        Mockito.doThrow(new ValidationException(NULL_TRAINING_ERROR))
                .when(trainingEntityValidation).validateTrainingForAddition(null);

        Assertions.assertThatThrownBy(() -> trainingService.addTraining(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(NULL_TRAINING_ERROR);

        Mockito.verify(trainingEntityValidation, Mockito.times(1)).validateTrainingForAddition(null);
    }

    @Test
    @DisplayName("Should throw ValidationException when training data is invalid")
    void shouldThrowValidationExceptionWhenTrainingDataIsInvalid() {
        testTraining.setTrainingName(null);
        Mockito.doThrow(new ValidationException(NULL_TRAINING_NAME_ERROR))
                .when(trainingEntityValidation).validateTrainingForAddition(testTraining);

        Assertions.assertThatThrownBy(() -> trainingService.addTraining(testTraining))
                .isInstanceOf(ValidationException.class);

        Mockito.verify(trainingEntityValidation, Mockito.times(1)).validateTrainingForAddition(testTraining);
    }

    @Test
    @DisplayName("Should add training with correct duration when duration is valid")
    void shouldAddTrainingWithCorrectDurationWhenDurationIsValid() {
        testTraining.setTrainingDuration(UPDATED_TRAINING_DURATION);
        Mockito.doNothing().when(trainingEntityValidation).validateTrainingForAddition(ArgumentMatchers.any());
        Mockito.when(trainingRepository.save(ArgumentMatchers.any(Training.class))).thenReturn(testTraining);

        Training result = trainingService.addTraining(testTraining);

        Assertions.assertThat(result.getTrainingDuration()).isEqualTo(UPDATED_TRAINING_DURATION);

        Mockito.verify(trainingRepository, Mockito.times(1)).save(testTraining);
    }
}

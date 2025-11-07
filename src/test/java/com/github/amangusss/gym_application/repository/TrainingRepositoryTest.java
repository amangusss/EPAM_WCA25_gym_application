package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.CustomUser;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingRepository Tests")
class TrainingRepositoryTest {

    private static final Long TRAINEE_USER_ID = 1L;
    private static final Long TRAINEE_ID = 1L;
    private static final Long TRAINER_USER_ID = 2L;
    private static final Long TRAINER_ID = 1L;
    private static final Long TRAINING_ID = 1L;
    private static final Long TRAINING_TYPE_ID = 1L;
    private static final Long NON_EXISTENT_ID = 999L;
    private static final String TRAINEE_FIRST_NAME = "John";
    private static final String TRAINEE_LAST_NAME = "Doe";
    private static final String TRAINEE_USERNAME = "John.Doe";
    private static final String TRAINER_FIRST_NAME = "Jane";
    private static final String TRAINER_LAST_NAME = "Smith";
    private static final String TRAINER_USERNAME = "Jane.Smith";
    private static final String VALID_PASSWORD = "password123";
    private static final String TRAINEE_ADDRESS = "123 Main St";
    private static final LocalDate TRAINEE_BIRTH_DATE = LocalDate.of(1990, 5, 15);
    private static final String TRAINING_TYPE_NAME = "Yoga";
    private static final String TRAINING_NAME = "Morning Yoga";
    private static final int TRAINING_DURATION = 60;
    private static final boolean IS_ACTIVE = true;

    @Mock
    private TrainingRepository trainingRepository;

    private Training testTraining;

    @BeforeEach
    void setUp() {
        Mockito.reset(trainingRepository);

        TrainingType trainingType = TrainingType.builder()
                .id(TRAINING_TYPE_ID)
                .typeName(TRAINING_TYPE_NAME)
                .build();

        CustomUser traineeUser = CustomUser.builder()
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

        CustomUser trainerUser = CustomUser.builder()
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
                .specialization(trainingType)
                .build();

        testTraining = Training.builder()
                .id(TRAINING_ID)
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingName(TRAINING_NAME)
                .trainingType(trainingType)
                .trainingDate(LocalDate.now())
                .trainingDuration(TRAINING_DURATION)
                .build();
    }

    @Test
    @DisplayName("Should return saved training when saving")
    void shouldReturnSavedTrainingWhenSaving() {
        when(trainingRepository.save(any(Training.class)))
                .thenReturn(testTraining);

        Training saved = trainingRepository.save(testTraining);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(TRAINING_ID);
        assertThat(saved.getTrainingName()).isEqualTo(TRAINING_NAME);
        assertThat(saved.getTrainingDuration()).isEqualTo(TRAINING_DURATION);

        verify(trainingRepository, times(1)).save(testTraining);
    }

    @Test
    @DisplayName("Should return list of trainings when finding all")
    void shouldReturnListOfTrainingsWhenFindingAll() {
        List<Training> trainingList = List.of(testTraining);
        when(trainingRepository.findAll()).thenReturn(trainingList);

        List<Training> result = trainingRepository.findAll();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTrainingName()).isEqualTo(TRAINING_NAME);

        verify(trainingRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return training when finding by existing ID")
    void shouldReturnTrainingWhenFindingByExistingId() {
        when(trainingRepository.findById(TRAINING_ID))
                .thenReturn(Optional.of(testTraining));

        Optional<Training> result = trainingRepository.findById(TRAINING_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getTrainingName()).isEqualTo(TRAINING_NAME);

        verify(trainingRepository, times(1)).findById(TRAINING_ID);
    }

    @Test
    @DisplayName("Should return empty Optional when training not found by ID")
    void shouldReturnEmptyOptionalWhenTrainingNotFoundById() {
        when(trainingRepository.findById(NON_EXISTENT_ID))
                .thenReturn(Optional.empty());

        Optional<Training> result = trainingRepository.findById(NON_EXISTENT_ID);

        assertThat(result).isEmpty();

        verify(trainingRepository, times(1)).findById(NON_EXISTENT_ID);
    }

    @Test
    @DisplayName("Should return true when training exists by ID")
    void shouldReturnTrueWhenTrainingExistsById() {
        when(trainingRepository.existsById(TRAINING_ID)).thenReturn(true);

        boolean exists = trainingRepository.existsById(TRAINING_ID);

        assertThat(exists).isTrue();

        verify(trainingRepository, times(1)).existsById(TRAINING_ID);
    }

    @Test
    @DisplayName("Should call delete when deleting training")
    void shouldCallDeleteWhenDeletingTraining() {
        doNothing().when(trainingRepository).delete(testTraining);

        trainingRepository.delete(testTraining);

        verify(trainingRepository, times(1)).delete(testTraining);
    }
}

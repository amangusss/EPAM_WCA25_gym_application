package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerRepository Tests")
class TrainerRepositoryTest {

    private static final Long USER_ID = 1L;
    private static final Long TRAINER_ID = 1L;
    private static final Long TRAINING_TYPE_ID = 1L;
    private static final String FIRST_NAME = "Dastan";
    private static final String LAST_NAME = "Ibraimov";
    private static final String USERNAME = "Dastan.Ibraimov";
    private static final String VALID_PASSWORD = "password123";
    private static final String INVALID_PASSWORD = "wrongPassword";
    private static final String NON_EXISTENT_USERNAME = "NonExistent";
    private static final String TRAINING_TYPE_NAME = "Yoga";
    private static final boolean IS_ACTIVE = true;

    @Mock
    private TrainerRepository trainerRepository;

    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        Mockito.reset(trainerRepository);

        TrainingType testTrainingType = TrainingType.builder()
                .id(TRAINING_TYPE_ID)
                .typeName(TRAINING_TYPE_NAME)
                .build();

        User testUser = User.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(VALID_PASSWORD)
                .isActive(IS_ACTIVE)
                .build();

        testTrainer = Trainer.builder()
                .id(TRAINER_ID)
                .user(testUser)
                .specialization(testTrainingType)
                .build();
    }

    @Test
    @DisplayName("Should return trainer when finding by existing username")
    void shouldReturnTrainerWhenFindingByExistingUsername() {
        Mockito.when(trainerRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainer));

        Optional<Trainer> result = trainerRepository.findByUserUsername(USERNAME);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getUser().getUsername()).isEqualTo(USERNAME);
        Assertions.assertThat(result.get().getUser().getFirstName()).isEqualTo(FIRST_NAME);

        Mockito.verify(trainerRepository, Mockito.times(1)).findByUserUsername(USERNAME);
    }

    @Test
    @DisplayName("Should return empty Optional when trainer not found by username")
    void shouldReturnEmptyOptionalWhenTrainerNotFoundByUsername() {
        Mockito.when(trainerRepository.findByUserUsername(NON_EXISTENT_USERNAME))
                .thenReturn(Optional.empty());

        Optional<Trainer> result = trainerRepository.findByUserUsername(NON_EXISTENT_USERNAME);

        Assertions.assertThat(result).isEmpty();

        Mockito.verify(trainerRepository, Mockito.times(1)).findByUserUsername(NON_EXISTENT_USERNAME);
    }

    @Test
    @DisplayName("Should return saved trainer when saving")
    void shouldReturnSavedTrainerWhenSaving() {
        Mockito.when(trainerRepository.save(ArgumentMatchers.any(Trainer.class)))
                .thenReturn(testTrainer);

        Trainer saved = trainerRepository.save(testTrainer);

        Assertions.assertThat(saved).isNotNull();
        Assertions.assertThat(saved.getId()).isEqualTo(TRAINER_ID);
        Assertions.assertThat(saved.getUser().getUsername()).isEqualTo(USERNAME);

        Mockito.verify(trainerRepository, Mockito.times(1)).save(testTrainer);
    }

    @Test
    @DisplayName("Should return true when trainer exists by username and valid password")
    void shouldReturnTrueWhenTrainerExistsByUsernameAndValidPassword() {
        Mockito.when(trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD))
                .thenReturn(true);

        boolean exists = trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD);

        Assertions.assertThat(exists).isTrue();

        Mockito.verify(trainerRepository, Mockito.times(1))
                .existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD);
    }

    @Test
    @DisplayName("Should return false when checking existence with invalid password")
    void shouldReturnFalseWhenCheckingExistenceWithInvalidPassword() {
        Mockito.when(trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, INVALID_PASSWORD))
                .thenReturn(false);

        boolean exists = trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, INVALID_PASSWORD);

        Assertions.assertThat(exists).isFalse();

        Mockito.verify(trainerRepository, Mockito.times(1))
                .existsByUserUsernameAndUserPassword(USERNAME, INVALID_PASSWORD);
    }
}

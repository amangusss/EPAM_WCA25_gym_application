package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.CustomUser;
import com.github.amangusss.gym_application.entity.trainer.Trainer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        CustomUser testUser = CustomUser.builder()
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
        when(trainerRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainer));

        Optional<Trainer> result = trainerRepository.findByUserUsername(USERNAME);

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo(USERNAME);
        assertThat(result.get().getUser().getFirstName()).isEqualTo(FIRST_NAME);

        verify(trainerRepository, times(1)).findByUserUsername(USERNAME);
    }

    @Test
    @DisplayName("Should return empty Optional when trainer not found by username")
    void shouldReturnEmptyOptionalWhenTrainerNotFoundByUsername() {
        when(trainerRepository.findByUserUsername(NON_EXISTENT_USERNAME))
                .thenReturn(Optional.empty());

        Optional<Trainer> result = trainerRepository.findByUserUsername(NON_EXISTENT_USERNAME);

        assertThat(result).isEmpty();

        verify(trainerRepository, times(1)).findByUserUsername(NON_EXISTENT_USERNAME);
    }

    @Test
    @DisplayName("Should return saved trainer when saving")
    void shouldReturnSavedTrainerWhenSaving() {
        when(trainerRepository.save(ArgumentMatchers.any(Trainer.class)))
                .thenReturn(testTrainer);

        Trainer saved = trainerRepository.save(testTrainer);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(TRAINER_ID);
        assertThat(saved.getUser().getUsername()).isEqualTo(USERNAME);

        verify(trainerRepository, times(1)).save(testTrainer);
    }
}

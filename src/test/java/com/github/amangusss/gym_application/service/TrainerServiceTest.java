package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.exception.AuthenticationException;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.repository.UserRepository;
import com.github.amangusss.gym_application.service.impl.TrainerServiceImpl;
import com.github.amangusss.gym_application.util.credentials.PasswordGenerator;
import com.github.amangusss.gym_application.util.credentials.UsernameGenerator;
import com.github.amangusss.gym_application.validation.trainer.TrainerEntityValidation;
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

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerService Tests")
class TrainerServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long TRAINER_ID = 1L;
    private static final Long TRAINING_TYPE_ID = 1L;
    private static final Long UPDATED_TRAINING_TYPE_ID = 2L;
    private static final String FIRST_NAME = "Aman";
    private static final String LAST_NAME = "Nazarkulov";
    private static final String USERNAME = "Aman.Nazarkulov";
    private static final String VALID_PASSWORD = "password123";
    private static final String INVALID_PASSWORD = "wrongPassword";
    private static final String TRAINING_TYPE_NAME = "Yoga";
    private static final String UPDATED_TRAINING_TYPE_NAME = "Fitness";
    private static final boolean IS_ACTIVE = true;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private TrainerEntityValidation trainerEntityValidation;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        Mockito.reset(trainerRepository, userRepository, usernameGenerator,
                     passwordGenerator, trainerEntityValidation);

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
    @DisplayName("Should create trainer with generated credentials when creating trainer")
    void shouldCreateTrainerWithGeneratedCredentialsWhenCreatingTrainer() {
        Mockito.when(usernameGenerator.generateUsername(
                ArgumentMatchers.eq(FIRST_NAME),
                ArgumentMatchers.eq(LAST_NAME),
                ArgumentMatchers.any()))
                .thenReturn(USERNAME);
        Mockito.when(passwordGenerator.generatePassword()).thenReturn(VALID_PASSWORD);
        Mockito.doNothing().when(trainerEntityValidation).validateTrainerForCreationOrUpdate(ArgumentMatchers.any());
        Mockito.when(trainerRepository.save(ArgumentMatchers.any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.createTrainer(testTrainer);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getUser().getUsername()).isEqualTo(USERNAME);

        Mockito.verify(trainerRepository, Mockito.times(1)).save(ArgumentMatchers.any(Trainer.class));
    }

    @Test
    @DisplayName("Should return trainer when finding by username with valid password")
    void shouldReturnTrainerWhenFindingByUsernameWithValidPassword() {
        Mockito.when(trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD))
                .thenReturn(true);
        Mockito.when(trainerRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainer));

        Trainer result = trainerService.findTrainerByUsername(USERNAME, VALID_PASSWORD);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getUser().getUsername()).isEqualTo(USERNAME);

        Mockito.verify(trainerRepository, Mockito.times(1)).findByUserUsername(USERNAME);
    }

    @Test
    @DisplayName("Should throw AuthenticationException when password is invalid")
    void shouldThrowAuthenticationExceptionWhenPasswordIsInvalid() {
        Mockito.when(trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, INVALID_PASSWORD))
                .thenReturn(false);

        Assertions.assertThatThrownBy(() -> trainerService.findTrainerByUsername(USERNAME, INVALID_PASSWORD))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Authentication failed for trainer: " + USERNAME);
    }

    @Test
    @DisplayName("Should update trainer successfully when updating with valid data")
    void shouldUpdateTrainerSuccessfullyWhenUpdatingWithValidData() {
        TrainingType updatedType = TrainingType.builder()
                .id(UPDATED_TRAINING_TYPE_ID)
                .typeName(UPDATED_TRAINING_TYPE_NAME)
                .build();

        Trainer updateData = Trainer.builder()
                .user(User.builder()
                        .firstName(FIRST_NAME)
                        .lastName(LAST_NAME)
                        .isActive(IS_ACTIVE)
                        .build())
                .specialization(updatedType)
                .build();

        Mockito.when(trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD))
                .thenReturn(true);
        Mockito.when(trainerRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainer));
        Mockito.doNothing().when(trainerEntityValidation).validateTrainerForCreationOrUpdate(ArgumentMatchers.any());
        Mockito.when(trainerRepository.save(ArgumentMatchers.any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.updateTrainer(USERNAME, VALID_PASSWORD, updateData);

        Assertions.assertThat(result).isNotNull();

        Mockito.verify(trainerRepository, Mockito.times(1)).save(ArgumentMatchers.any(Trainer.class));
    }

    @Test
    @DisplayName("Should activate trainer successfully when activating")
    void shouldActivateTrainerSuccessfullyWhenActivating() {
        Mockito.when(trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD))
                .thenReturn(true);
        Mockito.when(trainerRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainer));
        Mockito.when(trainerRepository.save(ArgumentMatchers.any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.activateTrainer(USERNAME, VALID_PASSWORD);

        Assertions.assertThat(result).isNotNull();

        Mockito.verify(trainerRepository, Mockito.times(1)).save(testTrainer);
    }

    @Test
    @DisplayName("Should deactivate trainer successfully when deactivating")
    void shouldDeactivateTrainerSuccessfullyWhenDeactivating() {
        Mockito.when(trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD))
                .thenReturn(true);
        Mockito.when(trainerRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainer));
        Mockito.when(trainerRepository.save(ArgumentMatchers.any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.deactivateTrainer(USERNAME, VALID_PASSWORD);

        Assertions.assertThat(result).isNotNull();

        Mockito.verify(trainerRepository, Mockito.times(1)).save(testTrainer);
    }
}

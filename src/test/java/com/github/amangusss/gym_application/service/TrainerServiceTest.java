package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.exception.AuthenticationException;
import com.github.amangusss.gym_application.mapper.TrainerMapper;
import com.github.amangusss.gym_application.mapper.TrainingMapper;
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
    private static final String OLD_PASSWORD = "oldPassword123";
    private static final String NEW_PASSWORD = "newPassword123";
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

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer testTrainer;
    private TrainingType testTrainingType;
    private TrainerDTO.Request.Register registerRequest;
    private TrainerDTO.Response.Registered registeredResponse;

    @BeforeEach
    void setUp() {
        Mockito.reset(trainerRepository, userRepository, usernameGenerator,
                     passwordGenerator, trainerEntityValidation, trainerMapper,
                     trainingMapper, trainingTypeService);

        testTrainingType = TrainingType.builder()
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

        registerRequest = new TrainerDTO.Request.Register(FIRST_NAME, LAST_NAME, TRAINING_TYPE_ID);
        registeredResponse = new TrainerDTO.Response.Registered(USERNAME, VALID_PASSWORD);
    }

    @Test
    @DisplayName("Should register trainer with generated credentials when registering trainer")
    void shouldRegisterTrainerWithGeneratedCredentialsWhenRegisteringTrainer() {
        Mockito.when(trainingTypeService.findById(TRAINING_TYPE_ID)).thenReturn(testTrainingType);
        Mockito.when(trainerMapper.toEntity(registerRequest, testTrainingType)).thenReturn(testTrainer);
        Mockito.when(usernameGenerator.generateUsername(
                ArgumentMatchers.eq(FIRST_NAME),
                ArgumentMatchers.eq(LAST_NAME),
                ArgumentMatchers.any()))
                .thenReturn(USERNAME);
        Mockito.when(passwordGenerator.generatePassword()).thenReturn(VALID_PASSWORD);
        Mockito.doNothing().when(trainerEntityValidation).validateTrainerForCreationOrUpdate(ArgumentMatchers.any());
        Mockito.when(trainerRepository.save(ArgumentMatchers.any(Trainer.class))).thenReturn(testTrainer);
        Mockito.when(trainerMapper.toRegisteredResponse(testTrainer)).thenReturn(registeredResponse);

        TrainerDTO.Response.Registered result = trainerService.registerTrainer(registerRequest);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.username()).isEqualTo(USERNAME);
        Assertions.assertThat(result.password()).isEqualTo(VALID_PASSWORD);

        Mockito.verify(trainerRepository, Mockito.times(1)).save(ArgumentMatchers.any(Trainer.class));
    }

    @Test
    @DisplayName("Should return trainer profile when getting profile with valid password")
    void shouldReturnTrainerProfileWhenGettingProfileWithValidPassword() {
        TrainerDTO.Response.Profile profileResponse = new TrainerDTO.Response.Profile(
                FIRST_NAME, LAST_NAME, testTrainingType.getTypeName(), IS_ACTIVE, null);

        Mockito.when(trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD))
                .thenReturn(true);
        Mockito.when(trainerRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainer));
        Mockito.when(trainerMapper.toProfileResponse(testTrainer)).thenReturn(profileResponse);

        TrainerDTO.Response.Profile result = trainerService.getTrainerProfile(USERNAME, VALID_PASSWORD);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.firstName()).isEqualTo(FIRST_NAME);
        Assertions.assertThat(result.lastName()).isEqualTo(LAST_NAME);

        Mockito.verify(trainerRepository, Mockito.times(1)).findByUserUsername(USERNAME);
    }

    @Test
    @DisplayName("Should throw AuthenticationException when password is invalid")
    void shouldThrowAuthenticationExceptionWhenPasswordIsInvalid() {
        Mockito.when(trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, INVALID_PASSWORD))
                .thenReturn(false);

        Assertions.assertThatThrownBy(() -> trainerService.getTrainerProfile(USERNAME, INVALID_PASSWORD))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Authentication failed for trainer: " + USERNAME);
    }

    @Test
    @DisplayName("Should update trainer status successfully when updating status")
    void shouldUpdateTrainerStatusSuccessfullyWhenUpdatingStatus() {
        Mockito.when(trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD))
                .thenReturn(true);
        Mockito.when(trainerRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainer));
        Mockito.when(trainerRepository.save(ArgumentMatchers.any(Trainer.class))).thenReturn(testTrainer);

        trainerService.updateTrainerStatus(USERNAME, true, VALID_PASSWORD);

        Mockito.verify(trainerRepository, Mockito.times(1)).save(ArgumentMatchers.any(Trainer.class));
    }

    @Test
    @DisplayName("Should change password successfully when changing password")
    void shouldChangePasswordSuccessfullyWhenChangingPassword() {
        Mockito.when(trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, OLD_PASSWORD))
                .thenReturn(true);
        Mockito.when(trainerRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainer));
        Mockito.doNothing().when(trainerEntityValidation).validatePasswordChange(OLD_PASSWORD, NEW_PASSWORD);
        Mockito.when(trainerRepository.save(ArgumentMatchers.any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.changeTrainerPassword(USERNAME, OLD_PASSWORD, NEW_PASSWORD);

        Assertions.assertThat(result).isNotNull();
        Mockito.verify(trainerRepository, Mockito.times(1)).save(ArgumentMatchers.any(Trainer.class));
    }

    @Test
    @DisplayName("Should authenticate trainer successfully when credentials are valid")
    void shouldAuthenticateTrainerSuccessfullyWhenCredentialsAreValid() {
        Mockito.when(trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD))
                .thenReturn(true);

        boolean result = trainerService.authenticateTrainer(USERNAME, VALID_PASSWORD);

        Assertions.assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when authenticating with invalid credentials")
    void shouldReturnFalseWhenAuthenticatingWithInvalidCredentials() {
        Mockito.when(trainerRepository.existsByUserUsernameAndUserPassword(USERNAME, INVALID_PASSWORD))
                .thenReturn(false);

        boolean result = trainerService.authenticateTrainer(USERNAME, INVALID_PASSWORD);

        Assertions.assertThat(result).isFalse();
    }
}

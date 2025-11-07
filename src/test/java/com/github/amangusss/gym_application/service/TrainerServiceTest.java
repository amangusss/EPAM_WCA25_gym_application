package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.CustomUser;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;
import com.github.amangusss.gym_application.mapper.TrainerMapper;
import com.github.amangusss.gym_application.mapper.TrainingMapper;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.repository.UserRepository;
import com.github.amangusss.gym_application.service.impl.TrainerServiceImpl;
import com.github.amangusss.gym_application.util.credentials.PasswordGenerator;
import com.github.amangusss.gym_application.util.credentials.UsernameGenerator;
import com.github.amangusss.gym_application.validation.entity.EntityValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerService Tests")
@MockitoSettings(strictness = Strictness.LENIENT)
class TrainerServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long TRAINER_ID = 1L;
    private static final Long TRAINING_TYPE_ID = 1L;
    private static final String FIRST_NAME = "Aman";
    private static final String LAST_NAME = "Nazarkulov";
    private static final String USERNAME = "Aman.Nazarkulov";
    private static final String VALID_PASSWORD = "password123";
    private static final String ENCODED_VALID_PASSWORD = "encoded_password123";
    private static final String INVALID_PASSWORD = "wrongPassword";
    private static final String OLD_PASSWORD = "oldPassword123";
    private static final String ENCODED_OLD_PASSWORD = "encoded_oldPassword123";
    private static final String NEW_PASSWORD = "newPassword123";
    private static final String TRAINING_TYPE_NAME = "Yoga";
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
    private EntityValidator entityValidator;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer testTrainer;
    private TrainingType testTrainingType;
    private TrainerDTO.Request.Register registerRequest;
    private TrainerDTO.Response.Registered registeredResponse;

    @BeforeEach
    void setUp() {
        reset(trainerRepository, userRepository, usernameGenerator,
                passwordGenerator, entityValidator, trainerMapper,
                trainingMapper, trainingTypeService, passwordEncoder);

        when(passwordEncoder.encode(VALID_PASSWORD))
                .thenReturn(ENCODED_VALID_PASSWORD);
        when(passwordEncoder.encode(OLD_PASSWORD))
                .thenReturn(ENCODED_OLD_PASSWORD);
        when(passwordEncoder.encode(NEW_PASSWORD))
                .thenReturn("encoded_" + NEW_PASSWORD);

        when(passwordEncoder.matches(VALID_PASSWORD, ENCODED_VALID_PASSWORD))
                .thenReturn(true);
        when(passwordEncoder.matches(INVALID_PASSWORD, ENCODED_VALID_PASSWORD))
                .thenReturn(false);
        when(passwordEncoder.matches(OLD_PASSWORD, ENCODED_OLD_PASSWORD))
                .thenReturn(true);

        testTrainingType = TrainingType.builder()
                .id(TRAINING_TYPE_ID)
                .typeName(TRAINING_TYPE_NAME)
                .build();

        CustomUser testUser = CustomUser.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(ENCODED_VALID_PASSWORD)
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
        when(trainingTypeService.findById(TRAINING_TYPE_ID)).thenReturn(testTrainingType);
        when(trainerMapper.toEntity(registerRequest, testTrainingType)).thenReturn(testTrainer);
        when(usernameGenerator.generateUsername(
                        eq(FIRST_NAME),
                        eq(LAST_NAME),
                        any()))
                .thenReturn(USERNAME);
        when(passwordGenerator.generatePassword()).thenReturn(VALID_PASSWORD);
        doNothing().when(entityValidator).validateTrainerForCreation(any());
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);
        when(trainerMapper.toRegisteredResponse(any(Trainer.class), eq(VALID_PASSWORD)))
                .thenReturn(registeredResponse);

        TrainerDTO.Response.Registered result = trainerService.registerTrainer(registerRequest);

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(USERNAME);
        assertThat(result.password()).isEqualTo(VALID_PASSWORD);

        verify(trainerRepository, times(1)).save(any(Trainer.class));
        verify(passwordEncoder, times(1)).encode(VALID_PASSWORD);
    }

    @Test
    @DisplayName("Should return trainer profile when getting profile with valid password")
    void shouldReturnTrainerProfileWhenGettingProfileWithValidPassword() {
        TrainerDTO.Response.Profile profileResponse = new TrainerDTO.Response.Profile(
                FIRST_NAME, LAST_NAME, testTrainingType.getTypeName(), IS_ACTIVE, null);

        when(trainerRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainer));
        when(trainerMapper.toProfileResponse(testTrainer)).thenReturn(profileResponse);

        TrainerDTO.Response.Profile result = trainerService.getTrainerProfile(USERNAME);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo(FIRST_NAME);
        assertThat(result.lastName()).isEqualTo(LAST_NAME);
        assertThat(result.specializationName()).isEqualTo(TRAINING_TYPE_NAME);

        verify(trainerRepository, times(1)).findByUserUsername(USERNAME);
    }

    @Test
    @DisplayName("Should update trainer successfully when updating with valid data")
    void shouldUpdateTrainerSuccessfullyWhenUpdatingWithValidData() {
        TrainingType updatedTrainingType = TrainingType.builder()
                .id(2L)
                .typeName("Fitness")
                .build();

        TrainerDTO.Request.Update updateRequest = new TrainerDTO.Request.Update(
                FIRST_NAME, LAST_NAME, 2L, IS_ACTIVE);
        TrainerDTO.Response.Updated updatedResponse = new TrainerDTO.Response.Updated(
                USERNAME, FIRST_NAME, LAST_NAME, updatedTrainingType.getTypeName(), IS_ACTIVE, null);

        Trainer updateData = Trainer.builder()
                .user(CustomUser.builder()
                        .firstName(FIRST_NAME)
                        .lastName(LAST_NAME)
                        .isActive(IS_ACTIVE)
                        .build())
                .specialization(updatedTrainingType)
                .build();

        when(trainerRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainer));
        when(trainingTypeService.findById(2L)).thenReturn(updatedTrainingType);
        when(trainerMapper.toUpdateEntity(updateRequest, updatedTrainingType)).thenReturn(updateData);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);
        when(trainerMapper.toUpdatedResponse(testTrainer)).thenReturn(updatedResponse);

        TrainerDTO.Response.Updated result = trainerService.updateTrainerProfile(updateRequest, USERNAME);

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(USERNAME);

        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

    @Test
    @DisplayName("Should update trainer status successfully when updating status")
    void shouldUpdateTrainerStatusSuccessfullyWhenUpdatingStatus() {
        when(trainerRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        trainerService.updateTrainerStatus(USERNAME, true);

        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

    @Test
    @DisplayName("Should change password successfully when changing password")
    void shouldChangePasswordSuccessfullyWhenChangingPassword() {
        testTrainer.getUser().setPassword(ENCODED_OLD_PASSWORD);

        when(trainerRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainer));
        doNothing().when(entityValidator).validatePasswordChange(
                eq(OLD_PASSWORD),
                eq(NEW_PASSWORD),
                eq(ENCODED_OLD_PASSWORD));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.changeTrainerPassword(USERNAME, OLD_PASSWORD, NEW_PASSWORD);

        assertThat(result).isNotNull();

        verify(trainerRepository, times(1)).save(any(Trainer.class));
        verify(passwordEncoder, times(1)).encode(NEW_PASSWORD);
    }

    @Test
    @DisplayName("Should throw TrainerNotFoundException when trainer not found")
    void shouldThrowTrainerNotFoundExceptionWhenTrainerNotFound() {
        when(trainerRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.getTrainerProfile(USERNAME))
                .isInstanceOf(TrainerNotFoundException.class);
    }
}
package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.exception.AuthenticationException;
import com.github.amangusss.gym_application.mapper.TraineeMapper;
import com.github.amangusss.gym_application.mapper.TrainerMapper;
import com.github.amangusss.gym_application.mapper.TrainingMapper;
import com.github.amangusss.gym_application.repository.TraineeRepository;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.repository.TrainingTypeRepository;
import com.github.amangusss.gym_application.repository.UserRepository;
import com.github.amangusss.gym_application.service.impl.TraineeServiceImpl;
import com.github.amangusss.gym_application.util.credentials.PasswordGenerator;
import com.github.amangusss.gym_application.util.credentials.UsernameGenerator;
import com.github.amangusss.gym_application.validation.trainee.TraineeEntityValidation;

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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraineeService Tests")
class TraineeServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long TRAINEE_ID = 1L;
    private static final String FIRST_NAME = "Dastan";
    private static final String LAST_NAME = "Ibraimov";
    private static final String USERNAME = "Dastan.Ibraimov";
    private static final String VALID_PASSWORD = "password123";
    private static final String INVALID_PASSWORD = "wrongPassword";
    private static final String OLD_PASSWORD = "oldPassword123";
    private static final String NEW_PASSWORD = "newPassword123";
    private static final String ADDRESS = "Panfilov St, 46A";
    private static final String NEW_ADDRESS = "New Address";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(2004, 8, 14);
    private static final boolean IS_ACTIVE = true;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private TraineeEntityValidation traineeEntityValidation;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee testTrainee;
    private TraineeDTO.Request.Register registerRequest;
    private TraineeDTO.Response.Registered registeredResponse;

    @BeforeEach
    void setUp() {
        Mockito.reset(traineeRepository, trainerRepository, trainingTypeRepository, userRepository,
                     usernameGenerator, passwordGenerator, traineeEntityValidation,
                     traineeMapper, trainerMapper, trainingMapper);

        User testUser = User.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(VALID_PASSWORD)
                .isActive(IS_ACTIVE)
                .build();

        testTrainee = Trainee.builder()
                .id(TRAINEE_ID)
                .user(testUser)
                .dateOfBirth(DATE_OF_BIRTH)
                .address(ADDRESS)
                .build();

        registerRequest = new TraineeDTO.Request.Register(FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, ADDRESS);
        registeredResponse = new TraineeDTO.Response.Registered(USERNAME, VALID_PASSWORD);
    }

    @Test
    @DisplayName("Should create trainee with generated credentials when creating trainee")
    void shouldCreateTraineeWithGeneratedCredentialsWhenCreatingTrainee() {
        Mockito.when(traineeMapper.toEntity(registerRequest)).thenReturn(testTrainee);
        Mockito.when(usernameGenerator.generateUsername(
                ArgumentMatchers.eq(FIRST_NAME),
                ArgumentMatchers.eq(LAST_NAME),
                ArgumentMatchers.any()))
                .thenReturn(USERNAME);
        Mockito.when(passwordGenerator.generatePassword()).thenReturn(VALID_PASSWORD);
        Mockito.doNothing().when(traineeEntityValidation).validateTraineeForCreationOrUpdate(ArgumentMatchers.any());
        Mockito.when(traineeRepository.save(ArgumentMatchers.any(Trainee.class))).thenReturn(testTrainee);
        Mockito.when(traineeMapper.toRegisteredResponse(testTrainee)).thenReturn(registeredResponse);

        TraineeDTO.Response.Registered result = traineeService.createTrainee(registerRequest);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.username()).isEqualTo(USERNAME);
        Assertions.assertThat(result.password()).isEqualTo(VALID_PASSWORD);

        Mockito.verify(traineeRepository, Mockito.times(1)).save(ArgumentMatchers.any(Trainee.class));
    }

    @Test
    @DisplayName("Should return trainee profile when finding by username with valid password")
    void shouldReturnTraineeProfileWhenFindingByUsernameWithValidPassword() {
        TraineeDTO.Response.Profile profileResponse = new TraineeDTO.Response.Profile(
                FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, ADDRESS, IS_ACTIVE, null);

        Mockito.when(traineeRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD))
                .thenReturn(true);
        Mockito.when(traineeRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainee));
        Mockito.when(traineeMapper.toProfileResponse(testTrainee)).thenReturn(profileResponse);

        TraineeDTO.Response.Profile result = traineeService.findTraineeByUsername(USERNAME, VALID_PASSWORD);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.firstName()).isEqualTo(FIRST_NAME);
        Assertions.assertThat(result.lastName()).isEqualTo(LAST_NAME);

        Mockito.verify(traineeRepository, Mockito.times(1)).findByUserUsername(USERNAME);
    }

    @Test
    @DisplayName("Should throw AuthenticationException when password is invalid")
    void shouldThrowAuthenticationExceptionWhenPasswordIsInvalid() {
        Mockito.when(traineeRepository.existsByUserUsernameAndUserPassword(USERNAME, INVALID_PASSWORD))
                .thenReturn(false);

        Assertions.assertThatThrownBy(() -> traineeService.findTraineeByUsername(USERNAME, INVALID_PASSWORD))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Authentication failed for trainee: " + USERNAME);
    }

    @Test
    @DisplayName("Should update trainee successfully when updating with valid data")
    void shouldUpdateTraineeSuccessfullyWhenUpdatingWithValidData() {
        TraineeDTO.Request.Update updateRequest = new TraineeDTO.Request.Update(
                FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, NEW_ADDRESS, IS_ACTIVE);
        TraineeDTO.Response.Updated updatedResponse = new TraineeDTO.Response.Updated(
                USERNAME, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, NEW_ADDRESS, IS_ACTIVE, null);

        Trainee updateData = Trainee.builder()
                .user(User.builder()
                        .firstName(FIRST_NAME)
                        .lastName(LAST_NAME)
                        .isActive(IS_ACTIVE)
                        .build())
                .dateOfBirth(DATE_OF_BIRTH)
                .address(NEW_ADDRESS)
                .build();

        Mockito.when(traineeRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD))
                .thenReturn(true);
        Mockito.when(traineeMapper.toUpdateEntity(updateRequest)).thenReturn(updateData);
        Mockito.when(traineeRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainee));
        Mockito.doNothing().when(traineeEntityValidation).validateTraineeForCreationOrUpdate(ArgumentMatchers.any());
        Mockito.when(traineeRepository.save(ArgumentMatchers.any(Trainee.class))).thenReturn(testTrainee);
        Mockito.when(traineeMapper.toUpdatedResponse(testTrainee)).thenReturn(updatedResponse);

        TraineeDTO.Response.Updated result = traineeService.updateTrainee(updateRequest, USERNAME, VALID_PASSWORD);

        Assertions.assertThat(result).isNotNull();
        Mockito.verify(traineeRepository, Mockito.times(1)).save(ArgumentMatchers.any(Trainee.class));
    }

    @Test
    @DisplayName("Should delete trainee when deleting by username")
    void shouldDeleteTraineeWhenDeletingByUsername() {
        Mockito.when(traineeRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD))
                .thenReturn(true);
        Mockito.when(traineeRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainee));
        Mockito.doNothing().when(traineeRepository).delete(ArgumentMatchers.any(Trainee.class));

        traineeService.deleteTraineeByUsername(USERNAME, VALID_PASSWORD);

        Mockito.verify(traineeRepository, Mockito.times(1)).delete(testTrainee);
    }

    @Test
    @DisplayName("Should update trainee status successfully when updating status")
    void shouldUpdateTraineeStatusSuccessfullyWhenUpdatingStatus() {
        Mockito.when(traineeRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD))
                .thenReturn(true);
        Mockito.when(traineeRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainee));
        Mockito.when(traineeRepository.save(ArgumentMatchers.any(Trainee.class))).thenReturn(testTrainee);

        traineeService.updateTraineeStatus(USERNAME, VALID_PASSWORD, true);

        Mockito.verify(traineeRepository, Mockito.times(1)).save(ArgumentMatchers.any(Trainee.class));
    }

    @Test
    @DisplayName("Should change password successfully when changing password")
    void shouldChangePasswordSuccessfullyWhenChangingPassword() {
        Mockito.when(traineeRepository.existsByUserUsernameAndUserPassword(USERNAME, OLD_PASSWORD))
                .thenReturn(true);
        Mockito.when(traineeRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainee));
        Mockito.doNothing().when(traineeEntityValidation).validatePasswordChange(OLD_PASSWORD, NEW_PASSWORD);
        Mockito.when(traineeRepository.save(ArgumentMatchers.any(Trainee.class))).thenReturn(testTrainee);

        Trainee result = traineeService.changeTraineePassword(USERNAME, OLD_PASSWORD, NEW_PASSWORD);

        Assertions.assertThat(result).isNotNull();
        Mockito.verify(traineeRepository, Mockito.times(1)).save(ArgumentMatchers.any(Trainee.class));
    }

    @Test
    @DisplayName("Should authenticate trainee successfully when credentials are valid")
    void shouldAuthenticateTraineeSuccessfullyWhenCredentialsAreValid() {
        Mockito.when(traineeRepository.existsByUserUsernameAndUserPassword(USERNAME, VALID_PASSWORD))
                .thenReturn(true);

        boolean result = traineeService.authenticateTrainee(USERNAME, VALID_PASSWORD);

        Assertions.assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when authenticating with invalid credentials")
    void shouldReturnFalseWhenAuthenticatingWithInvalidCredentials() {
        Mockito.when(traineeRepository.existsByUserUsernameAndUserPassword(USERNAME, INVALID_PASSWORD))
                .thenReturn(false);

        boolean result = traineeService.authenticateTrainee(USERNAME, INVALID_PASSWORD);

        Assertions.assertThat(result).isFalse();
    }
}

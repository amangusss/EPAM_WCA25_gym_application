package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.entity.CustomUser;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TraineeService Tests")
class TraineeServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long TRAINEE_ID = 1L;
    private static final String FIRST_NAME = "Dastan";
    private static final String LAST_NAME = "Ibraimov";
    private static final String USERNAME = "Dastan.Ibraimov";
    private static final String VALID_PASSWORD = "password123";
    private static final String ENCODED_VALID_PASSWORD = "encoded_password123";
    private static final String INVALID_PASSWORD = "wrongPassword";
    private static final String OLD_PASSWORD = "oldPassword123";
    private static final String ENCODED_OLD_PASSWORD = "encoded_oldPassword123";
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
    private EntityValidator entityValidator;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee testTrainee;
    private TraineeDTO.Request.Register registerRequest;
    private TraineeDTO.Response.Registered registeredResponse;

    @BeforeEach
    void setUp() {
        reset(traineeRepository, trainerRepository, trainingTypeRepository, userRepository,
                usernameGenerator, passwordGenerator, entityValidator,
                traineeMapper, trainerMapper, trainingMapper, passwordEncoder);

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

        CustomUser testUser = CustomUser.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(ENCODED_VALID_PASSWORD)
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
        when(traineeMapper.toEntity(registerRequest)).thenReturn(testTrainee);
        when(usernameGenerator.generateUsername(
                        eq(FIRST_NAME),
                        eq(LAST_NAME),
                        any()))
                .thenReturn(USERNAME);
        when(passwordGenerator.generatePassword()).thenReturn(VALID_PASSWORD);
        doNothing().when(entityValidator).validateTraineeForCreation(any());
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);
        when(traineeMapper.toRegisteredResponse(any(Trainee.class), eq(VALID_PASSWORD)))
                .thenReturn(registeredResponse);

        TraineeDTO.Response.Registered result = traineeService.createTrainee(registerRequest);

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(USERNAME);
        assertThat(result.password()).isEqualTo(VALID_PASSWORD);

        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    @DisplayName("Should return trainee profile when finding by username with valid password")
    void shouldReturnTraineeProfileWhenFindingByUsernameWithValidPassword() {
        TraineeDTO.Response.Profile profileResponse = new TraineeDTO.Response.Profile(
                FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, ADDRESS, IS_ACTIVE, null);

        when(traineeRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainee));
        when(traineeMapper.toProfileResponse(testTrainee)).thenReturn(profileResponse);

        TraineeDTO.Response.Profile result = traineeService.getTraineeProfile(USERNAME);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo(FIRST_NAME);
        assertThat(result.lastName()).isEqualTo(LAST_NAME);

        verify(traineeRepository, times(1)).findByUserUsername(USERNAME);
    }

    @Test
    @DisplayName("Should update trainee successfully when updating with valid data")
    void shouldUpdateTraineeSuccessfullyWhenUpdatingWithValidData() {
        TraineeDTO.Request.Update updateRequest = new TraineeDTO.Request.Update(
                FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, NEW_ADDRESS, IS_ACTIVE);
        TraineeDTO.Response.Updated updatedResponse = new TraineeDTO.Response.Updated(
                USERNAME, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, NEW_ADDRESS, IS_ACTIVE, null);

        Trainee updateData = Trainee.builder()
                .user(CustomUser.builder()
                        .firstName(FIRST_NAME)
                        .lastName(LAST_NAME)
                        .isActive(IS_ACTIVE)
                        .build())
                .dateOfBirth(DATE_OF_BIRTH)
                .address(NEW_ADDRESS)
                .build();

        when(traineeMapper.toUpdateEntity(updateRequest)).thenReturn(updateData);
        when(traineeRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainee));
        doNothing().when(entityValidator).validateTrainee(any());
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);
        when(traineeMapper.toUpdatedResponse(testTrainee)).thenReturn(updatedResponse);

        TraineeDTO.Response.Updated result = traineeService.updateTrainee(updateRequest, USERNAME);

        assertThat(result).isNotNull();
        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    @DisplayName("Should delete trainee when deleting by username")
    void shouldDeleteTraineeWhenDeletingByUsername() {
        when(traineeRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainee));
        doNothing().when(traineeRepository).delete(any(Trainee.class));

        traineeService.deleteTraineeByUsername(USERNAME);

        verify(traineeRepository, times(1)).delete(testTrainee);
    }

    @Test
    @DisplayName("Should update trainee isActive successfully when updating isActive")
    void shouldUpdateTraineeStatusSuccessfullyWhenUpdatingStatus() {
        when(traineeRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        traineeService.updateTraineeStatus(USERNAME, true);

        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    @DisplayName("Should change password successfully when changing password")
    void shouldChangePasswordSuccessfullyWhenChangingPassword() {
        testTrainee.getUser().setPassword(ENCODED_OLD_PASSWORD);

        when(traineeRepository.findByUserUsername(USERNAME))
                .thenReturn(Optional.of(testTrainee));
        doNothing().when(entityValidator).validatePasswordChange(
                eq(OLD_PASSWORD),
                eq(NEW_PASSWORD),
                eq(ENCODED_OLD_PASSWORD));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        Trainee result = traineeService.changeTraineePassword(USERNAME, OLD_PASSWORD, NEW_PASSWORD);

        assertThat(result).isNotNull();
        verify(traineeRepository, times(1)).save(any(Trainee.class));
        verify(passwordEncoder, times(1)).encode(NEW_PASSWORD);
    }
}
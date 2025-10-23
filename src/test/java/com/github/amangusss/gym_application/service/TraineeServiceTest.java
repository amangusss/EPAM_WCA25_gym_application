package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.exception.AuthenticationException;
import com.github.amangusss.gym_application.repository.TraineeRepository;
import com.github.amangusss.gym_application.repository.UserRepository;
import com.github.amangusss.gym_application.service.impl.TraineeServiceImpl;
import com.github.amangusss.gym_application.util.credentials.PasswordGenerator;
import com.github.amangusss.gym_application.util.credentials.UsernameGenerator;
import com.github.amangusss.gym_application.validation.trainee.TraineeEntityValidation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraineeService Tests")
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private TraineeEntityValidation traineeEntityValidation;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee testTrainee;

    @BeforeEach
    void setUp() {
        User testUser = User.builder()
                .id(1L)
                .firstName("Dastan")
                .lastName("Ibraimov")
                .username("Dastan.Ibraimov")
                .password("password123")
                .isActive(true)
                .build();

        testTrainee = Trainee.builder()
                .id(1L)
                .user(testUser)
                .dateOfBirth(LocalDate.of(2004, 8, 14))
                .address("Panfilov St, 46A")
                .build();
    }

    @Test
    @DisplayName("Should create trainee with generated credentials")
    void createTrainee_ShouldGenerateCredentialsAndSave() {
        when(usernameGenerator.generateUsername(eq("Dastan"), eq("Ibraimov"), any()))
                .thenReturn("Dastan.Ibraimov");
        when(passwordGenerator.generatePassword()).thenReturn("password123");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        doNothing().when(traineeEntityValidation).validateTraineeForCreationOrUpdate(any());
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        Trainee result = traineeService.createTrainee(testTrainee);

        assertThat(result).isNotNull();
        assertThat(result.getUser().getUsername()).isEqualTo("Dastan.Ibraimov");
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    @DisplayName("Should find trainee by username with valid password")
    void findTraineeByUsername_WithValidPassword_ShouldReturnTrainee() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("Dastan.Ibraimov", "password123"))
                .thenReturn(true);
        when(traineeRepository.findByUserUsername("Dastan.Ibraimov"))
                .thenReturn(Optional.of(testTrainee));

        Trainee result = traineeService.findTraineeByUsername("Dastan.Ibraimov", "password123");

        assertThat(result).isNotNull();
        assertThat(result.getUser().getUsername()).isEqualTo("Dastan.Ibraimov");
        verify(traineeRepository).findByUserUsername("Dastan.Ibraimov");
    }

    @Test
    @DisplayName("Should throw exception when password is invalid")
    void findTraineeByUsername_WithInvalidPassword_ShouldThrowException() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("Dastan.Ibraimov", "wrongPassword"))
                .thenReturn(false);

        assertThatThrownBy(() -> traineeService.findTraineeByUsername("Dastan.Ibraimov", "wrongPassword"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    @DisplayName("Should update trainee successfully")
    void updateTrainee_ShouldUpdateAndReturnTrainee() {
        Trainee updateData = Trainee.builder()
                .user(User.builder()
                        .firstName("Dastan")
                        .lastName("Ibraimov")
                        .isActive(true)
                        .build())
                .dateOfBirth(LocalDate.of(2004, 8, 14))
                .address("New Address")
                .build();

        when(traineeRepository.existsByUserUsernameAndUserPassword("Dastan.Ibraimov", "password123"))
                .thenReturn(true);
        when(traineeRepository.findByUserUsername("Dastan.Ibraimov"))
                .thenReturn(Optional.of(testTrainee));
        doNothing().when(traineeEntityValidation).validateTraineeForCreationOrUpdate(any());
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        Trainee result = traineeService.updateTrainee("Dastan.Ibraimov", "password123", updateData);

        assertThat(result).isNotNull();
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    @DisplayName("Should delete trainee by username")
    void deleteTraineeByUsername_ShouldCallDelete() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("Dastan.Ibraimov", "password123"))
                .thenReturn(true);
        when(traineeRepository.findByUserUsername("Dastan.Ibraimov"))
                .thenReturn(Optional.of(testTrainee));
        doNothing().when(traineeRepository).delete(any(Trainee.class));

        traineeService.deleteTraineeByUsername("Dastan.Ibraimov", "password123");

        verify(traineeRepository).delete(testTrainee);
    }

    @Test
    @DisplayName("Should activate trainee successfully")
    void activateTrainee_ShouldSetActiveStatusToTrue() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("Dastan.Ibraimov", "password123"))
                .thenReturn(true);
        when(traineeRepository.findByUserUsername("Dastan.Ibraimov"))
                .thenReturn(Optional.of(testTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        Trainee result = traineeService.activateTrainee("Dastan.Ibraimov", "password123");

        assertThat(result).isNotNull();
        verify(traineeRepository).save(testTrainee);
    }

    @Test
    @DisplayName("Should deactivate trainee successfully")
    void deactivateTrainee_ShouldSetActiveStatusToFalse() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("Dastan.Ibraimov", "password123"))
                .thenReturn(true);
        when(traineeRepository.findByUserUsername("Dastan.Ibraimov"))
                .thenReturn(Optional.of(testTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        Trainee result = traineeService.deactivateTrainee("Dastan.Ibraimov", "password123");

        assertThat(result).isNotNull();
        verify(traineeRepository).save(testTrainee);
    }
}

package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.AuthenticationException;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.repository.TraineeRepository;
import com.github.amangusss.gym_application.service.impl.TraineeServiceImpl;
import com.github.amangusss.gym_application.util.credentials.PasswordGenerator;
import com.github.amangusss.gym_application.util.credentials.UsernameGenerator;
import com.github.amangusss.gym_application.validation.trainee.TraineeEntityValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private TraineeEntityValidation traineeEntityValidation;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee testTrainee;
    private TrainingType testTrainingType;

    @BeforeEach
    void setUp() {
        testTrainingType = TrainingType.builder()
                .id(1L)
                .typeName("YOGA")
                .build();

        User user = User.builder()
                .firstName("Dastan")
                .lastName("Ibraimov")
                .build();

        testTrainee = Trainee.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(2004, 8, 14))
                .address("Panfilov St, 46A")
                .build();
    }

    @Test
    void createTrainee_ShouldGenerateCredentialsAndSave() {
        when(usernameGenerator.generateUsername(eq("Dastan"), eq("Ibraimov"), any(Predicate.class)))
                .thenReturn("Dastan.Ibraimov");
        when(passwordGenerator.generatePassword()).thenReturn("password123");
        doNothing().when(traineeEntityValidation).validateTraineeForCreationOrUpdate(any());
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        Trainee result = traineeService.createTrainee(testTrainee);

        assertNotNull(result);
        assertEquals("Dastan.Ibraimov", result.getUser().getUsername());
        assertEquals("password123", result.getUser().getPassword());
        assertTrue(result.getUser().isActive());

        verify(usernameGenerator).generateUsername(eq("Dastan"), eq("Ibraimov"), any(Predicate.class));
        verify(passwordGenerator).generatePassword();
        verify(traineeRepository).save(testTrainee);
    }

    @Test
    void findTraineeByUsername_ShouldReturnTrainee() {
        testTrainee.getUser().setUsername("Dastan.Ibraimov");
        testTrainee.getUser().setPassword("password123");

        when(traineeRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "password123")).thenReturn(true);
        when(traineeRepository.findByUsername("Dastan.Ibraimov")).thenReturn(testTrainee);

        Trainee result = traineeService.findTraineeByUsername("Dastan.Ibraimov", "password123");

        assertNotNull(result);
        assertEquals("Dastan.Ibraimov", result.getUser().getUsername());
        verify(traineeRepository).findByUsername("Dastan.Ibraimov");
    }

    @Test
    void findTraineeByUsername_WithInvalidCredentials_ShouldThrowException() {
        when(traineeRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "wrongPassword")).thenReturn(false);

        assertThrows(AuthenticationException.class,
                () -> traineeService.findTraineeByUsername("Dastan.Ibraimov", "wrongPassword"));
    }

    @Test
    void updateTrainee_ShouldUpdateAndReturnTrainee() {
        User updatedUser = User.builder()
                .firstName("Dastan")
                .lastName("Ibraimov")
                .build();

        Trainee updatedTrainee = Trainee.builder()
                .user(updatedUser)
                .dateOfBirth(LocalDate.of(2004, 8, 14))
                .address("Erkindik boulevard, 10")
                .build();

        testTrainee.getUser().setUsername("Dastan.Ibraimov");
        testTrainee.getUser().setPassword("password123");

        when(traineeRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "password123")).thenReturn(true);
        when(traineeRepository.findByUsername("Dastan.Ibraimov")).thenReturn(testTrainee);
        doNothing().when(traineeEntityValidation).validateTraineeForCreationOrUpdate(any());
        when(traineeRepository.update(any(Trainee.class))).thenReturn(testTrainee);

        Trainee result = traineeService.updateTrainee("Dastan.Ibraimov", "password123", updatedTrainee);

        assertNotNull(result);
        verify(traineeRepository).update(testTrainee);
    }

    @Test
    void deleteTraineeByUsername_ShouldDeleteTrainee() {
        when(traineeRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "password123")).thenReturn(true);
        doNothing().when(traineeRepository).deleteByUsername("Dastan.Ibraimov");

        traineeService.deleteTraineeByUsername("Dastan.Ibraimov", "password123");

        verify(traineeRepository).deleteByUsername("Dastan.Ibraimov");
    }

    @Test
    void authenticateTrainee_WithValidCredentials_ShouldReturnTrue() {
        when(traineeRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "password123")).thenReturn(true);

        boolean result = traineeService.authenticateTrainee("Dastan.Ibraimov", "password123");

        assertTrue(result);
    }

    @Test
    void authenticateTrainee_WithInvalidCredentials_ShouldReturnFalse() {
        when(traineeRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "wrongPassword")).thenReturn(false);

        boolean result = traineeService.authenticateTrainee("Dastan.Ibraimov", "wrongPassword");

        assertFalse(result);
    }

    @Test
    void authenticateTrainee_WithNullUsername_ShouldReturnFalse() {
        boolean result = traineeService.authenticateTrainee(null, "password123");

        assertFalse(result);
        verify(traineeRepository, never()).existsByUsernameAndPassword(anyString(), anyString());
    }

    @Test
    void authenticateTrainee_WithEmptyPassword_ShouldReturnFalse() {
        boolean result = traineeService.authenticateTrainee("Dastan.Ibraimov", "");

        assertFalse(result);
        verify(traineeRepository, never()).existsByUsernameAndPassword(anyString(), anyString());
    }

    @Test
    void changeTraineePassword_ShouldUpdatePassword() {
        testTrainee.getUser().setUsername("Dastan.Ibraimov");

        when(traineeRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "oldPassword")).thenReturn(true);
        doNothing().when(traineeEntityValidation).validatePasswordChange("oldPassword", "newPassword");
        when(traineeRepository.updatePasswordByUsername("Dastan.Ibraimov", "oldPassword", "newPassword"))
                .thenReturn(testTrainee);

        Trainee result = traineeService.changeTraineePassword("Dastan.Ibraimov", "oldPassword", "newPassword");

        assertNotNull(result);
        verify(traineeRepository).updatePasswordByUsername("Dastan.Ibraimov", "oldPassword", "newPassword");
    }

    @Test
    void activateTrainee_ShouldActivateTrainee() {
        testTrainee.getUser().setActive(true);

        when(traineeRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "password123")).thenReturn(true);
        when(traineeRepository.updateActiveStatusByUsername("Dastan.Ibraimov", true)).thenReturn(testTrainee);

        Trainee result = traineeService.activateTrainee("Dastan.Ibraimov", "password123");

        assertNotNull(result);
        verify(traineeRepository).updateActiveStatusByUsername("Dastan.Ibraimov", true);
    }

    @Test
    void deactivateTrainee_ShouldDeactivateTrainee() {
        testTrainee.getUser().setActive(false);

        when(traineeRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "password123")).thenReturn(true);
        when(traineeRepository.updateActiveStatusByUsername("Dastan.Ibraimov", false)).thenReturn(testTrainee);

        Trainee result = traineeService.deactivateTrainee("Dastan.Ibraimov", "password123");

        assertNotNull(result);
        assertFalse(result.getUser().isActive());
        verify(traineeRepository).updateActiveStatusByUsername("Dastan.Ibraimov", false);
    }

    @Test
    void getTraineeTrainingsList_ShouldReturnTrainings() {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(30);
        List<Training> trainings = Collections.emptyList();

        when(traineeRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "password123")).thenReturn(true);
        doNothing().when(traineeEntityValidation).validateDateRange(fromDate, toDate);
        when(traineeRepository.findTrainingsByUsername("Dastan.Ibraimov", fromDate, toDate, "Jane", testTrainingType))
                .thenReturn(trainings);

        List<Training> result = traineeService.getTraineeTrainingsList(
                "Dastan.Ibraimov", "password123", fromDate, toDate, "Jane", testTrainingType);

        assertNotNull(result);
        verify(traineeRepository).findTrainingsByUsername("Dastan.Ibraimov", fromDate, toDate, "Jane", testTrainingType);
    }

    @Test
    void getTrainersNotAssignedToTrainee_ShouldReturnTrainers() {
        List<Trainer> trainers = Collections.emptyList();

        when(traineeRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "password123")).thenReturn(true);
        when(traineeRepository.findTrainersNotAssignedOnTraineeByUsername("Dastan.Ibraimov")).thenReturn(trainers);

        List<Trainer> result = traineeService.getTrainersNotAssignedToTrainee("Dastan.Ibraimov", "password123");

        assertNotNull(result);
        verify(traineeRepository).findTrainersNotAssignedOnTraineeByUsername("Dastan.Ibraimov");
    }

    @Test
    void updateTraineeTrainersList_ShouldUpdateTrainersList() {
        Set<Trainer> trainers = new HashSet<>();
        testTrainee.getUser().setUsername("Dastan.Ibraimov");

        when(traineeRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "password123")).thenReturn(true);
        when(traineeRepository.updateTrainersListByUsername("Dastan.Ibraimov", trainers)).thenReturn(testTrainee);

        Trainee result = traineeService.updateTraineeTrainersList("Dastan.Ibraimov", "password123", trainers);

        assertNotNull(result);
        verify(traineeRepository).updateTrainersListByUsername("Dastan.Ibraimov", trainers);
    }

    @Test
    void updateTraineeTrainersList_WithNullTrainers_ShouldThrowException() {
        when(traineeRepository.existsByUsernameAndPassword("Dastan.Ibraimov", "password123")).thenReturn(true);

        assertThrows(ValidationException.class,
                () -> traineeService.updateTraineeTrainersList("Dastan.Ibraimov", "password123", null));

        verify(traineeRepository, never()).updateTrainersListByUsername(anyString(), any());
    }
}
package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.AuthenticationException;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.service.impl.TrainerServiceImpl;
import com.github.amangusss.gym_application.util.credentials.PasswordGenerator;
import com.github.amangusss.gym_application.util.credentials.UsernameGenerator;
import com.github.amangusss.gym_application.util.validation.service.trainer.TrainerServiceValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private TrainerServiceValidation trainerServiceValidation;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        testTrainer = Trainer.builder()
                .firstName("Aman")
                .lastName("Nazarkulov")
                .specialization(TrainingType.YOGA)
                .build();
    }

    @Test
    void createTrainer_ShouldGenerateCredentialsAndSave() {
        when(usernameGenerator.generateUsername(eq("Aman"), eq("Nazarkulov"), any(Predicate.class)))
                .thenReturn("Aman.Nazarkulov");
        when(passwordGenerator.generatePassword()).thenReturn("password123");
        doNothing().when(trainerServiceValidation).validateTrainerForCreationOrUpdate(any());
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.createTrainer(testTrainer);

        assertNotNull(result);
        assertEquals("Aman.Nazarkulov", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertTrue(result.isActive());

        verify(usernameGenerator).generateUsername(eq("Aman"), eq("Nazarkulov"), any(Predicate.class));
        verify(passwordGenerator).generatePassword();
        verify(trainerRepository).save(testTrainer);
    }

    @Test
    void findTrainerByUsername_ShouldReturnTrainer() {
        testTrainer.setUsername("Aman.Nazarkulov");
        testTrainer.setPassword("password123");

        when(trainerRepository.existsByUsernameAndPassword("Aman.Nazarkulov", "password123")).thenReturn(true);
        when(trainerRepository.findByUsername("Aman.Nazarkulov")).thenReturn(testTrainer);

        Trainer result = trainerService.findTrainerByUsername("Aman.Nazarkulov", "password123");

        assertNotNull(result);
        assertEquals("Aman.Nazarkulov", result.getUsername());
        verify(trainerRepository).findByUsername("Aman.Nazarkulov");
    }

    @Test
    void findTrainerByUsername_WithInvalidCredentials_ShouldThrowException() {
        when(trainerRepository.existsByUsernameAndPassword("Aman.Nazarkulov", "wrongPassword")).thenReturn(false);

        assertThrows(AuthenticationException.class,
                () -> trainerService.findTrainerByUsername("Aman.Nazarkulov", "wrongPassword"));
    }

    @Test
    void updateTrainer_ShouldUpdateAndReturnTrainer() {
        Trainer updatedTrainer = Trainer.builder()
                .firstName("Aman")
                .lastName("Nazarkulov")
                .specialization(TrainingType.FITNESS)
                .build();

        testTrainer.setUsername("Aman.Nazarkulov");
        testTrainer.setPassword("password123");

        when(trainerRepository.existsByUsernameAndPassword("Aman.Nazarkulov", "password123")).thenReturn(true);
        when(trainerRepository.findByUsername("Aman.Nazarkulov")).thenReturn(testTrainer);
        doNothing().when(trainerServiceValidation).validateTrainerForCreationOrUpdate(any());
        when(trainerRepository.update(any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.updateTrainer("Aman.Nazarkulov", "password123", updatedTrainer);

        assertNotNull(result);
        verify(trainerRepository).update(testTrainer);
    }

    @Test
    void authenticateTrainer_WithValidCredentials_ShouldReturnTrue() {
        when(trainerRepository.existsByUsernameAndPassword("Aman.Nazarkulov", "password123")).thenReturn(true);

        boolean result = trainerService.authenticateTrainer("Aman.Nazarkulov", "password123");

        assertTrue(result);
        verify(trainerRepository).existsByUsernameAndPassword("Aman.Nazarkulov", "password123");
    }

    @Test
    void authenticateTrainer_WithInvalidCredentials_ShouldReturnFalse() {
        when(trainerRepository.existsByUsernameAndPassword("Aman.Nazarkulov", "wrongPassword")).thenReturn(false);

        boolean result = trainerService.authenticateTrainer("Aman.Nazarkulov", "wrongPassword");

        assertFalse(result);
    }

    @Test
    void changeTrainerPassword_ShouldUpdatePassword() {
        testTrainer.setUsername("Aman.Nazarkulov");

        when(trainerRepository.existsByUsernameAndPassword("Aman.Nazarkulov", "oldPassword")).thenReturn(true);
        doNothing().when(trainerServiceValidation).validatePasswordChange("oldPassword", "newPassword");
        when(trainerRepository.updatePasswordByUsername("Aman.Nazarkulov", "oldPassword", "newPassword"))
                .thenReturn(testTrainer);

        Trainer result = trainerService.changeTrainerPassword("Aman.Nazarkulov", "oldPassword", "newPassword");

        assertNotNull(result);
        verify(trainerRepository).updatePasswordByUsername("Aman.Nazarkulov", "oldPassword", "newPassword");
    }

    @Test
    void activateTrainer_ShouldActivateTrainer() {
        testTrainer.setActive(true);

        when(trainerRepository.existsByUsernameAndPassword("Aman.Nazarkulov", "password123")).thenReturn(true);
        when(trainerRepository.updateActiveStatusByUsername("Aman.Nazarkulov", true)).thenReturn(testTrainer);

        Trainer result = trainerService.activateTrainer("Aman.Nazarkulov", "password123");

        assertNotNull(result);
        assertTrue(result.isActive());
        verify(trainerRepository).updateActiveStatusByUsername("Aman.Nazarkulov", true);
    }

    @Test
    void deactivateTrainer_ShouldDeactivateTrainer() {
        testTrainer.setActive(false);

        when(trainerRepository.existsByUsernameAndPassword("Aman.Nazarkulov", "password123")).thenReturn(true);
        when(trainerRepository.updateActiveStatusByUsername("Aman.Nazarkulov", false)).thenReturn(testTrainer);

        Trainer result = trainerService.deactivateTrainer("Aman.Nazarkulov", "password123");

        assertNotNull(result);
        assertFalse(result.isActive());
        verify(trainerRepository).updateActiveStatusByUsername("Aman.Nazarkulov", false);
    }

    @Test
    void getTrainerTrainingsList_ShouldReturnTrainings() {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(30);
        List<Training> trainings = Collections.emptyList();

        when(trainerRepository.existsByUsernameAndPassword("Aman.Nazarkulov", "password123")).thenReturn(true);
        doNothing().when(trainerServiceValidation).validateDateRange(fromDate, toDate);
        when(trainerRepository.findTrainingsByUsername("Aman.Nazarkulov", fromDate, toDate, "John"))
                .thenReturn(trainings);

        List<Training> result = trainerService.getTrainerTrainingsList(
                "Aman.Nazarkulov", "password123", fromDate, toDate, "John");

        assertNotNull(result);
        verify(trainerRepository).findTrainingsByUsername("Aman.Nazarkulov", fromDate, toDate, "John");
    }

    @Test
    void getTrainerTrainingsList_WithNullDates_ShouldReturnAllTrainings() {
        List<Training> trainings = Collections.emptyList();

        when(trainerRepository.existsByUsernameAndPassword("Aman.Nazarkulov", "password123")).thenReturn(true);
        doNothing().when(trainerServiceValidation).validateDateRange(null, null);
        when(trainerRepository.findTrainingsByUsername("Aman.Nazarkulov", null, null, null))
                .thenReturn(trainings);

        List<Training> result = trainerService.getTrainerTrainingsList(
                "Aman.Nazarkulov", "password123", null, null, null);

        assertNotNull(result);
        verify(trainerRepository).findTrainingsByUsername("Aman.Nazarkulov", null, null, null);
    }

    @Test
    void changeTrainerPassword_WithInvalidCredentials_ShouldThrowException() {
        when(trainerRepository.existsByUsernameAndPassword("Aman.Nazarkulov", "wrongPassword")).thenReturn(false);

        assertThrows(AuthenticationException.class,
                () -> trainerService.changeTrainerPassword("Aman.Nazarkulov", "wrongPassword", "newPassword"));

        verify(trainerRepository, never()).updatePasswordByUsername(anyString(), anyString(), anyString());
    }
}
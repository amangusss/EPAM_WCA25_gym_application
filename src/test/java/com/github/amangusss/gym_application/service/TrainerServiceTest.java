package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.Trainer;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.repository.dao.TraineeDAO;
import com.github.amangusss.gym_application.repository.dao.TrainerDAO;
import com.github.amangusss.gym_application.service.impl.TrainerServiceImpl;
import com.github.amangusss.gym_application.util.PasswordGenerator;
import com.github.amangusss.gym_application.util.UsernameGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerDAO trainerDAO;

    @Mock
    private TraineeDAO traineeDAO;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        testTrainer = new Trainer("Jane", "Smith", TrainingType.FITNESS);
    }

    @Test
    void createTrainer_ShouldGenerateCredentialsAndSave() {
        // Given
        when(usernameGenerator.generateUsername(eq("Jane"), eq("Smith"), anySet()))
                .thenReturn("Jane.Smith");
        when(passwordGenerator.generatePassword()).thenReturn("password123");
        when(traineeDAO.findAll()).thenReturn(List.of());
        when(trainerDAO.findAll()).thenReturn(List.of());
        when(trainerDAO.save(any(Trainer.class))).thenReturn(testTrainer);

        // When
        Trainer result = trainerService.createTrainer(testTrainer);

        // Then
        assertNotNull(result);
        assertEquals("Jane.Smith", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertTrue(result.isActive());
        
        verify(usernameGenerator).generateUsername(eq("Jane"), eq("Smith"), anySet());
        verify(passwordGenerator).generatePassword();
        verify(trainerDAO).save(testTrainer);
    }

    @Test
    void createTrainer_WithNullTrainer_ShouldThrowException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            trainerService.createTrainer(null);
        });
    }

    @Test
    void createTrainer_WithEmptyFirstName_ShouldThrowException() {
        // Given
        testTrainer.setFirstName("");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            trainerService.createTrainer(testTrainer);
        });
    }

    @Test
    void createTrainer_WithNullSpecialization_ShouldThrowException() {
        // Given
        testTrainer.setSpecialization(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            trainerService.createTrainer(testTrainer);
        });
    }

    @Test
    void updateTrainer_ShouldUpdateAndReturnTrainer() {
        // Given
        testTrainer.setId(1L);
        when(trainerDAO.findById(1L)).thenReturn(testTrainer);
        when(trainerDAO.update(any(Trainer.class))).thenReturn(testTrainer);

        // When
        Trainer result = trainerService.updateTrainer(testTrainer);

        // Then
        assertNotNull(result);
        verify(trainerDAO).findById(1L);
        verify(trainerDAO).update(testTrainer);
    }

    @Test
    void findTrainerById_ShouldReturnTrainer() {
        // Given
        Long trainerId = 1L;
        when(trainerDAO.findById(trainerId)).thenReturn(testTrainer);

        // When
        Trainer result = trainerService.findTrainerById(trainerId);

        // Then
        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(trainerDAO).findById(trainerId);
    }

    @Test
    void findAllTrainers_ShouldReturnAllTrainers() {
        // Given
        List<Trainer> trainers = Collections.singletonList(testTrainer);
        when(trainerDAO.findAll()).thenReturn(trainers);

        // When
        List<Trainer> result = trainerService.findAllTrainers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTrainer, result.get(0));
        verify(trainerDAO).findAll();
    }
}

package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.repository.TraineeDAO;
import com.github.amangusss.gym_application.repository.TrainerDAO;
import com.github.amangusss.gym_application.service.impl.TrainerServiceImpl;
import com.github.amangusss.gym_application.util.PasswordGenerator;
import com.github.amangusss.gym_application.util.UsernameGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.github.amangusss.gym_application.exception.ValidationException;

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
        when(usernameGenerator.generateUsername(eq("Jane"), eq("Smith"), anySet()))
                .thenReturn("Jane.Smith");
        when(passwordGenerator.generatePassword()).thenReturn("password123");
        when(traineeDAO.findAll()).thenReturn(List.of());
        when(trainerDAO.findAll()).thenReturn(List.of());
        when(trainerDAO.save(any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.createTrainer(testTrainer);

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
        assertThrows(ValidationException.class, () -> trainerService.createTrainer(null));
    }

    @Test
    void createTrainer_WithEmptyFirstName_ShouldThrowException() {
        testTrainer.setFirstName("");

        assertThrows(ValidationException.class, () -> trainerService.createTrainer(testTrainer));
    }

    @Test
    void createTrainer_WithNullSpecialization_ShouldThrowException() {
        testTrainer.setSpecialization(null);

        assertThrows(ValidationException.class, () -> trainerService.createTrainer(testTrainer));
    }

    @Test
    void updateTrainer_ShouldUpdateAndReturnTrainer() {
        testTrainer.setId(1L);
        when(trainerDAO.findById(1L)).thenReturn(testTrainer);
        when(trainerDAO.update(any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.updateTrainer(testTrainer);

        assertNotNull(result);
        verify(trainerDAO).findById(1L);
        verify(trainerDAO).update(testTrainer);
    }

    @Test
    void findTrainerById_ShouldReturnTrainer() {
        Long trainerId = 1L;
        when(trainerDAO.findById(trainerId)).thenReturn(testTrainer);

        Trainer result = trainerService.findTrainerById(trainerId);

        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(trainerDAO).findById(trainerId);
    }

    @Test
    void findAllTrainers_ShouldReturnAllTrainers() {
        List<Trainer> trainers = Collections.singletonList(testTrainer);
        when(trainerDAO.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.findAllTrainers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTrainer, result.get(0));
        verify(trainerDAO).findAll();
    }
}
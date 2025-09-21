package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.Trainee;
import com.github.amangusss.gym_application.entity.Trainer;
import com.github.amangusss.gym_application.entity.Training;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.repository.dao.TraineeDAO;
import com.github.amangusss.gym_application.repository.dao.TrainerDAO;
import com.github.amangusss.gym_application.repository.dao.TrainingDAO;
import com.github.amangusss.gym_application.service.impl.TrainingServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingDAO trainingDAO;

    @Mock
    private TrainerDAO trainerDAO;

    @Mock
    private TraineeDAO traineeDAO;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training testTraining;
    private Trainee testTrainee;
    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        testTrainee = new Trainee("John", "Doe");
        testTrainee.setId(1L);
        testTrainee.setActive(true);

        testTrainer = new Trainer("Jane", "Smith", TrainingType.FITNESS);
        testTrainer.setId(2L);
        testTrainer.setActive(true);

        testTraining = new Training(1L, 2L, "Morning Workout", TrainingType.FITNESS, 
                LocalDate.now().plusDays(1), 60);
    }

    @Test
    void createTraining_ShouldCreateAndReturnTraining() {
        // Given
        when(traineeDAO.findById(1L)).thenReturn(testTrainee);
        when(trainerDAO.findById(2L)).thenReturn(testTrainer);
        when(trainingDAO.save(any(Training.class))).thenReturn(testTraining);

        // When
        Training result = trainingService.createTraining(testTraining);

        // Then
        assertNotNull(result);
        assertEquals(testTraining, result);
        verify(traineeDAO).findById(1L);
        verify(trainerDAO).findById(2L);
        verify(trainingDAO).save(testTraining);
    }

    @Test
    void createTraining_WithNullTraining_ShouldThrowException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(null));
    }

    @Test
    void createTraining_WithNonExistentTrainee_ShouldThrowException() {
        // Given
        when(traineeDAO.findById(1L)).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(testTraining));
    }

    @Test
    void createTraining_WithNonExistentTrainer_ShouldThrowException() {
        // Given
        when(traineeDAO.findById(1L)).thenReturn(testTrainee);
        when(trainerDAO.findById(2L)).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(testTraining));
    }

    @Test
    void createTraining_WithInactiveTrainee_ShouldThrowException() {
        // Given
        testTrainee.setActive(false);
        when(traineeDAO.findById(1L)).thenReturn(testTrainee);
        when(trainerDAO.findById(2L)).thenReturn(testTrainer);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(testTraining));
    }

    @Test
    void createTraining_WithInactiveTrainer_ShouldThrowException() {
        // Given
        testTrainer.setActive(false);
        when(traineeDAO.findById(1L)).thenReturn(testTrainee);
        when(trainerDAO.findById(2L)).thenReturn(testTrainer);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(testTraining));
    }

    @Test
    void findTraining_ShouldReturnTraining() {
        // Given
        Long trainingId = 1L;
        when(trainingDAO.findById(trainingId)).thenReturn(testTraining);

        // When
        Training result = trainingService.findTraining(trainingId);

        // Then
        assertNotNull(result);
        assertEquals(testTraining, result);
        verify(trainingDAO).findById(trainingId);
    }

    @Test
    void findAllTrainings_ShouldReturnAllTrainings() {
        // Given
        List<Training> trainings = Collections.singletonList(testTraining);
        when(trainingDAO.findAll()).thenReturn(trainings);

        // When
        List<Training> result = trainingService.findAllTrainings();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTraining, result.get(0));
        verify(trainingDAO).findAll();
    }
}

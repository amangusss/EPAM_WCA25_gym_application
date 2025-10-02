package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.repository.TraineeDAO;
import com.github.amangusss.gym_application.repository.TrainerDAO;
import com.github.amangusss.gym_application.repository.TrainingDAO;
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

import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.exception.TraineeNotFoundException;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;

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
        when(traineeDAO.findById(1L)).thenReturn(testTrainee);
        when(trainerDAO.findById(2L)).thenReturn(testTrainer);
        when(trainingDAO.save(any(Training.class))).thenReturn(testTraining);

        Training result = trainingService.createTraining(testTraining);

        assertNotNull(result);
        assertEquals(testTraining, result);
        verify(traineeDAO).findById(1L);
        verify(trainerDAO).findById(2L);
        verify(trainingDAO).save(testTraining);
    }

    @Test
    void createTraining_WithNullTraining_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> trainingService.createTraining(null));
    }

    @Test
    void createTraining_WithNonExistentTrainee_ShouldThrowException() {
        when(traineeDAO.findById(1L)).thenReturn(null);

        assertThrows(TraineeNotFoundException.class, () -> trainingService.createTraining(testTraining));
    }

    @Test
    void createTraining_WithNonExistentTrainer_ShouldThrowException() {
        when(traineeDAO.findById(1L)).thenReturn(testTrainee);
        when(trainerDAO.findById(2L)).thenReturn(null);

        assertThrows(TrainerNotFoundException.class, () -> trainingService.createTraining(testTraining));
    }

    @Test
    void createTraining_WithInactiveTrainee_ShouldThrowException() {
        testTrainee.setActive(false);
        when(traineeDAO.findById(1L)).thenReturn(testTrainee);
        when(trainerDAO.findById(2L)).thenReturn(testTrainer);

        assertThrows(ValidationException.class, () -> trainingService.createTraining(testTraining));
    }

    @Test
    void createTraining_WithInactiveTrainer_ShouldThrowException() {
        testTrainer.setActive(false);
        when(traineeDAO.findById(1L)).thenReturn(testTrainee);
        when(trainerDAO.findById(2L)).thenReturn(testTrainer);

        assertThrows(ValidationException.class, () -> trainingService.createTraining(testTraining));
    }

    @Test
    void findTraining_ShouldReturnTraining() {
        Long trainingId = 1L;
        when(trainingDAO.findById(trainingId)).thenReturn(testTraining);

        Training result = trainingService.findTraining(trainingId);

        assertNotNull(result);
        assertEquals(testTraining, result);
        verify(trainingDAO).findById(trainingId);
    }

    @Test
    void findAllTrainings_ShouldReturnAllTrainings() {
        List<Training> trainings = Collections.singletonList(testTraining);
        when(trainingDAO.findAll()).thenReturn(trainings);

        List<Training> result = trainingService.findAllTrainings();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTraining, result.get(0));
        verify(trainingDAO).findAll();
    }
}
package com.github.amangusss.gym_application.repository.impl;

import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.training.TrainingBuilder;
import com.github.amangusss.gym_application.storage.TrainingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingDAOImplTest {

    @Mock
    private TrainingStorage trainingStorage;

    @InjectMocks
    private TrainingDAOImpl trainingDAO;

    private Training testTraining;

    @BeforeEach
    void setUp() {
        testTraining = TrainingBuilder.builder()
                .traineeId(1L)
                .trainerId(1L)
                .trainingName("Morning Workout")
                .trainingType(TrainingType.FITNESS)
                .trainingDate(LocalDate.of(2024, 1, 15))
                .trainingDuration(60)
                .build();
        testTraining.setId(1L);
    }

    @Test
    void save_WithValidTraining_ShouldCallStorageSave() {
        when(trainingStorage.save(testTraining)).thenReturn(testTraining);

        Training result = trainingDAO.save(testTraining);

        assertNotNull(result);
        assertEquals(testTraining, result);
        verify(trainingStorage).save(testTraining);
    }

    @Test
    void findById_WithValidId_ShouldReturnTraining() {
        when(trainingStorage.findById(1L)).thenReturn(testTraining);

        Training result = trainingDAO.findById(1L);

        assertNotNull(result);
        assertEquals(testTraining, result);
        verify(trainingStorage).findById(1L);
    }

    @Test
    void findAll_ShouldReturnAllTrainings() {
        List<Training> trainings = Collections.singletonList(testTraining);
        when(trainingStorage.findAll()).thenReturn(trainings);

        List<Training> result = trainingDAO.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTraining, result.get(0));
        verify(trainingStorage).findAll();
    }

    @Test
    void findByTraineeId_WithValidId_ShouldReturnTrainings() {
        List<Training> trainings = Collections.singletonList(testTraining);
        when(trainingStorage.findAll()).thenReturn(trainings);

        List<Training> result = trainingDAO.findByTraineeId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTraining, result.get(0));
        verify(trainingStorage).findAll();
    }

    @Test
    void findByTrainerId_WithValidId_ShouldReturnTrainings() {
        List<Training> trainings = Collections.singletonList(testTraining);
        when(trainingStorage.findAll()).thenReturn(trainings);

        List<Training> result = trainingDAO.findByTrainerId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTraining, result.get(0));
        verify(trainingStorage).findAll();
    }

    @Test
    void findByTrainingType_WithValidType_ShouldReturnTrainings() {
        List<Training> trainings = Collections.singletonList(testTraining);
        when(trainingStorage.findAll()).thenReturn(trainings);

        List<Training> result = trainingDAO.findByTrainingType(TrainingType.FITNESS);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTraining, result.get(0));
        verify(trainingStorage).findAll();
    }

    @Test
    void findByDateRange_WithValidDates_ShouldReturnTrainings() {
        List<Training> trainings = Collections.singletonList(testTraining);
        when(trainingStorage.findAll()).thenReturn(trainings);
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        List<Training> result = trainingDAO.findByDateRange(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTraining, result.get(0));
        verify(trainingStorage).findAll();
    }
}
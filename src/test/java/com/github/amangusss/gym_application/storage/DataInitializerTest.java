package com.github.amangusss.gym_application.storage;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.exception.DataInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private TraineeStorage traineeStorage;

    @Mock
    private TrainerStorage trainerStorage;

    @Mock
    private TrainingStorage trainingStorage;

    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer = new DataInitializer(
                traineeStorage,
                trainerStorage,
                trainingStorage,
                "bootstrap/trainees.txt",
                "bootstrap/trainers.txt",
                "bootstrap/trainings.txt"
        );
    }

    @Test
    void init_WithValidFiles_ShouldLoadDataSuccessfully() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        Training training = new Training();
        training.setId(1L);
        training.setTrainingName("Morning Workout");

        when(traineeStorage.findAll()).thenReturn(Collections.singletonList(trainee));
        when(trainerStorage.findAll()).thenReturn(Collections.singletonList(trainer));
        when(trainingStorage.findAll()).thenReturn(Collections.singletonList(training));

        assertDoesNotThrow(() -> dataInitializer.init());

        verify(traineeStorage).findAll();
        verify(trainerStorage).findAll();
        verify(trainingStorage).findAll();
    }

    @Test
    void init_WithInvalidTraineeFile_ShouldNotThrowException() {
        TraineeStorage testTraineeStorage = new TraineeStorage();
        TrainerStorage testTrainerStorage = new TrainerStorage();
        TrainingStorage testTrainingStorage = new TrainingStorage();
        
        DataInitializer invalidDataInitializer = new DataInitializer(
                testTraineeStorage,
                testTrainerStorage,
                testTrainingStorage,
                "invalid/path/trainees.txt",
                "bootstrap/trainers.txt",
                "bootstrap/trainings.txt"
        );

        assertDoesNotThrow(invalidDataInitializer::init);
        
        assertFalse(testTrainerStorage.findAll().isEmpty());
        assertFalse(testTrainingStorage.findAll().isEmpty());
    }

    @Test
    void init_WithInvalidTrainerFile_ShouldNotThrowException() {
        TraineeStorage testTraineeStorage = new TraineeStorage();
        TrainerStorage testTrainerStorage = new TrainerStorage();
        TrainingStorage testTrainingStorage = new TrainingStorage();
        
        DataInitializer invalidDataInitializer = new DataInitializer(
                testTraineeStorage,
                testTrainerStorage,
                testTrainingStorage,
                "bootstrap/trainees.txt",
                "invalid/path/trainers.txt",
                "bootstrap/trainings.txt"
        );

        assertDoesNotThrow(invalidDataInitializer::init);
        
        assertFalse(testTraineeStorage.findAll().isEmpty());
        assertFalse(testTrainingStorage.findAll().isEmpty());
    }

    @Test
    void init_WithInvalidTrainingFile_ShouldNotThrowException() {
        TraineeStorage testTraineeStorage = new TraineeStorage();
        TrainerStorage testTrainerStorage = new TrainerStorage();
        TrainingStorage testTrainingStorage = new TrainingStorage();
        
        DataInitializer invalidDataInitializer = new DataInitializer(
                testTraineeStorage,
                testTrainerStorage,
                testTrainingStorage,
                "bootstrap/trainees.txt",
                "bootstrap/trainers.txt",
                "invalid/path/trainings.txt"
        );

        assertDoesNotThrow(invalidDataInitializer::init);
        
        assertFalse(testTraineeStorage.findAll().isEmpty());
        assertFalse(testTrainerStorage.findAll().isEmpty());
    }

    @Test
    void loadTrainees_WithValidFile_ShouldLoadTrainees() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(traineeStorage.save(any(Trainee.class))).thenReturn(trainee);

        assertDoesNotThrow(() -> dataInitializer.init());

        verify(traineeStorage, atLeastOnce()).save(any(Trainee.class));
    }

    @Test
    void loadTrainers_WithValidFile_ShouldLoadTrainers() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        when(trainerStorage.save(any(Trainer.class))).thenReturn(trainer);

        assertDoesNotThrow(() -> dataInitializer.init());

        verify(trainerStorage, atLeastOnce()).save(any(Trainer.class));
    }

    @Test
    void loadTrainings_WithValidFile_ShouldLoadTrainings() {
        Training training = new Training();
        training.setId(1L);
        training.setTrainingName("Morning Workout");

        when(trainingStorage.save(any(Training.class))).thenReturn(training);

        assertDoesNotThrow(() -> dataInitializer.init());

        verify(trainingStorage, atLeastOnce()).save(any(Training.class));
    }

    @Test
    void constructor_WithValidParameters_ShouldCreateInstance() {
        DataInitializer initializer = new DataInitializer(
                traineeStorage,
                trainerStorage,
                trainingStorage,
                "bootstrap/trainees.txt",
                "bootstrap/trainers.txt",
                "bootstrap/trainings.txt"
        );

        assertNotNull(initializer);
    }

    @Test
    void constructor_WithNullStorages_ShouldCreateInstance() {
        DataInitializer initializer = new DataInitializer(
                null,
                null,
                null,
                "bootstrap/trainees.txt",
                "bootstrap/trainers.txt",
                "bootstrap/trainings.txt"
        );

        assertNotNull(initializer);
    }
}
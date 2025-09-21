package com.github.amangusss.gym_application.service.facade;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.exception.TrainingNotFoundException;
import com.github.amangusss.gym_application.service.TraineeService;
import com.github.amangusss.gym_application.service.TrainerService;
import com.github.amangusss.gym_application.service.TrainingService;
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
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    private GymFacade gymFacade;

    @BeforeEach
    void setUp() {
        gymFacade = new GymFacade(traineeService, trainerService, trainingService);
    }

    @Test
    void createTrainee_ShouldCallServiceAndReturnTrainee() {
        String firstName = "John";
        String lastName = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 5, 15);
        String address = "123 Main St";

        Trainee expectedTrainee = new Trainee();
        expectedTrainee.setId(1L);
        expectedTrainee.setFirstName(firstName);
        expectedTrainee.setLastName(lastName);
        expectedTrainee.setDateOfBirth(dateOfBirth);
        expectedTrainee.setAddress(address);

        when(traineeService.createTrainee(any(Trainee.class))).thenReturn(expectedTrainee);

        Trainee result = gymFacade.createTrainee(firstName, lastName, dateOfBirth, address);

        assertNotNull(result);
        assertEquals(expectedTrainee, result);
        verify(traineeService).createTrainee(any(Trainee.class));
    }

    @Test
    void updateTrainee_ShouldCallServiceAndReturnTrainee() {
        Long id = 1L;
        String firstName = "John";
        String lastName = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 5, 15);
        String address = "456 Oak Ave";
        boolean isActive = true;

        Trainee expectedTrainee = new Trainee();
        expectedTrainee.setId(id);
        expectedTrainee.setFirstName(firstName);
        expectedTrainee.setLastName(lastName);
        expectedTrainee.setDateOfBirth(dateOfBirth);
        expectedTrainee.setAddress(address);
        expectedTrainee.setActive(isActive);

        when(traineeService.findTraineeById(id)).thenReturn(expectedTrainee);
        when(traineeService.updateTrainee(any(Trainee.class))).thenReturn(expectedTrainee);

        Trainee result = gymFacade.updateTrainee(id, firstName, lastName, dateOfBirth, address, isActive);

        assertNotNull(result);
        assertEquals(expectedTrainee, result);
        verify(traineeService).findTraineeById(id);
        verify(traineeService).updateTrainee(any(Trainee.class));
    }

    @Test
    void deleteTrainee_ShouldCallServiceAndReturnResult() {
        Long id = 1L;
        when(traineeService.deleteTrainee(id)).thenReturn(true);

        boolean result = gymFacade.deleteTrainee(id);

        assertTrue(result);
        verify(traineeService).deleteTrainee(id);
    }

    @Test
    void findTraineeById_ShouldCallServiceAndReturnTrainee() {
        Long id = 1L;
        Trainee expectedTrainee = new Trainee();
        expectedTrainee.setId(id);
        expectedTrainee.setFirstName("John");
        expectedTrainee.setLastName("Doe");

        when(traineeService.findTraineeById(id)).thenReturn(expectedTrainee);

        Trainee result = gymFacade.findTraineeById(id);

        assertNotNull(result);
        assertEquals(expectedTrainee, result);
        verify(traineeService).findTraineeById(id);
    }

    @Test
    void findAllTrainees_ShouldCallServiceAndReturnList() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        List<Trainee> expectedTrainees = Collections.singletonList(trainee);
        when(traineeService.findAllTrainees()).thenReturn(expectedTrainees);

        List<Trainee> result = gymFacade.findAllTrainees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedTrainees, result);
        verify(traineeService).findAllTrainees();
    }

    @Test
    void createTrainer_ShouldCallServiceAndReturnTrainer() {
        String firstName = "Jane";
        String lastName = "Smith";
        TrainingType specialization = TrainingType.FITNESS;

        Trainer expectedTrainer = new Trainer();
        expectedTrainer.setId(1L);
        expectedTrainer.setFirstName(firstName);
        expectedTrainer.setLastName(lastName);
        expectedTrainer.setSpecialization(specialization);

        when(trainerService.createTrainer(any(Trainer.class))).thenReturn(expectedTrainer);
        Trainer result = gymFacade.createTrainer(firstName, lastName, specialization);

        assertNotNull(result);
        assertEquals(expectedTrainer, result);
        verify(trainerService).createTrainer(any(Trainer.class));
    }

    @Test
    void updateTrainer_ShouldCallServiceAndReturnTrainer() {
        Long id = 1L;
        String firstName = "Jane";
        String lastName = "Smith";
        TrainingType specialization = TrainingType.YOGA;
        boolean isActive = true;

        Trainer expectedTrainer = new Trainer();
        expectedTrainer.setId(id);
        expectedTrainer.setFirstName(firstName);
        expectedTrainer.setLastName(lastName);
        expectedTrainer.setSpecialization(specialization);
        expectedTrainer.setActive(isActive);

        when(trainerService.findTrainerById(id)).thenReturn(expectedTrainer);
        when(trainerService.updateTrainer(any(Trainer.class))).thenReturn(expectedTrainer);

        Trainer result = gymFacade.updateTrainer(id, firstName, lastName, specialization, isActive);

        assertNotNull(result);
        assertEquals(expectedTrainer, result);
        verify(trainerService).findTrainerById(id);
        verify(trainerService).updateTrainer(any(Trainer.class));
    }

    @Test
    void findTrainerById_ShouldCallServiceAndReturnTrainer() {
        Long id = 1L;
        Trainer expectedTrainer = new Trainer();
        expectedTrainer.setId(id);
        expectedTrainer.setFirstName("Jane");
        expectedTrainer.setLastName("Smith");

        when(trainerService.findTrainerById(id)).thenReturn(expectedTrainer);
        Trainer result = gymFacade.findTrainerById(id);

        assertNotNull(result);
        assertEquals(expectedTrainer, result);
        verify(trainerService).findTrainerById(id);
    }

    @Test
    void findAllTrainers_ShouldCallServiceAndReturnList() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        List<Trainer> expectedTrainers = Collections.singletonList(trainer);
        when(trainerService.findAllTrainers()).thenReturn(expectedTrainers);
        List<Trainer> result = gymFacade.findAllTrainers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedTrainers, result);
        verify(trainerService).findAllTrainers();
    }

    @Test
    void createTraining_ShouldCallServiceAndReturnTraining() {
        Long traineeId = 1L;
        Long trainerId = 2L;
        String trainingName = "Morning Workout";
        TrainingType trainingType = TrainingType.FITNESS;
        LocalDate trainingDate = LocalDate.now();
        Integer trainingDuration = 60;

        Training expectedTraining = new Training();
        expectedTraining.setId(1L);
        expectedTraining.setTraineeId(traineeId);
        expectedTraining.setTrainerId(trainerId);
        expectedTraining.setTrainingName(trainingName);
        expectedTraining.setTrainingType(trainingType);
        expectedTraining.setTrainingDate(trainingDate);
        expectedTraining.setTrainingDuration(trainingDuration);

        when(trainingService.createTraining(any(Training.class))).thenReturn(expectedTraining);
        Training result = gymFacade.createTraining(traineeId, trainerId, trainingName, trainingType, trainingDate, trainingDuration);

        assertNotNull(result);
        assertEquals(expectedTraining, result);
        verify(trainingService).createTraining(any(Training.class));
    }

    @Test
    void findTrainingById_WithExistingTraining_ShouldReturnTraining() {
        Long trainingId = 1L;
        Training expectedTraining = new Training();
        expectedTraining.setId(trainingId);
        expectedTraining.setTrainingName("Morning Workout");

        when(trainingService.findTraining(trainingId)).thenReturn(expectedTraining);
        Training result = gymFacade.findTrainingById(trainingId);

        assertNotNull(result);
        assertEquals(expectedTraining, result);
        verify(trainingService).findTraining(trainingId);
    }

    @Test
    void findTrainingById_WithNonExistingTraining_ShouldThrowException() {
        Long trainingId = 999L;
        when(trainingService.findTraining(trainingId)).thenReturn(null);

        assertThrows(TrainingNotFoundException.class, () -> gymFacade.findTrainingById(trainingId));
        verify(trainingService).findTraining(trainingId);
    }

    @Test
    void findAllTrainings_ShouldCallServiceAndReturnList() {
        Training training = new Training();
        training.setId(1L);
        training.setTrainingName("Morning Workout");

        List<Training> expectedTrainings = Collections.singletonList(training);
        when(trainingService.findAllTrainings()).thenReturn(expectedTrainings);

        List<Training> result = gymFacade.findAllTrainings();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedTrainings, result);
        verify(trainingService).findAllTrainings();
    }
}
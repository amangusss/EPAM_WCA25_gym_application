package com.github.amangusss.gym_application.service.facade;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.service.TraineeService;
import com.github.amangusss.gym_application.service.TrainerService;
import com.github.amangusss.gym_application.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private static final Logger logger = LoggerFactory.getLogger(GymFacade.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    // Trainee

    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        logger.info("Creating trainee: {} {}", firstName, lastName);

        Trainee trainee = Trainee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .address(address)
                .build();
        
        Trainee createdTrainee = traineeService.createTrainee(trainee);
        logger.info("Trainee created with username: {} and password: {}",
                createdTrainee.getUsername(), createdTrainee.getPassword());

        return createdTrainee;
    }

    public Trainee findTraineeByUsername(String username, String password) {
        logger.info("Finding trainee by username: {}", username);
        return traineeService.findTraineeByUsername(username, password);
    }

    public Trainee updateTrainee(String username, String password, String firstName, String lastName,
                                 LocalDate dateOfBirth, String address) {
        logger.info("Updating trainee: {}", username);

        Trainee trainee = Trainee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .address(address)
                .build();

        return traineeService.updateTrainee(username, password, trainee);
    }

    public void deleteTrainee(String username, String password) {
        logger.info("Deleting trainee: {}", username);
        traineeService.deleteTraineeByUsername(username, password);
    }

    public boolean authenticateTrainee(String username, String password) {
        logger.info("Authenticating trainee: {}", username);
        return traineeService.authenticateTrainee(username, password);
    }

    public Trainee changeTraineePassword(String username, String oldPassword, String newPassword) {
        logger.info("Changing password for trainee: {}", username);
        return traineeService.changeTraineePassword(username, oldPassword, newPassword);
    }

    public Trainee activateTrainee(String username, String password) {
        logger.info("Activating trainee: {}", username);
        return traineeService.activateTrainee(username, password);
    }

    public Trainee deactivateTrainee(String username, String password) {
        logger.info("Deactivating trainee: {}", username);
        return traineeService.deactivateTrainee(username, password);
    }

    public List<Training> getTraineeTrainings(String username, String password, LocalDate fromDate, LocalDate toDate,
                                               String trainerName, TrainingType trainingType) {
        logger.info("Getting trainings for trainee: {}", username);
        return traineeService.getTraineeTrainingsList(username, password, fromDate, toDate, trainerName, trainingType);
    }

    public List<Trainer> getUnassignedTrainers(String traineeUsername, String traineePassword) {
        logger.info("Getting unassigned trainers for trainee: {}", traineeUsername);
        return traineeService.getTrainersNotAssignedToTrainee(traineeUsername, traineePassword);
    }

    public Trainee updateTraineeTrainersList(String username, String password, Set<Trainer> trainers) {
        logger.info("Updating trainers list for trainee: {}", username);
        return traineeService.updateTraineeTrainersList(username, password, trainers);
    }

    // Trainer
    public Trainer createTrainer(String firstName, String lastName, TrainingType specialization) {
        logger.info("Creating trainer: {} {}", firstName, lastName);

        Trainer trainer = Trainer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization)
                .build();

        Trainer createdTrainer = trainerService.createTrainer(trainer);
        logger.info("Trainer created with username: {} and password: {}",
                createdTrainer.getUsername(), createdTrainer.getPassword());

        return createdTrainer;
    }

    public Trainer updateTrainer(String username, String password, String firstName, String lastName,
                                 TrainingType specialization) {
        logger.info("Updating trainer: {}", username);

        Trainer trainer = Trainer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization)
                .build();

        return trainerService.updateTrainer(username, password, trainer);
    }

    public boolean authenticateTrainer(String username, String password) {
        logger.info("Authenticating trainer: {}", username);
        return trainerService.authenticateTrainer(username, password);
    }

    public Trainer changeTrainerPassword(String username, String oldPassword, String newPassword) {
        logger.info("Changing password for trainer: {}", username);
        return trainerService.changeTrainerPassword(username, oldPassword, newPassword);
    }

    public Trainer activateTrainer(String username, String password) {
        logger.info("Activating trainer: {}", username);
        return trainerService.activateTrainer(username, password);
    }

    public Trainer deactivateTrainer(String username, String password) {
        logger.info("Deactivating trainer: {}", username);
        return trainerService.deactivateTrainer(username, password);
    }

    public List<Training> getTrainerTrainings(String username, String password, LocalDate fromDate, LocalDate toDate,
                                               String traineeName) {
        logger.info("Getting trainings for trainer: {}", username);
        return trainerService.getTrainerTrainingsList(username, password, fromDate, toDate, traineeName);
    }

    // Training
    public Training addTraining(Training training) {
        logger.info("Adding training: {}", training.getTrainingName());
        return trainingService.addTraining(training);
    }
}

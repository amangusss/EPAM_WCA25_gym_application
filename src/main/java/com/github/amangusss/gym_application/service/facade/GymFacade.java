package com.github.amangusss.gym_application.service.facade;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainee.TraineeBuilder;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.trainer.TrainerBuilder;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.training.TrainingBuilder;
import com.github.amangusss.gym_application.exception.TraineeNotFoundException;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;
import com.github.amangusss.gym_application.exception.TrainingNotFoundException;
import com.github.amangusss.gym_application.service.TraineeService;
import com.github.amangusss.gym_application.service.TrainerService;
import com.github.amangusss.gym_application.service.TrainingService;
import com.github.amangusss.gym_application.util.constants.ConfigConstants;
import com.github.amangusss.gym_application.util.constants.LoggerConstants;
import com.github.amangusss.gym_application.util.constants.ValidationConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component(ConfigConstants.BEAN_GYM_FACADE)
public class GymFacade {

    public static final Logger logger = LoggerFactory.getLogger(GymFacade.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        logger.info(LoggerConstants.FACADE_INITIALIZED);
    }

    //Trainee
    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        logger.debug(LoggerConstants.DEBUG_CREATING_TRAINEE, 
                firstName, lastName, dateOfBirth, address);
        
        Trainee trainee = TraineeBuilder.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .address(address)
                .build();
        
        logger.debug(LoggerConstants.DEBUG_BUILT_TRAINEE, trainee);
        
        Trainee savedTrainee = traineeService.createTrainee(trainee);
        logger.debug(LoggerConstants.DEBUG_TRAINEE_CREATED, savedTrainee.getId());
        
        return savedTrainee;
    }

    public Trainee updateTrainee(Long id, String firstName, String lastName, LocalDate dateOfBirth, String address, Boolean isActive) {
        Trainee trainee = traineeService.findTraineeById(id);
        if (trainee == null) {
            throw new TraineeNotFoundException(String.format(ValidationConstants.TRAINEE_NOT_FOUND_BY_ID, id));
        }

        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.setActive(isActive);

        return traineeService.updateTrainee(trainee);
    }

    public boolean deleteTrainee(Long traineeId) {
        return traineeService.deleteTrainee(traineeId);
    }

    public Trainee findTraineeById(Long traineeId) {
        return traineeService.findTraineeById(traineeId);
    }

    public List<Trainee> findAllTrainees() {
        return traineeService.findAllTrainees();
    }

    //Trainer
    public Trainer createTrainer(String firstName, String lastName, TrainingType specialization) {
        Trainer trainer = TrainerBuilder.builder()
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization)
                .build();
        return trainerService.createTrainer(trainer);
    }

    public Trainer updateTrainer(Long id, String firstName, String lastName, TrainingType specialization, Boolean isActive) {
        Trainer trainer = trainerService.findTrainerById(id);
        if (trainer == null) {
            throw new TrainerNotFoundException(String.format(ValidationConstants.TRAINER_NOT_FOUND_BY_ID, id));
        }

        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainer.setActive(isActive);

        return trainerService.updateTrainer(trainer);
    }

    public Trainer findTrainerById(Long trainerId) {
        return trainerService.findTrainerById(trainerId);
    }

    public List<Trainer> findAllTrainers() {
        return trainerService.findAllTrainers();
    }

    //Training
    public Training createTraining(Long traineeId, Long trainerId, String trainingName,
                                   TrainingType trainingType, LocalDate trainingDate, Integer trainingDuration) {
        Training training = TrainingBuilder.builder()
                .traineeId(traineeId)
                .trainerId(trainerId)
                .trainingName(trainingName)
                .trainingType(trainingType)
                .trainingDate(trainingDate)
                .trainingDuration(trainingDuration)
                .build();
        return trainingService.createTraining(training);
    }

    public Training findTrainingById(Long trainingId) {
        Training training = trainingService.findTraining(trainingId);
        if (training == null) {
            throw new TrainingNotFoundException(String.format(ValidationConstants.TRAINING_NOT_FOUND_BY_ID, trainingId));
        }
        return training;
    }

    public List<Training> findAllTrainings() {
        return trainingService.findAllTrainings();
    }
}

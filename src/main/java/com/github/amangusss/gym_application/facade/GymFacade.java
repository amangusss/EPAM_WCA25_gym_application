package com.github.amangusss.gym_application.facade;

import com.github.amangusss.gym_application.entity.Trainee;
import com.github.amangusss.gym_application.entity.Trainer;
import com.github.amangusss.gym_application.entity.Training;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.service.TraineeService;
import com.github.amangusss.gym_application.service.TrainerService;
import com.github.amangusss.gym_application.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
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
        logger.info("Gym facade initialized with all services");
    }

    //Trainee
    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        Trainee trainee = new Trainee(firstName, lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        return traineeService.createTrainee(trainee);
    }

    public Trainee updateTrainee(Long id, String firstName, String lastName, LocalDate dateOfBirth, String address, Boolean isActive) {
        Trainee trainee = traineeService.findTraineeById(id);
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee with id " + id + " not found");
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
        Trainer trainer = new Trainer(firstName, lastName, specialization);
        return trainerService.createTrainer(trainer);
    }

    public Trainer updateTrainer(Long id, String firstName, String lastName, TrainingType specialization, Boolean isActive) {
        Trainer trainer = trainerService.findTrainerById(id);
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer with id " + id + " not found");
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
        Training training = new Training(traineeId, trainerId, trainingName, trainingType, trainingDate, trainingDuration);
        return trainingService.createTraining(training);
    }

    public Training findTrainingById(Long trainingId) {
        return trainingService.findTraining(trainingId);
    }

    public List<Training> findAllTrainings() {
        return trainingService.findAllTrainings();
    }
}

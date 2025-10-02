package com.github.amangusss.gym_application;

import com.github.amangusss.gym_application.config.GymApplicationConfig;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.service.facade.GymFacade;
import com.github.amangusss.gym_application.util.constants.ConfigConstants;
import com.github.amangusss.gym_application.util.constants.LoggerConstants;
import com.github.amangusss.gym_application.util.constants.DemoConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;

public class GymApplication {

    public static final Logger logger = LoggerFactory.getLogger(GymApplication.class);

    public static void main(String[] args) {
        logger.info(LoggerConstants.APP_STARTING);
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(GymApplicationConfig.class)) {
            logger.info(LoggerConstants.APP_CONTEXT_INITIALIZED);

            demoApplication(context);

            logger.info(LoggerConstants.APP_FINISHED);
        } catch (Exception e) {
            logger.error(LoggerConstants.APP_FAILED, e);
            System.exit(ConfigConstants.SYSTEM_EXIT_ERROR);
        }
    }

    private static void demoApplication(AnnotationConfigApplicationContext context) {
        logger.info(LoggerConstants.DEMO_STARTED);

        GymFacade gymFacade = context.getBean(GymFacade.class);

        demoTraineeOperations(gymFacade);
        demoTrainerOperations(gymFacade);
        demoTrainingOperations(gymFacade);
        demoDeleteOperations(gymFacade);
    }

    private static void demoTraineeOperations(GymFacade gymFacade) {
        logger.info(LoggerConstants.DEMO_TRAINEE_STARTED);

        Trainee savedTrainee = gymFacade.createTrainee(
                DemoConstants.TRAINEE_FIRST_NAME, 
                DemoConstants.TRAINEE_LAST_NAME, 
                LocalDate.of(DemoConstants.TRAINEE_BIRTH_YEAR, DemoConstants.TRAINEE_BIRTH_MONTH, DemoConstants.TRAINEE_BIRTH_DAY), 
                DemoConstants.TRAINEE_ADDRESS_1
        );
        logger.info(LoggerConstants.TRAINEE_CREATED_DETAILS,
                savedTrainee.getFirstName() + " " + savedTrainee.getLastName(),
                savedTrainee.getUsername(),
                savedTrainee.getPassword());

        Trainee updatedTrainee = gymFacade.updateTrainee(
                savedTrainee.getId(),
                savedTrainee.getFirstName(),
                savedTrainee.getLastName(),
                savedTrainee.getDateOfBirth(),
                DemoConstants.TRAINEE_ADDRESS_2,
                true
        );
        logger.info(LoggerConstants.TRAINEE_UPDATED_ADDRESS, updatedTrainee.getAddress());

        List<Trainee> trainees = gymFacade.findAllTrainees();
        logger.info(LoggerConstants.TRAINEES_FOUND, trainees.size());
        
        if (!trainees.isEmpty()) {
            Trainee foundTrainee = gymFacade.findTraineeById(trainees.get(0).getId());
            if (foundTrainee != null) {
                logger.info(LoggerConstants.DEMO_FOUND_TRAINEE_BY_ID, foundTrainee.getFirstName(), foundTrainee.getLastName());
            }
        }
    }

    private static void demoTrainerOperations(GymFacade gymFacade) {
        logger.info(LoggerConstants.DEMO_TRAINER_STARTED);

        Trainer savedTrainer = gymFacade.createTrainer(
                DemoConstants.TRAINER_FIRST_NAME, 
                DemoConstants.TRAINER_LAST_NAME, 
                TrainingType.STRETCHING
        );
        logger.info(LoggerConstants.TRAINER_CREATED_DETAILS,
                savedTrainer.getFirstName() + " " + savedTrainer.getLastName(),
                savedTrainer.getUsername(),
                savedTrainer.getPassword(),
                savedTrainer.getSpecialization());

        List<Trainer> trainers = gymFacade.findAllTrainers();
        logger.info(LoggerConstants.TRAINERS_FOUND, trainers.size());
        
        if (!trainers.isEmpty()) {
            Trainer trainerToUpdate = trainers.get(0);
            Trainer updatedTrainer = gymFacade.updateTrainer(
                    trainerToUpdate.getId(),
                    trainerToUpdate.getFirstName(),
                    trainerToUpdate.getLastName(),
                    TrainingType.FITNESS,
                    true
            );
            logger.info(LoggerConstants.DEMO_UPDATED_TRAINER_SPECIALIZATION, updatedTrainer.getSpecialization());
        }
        
        if (!trainers.isEmpty()) {
            Trainer foundTrainer = gymFacade.findTrainerById(trainers.get(0).getId());
            if (foundTrainer != null) {
                logger.info(LoggerConstants.DEMO_FOUND_TRAINER_BY_ID, 
                        foundTrainer.getFirstName(), 
                        foundTrainer.getLastName(),
                        foundTrainer.getSpecialization());
            }
        }
    }

    private static void demoTrainingOperations(GymFacade gymFacade) {
        logger.info(LoggerConstants.DEMO_TRAINING_STARTED);

        List<Trainee> trainees = gymFacade.findAllTrainees();
        List<Trainer> trainers = gymFacade.findAllTrainers();

        if (!trainees.isEmpty() && !trainers.isEmpty()) {
            Trainee trainee = trainees.get(0);
            Trainer trainer = trainers.stream()
                    .filter(t -> t.getSpecialization().equals(TrainingType.STRETCHING))
                    .findFirst()
                    .orElse(trainers.get(0));

            Training savedTraining = gymFacade.createTraining(
                    trainee.getId(),
                    trainer.getId(),
                    DemoConstants.TRAINING_NAME,
                    trainer.getSpecialization(),
                    LocalDate.now().plusDays(DemoConstants.TRAINING_DAYS_FROM_NOW),
                    DemoConstants.TRAINING_DURATION
            );
            logger.info(LoggerConstants.TRAINING_CREATED_DETAILS,
                    savedTraining.getTrainingName(),
                    savedTraining.getTrainingDuration(),
                    savedTraining.getTrainingDate());

            List<Training> trainings = gymFacade.findAllTrainings();
            logger.info(LoggerConstants.TRAININGS_FOUND, trainings.size());
            
            if (!trainings.isEmpty()) {
                Training foundTraining = gymFacade.findTrainingById(trainings.get(0).getId());
                if (foundTraining != null) {
                    logger.info(LoggerConstants.DEMO_FOUND_TRAINING_BY_ID, 
                            foundTraining.getTrainingName(),
                            foundTraining.getTrainingDuration(),
                            foundTraining.getTrainingDate());
                }
            }
        }
    }
    
    private static void demoDeleteOperations(GymFacade gymFacade) {
        logger.info(LoggerConstants.DEMO_DELETE_STARTED);
        
        Trainee traineeToDelete = gymFacade.createTrainee(
                DemoConstants.DEMO_DELETE_TRAINEE_FIRST_NAME,
                DemoConstants.DEMO_DELETE_TRAINEE_LAST_NAME,
                LocalDate.of(DemoConstants.DEMO_DELETE_TRAINEE_BIRTH_YEAR, 
                           DemoConstants.DEMO_DELETE_TRAINEE_BIRTH_MONTH, 
                           DemoConstants.DEMO_DELETE_TRAINEE_BIRTH_DAY),
                DemoConstants.DEMO_DELETE_TRAINEE_ADDRESS
        );
        logger.info(LoggerConstants.DEMO_TRAINEE_CREATED_FOR_DELETION, 
                traineeToDelete.getFirstName(), 
                traineeToDelete.getLastName());
        
        boolean deleted = gymFacade.deleteTrainee(traineeToDelete.getId());
        if (deleted) {
            logger.info(LoggerConstants.DEMO_TRAINEE_DELETED_SUCCESS, traineeToDelete.getId());
        } else {
            logger.warn(LoggerConstants.DEMO_TRAINEE_DELETE_FAILED, traineeToDelete.getId());
        }
        
        Trainee foundTrainee = gymFacade.findTraineeById(traineeToDelete.getId());
        if (foundTrainee == null) {
            logger.info(LoggerConstants.DEMO_TRAINEE_CONFIRMED_DELETED, traineeToDelete.getId());
        } else {
            logger.warn(LoggerConstants.DEMO_TRAINEE_STILL_EXISTS, traineeToDelete.getId());
        }
        
        List<Trainee> finalTrainees = gymFacade.findAllTrainees();
        List<Trainer> finalTrainers = gymFacade.findAllTrainers();
        List<Training> finalTrainings = gymFacade.findAllTrainings();
        
        logger.info(LoggerConstants.DEMO_FINAL_STATISTICS, 
                finalTrainees.size(), finalTrainers.size(), finalTrainings.size());
    }
}
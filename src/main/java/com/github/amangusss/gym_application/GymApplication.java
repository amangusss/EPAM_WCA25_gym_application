package com.github.amangusss.gym_application;

import com.github.amangusss.gym_application.config.GymApplicationConfig;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.service.facade.GymFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GymApplication {

    private static final Logger logger = LoggerFactory.getLogger(GymApplication.class);

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(GymApplicationConfig.class)) {
            logger.info("Application context initialized successfully");

            GymFacade gymFacade = context.getBean(GymFacade.class);
            demonstrateFullFunctionality(gymFacade);
        } catch (Exception e) {
            logger.error("Application failed with error", e);
            System.exit(1);
        }
    }

    private static void demonstrateFullFunctionality(GymFacade gymFacade) {
        logger.info("Demo started...");

        Trainee trainee = demoCreateTrainee(gymFacade);
        Trainer trainer = demoCreateTrainer(gymFacade);

        demoAuthentication(gymFacade, trainee, trainer);
        demoUpdateProfiles(gymFacade, trainee, trainer);
        demoPasswordChange(gymFacade, trainee, trainer);
        demoAddTraining(gymFacade, trainee, trainer);
        demoGetTrainings(gymFacade, trainee, trainer);
        demoGetUnassignedTrainers(gymFacade, trainee);
        demoUpdateTrainersList(gymFacade, trainee, trainer);
        demoActivateDeactivate(gymFacade, trainee, trainer);
        demoDeleteTrainee(gymFacade, trainee);

        logger.info("Demo completed.");
    }

    private static Trainee demoCreateTrainee(GymFacade gymFacade) {
        Trainee trainee = gymFacade.createTrainee(
                "Aman",
                "Nazarkulov",
                LocalDate.of(2004, 2, 14),
                "Isakeev st. 18/10 Block 15"
        );

        logger.info("Trainee created: {} {} (username: {}, password: {})",
                trainee.getFirstName(), trainee.getLastName(),
                trainee.getUsername(), trainee.getPassword());

        return trainee;
    }

    private static Trainer demoCreateTrainer(GymFacade gymFacade) {
        Trainer trainer = gymFacade.createTrainer(
                "Dastan",
                "Ibraimov",
                TrainingType.FITNESS
        );

        logger.info("Trainer created: {} {} (username: {}, password: {}, specialization: {})",
                trainer.getFirstName(), trainer.getLastName(),
                trainer.getUsername(), trainer.getPassword(), trainer.getSpecialization());

        return trainer;
    }

    private static void demoAuthentication(GymFacade gymFacade, Trainee trainee, Trainer trainer) {
        boolean traineeAuth = gymFacade.authenticateTrainee(trainee.getUsername(), trainee.getPassword());
        logger.info("Trainee authentication: {}", traineeAuth ? "success" : "failed");

        boolean trainerAuth = gymFacade.authenticateTrainer(trainer.getUsername(), trainer.getPassword());
        logger.info("Trainer authentication: {}", trainerAuth ? "success" : "failed");

        boolean invalidAuth = gymFacade.authenticateTrainee(trainee.getUsername(), "wrong_password");
        logger.info("Invalid authentication test: {}", invalidAuth ? "failed (should be false)" : "success");
    }

    private static void demoUpdateProfiles(GymFacade gymFacade, Trainee trainee, Trainer trainer) {
        Trainee updatedTrainee = gymFacade.updateTrainee(
                trainee.getUsername(),
                trainee.getPassword(),
                "Aman",
                "Nazarkulov",
                LocalDate.of(2004, 2, 14),
                "Erkindik boulevard 8"
        );
        logger.info("Trainee profile updated. New address: {}", updatedTrainee.getAddress());

        Trainer updatedTrainer = gymFacade.updateTrainer(
                trainer.getUsername(),
                trainer.getPassword(),
                "Dastan",
                "Ibraimov",
                TrainingType.YOGA
        );
        logger.info("Trainer profile updated. New specialization: {}", updatedTrainer.getSpecialization());

        trainer.setSpecialization(updatedTrainer.getSpecialization());
    }

    private static void demoPasswordChange(GymFacade gymFacade, Trainee trainee, Trainer trainer) {
        String oldTraineePassword = trainee.getPassword();
        String newTraineePassword = "newPassword123";

        Trainee traineeWithNewPassword = gymFacade.changeTraineePassword(
                trainee.getUsername(),
                oldTraineePassword,
                newTraineePassword
        );
        trainee.setPassword(newTraineePassword);
        logger.info("Trainee password changed successfully. Username: {}", traineeWithNewPassword.getUsername());

        String oldTrainerPassword = trainer.getPassword();
        String newTrainerPassword = "trainerNewPass456";

        Trainer trainerWithNewPassword = gymFacade.changeTrainerPassword(
                trainer.getUsername(),
                oldTrainerPassword,
                newTrainerPassword
        );
        trainer.setPassword(newTrainerPassword);
        logger.info("Trainer password changed successfully. Username: {}", trainerWithNewPassword.getUsername());
    }

    private static void demoAddTraining(GymFacade gymFacade, Trainee trainee, Trainer trainer) {
        Training training = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Morning Yoga Session")
                .trainingType(trainer.getSpecialization())
                .trainingDate(LocalDate.now().plusDays(7))
                .trainingDuration(60)
                .build();

        Training savedTraining = gymFacade.addTraining(training);
        logger.info("Training added: {} (duration: {} min, date: {})",
                savedTraining.getTrainingName(),
                savedTraining.getTrainingDuration(),
                savedTraining.getTrainingDate());
    }

    private static void demoGetTrainings(GymFacade gymFacade, Trainee trainee, Trainer trainer) {
        List<Training> traineeTrainings = gymFacade.getTraineeTrainings(
                trainee.getUsername(),
                trainee.getPassword(),
                null,
                null,
                trainer.getFirstName() + " " + trainer.getLastName(),
                null
        );
        logger.info("Found {} trainings for trainee {}", traineeTrainings.size(), trainee.getUsername());

        List<Training> trainerTrainings = gymFacade.getTrainerTrainings(
                trainer.getUsername(),
                trainer.getPassword(),
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                trainee.getFirstName()
        );
        logger.info("Found {} trainings for trainer {}", trainerTrainings.size(), trainer.getUsername());
    }

    private static void demoGetUnassignedTrainers(GymFacade gymFacade, Trainee trainee) {
        List<Trainer> unassignedTrainers = gymFacade.getUnassignedTrainers(
                trainee.getUsername(),
                trainee.getPassword()
        );
        logger.info("Found {} unassigned trainers for trainee {}",
                unassignedTrainers.size(), trainee.getUsername());
    }

    private static void demoUpdateTrainersList(GymFacade gymFacade, Trainee trainee, Trainer trainer) {
        Set<Trainer> trainers = new HashSet<>();
        trainers.add(trainer);

        Trainee updatedTrainee = gymFacade.updateTraineeTrainersList(
                trainee.getUsername(),
                trainee.getPassword(),
                trainers
        );
        logger.info("Updated trainers list for trainee {}. Assigned trainers: {}",
                updatedTrainee.getUsername(), trainers.size());
    }

    private static void demoActivateDeactivate(GymFacade gymFacade, Trainee trainee, Trainer trainer) {
        Trainee deactivatedTrainee = gymFacade.deactivateTrainee(
                trainee.getUsername(),
                trainee.getPassword()
        );
        logger.info("Trainee {} deactivated. isActive: {}",
                trainee.getUsername(), deactivatedTrainee.isActive());

        Trainee activatedTrainee = gymFacade.activateTrainee(
                trainee.getUsername(),
                trainee.getPassword()
        );
        logger.info("Trainee {} activated. isActive: {}",
                trainee.getUsername(), activatedTrainee.isActive());

        try {
            gymFacade.activateTrainee(trainee.getUsername(), trainee.getPassword());
            logger.warn("Activate/Deactivate is IDEMPOTENT (should not be!)");
        } catch (Exception e) {
            logger.info("Second activation correctly rejected (NOT idempotent): {}", e.getMessage());
        }
        
        Trainer deactivatedTrainer = gymFacade.deactivateTrainer(
                trainer.getUsername(),
                trainer.getPassword()
        );
        logger.info("Trainer {} deactivated. isActive: {}",
                trainer.getUsername(), deactivatedTrainer.isActive());

        Trainer activatedTrainer = gymFacade.activateTrainer(
                trainer.getUsername(),
                trainer.getPassword()
        );
        logger.info("Trainer {} activated. isActive: {}",
                trainer.getUsername(), activatedTrainer.isActive());
    }

    private static void demoDeleteTrainee(GymFacade gymFacade, Trainee trainee) {
        logger.info("Deleting trainee: {}", trainee.getUsername());
        gymFacade.deleteTrainee(trainee.getUsername(), trainee.getPassword());
        logger.info("Trainee {} deleted with cascade deletion of related trainings", trainee.getUsername());

        try {
            Trainee deletedTrainee = gymFacade.findTraineeByUsername(trainee.getUsername(), trainee.getPassword());
            logger.warn("Trainee still exists after deletion: {}", deletedTrainee.getUsername());
        } catch (Exception e) {
            logger.info("Confirmed: Trainee {} no longer exists", trainee.getUsername());
        }
    }
}
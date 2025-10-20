package com.github.amangusss.gym_application;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.repository.TrainingTypeRepository;
import com.github.amangusss.gym_application.service.facade.GymFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class GymApplication {

    private static final Logger logger = LoggerFactory.getLogger(GymApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GymApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(GymFacade gymFacade, TrainingTypeRepository trainingTypeRepository) {
        return args -> {
            try {
                logger.info("Application started successfully");
                demonstrateFullFunctionality(gymFacade, trainingTypeRepository);
            } catch (Exception e) {
                logger.error("Application failed with error", e);
            }
        };
    }

    private static void demonstrateFullFunctionality(GymFacade gymFacade, TrainingTypeRepository trainingTypeRepository) {
        logger.info("Demo started...");

        Trainee trainee = demoCreateTrainee(gymFacade);
        Trainer trainer = demoCreateTrainer(gymFacade, trainingTypeRepository);

        demoAuthentication(gymFacade, trainee, trainer);
        demoUpdateProfiles(gymFacade, trainee, trainer, trainingTypeRepository);
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
                trainee.getUser().getFirstName(), trainee.getUser().getLastName(),
                trainee.getUser().getUsername(), trainee.getUser().getPassword());

        return trainee;
    }

    private static Trainer demoCreateTrainer(GymFacade gymFacade, TrainingTypeRepository trainingTypeRepository) {
        TrainingType fitnessType = trainingTypeRepository.findByTypeName("FITNESS")
                .orElseThrow(() -> new RuntimeException("TrainingType 'FITNESS' not found in database. Please add it first."));

        Trainer trainer = gymFacade.createTrainer(
                "Dastan",
                "Ibraimov",
                fitnessType
        );

        logger.info("Trainer created: {} {} (username: {}, password: {}, specialization: {})",
                trainer.getUser().getFirstName(), trainer.getUser().getLastName(),
                trainer.getUser().getUsername(), trainer.getUser().getPassword(),
                trainer.getSpecialization().getTypeName());

        return trainer;
    }

    private static void demoAuthentication(GymFacade gymFacade, Trainee trainee, Trainer trainer) {
        boolean traineeAuth = gymFacade.authenticateTrainee(trainee.getUser().getUsername(), trainee.getUser().getPassword());
        logger.info("Trainee authentication: {}", traineeAuth ? "success" : "failed");

        boolean trainerAuth = gymFacade.authenticateTrainer(trainer.getUser().getUsername(), trainer.getUser().getPassword());
        logger.info("Trainer authentication: {}", trainerAuth ? "success" : "failed");

        boolean invalidAuth = gymFacade.authenticateTrainee(trainee.getUser().getUsername(), "wrong_password");
        logger.info("Invalid authentication test: {}", invalidAuth ? "failed (should be false)" : "success");
    }

    private static void demoUpdateProfiles(GymFacade gymFacade, Trainee trainee, Trainer trainer, TrainingTypeRepository trainingTypeRepository) {
        Trainee updatedTrainee = gymFacade.updateTrainee(
                trainee.getUser().getUsername(),
                trainee.getUser().getPassword(),
                "Aman",
                "Nazarkulov",
                LocalDate.of(2004, 2, 14),
                "Erkindik boulevard 8"
        );
        logger.info("Trainee profile updated. New address: {}", updatedTrainee.getAddress());

        TrainingType yogaType = trainingTypeRepository.findByTypeName("YOGA")
                .orElseThrow(() -> new RuntimeException("TrainingType 'YOGA' not found in database. Please add it first."));

        Trainer updatedTrainer = gymFacade.updateTrainer(
                trainer.getUser().getUsername(),
                trainer.getUser().getPassword(),
                "Dastan",
                "Ibraimov",
                yogaType
        );
        logger.info("Trainer profile updated. New specialization: {}", updatedTrainer.getSpecialization().getTypeName());

        trainer.setSpecialization(updatedTrainer.getSpecialization());
    }

    private static void demoPasswordChange(GymFacade gymFacade, Trainee trainee, Trainer trainer) {
        String oldTraineePassword = trainee.getUser().getPassword();
        String newTraineePassword = "newPassword123";

        Trainee traineeWithNewPassword = gymFacade.changeTraineePassword(
                trainee.getUser().getUsername(),
                oldTraineePassword,
                newTraineePassword
        );
        trainee.getUser().setPassword(newTraineePassword);
        logger.info("Trainee password changed successfully. Username: {}", traineeWithNewPassword.getUser().getUsername());

        String oldTrainerPassword = trainer.getUser().getPassword();
        String newTrainerPassword = "trainerNewPass456";

        Trainer trainerWithNewPassword = gymFacade.changeTrainerPassword(
                trainer.getUser().getUsername(),
                oldTrainerPassword,
                newTrainerPassword
        );
        trainer.getUser().setPassword(newTrainerPassword);
        logger.info("Trainer password changed successfully. Username: {}", trainerWithNewPassword.getUser().getUsername());
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
                trainee.getUser().getUsername(),
                trainee.getUser().getPassword(),
                null,
                null,
                trainer.getUser().getFirstName() + " " + trainer.getUser().getLastName(),
                null
        );
        logger.info("Found {} trainings for trainee {}", traineeTrainings.size(), trainee.getUser().getUsername());

        List<Training> trainerTrainings = gymFacade.getTrainerTrainings(
                trainer.getUser().getUsername(),
                trainer.getUser().getPassword(),
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                trainee.getUser().getFirstName()
        );
        logger.info("Found {} trainings for trainer {}", trainerTrainings.size(), trainer.getUser().getUsername());
    }

    private static void demoGetUnassignedTrainers(GymFacade gymFacade, Trainee trainee) {
        List<Trainer> unassignedTrainers = gymFacade.getUnassignedTrainers(
                trainee.getUser().getUsername(),
                trainee.getUser().getPassword()
        );
        logger.info("Found {} unassigned trainers for trainee {}",
                unassignedTrainers.size(), trainee.getUser().getUsername());
    }

    private static void demoUpdateTrainersList(GymFacade gymFacade, Trainee trainee, Trainer trainer) {
        Set<Trainer> trainers = new HashSet<>();
        trainers.add(trainer);

        Trainee updatedTrainee = gymFacade.updateTraineeTrainersList(
                trainee.getUser().getUsername(),
                trainee.getUser().getPassword(),
                trainers
        );
        logger.info("Updated trainers list for trainee {}. Assigned trainers: {}",
                updatedTrainee.getUser().getUsername(), trainers.size());
    }

    private static void demoActivateDeactivate(GymFacade gymFacade, Trainee trainee, Trainer trainer) {
        Trainee deactivatedTrainee = gymFacade.deactivateTrainee(
                trainee.getUser().getUsername(),
                trainee.getUser().getPassword()
        );
        logger.info("Trainee {} deactivated. isActive: {}",
                trainee.getUser().getUsername(), deactivatedTrainee.getUser().isActive());

        Trainee activatedTrainee = gymFacade.activateTrainee(
                trainee.getUser().getUsername(),
                trainee.getUser().getPassword()
        );
        logger.info("Trainee {} activated. isActive: {}",
                trainee.getUser().getUsername(), activatedTrainee.getUser().isActive());

        try {
            gymFacade.activateTrainee(trainee.getUser().getUsername(), trainee.getUser().getPassword());
            logger.warn("Activate/Deactivate is IDEMPOTENT (should not be!)");
        } catch (Exception e) {
            logger.info("Second activation correctly rejected (NOT idempotent): {}", e.getMessage());
        }

        Trainer deactivatedTrainer = gymFacade.deactivateTrainer(
                trainer.getUser().getUsername(),
                trainer.getUser().getPassword()
        );
        logger.info("Trainer {} deactivated. isActive: {}",
                trainer.getUser().getUsername(), deactivatedTrainer.getUser().isActive());

        Trainer activatedTrainer = gymFacade.activateTrainer(
                trainer.getUser().getUsername(),
                trainer.getUser().getPassword()
        );
        logger.info("Trainer {} activated. isActive: {}",
                trainer.getUser().getUsername(), activatedTrainer.getUser().isActive());
    }

    private static void demoDeleteTrainee(GymFacade gymFacade, Trainee trainee) {
        logger.info("Deleting trainee: {}", trainee.getUser().getUsername());
        gymFacade.deleteTrainee(trainee.getUser().getUsername(), trainee.getUser().getPassword());
        logger.info("Trainee {} deleted with cascade deletion of related trainings", trainee.getUser().getUsername());

        try {
            Trainee deletedTrainee = gymFacade.findTraineeByUsername(trainee.getUser().getUsername(), trainee.getUser().getPassword());
            logger.warn("Trainee still exists after deletion: {}", deletedTrainee.getUser().getUsername());
        } catch (Exception e) {
            logger.info("Confirmed: Trainee {} no longer exists", trainee.getUser().getUsername());
        }
    }
}
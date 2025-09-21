package com.github.amangusss.gym_application;

import com.github.amangusss.gym_application.config.GymApplicationConfig;
import com.github.amangusss.gym_application.entity.Trainee;
import com.github.amangusss.gym_application.entity.Trainer;
import com.github.amangusss.gym_application.entity.Training;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.service.TraineeService;
import com.github.amangusss.gym_application.service.TrainerService;
import com.github.amangusss.gym_application.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;

public class GymApplication {

    public static final Logger logger = LoggerFactory.getLogger(GymApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Gym application");
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(GymApplicationConfig.class)) {
            logger.info("Application context initialized");

            demoApplication(context);

            logger.info("Application finished");
        } catch (Exception e) {
            logger.error("Application failed", e);
            System.exit(1);
        }
    }

    private static void demoApplication(AnnotationConfigApplicationContext context) {
        logger.info("Demo application started");

        TraineeService traineeService = context.getBean(TraineeService.class);
        TrainerService trainerService = context.getBean(TrainerService.class);
        TrainingService trainingService = context.getBean(TrainingService.class);

        demoTraineeOperations(traineeService);
        demoTrainerOperations(trainerService);
        demoTrainingOperations(trainingService, trainerService, traineeService);
    }

    private static void demoTraineeOperations(TraineeService traineeService) {
        logger.info("Demo trainee operations started");

        Trainee trainee = new Trainee("Aman", "Nazarkulov");
        trainee.setDateOfBirth(LocalDate.of(2004, 2, 14));
        trainee.setAddress("Isakeev st. 18/10");

        Trainee savedTrainee = traineeService.createTrainee(trainee);
        logger.info("Created trainee: {} (username: {}, password: {})",
                savedTrainee.getFirstName() + " " + savedTrainee.getLastName(),
                savedTrainee.getUsername(),
                savedTrainee.getPassword());

        savedTrainee.setAddress("Erkindik boulevard 8");
        Trainee updatedTrainee = traineeService.updateTrainee(savedTrainee);
        logger.info("Updated trainee address: {}", updatedTrainee.getAddress());

        List<Trainee> trainees = traineeService.findAllTrainees();
        logger.info("Found {} trainees", trainees.size());
    }

    private static void demoTrainerOperations(TrainerService trainerService) {
        logger.info("Demo trainer operations started");

        Trainer trainer = new Trainer("Dastan", "Ibraimov", TrainingType.STRETCHING);

        Trainer savedTrainer = trainerService.createTrainer(trainer);
        logger.info("Created trainer: {} (username: {}, password: {}, specialization: {})",
                savedTrainer.getFirstName() + " " + savedTrainer.getLastName(),
                savedTrainer.getUsername(),
                savedTrainer.getPassword(),
                savedTrainer.getSpecialization());

        List<Trainer> trainers = trainerService.findAllTrainers();
        logger.info("Found {} trainers", trainers.size());
    }

    private static void demoTrainingOperations(TrainingService trainingService, TrainerService trainerService, TraineeService traineeService) {
        logger.info("Demo training operations started");

        List<Trainee> trainees = traineeService.findAllTrainees();
        List<Trainer> trainers = trainerService.findAllTrainers();

        if (!trainees.isEmpty() && !trainers.isEmpty()) {
            Trainee trainee = trainees.get(0);
            Trainer trainer = trainers.stream()
                    .filter(t -> t.getSpecialization().equals(TrainingType.STRETCHING))
                    .findFirst()
                    .orElse(trainers.get(0));

            Training training = new Training(
                    trainee.getId(),
                    trainer.getId(),
                    "Morning stretching training",
                    trainer.getSpecialization(),
                    LocalDate.now().plusDays(1) ,
                    15);

            Training savedTraining = trainingService.createTraining(training);
            logger.info("Created training: {} (duration: {} min, date: {})",
                    savedTraining.getTrainingName(),
                    savedTraining.getTrainingDuration(),
                    savedTraining.getTrainingDate());

            List<Training> trainings = trainingService.findAllTrainings();
            logger.info("Found {} trainings", trainings.size());
        }
    }
}
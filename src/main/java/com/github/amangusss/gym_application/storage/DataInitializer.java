package com.github.amangusss.gym_application.storage;

import javax.annotation.PostConstruct;

import com.github.amangusss.gym_application.entity.Trainee;
import com.github.amangusss.gym_application.entity.Trainer;
import com.github.amangusss.gym_application.entity.Training;
import com.github.amangusss.gym_application.entity.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class DataInitializer {

    public static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final TraineeStorage traineeStorage;
    private final TrainerStorage trainerStorage;
    private final TrainingStorage trainingStorage;
    private final String traineeFilePath;
    private final String trainerFilePath;
    private final String trainingFilePath;

    @Autowired
    public DataInitializer(TraineeStorage traineeStorage,
                           TrainerStorage trainerStorage,
                           TrainingStorage trainingStorage,
                           @Value("${data.trainee.file}") String traineeFilePath,
                           @Value("${data.trainer.file}") String trainerFilePath,
                           @Value("${data.training.file}") String trainingFilePath) {

        this.traineeStorage = traineeStorage;
        this.trainerStorage = trainerStorage;
        this.trainingStorage = trainingStorage;
        this.traineeFilePath = traineeFilePath;
        this.trainerFilePath = trainerFilePath;
        this.trainingFilePath = trainingFilePath;
    }

    @PostConstruct
    public void init() {
        logger.info("Initializing data");

        try {
            loadTrainees();
            loadTrainers();
            loadTrainings();

            logger.info("Data initialization completed! " +
                        "Loaded: {} trainees, {} trainers, {} trainings",
                        traineeStorage.findAll().size(),
                        trainerStorage.findAll().size(),
                        trainingStorage.findAll().size());
        } catch (Exception e) {
            logger.error("Failed to initialize data", e);
        }
    }

    private void loadTrainees() {
        loadDataFromFile(traineeFilePath, "trainee", this::parseTraineeFile, traineeStorage::save);
    }

    private Trainee parseTraineeFile(String line) {
        try {
            String[] fields = line.split(",");
            if (fields.length < 5) {
                logger.warn("Invalid trainee line: {}", line);
                return null;
            }

            Trainee trainee = new Trainee();
            trainee.setFirstName(fields[0].trim());
            trainee.setLastName(fields[1].trim());
            trainee.setUsername(fields[2].trim());
            trainee.setPassword(fields[3].trim());
            trainee.setActive(Boolean.parseBoolean(fields[4].trim()));

            if (fields.length > 5 && !fields[5].trim().isEmpty()) {
                trainee.setDateOfBirth(LocalDate.parse(fields[5].trim()));
            }

            if (fields.length > 6 && !fields[6].trim().isEmpty()) {
                trainee.setAddress(fields[6].trim());
            }

            return trainee;
        } catch (Exception e) {
            logger.error("Failed to parse trainee line: {}", line, e);
            return null;
        }
    }

    private void loadTrainers() {
        loadDataFromFile(trainerFilePath, "trainer", this::parseTrainerFile, trainerStorage::save);
    }

    private Trainer parseTrainerFile(String line) {
        try {
            String[] fields = line.split(",");
            if (fields.length < 6) {
                logger.warn("Invalid trainer line: {}", line);
                return null;
            }

            Trainer trainer = new Trainer();
            trainer.setFirstName(fields[0].trim());
            trainer.setLastName(fields[1].trim());
            trainer.setUsername(fields[2].trim());
            trainer.setPassword(fields[3].trim());
            trainer.setActive(Boolean.parseBoolean(fields[4].trim()));
            trainer.setSpecialization(TrainingType.fromDisplayName(fields[5].trim()));

            return trainer;
        } catch (Exception e) {
            logger.error("Failed to parse trainer line: {}", line, e);
            return null;
        }
    }

    private void loadTrainings() {
        loadDataFromFile(trainingFilePath, "training", this::parseTrainingFile, trainingStorage::save);
    }

    private Training parseTrainingFile(String line) {
        try {
            String[] fields = line.split(",");
            if (fields.length < 6) {
                logger.warn("Invalid training line: {}", line);
                return null;
            }

            Training training = new Training();
            training.setTraineeId(Long.parseLong(fields[0].trim()));
            training.setTrainerId(Long.parseLong(fields[1].trim()));
            training.setTrainingName(fields[2].trim());
            training.setTrainingType(TrainingType.fromDisplayName(fields[3].trim()));
            training.setTrainingDate(LocalDate.parse(fields[4].trim()));
            training.setTrainingDuration(Integer.parseInt(fields[5].trim()));

            return training;
        } catch (Exception e) {
            logger.error("Failed to parse training line: {}", line, e);
            return null;
        }
    }

    private <T> void loadDataFromFile(String filePath, String entityType,
                                      Function<String, T> parser, Consumer<T> saver) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            if (!resource.exists()) {
                logger.warn("{} file not found: {}. Skipping initialization for {}",
                        entityType, filePath, entityType);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                int count = 0;
                int lineNumber = 0;
                String line;

                while((line = reader.readLine()) != null) {
                    lineNumber++;

                    if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                        continue;
                    }

                    try {
                        T entity = parser.apply(line);
                        if (entity != null) {
                            saver.accept(entity);
                            count++;
                        }
                    } catch (Exception e) {
                        logger.error("Failed to parse {} line {}: {}", entityType, lineNumber, line, e);
                    }
                }

                logger.info("Loaded {} {} from file: {}", count, entityType, filePath);
            }
        } catch (Exception e) {
            logger.error("Failed to load {} from file: {}", entityType, filePath, e);
        }
    }

}

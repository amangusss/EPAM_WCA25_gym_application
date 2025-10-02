package com.github.amangusss.gym_application.storage;

import javax.annotation.PostConstruct;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainee.TraineeBuilder;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.trainer.TrainerBuilder;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.training.TrainingBuilder;
import com.github.amangusss.gym_application.exception.DataInitializationException;
import com.github.amangusss.gym_application.util.constants.ConfigConstants;
import com.github.amangusss.gym_application.util.constants.LoggerConstants;
import com.github.amangusss.gym_application.util.constants.ValidationConstants;

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

@Component(ConfigConstants.BEAN_DATA_INITIALIZER)
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
                           @Value("${" + ConfigConstants.PROP_TRAINEE_FILE + "}") String traineeFilePath,
                           @Value("${" + ConfigConstants.PROP_TRAINER_FILE + "}") String trainerFilePath,
                           @Value("${" + ConfigConstants.PROP_TRAINING_FILE + "}") String trainingFilePath) {

        this.traineeStorage = traineeStorage;
        this.trainerStorage = trainerStorage;
        this.trainingStorage = trainingStorage;
        this.traineeFilePath = traineeFilePath;
        this.trainerFilePath = trainerFilePath;
        this.trainingFilePath = trainingFilePath;
    }

    @PostConstruct
    public void init() {
        logger.info(LoggerConstants.DATA_INIT_STARTING);

        try {
            loadTrainees();
            loadTrainers();
            loadTrainings();

            logger.info(LoggerConstants.DATA_INIT_COMPLETED,
                        traineeStorage.findAll().size(),
                        trainerStorage.findAll().size(),
                        trainingStorage.findAll().size());
        } catch (Exception e) {
            logger.error(LoggerConstants.DATA_INIT_FAILED, e);
            throw new DataInitializationException(String.format(ValidationConstants.DATA_INITIALIZATION_FAILED, e.getMessage()), e);
        }
    }

    private void loadTrainees() {
        logger.info(LoggerConstants.LOADING_TRAINEES, traineeFilePath);
        loadDataFromFile(traineeFilePath, "trainee", this::parseTraineeFile, traineeStorage::save);
    }

    private Trainee parseTraineeFile(String line) {
        try {
            String[] fields = line.split(",");
            if (fields.length < 5) {
                logger.warn(LoggerConstants.INVALID_LINE, "trainee", line);
                return null;
            }

            TraineeBuilder builder = TraineeBuilder.builder()
                    .firstName(fields[0].trim())
                    .lastName(fields[1].trim())
                    .username(fields[2].trim())
                    .password(fields[3].trim())
                    .isActive(Boolean.parseBoolean(fields[4].trim()));

            if (fields.length > 5 && !fields[5].trim().isEmpty()) {
                builder.dateOfBirth(LocalDate.parse(fields[5].trim()));
            }

            if (fields.length > 6 && !fields[6].trim().isEmpty()) {
                builder.address(fields[6].trim());
            }

            return builder.build();
        } catch (Exception e) {
            logger.error(LoggerConstants.FAILED_PARSE_LINE_TWO_ARGUMENTS, "trainee", line, e);
            return null;
        }
    }

    private void loadTrainers() {
        logger.info(LoggerConstants.LOADING_TRAINERS, trainerFilePath);
        loadDataFromFile(trainerFilePath, "trainer", this::parseTrainerFile, trainerStorage::save);
    }

    private Trainer parseTrainerFile(String line) {
        try {
            String[] fields = line.split(",");
            if (fields.length < 6) {
                logger.warn(LoggerConstants.INVALID_LINE, "trainer", line);
                return null;
            }

            return TrainerBuilder.builder()
                    .firstName(fields[0].trim())
                    .lastName(fields[1].trim())
                    .username(fields[2].trim())
                    .password(fields[3].trim())
                    .isActive(Boolean.parseBoolean(fields[4].trim()))
                    .specialization(TrainingType.fromDisplayName(fields[5].trim()))
                    .build();
        } catch (Exception e) {
            logger.error(LoggerConstants.FAILED_PARSE_LINE_TWO_ARGUMENTS, "trainer", line, e);
            return null;
        }
    }

    private void loadTrainings() {
        logger.info(LoggerConstants.LOADING_TRAININGS, trainingFilePath);
        loadDataFromFile(trainingFilePath, "training", this::parseTrainingFile, trainingStorage::save);
    }

    private Training parseTrainingFile(String line) {
        try {
            String[] fields = line.split(",");
            if (fields.length < 6) {
                logger.warn(LoggerConstants.INVALID_LINE, "training", line);
                return null;
            }

            return TrainingBuilder.builder()
                    .traineeId(Long.parseLong(fields[0].trim()))
                    .trainerId(Long.parseLong(fields[1].trim()))
                    .trainingName(fields[2].trim())
                    .trainingType(TrainingType.fromDisplayName(fields[3].trim()))
                    .trainingDate(LocalDate.parse(fields[4].trim()))
                    .trainingDuration(Integer.parseInt(fields[5].trim()))
                    .build();
        } catch (Exception e) {
            logger.error(LoggerConstants.FAILED_PARSE_LINE_TWO_ARGUMENTS, "training", line, e);
            return null;
        }
    }

    private <T> void loadDataFromFile(String filePath, String entityType,
                                      Function<String, T> parser, Consumer<T> saver) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            if (!resource.exists()) {
                logger.warn(LoggerConstants.FILE_NOT_FOUND,
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
                        logger.error(LoggerConstants.FAILED_PARSE_LINE_THREE_ARGUMENTS, entityType, lineNumber, line, e);
                    }
                }

                logger.info(LoggerConstants.LOADED_ENTITIES, count, entityType);
            }
        } catch (Exception e) {
            logger.error(LoggerConstants.FAILED_TO_LOAD_FROM_FILE, entityType, filePath, e);
            throw new DataInitializationException(String.format(ValidationConstants.DATA_INITIALIZATION_FAILED, filePath), e);
        }
    }

}

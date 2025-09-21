package com.github.amangusss.gym_application.repository.impl;

import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.repository.TrainingDAO;
import com.github.amangusss.gym_application.storage.TrainingStorage;
import com.github.amangusss.gym_application.util.constants.LoggerConstants;
import com.github.amangusss.gym_application.util.constants.ValidationConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TrainingDAOImpl implements TrainingDAO {

    public static final Logger logger = LoggerFactory.getLogger(TrainingDAOImpl.class);

    private final TrainingStorage trainingStorage;

    @Autowired
    public TrainingDAOImpl(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public Training save(Training training) {
        if (training == null) {
            throw new ValidationException(ValidationConstants.TRAINING_NULL);
        }

        logger.debug(LoggerConstants.DAO_SAVING, "training", training.getTrainingName());
        Training saved =  trainingStorage.save(training);
        logger.info(LoggerConstants.DAO_SAVED, "Training", saved.getId());
        return saved;
    }

    @Override
    public Training findById(Long id) {
        if (id == null) {
            return null;
        }

        logger.debug(LoggerConstants.DAO_FINDING, "training", id);
        Training training = trainingStorage.findById(id);

        if (training != null) {
            logger.info(LoggerConstants.DAO_FOUND, "Training", id);
        } else {
            logger.debug(LoggerConstants.DAO_NOT_FOUND, "Training", id);
        }

        return training;
    }

    @Override
    public List<Training> findAll() {
        logger.debug(LoggerConstants.DAO_FINDING_ALL, "trainings");
        List<Training> trainings = trainingStorage.findAll();
        logger.info(LoggerConstants.DAO_FOUND_ALL, trainings.size(), "trainings");
        return trainings;
    }

    @Override
    public List<Training> findByTrainerId(Long trainerId) {
        if (trainerId == null) {
            return new ArrayList<>();
        }

        logger.debug(LoggerConstants.DAO_FINDING_BY_TRAINER, "trainings", trainerId);
        List<Training> trainings = trainingStorage.findAll().stream()
                .filter(t -> t.getTrainerId().equals(trainerId))
                .toList();
        logger.info(LoggerConstants.DAO_FOUND_BY_TRAINER, trainings.size(), "trainings", trainerId);
        return trainings;
    }

    @Override
    public List<Training> findByTraineeId(Long traineeId) {
        if (traineeId == null) {
            return new ArrayList<>();
        }

        logger.debug(LoggerConstants.DAO_FINDING_BY_TRAINEE, "trainings", traineeId);
        List<Training> trainings = trainingStorage.findAll().stream()
                .filter(t -> t.getTraineeId().equals(traineeId))
                .toList();
        logger.info(LoggerConstants.DAO_FOUND_BY_TRAINEE, trainings.size(), "trainings", traineeId);
        return trainings;
    }

    @Override
    public List<Training> findByTrainingType(TrainingType trainingType) {
        if (trainingType == null) {
            return new ArrayList<>();
        }

        logger.debug(LoggerConstants.DAO_FINDING_BY_TYPE, "trainings", trainingType);
        List<Training> trainings = trainingStorage.findAll().stream()
                .filter(t -> trainingType.equals(t.getTrainingType()))
                .toList();
        logger.info(LoggerConstants.DAO_FOUND_BY_TYPE, trainings.size(), "trainings", trainingType);
        return trainings;
    }

    @Override
    public List<Training> findByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null ||  endDate == null) {
            return new ArrayList<>();
        }

        if (startDate.isAfter(endDate)) {
            throw new ValidationException(ValidationConstants.START_DATE_AFTER_END_DATE);
        }

        logger.debug(LoggerConstants.DAO_FINDING_BY_DATE_RANGE, "trainings", startDate, endDate);
        List<Training> trainings = trainingStorage.findAll().stream()
                .filter(t -> {
                    LocalDate trainingDate = t.getTrainingDate();
                    return trainingDate != null &&
                            !trainingDate.isBefore(startDate) &&
                            !trainingDate.isAfter(endDate);
                })
                .toList();
        logger.info(LoggerConstants.DAO_FOUND_BY_DATE_RANGE, trainings.size(), "trainings", startDate, endDate);
        return trainings;
    }
}
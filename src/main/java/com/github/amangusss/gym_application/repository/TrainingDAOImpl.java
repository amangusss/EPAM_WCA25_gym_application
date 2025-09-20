package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.Training;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.repository.dao.TrainingDAO;
import com.github.amangusss.gym_application.storage.TrainingStorage;
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
            throw new IllegalArgumentException("training cannot be null");
        }

        logger.debug("Saving training: {}", training.getTrainingName());
        Training saved =  trainingStorage.save(training);
        logger.info("Training saved successfully with id: {}", saved.getId());
        return saved;
    }

    @Override
    public Training findById(Long id) {
        if (id == null) {
            return null;
        }

        logger.debug("Finding training: {}", id);
        Training training = trainingStorage.findById(id);

        if (training != null) {
            logger.info("Training found: {}", training.getTrainingName());
        } else {
            logger.debug("Training not found with id: {}", id);
        }

        return training;
    }

    @Override
    public Iterable<Training> findAll() {
        logger.debug("Finding all trainings");
        List<Training> trainings = trainingStorage.findAll();
        logger.info("All trainings found: {}", trainings.size());
        return trainings;
    }

    @Override
    public Iterable<Training> findByTrainerId(Long trainerId) {
        if (trainerId == null) {
            return new ArrayList<>();
        }

        logger.debug("Finding trainings by trainer id: {}", trainerId);
        List<Training> trainings = trainingStorage.findAll().stream()
                .filter(t -> t.getTrainerId().equals(trainerId))
                .toList();
        logger.info("Found {} trainings for trainer {}", trainings.size(), trainerId);
        return trainings;
    }

    @Override
    public Iterable<Training> findByTraineeId(Long traineeId) {
        if (traineeId == null) {
            return new ArrayList<>();
        }

        logger.debug("Finding trainings by trainee id: {}", traineeId);
        List<Training> trainings = trainingStorage.findAll().stream()
                .filter(t -> t.getTraineeId().equals(traineeId))
                .toList();
        logger.info("Found {} trainings for trainee {}", trainings.size(), traineeId);
        return trainings;
    }

    @Override
    public Iterable<Training> findByTrainingType(TrainingType trainingType) {
        if (trainingType == null) {
            return new ArrayList<>();
        }

        logger.debug("Finding trainings by type: {}", trainingType);
        List<Training> trainings = trainingStorage.findAll().stream()
                .filter(t -> trainingType.equals(t.getTrainingType()))
                .toList();
        logger.info("Found {} trainings with type {}", trainings.size(), trainingType);
        return trainings;
    }

    @Override
    public Iterable<Training> findByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null ||  endDate == null) {
            return new ArrayList<>();
        }

        if (startDate.isAfter(endDate)) {
            throw new  IllegalArgumentException("startDate cannot be after endDate");
        }

        logger.debug("Finding trainings by date range: {} to {}", startDate, endDate);
        List<Training> trainings = trainingStorage.findAll().stream()
                .filter(t -> {
                    LocalDate trainingDate = t.getTrainingDate();
                    return trainingDate != null &&
                            !trainingDate.isAfter(startDate) &&
                            !trainingDate.isBefore(endDate);
                })
                .toList();
        logger.info("Found {} trainings in date range {} to {}", trainings.size(), startDate, endDate);
        return trainings;
    }
}
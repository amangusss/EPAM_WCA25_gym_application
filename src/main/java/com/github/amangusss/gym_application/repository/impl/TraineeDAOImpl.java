package com.github.amangusss.gym_application.repository.impl;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.exception.TraineeNotFoundException;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.repository.TraineeDAO;
import com.github.amangusss.gym_application.storage.TraineeStorage;
import com.github.amangusss.gym_application.util.constants.LoggerConstants;
import com.github.amangusss.gym_application.util.constants.ValidationConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TraineeDAOImpl implements TraineeDAO {

    public static final Logger logger = LoggerFactory.getLogger(TraineeDAOImpl.class);

    private final TraineeStorage traineeStorage;

    @Autowired
    public TraineeDAOImpl(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
        logger.info(LoggerConstants.DAO_CREATED, "TraineeDAOImpl");
    }

    @Override
    public Trainee save(Trainee trainee) {
        if (trainee == null) {
            throw new ValidationException(ValidationConstants.TRAINEE_NULL);
        }

        logger.debug(LoggerConstants.DAO_SAVING, "trainee", trainee.getUsername());
        Trainee savedTrainee = traineeStorage.save(trainee);
        logger.info(LoggerConstants.DAO_SAVED, "Trainee", savedTrainee.getId());
        return savedTrainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        if (trainee == null) {
            throw new ValidationException(ValidationConstants.TRAINEE_NULL);
        }

        if (trainee.getId() == null) {
            throw new ValidationException(ValidationConstants.TRAINEE_ID_NULL);
        }

        if (!traineeStorage.existsById(trainee.getId())) {
            throw new TraineeNotFoundException(String.format(ValidationConstants.TRAINEE_NOT_FOUND_BY_ID, trainee.getId()));
        }

        logger.debug(LoggerConstants.DAO_UPDATING, "trainee", trainee.getUsername());
        Trainee updatedTrainee = traineeStorage.update(trainee);
        logger.info(LoggerConstants.DAO_UPDATED, "Trainee", updatedTrainee.getId());
        return updatedTrainee;
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }

        logger.debug(LoggerConstants.DAO_DELETING, "trainee", id);
        boolean removed = traineeStorage.deleteById(id);

        if (removed) {
            logger.info(LoggerConstants.DAO_DELETED, "Trainee", id);
        } else {
            logger.warn(LoggerConstants.DAO_NOT_FOUND, "Trainee", id);
        }

        return removed;
    }

    @Override
    public Trainee findById(Long id) {
        if (id == null) {
            return null;
        }

        logger.debug(LoggerConstants.DAO_FINDING, "trainee", id);
        Trainee trainee = traineeStorage.findById(id);

        if (trainee != null) {
            logger.info(LoggerConstants.DAO_FOUND, "Trainee", id);
        } else {
            logger.debug(LoggerConstants.DAO_NOT_FOUND, "Trainee", id);
        }

        return trainee;
    }

    @Override
    public List<Trainee> findAll() {
        logger.debug(LoggerConstants.DAO_FINDING_ALL, "trainees");
        List<Trainee> trainees = traineeStorage.findAll();
        logger.info(LoggerConstants.DAO_FOUND_ALL, trainees.size(), "trainees");
        return trainees;
    }

    @Override
    public Trainee findByUsername(String username) {
        if (username == null) {
            return null;
        }

        logger.debug(LoggerConstants.DAO_FINDING_BY_USERNAME, "trainee", username);
        Trainee trainee = traineeStorage.findAll().stream()
                .filter(t -> t.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (trainee != null) {
            logger.info(LoggerConstants.DAO_FOUND_BY_USERNAME, "Trainee", username);
        } else {
            logger.debug(LoggerConstants.DAO_NOT_FOUND_BY_USERNAME, "Trainee", username);
        }

        return trainee;
    }

    @Override
    public List<Trainee> findActiveTrainees() {
        logger.debug(LoggerConstants.DAO_FINDING_ACTIVE, "trainees");
        List<Trainee> trainees = traineeStorage.findAll().stream()
                .filter(User::isActive)
                .toList();

        logger.info(LoggerConstants.DAO_FOUND_ACTIVE, trainees.size(), "trainees");
        return trainees;
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        boolean exists = findByUsername(username) != null;
        logger.debug(LoggerConstants.DAO_EXISTS_BY_USERNAME, "Trainee", username, exists);
        return exists;
    }
}

package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.Trainee;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.repository.dao.TraineeDAO;
import com.github.amangusss.gym_application.storage.TraineeStorage;

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
        logger.info("TraineeDAOImpl created");
    }

    @Override
    public Trainee save(Trainee trainee) {
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee must not be null");
        }

        logger.debug("Saving trainee: {}", trainee.getUsername());
        Trainee savedTrainee = traineeStorage.save(trainee);
        logger.info("Trainee saved successfully with id: {}", savedTrainee.getId());
        return savedTrainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee must not be null");
        }

        if (trainee.getId() == null) {
            throw new IllegalArgumentException("Trainee with id " + trainee.getId() + " not found");
        }

        if (!traineeStorage.existsById(trainee.getId())) {
            throw new IllegalArgumentException("Trainee with id " + trainee.getId() + " not found");
        }

        logger.debug("Updating trainee: {}", trainee.getUsername());
        Trainee updatedTrainee = traineeStorage.update(trainee);
        logger.info("Trainee updated successfully with id: {}", updatedTrainee.getId());
        return updatedTrainee;
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }

        logger.debug("Deleting trainee with id: {}", id);
        boolean removed = traineeStorage.deleteById(id);

        if (removed) {
            logger.info("Trainee with id {} deleted successfully", id);
        } else {
            logger.warn("Trainee with id {} not found", id);
        }

        return removed;
    }

    @Override
    public Trainee findById(Long id) {
        if (id == null) {
            return null;
        }

        logger.debug("Finding trainee with id: {}", id);
        Trainee trainee = traineeStorage.findById(id);

        if (trainee != null) {
            logger.info("Trainee found successfully with id: {}", id);
        } else {
            logger.debug("Trainee with id {} not found", id);
        }

        return trainee;
    }

    @Override
    public List<Trainee> findAll() {
        logger.debug("Finding all trainees");
        List<Trainee> trainees = traineeStorage.findAll();
        logger.info("Found {} trainees", trainees.size());
        return trainees;
    }

    @Override
    public Trainee findByUsername(String username) {
        if (username == null) {
            return null;
        }

        logger.debug("Finding trainee with username: {}", username);
        Trainee trainee = traineeStorage.findAll().stream()
                .filter(t -> t.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (trainee != null) {
            logger.info("Trainee found successfully with username: {}", username);
        } else {
            logger.debug("Trainee with username {} not found", username);
        }

        return trainee;
    }

    @Override
    public List<Trainee> findActiveTrainees() {
        logger.debug("Finding all active trainees");
        List<Trainee> trainees = traineeStorage.findAll().stream()
                .filter(User::isActive)
                .toList();

        logger.info("Found {} active trainees", trainees.size());
        return trainees;
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        boolean exists = findByUsername(username) != null;
        logger.debug("Trainee with username {} exists: {}", username, exists);
        return exists;
    }
}

package com.github.amangusss.gym_application.repository.impl;

import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.repository.TrainerDAO;
import com.github.amangusss.gym_application.storage.TrainerStorage;
import com.github.amangusss.gym_application.util.constants.LoggerConstants;
import com.github.amangusss.gym_application.util.constants.ValidationConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TrainerDAOImpl implements TrainerDAO {

    public static final Logger logger = LoggerFactory.getLogger(TrainerDAOImpl.class);

    private final TrainerStorage trainerStorage;

    @Autowired
    public TrainerDAOImpl(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public Trainer save(Trainer trainer) {
        if (trainer == null) {
            throw new ValidationException(ValidationConstants.TRAINER_NULL);
        }

        logger.debug(LoggerConstants.DAO_SAVING, "trainer", trainer.getUsername());
        Trainer saved = trainerStorage.save(trainer);
        logger.info(LoggerConstants.DAO_SAVED, "Trainer", saved.getId());
        return  saved;
    }

    @Override
    public Trainer update(Trainer trainer) {
        if (trainer == null) {
            throw new ValidationException(ValidationConstants.TRAINER_NULL);
        }

        if (trainer.getId() == null) {
            throw new ValidationException(ValidationConstants.TRAINER_ID_NULL);
        }

        if (!trainerStorage.existsById(trainer.getId())) {
            throw new TrainerNotFoundException(String.format(ValidationConstants.TRAINER_NOT_FOUND_BY_ID, trainer.getId()));
        }

        logger.debug(LoggerConstants.DAO_UPDATING, "trainer", trainer.getUsername());
        Trainer updated = trainerStorage.update(trainer);
        logger.info(LoggerConstants.DAO_UPDATED, "Trainer", updated.getId());
        return updated;
    }

    @Override
    public Trainer findById(Long id) {
        if (id == null) {
            return null;
        }

        logger.debug(LoggerConstants.DAO_FINDING, "trainer", id);
        Trainer trainer = trainerStorage.findById(id);

        if (trainer != null) {
            logger.info(LoggerConstants.DAO_FOUND, "Trainer", id);
        } else {
            logger.debug(LoggerConstants.DAO_NOT_FOUND, "Trainer", id);
        }

        return trainer;
    }

    @Override
    public List<Trainer> findAll() {
        logger.debug(LoggerConstants.DAO_FINDING_ALL, "trainers");
        List<Trainer> trainers = trainerStorage.findAll();
        logger.info(LoggerConstants.DAO_FOUND_ALL, trainers.size(), "trainers");
        return trainers;
    }

    @Override
    public Trainer findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        logger.debug(LoggerConstants.DAO_FINDING_BY_USERNAME, "trainer", username);
        Trainer trainer = trainerStorage.findAll().stream()
                .filter(t -> t.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (trainer == null) {
            logger.debug(LoggerConstants.DAO_NOT_FOUND_BY_USERNAME, "Trainer", username);
        } else {
            logger.info(LoggerConstants.DAO_FOUND_BY_USERNAME, "Trainer", username);
        }

        return trainer;
    }

    @Override
    public List<Trainer> findBySpecialization(TrainingType specialization) {
        if (specialization == null) {
            return new ArrayList<>();
        }

        logger.debug(LoggerConstants.DAO_FINDING_BY_SPECIALIZATION, "trainers", specialization);
        List<Trainer> trainers = trainerStorage.findAll().stream()
                .filter(t -> t.getSpecialization().equals(specialization))
                .toList();

        logger.debug(LoggerConstants.DAO_FOUND_BY_SPECIALIZATION, trainers.size(), "trainers", specialization);
        return trainers;
    }

    @Override
    public List<Trainer> findActiveTrainers() {
        logger.debug(LoggerConstants.DAO_FINDING_ACTIVE, "trainers");
        List<Trainer> trainers = trainerStorage.findAll().stream()
                .filter(User::isActive)
                .toList();
        logger.info(LoggerConstants.DAO_FOUND_ACTIVE, trainers.size(), "trainers");
        return trainers;
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        boolean exists = findByUsername(username) != null;
        logger.debug(LoggerConstants.DAO_EXISTS_BY_USERNAME, "Trainer", username, exists);
        return exists;
    }
}

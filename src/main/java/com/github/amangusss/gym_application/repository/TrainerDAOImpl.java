package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.Trainer;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.repository.dao.TrainerDAO;
import com.github.amangusss.gym_application.storage.TrainerStorage;
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
            throw new IllegalArgumentException("trainer cannot be null");
        }

        logger.debug("Saving trainer: {}", trainer);
        Trainer saved = trainerStorage.save(trainer);
        logger.info("Trainer saved successfully with id: {}", saved.getId());
        return  saved;
    }

    @Override
    public Trainer update(Trainer trainer) {
        if (trainer == null) {
            throw new IllegalArgumentException("trainer cannot be null");
        }

        if (trainer.getId() == null) {
            throw new IllegalArgumentException("trainer id cannot be null");
        }

        if (!trainerStorage.existsById(trainer.getId())) {
            throw new IllegalArgumentException("trainer does not exist");
        }

        logger.debug("Updating trainer: {}", trainer);
        Trainer updated = trainerStorage.update(trainer);
        logger.info("Trainer updated: {}", updated.getUsername());
        return updated;
    }

    @Override
    public Trainer findById(Long id) {
        if (id == null) {
            return null;
        }

        logger.debug("Finding trainer: {}", id);
        Trainer trainer = trainerStorage.findById(id);

        if (trainer != null) {
            logger.info("Found trainer: {}", trainer.getUsername());
        } else {
            logger.error("Trainer with id {} not found", id);
        }

        return trainer;
    }

    @Override
    public List<Trainer> findAll() {
        logger.debug("Finding all trainers");
        List<Trainer> trainers = trainerStorage.findAll();
        logger.info("Found {} trainers", trainers.size());
        return trainers;
    }

    @Override
    public Trainer findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        logger.debug("Finding trainer: {}", username);
        Trainer trainer = trainerStorage.findAll().stream()
                .filter(t -> t.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (trainer == null) {
            logger.error("Trainer with id {} not found", username);
        } else {
            logger.info("Found trainer: {}", trainer.getUsername());
        }

        return trainer;
    }

    @Override
    public List<Trainer> findBySpecialization(TrainingType specialization) {
        if (specialization == null) {
            return new ArrayList<>();
        }

        logger.debug("Finding trainers by specialization: {}", specialization);
        List<Trainer> trainers = trainerStorage.findAll().stream()
                .filter(t -> t.getSpecialization().equals(specialization))
                .toList();

        logger.debug("Found {} trainers with specialization {}", trainers.size(), specialization);
        return trainers;
    }

    @Override
    public List<Trainer> findActiveTrainers() {
        logger.debug("Finding active trainers");
        List<Trainer> trainers = trainerStorage.findAll().stream()
                .filter(User::isActive)
                .toList();
        logger.info("Found {} active trainers", trainers.size());
        return trainers;
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        boolean exists = findByUsername(username) != null;
        logger.info("Trainer with username {} exists: {}", username, exists);
        return exists;
    }
}

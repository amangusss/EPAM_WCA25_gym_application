package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.service.TrainerService;
import com.github.amangusss.gym_application.util.credentials.PasswordGenerator;
import com.github.amangusss.gym_application.util.credentials.UsernameGenerator;

import com.github.amangusss.gym_application.validation.trainer.TrainerEntityValidation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    public static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerRepository trainerRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final TrainerEntityValidation trainerEntityValidation;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        logger.debug("Creating trainer profile: {} {}", trainer.getFirstName(), trainer.getLastName());
        trainerEntityValidation.validateTrainerForCreationOrUpdate(trainer);
        generateCredentials(trainer);

        trainer.setActive(true);

        Trainer savedTrainer = trainerRepository.save(trainer);
        logger.info("Successfully created trainer profile with username: {}", savedTrainer.getUsername());
        return savedTrainer;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer findTrainerByUsername(String username, String password) {
        logger.debug("Finding trainer by username: {}", username);
        authenticationCheck(username, password);

        Trainer trainer = trainerRepository.findByUsername(username);
        logger.info("Found trainer with username: {}", username);
        return trainer;
    }

    @Override
    public Trainer updateTrainer(String username, String password, Trainer trainer) {
        logger.debug("Updating trainer profile: {}", username);
        authenticationCheck(username, password);
        trainerEntityValidation.validateTrainerForCreationOrUpdate(trainer);

        Trainer existingTrainer = findTrainerByUsername(username, password);

        existingTrainer.setFirstName(trainer.getFirstName());
        existingTrainer.setLastName(trainer.getLastName());
        existingTrainer.setSpecialization(trainer.getSpecialization());

        Trainer updatedTrainer = trainerRepository.update(existingTrainer);
        logger.info("Successfully updated trainer profile with username: {}", updatedTrainer.getUsername());
        return updatedTrainer;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticateTrainer(String username, String password) {
        logger.debug("Authenticating trainer: {}", username);

        boolean authenticated = trainerRepository.existsByUsernameAndPassword(username, password);

        if (authenticated) {
            logger.info("Trainer authenticated successfully: {}", username);
        } else {
            logger.warn("Authentication failed for trainer: {}", username);
        }

        return authenticated;
    }

    @Override
    public Trainer changeTrainerPassword(String username, String oldPassword, String newPassword) {
        logger.debug("Changing password for trainer: {}", username);
        authenticationCheck(username, oldPassword);
        trainerEntityValidation.validatePasswordChange(oldPassword, newPassword);

        Trainer updatedTrainer = trainerRepository.updatePasswordByUsername(username, oldPassword, newPassword);
        logger.info("Successfully changed password for trainer: {}", username);
        return updatedTrainer;
    }

    @Override
    public Trainer activateTrainer(String username, String password) {
        logger.debug("Activating trainer: {}", username);
        authenticationCheck(username, password);

        Trainer trainer = trainerRepository.updateActiveStatusByUsername(username, true);
        logger.info("Successfully activated trainer: {}", username);
        return trainer;
    }

    @Override
    public Trainer deactivateTrainer(String username, String password) {
        logger.debug("Deactivating trainer: {}", username);
        authenticationCheck(username, password);

        Trainer trainer = trainerRepository.updateActiveStatusByUsername(username, false);
        logger.info("Successfully deactivated trainer: {}", username);
        return trainer;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainingsList(String username, String password, LocalDate fromDate, LocalDate toDate, String traineeName) {
        logger.debug("Getting trainings list for trainer: {} with filters", username);
        authenticationCheck(username, password);
        trainerEntityValidation.validateDateRange(fromDate, toDate);

        List<Training> trainings = trainerRepository.findTrainingsByUsername(username, fromDate, toDate, traineeName);
        logger.info("Retrieved {} trainings for trainer: {}", trainings.size(), username);
        return trainings;
    }

    private void generateCredentials(Trainer trainer) {
        String username = usernameGenerator.generateUsername(trainer.getFirstName(), trainer.getLastName(), this::usernameExists);
        trainer.setUsername(username);

        String password = passwordGenerator.generatePassword();
        trainer.setPassword(password);

        logger.debug("Generated credentials for trainer - username: {}", username);
    }

    private boolean usernameExists(String username) {
        try {
            Trainer trainer = trainerRepository.findByUsername(username);
            return trainer != null;
        } catch (Exception e) {
            logger.debug("Username does not exist: {}", username);
            return false;
        }
    }

    private void authenticationCheck(String username, String password) {
        if (!trainerRepository.existsByUsernameAndPassword(username, password)) {
            logger.error("Authentication failed for trainer: {}", username);
            throw new com.github.amangusss.gym_application.exception.AuthenticationException(
                    "Authentication failed for trainer: " + username);
        }
    }
}
package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.AuthenticationException;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.repository.TraineeRepository;
import com.github.amangusss.gym_application.service.TraineeService;
import com.github.amangusss.gym_application.util.credentials.PasswordGenerator;
import com.github.amangusss.gym_application.util.credentials.UsernameGenerator;

import com.github.amangusss.gym_application.validation.trainee.TraineeEntityValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeRepository traineeRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final TraineeEntityValidation traineeEntityValidation;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        logger.debug("Creating trainee profile: {} {}", trainee.getFirstName(), trainee.getLastName());
        traineeEntityValidation.validateTraineeForCreationOrUpdate(trainee);
        generateCredentials(trainee);

        trainee.setActive(true);

        Trainee savedTrainee = traineeRepository.save(trainee);
        logger.info("Successfully created trainee profile with username: {}", savedTrainee.getUsername());
        return savedTrainee;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee findTraineeByUsername(String username, String password) {
        logger.debug("Finding trainee by username: {}", username);
        authenticationCheck(username, password);

        Trainee trainee = traineeRepository.findByUsername(username);
        logger.info("Found trainee with username: {}", username);
        return trainee;
    }

    @Override
    public Trainee updateTrainee(String username, String password, Trainee trainee) {
        logger.debug("Updating trainee profile: {}", username);
        authenticationCheck(username, password);
        traineeEntityValidation.validateTraineeForCreationOrUpdate(trainee);

        Trainee existingTrainee = traineeRepository.findByUsername(username);

        existingTrainee.setFirstName(trainee.getFirstName());
        existingTrainee.setLastName(trainee.getLastName());
        existingTrainee.setDateOfBirth(trainee.getDateOfBirth());
        existingTrainee.setAddress(trainee.getAddress());

        Trainee updatedTrainee = traineeRepository.update(existingTrainee);
        logger.info("Successfully updated trainee profile: {}", username);
        return updatedTrainee;
    }

    @Override
    public void deleteTraineeByUsername(String username, String password) {
        logger.debug("Deleting trainee profile: {}", username);
        authenticationCheck(username, password);

        traineeRepository.deleteByUsername(username);
        logger.info("Successfully deleted trainee profile: {} with cascade deletion of trainings", username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticateTrainee(String username, String password) {
        logger.debug("Authenticating trainee: {}", username);

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            logger.warn("Authentication failed - username or password is null or empty");
            return false;
        }

        boolean authenticated = traineeRepository.existsByUsernameAndPassword(username, password);

        if (authenticated) {
            logger.info("Trainee authenticated successfully: {}", username);
        } else {
            logger.warn("Authentication failed for trainee: {}", username);
        }

        return authenticated;
    }

    @Override
    public Trainee changeTraineePassword(String username, String oldPassword, String newPassword) {
        logger.debug("Changing password for trainee: {}", username);
        authenticationCheck(username, oldPassword);
        traineeEntityValidation.validatePasswordChange(oldPassword, newPassword);

        Trainee updatedTrainee = traineeRepository.updatePasswordByUsername(username, oldPassword, newPassword);
        logger.info("Successfully changed password for trainee: {}", username);
        return updatedTrainee;
    }

    @Override
    public Trainee activateTrainee(String username, String password) {
        logger.debug("Activating trainee: {}", username);
        authenticationCheck(username, password);

        Trainee trainee = traineeRepository.updateActiveStatusByUsername(username, true);
        logger.info("Successfully activated trainee: {}", username);
        return trainee;
    }

    @Override
    public Trainee deactivateTrainee(String username, String password) {
        logger.debug("Deactivating trainee: {}", username);
        authenticationCheck(username, password);

        Trainee trainee = traineeRepository.updateActiveStatusByUsername(username, false);
        logger.info("Successfully deactivated trainee: {}", username);
        return trainee;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainingsList(String username, String password, LocalDate fromDate, LocalDate toDate,
                                                   String trainerName, TrainingType trainingType) {
        logger.debug("Getting trainings list for trainee: {} with filters", username);
        authenticationCheck(username, password);
        traineeEntityValidation.validateDateRange(fromDate, toDate);

        List<Training> trainings = traineeRepository.findTrainingsByUsername(username, fromDate, toDate, trainerName, trainingType);
        logger.info("Retrieved {} trainings for trainee: {}", trainings.size(), username);
        return trainings;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getTrainersNotAssignedToTrainee(String username, String password) {
        logger.debug("Getting trainers not assigned to trainee: {}", username);
        authenticationCheck(username, password);

        List<Trainer> trainers = traineeRepository.findTrainersNotAssignedOnTraineeByUsername(username);
        logger.info("Found {} trainers not assigned to trainee: {}", trainers.size(), username);
        return trainers;
    }

    @Override
    public Trainee updateTraineeTrainersList(String username, String password, Set<Trainer> trainers) {
        logger.debug("Updating trainers list for trainee: {}", username);
        authenticationCheck(username, password);

        if (trainers == null) {
            logger.error("Failed to update trainers list - trainers set is null");
            throw new ValidationException("Trainers set cannot be null");
        }

        Trainee updatedTrainee = traineeRepository.updateTrainersListByUsername(username, trainers);
        logger.info("Successfully updated trainers list for trainee: {} with {} trainers",
                username, trainers.size());
        return updatedTrainee;
    }

    private void generateCredentials(Trainee trainee) {
        String username = usernameGenerator.generateUsername(trainee.getFirstName(), trainee.getLastName(), this::usernameExists);
        trainee.setUsername(username);

        String password = passwordGenerator.generatePassword();
        trainee.setPassword(password);

        logger.debug("Generated credentials for trainee - username: {}", username);
    }

    private boolean usernameExists(String username) {
        try {
            Trainee trainee = traineeRepository.findByUsername(username);
            return trainee != null;
        } catch (Exception e) {
            logger.debug("Username does not exist: {}", username);
            return false;
        }
    }

    private void authenticationCheck(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            logger.error("Authentication failed - username or password is null or empty");
            throw new AuthenticationException("Username and password cannot be null or empty");
        }

        if (!traineeRepository.existsByUsernameAndPassword(username, password)) {
            logger.error("Authentication failed for trainee: {}", username);
            throw new AuthenticationException(
                    "Authentication failed for trainee: " + username);
        }
    }
}
package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final TrainerEntityValidation trainerEntityValidation;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        logger.debug("Creating trainer profile: {} {}", trainer.getUser().getFirstName(), trainer.getUser().getLastName());
        trainerEntityValidation.validateTrainerForCreationOrUpdate(trainer);
        generateCredentials(trainer);

        trainer.getUser().setActive(true);

        Trainer savedTrainer = trainerRepository.save(trainer);
        logger.info("Successfully created trainer profile with username: {}", savedTrainer.getUser().getUsername());
        return savedTrainer;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer findTrainerByUsername(String username, String password) {
        logger.debug("Finding trainer by username: {}", username);
        authenticationCheck(username, password);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));
        logger.info("Found trainer with username: {}", username);
        return trainer;
    }

    @Override
    public Trainer updateTrainer(String username, String password, Trainer trainer) {
        logger.debug("Updating trainer profile: {}", username);
        authenticationCheck(username, password);
        trainerEntityValidation.validateTrainerForCreationOrUpdate(trainer);

        Trainer existingTrainer = findTrainerByUsername(username, password);

        existingTrainer.getUser().setFirstName(trainer.getUser().getFirstName());
        existingTrainer.getUser().setLastName(trainer.getUser().getLastName());
        existingTrainer.setSpecialization(trainer.getSpecialization());

        Trainer updatedTrainer = trainerRepository.save(existingTrainer);
        logger.info("Successfully updated trainer profile with username: {}", updatedTrainer.getUser().getUsername());
        return updatedTrainer;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticateTrainer(String username, String password) {
        logger.debug("Authenticating trainer: {}", username);

        boolean authenticated = trainerRepository.existsByUserUsernameAndUserPassword(username, password);

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

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));
        trainer.getUser().setPassword(newPassword);
        Trainer updatedTrainer = trainerRepository.save(trainer);
        logger.info("Successfully changed password for trainer: {}", username);
        return updatedTrainer;
    }

    @Override
    public Trainer activateTrainer(String username, String password) {
        logger.debug("Activating trainer: {}", username);
        authenticationCheck(username, password);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));
        trainer.getUser().setActive(true);
        Trainer updatedTrainer = trainerRepository.save(trainer);
        logger.info("Successfully activated trainer: {}", username);
        return updatedTrainer;
    }

    @Override
    public Trainer deactivateTrainer(String username, String password) {
        logger.debug("Deactivating trainer: {}", username);
        authenticationCheck(username, password);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));
        trainer.getUser().setActive(false);
        Trainer updatedTrainer = trainerRepository.save(trainer);
        logger.info("Successfully deactivated trainer: {}", username);
        return updatedTrainer;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainingsList(String username, String password, LocalDate fromDate, LocalDate toDate, String traineeName) {
        logger.debug("Getting trainings list for trainer: {} with filters", username);
        authenticationCheck(username, password);
        trainerEntityValidation.validateDateRange(fromDate, toDate);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));
        
        List<Training> trainings = trainer.getTrainings().stream()
                .filter(training -> {
                    if (fromDate != null && training.getTrainingDate().isBefore(fromDate)) return false;
                    if (toDate != null && training.getTrainingDate().isAfter(toDate)) return false;
                    if (traineeName != null && !traineeName.isEmpty()) {
                        String fullName = training.getTrainee().getUser().getFirstName() + " " + 
                                        training.getTrainee().getUser().getLastName();
                        return fullName.contains(traineeName);
                    }
                    return true;
                })
                .toList();
        logger.info("Retrieved {} trainings for trainer: {}", trainings.size(), username);
        return trainings;
    }

    private void generateCredentials(Trainer trainer) {
        String username = usernameGenerator.generateUsername(trainer.getUser().getFirstName(), trainer.getUser().getLastName(), this::usernameExists);
        trainer.getUser().setUsername(username);

        String password = passwordGenerator.generatePassword();
        trainer.getUser().setPassword(password);

        logger.debug("Generated credentials for trainer - username: {}", username);
    }

    private boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    private void authenticationCheck(String username, String password) {
        if (!trainerRepository.existsByUserUsernameAndUserPassword(username, password)) {
            throw new RuntimeException("Authentication failed for trainer: " + username);
        }
    }
}

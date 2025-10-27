package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.AuthenticationException;
import com.github.amangusss.gym_application.exception.TraineeNotFoundException;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.mapper.TraineeMapper;
import com.github.amangusss.gym_application.mapper.TrainerMapper;
import com.github.amangusss.gym_application.mapper.TrainingMapper;
import com.github.amangusss.gym_application.repository.TraineeRepository;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.repository.TrainingTypeRepository;
import com.github.amangusss.gym_application.repository.UserRepository;
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
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserRepository userRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final TraineeEntityValidation traineeEntityValidation;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;

    @Override
    @Transactional(readOnly = true)
    public boolean authenticateTrainee(String username, String password) {
        logger.debug("Authenticating trainee: {}", username);

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            logger.warn("Authentication failed - username or password is null or empty");
            return false;
        }

        boolean authenticated = traineeRepository.existsByUserUsernameAndUserPassword(username, password);

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

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with username: " + username));
        trainee.getUser().setPassword(newPassword);
        Trainee updatedTrainee = traineeRepository.save(trainee);
        logger.info("Successfully changed password for trainee: {}", username);
        return updatedTrainee;
    }

    @Override
    public TraineeDTO.Response.Registered createTrainee(TraineeDTO.Request.Register request) {
        logger.debug("Creating trainee profile: {} {}", request.firstName(), request.lastName());

        Trainee trainee = traineeMapper.toEntity(request);
        traineeEntityValidation.validateTraineeForCreationOrUpdate(trainee);
        generateCredentials(trainee);
        trainee.getUser().setActive(true);

        Trainee savedTrainee = traineeRepository.save(trainee);
        logger.info("Successfully created trainee profile with username: {}", savedTrainee.getUser().getUsername());
        return traineeMapper.toRegisteredResponse(savedTrainee);
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeDTO.Response.Profile findTraineeByUsername(String username, String password) {
        logger.debug("Finding trainee by username: {}", username);
        authenticationCheck(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with username: " + username));
        logger.info("Found trainee with username: {}", username);
        return traineeMapper.toProfileResponse(trainee);
    }

    @Override
    public TraineeDTO.Response.Updated updateTrainee(TraineeDTO.Request.Update request, String username, String password) {
        logger.debug("Updating trainee profile: {}", username);
        authenticationCheck(username, password);

        Trainee trainee = traineeMapper.toUpdateEntity(request);
        traineeEntityValidation.validateTraineeForCreationOrUpdate(trainee);

        Trainee existingTrainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with username: " + username));

        existingTrainee.getUser().setFirstName(trainee.getUser().getFirstName());
        existingTrainee.getUser().setLastName(trainee.getUser().getLastName());
        existingTrainee.setDateOfBirth(trainee.getDateOfBirth());
        existingTrainee.setAddress(trainee.getAddress());

        Trainee updatedTrainee = traineeRepository.save(existingTrainee);
        logger.info("Successfully updated trainee profile: {}", username);
        return traineeMapper.toUpdatedResponse(updatedTrainee);
    }

    @Override
    public void deleteTraineeByUsername(String username, String password) {
        logger.debug("Deleting trainee profile: {}", username);
        authenticationCheck(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with username: " + username));
        traineeRepository.delete(trainee);
        logger.info("Successfully deleted trainee profile: {} with cascade deletion of trainings", username);
    }

    @Override
    public void updateTraineeStatus(String username, String password, boolean isActive) {
        logger.debug("Updating trainee status for: {} to isActive: {}", username, isActive);
        authenticationCheck(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with username: " + username));
        trainee.getUser().setActive(isActive);
        traineeRepository.save(trainee);
        
        logger.info("Successfully updated trainee status for: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingDTO.Response.TraineeTraining> getTraineeTrainings(
            String username, String password, LocalDate periodFrom, LocalDate periodTo,
            String trainerName, String trainingType) {
        logger.debug("Fetching trainee trainings for username: {} with filters - periodFrom: {}, periodTo: {}, trainerName: {}, trainingType: {}",
                username, periodFrom, periodTo, trainerName, trainingType);
        authenticationCheck(username, password);
        traineeEntityValidation.validateDateRange(periodFrom, periodTo);

        TrainingType type = trainingType != null ?
                trainingTypeRepository.findByTypeName(trainingType).orElse(null) : null;

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with username: " + username));

        List<Training> trainings = trainee.getTrainings().stream()
                .filter(training -> {
                    if (periodFrom != null && training.getTrainingDate().isBefore(periodFrom)) return false;
                    if (periodTo != null && training.getTrainingDate().isAfter(periodTo)) return false;
                    if (trainerName != null && !trainerName.isEmpty()) {
                        String fullName = training.getTrainer().getUser().getFirstName() + " " +
                                training.getTrainer().getUser().getLastName();
                        if (!fullName.contains(trainerName)) return false;
                    }
                    return type == null || training.getTrainingType().equals(type);
                })
                .toList();

        List<TrainingDTO.Response.TraineeTraining> response = trainings.stream()
                .map(trainingMapper::toTraineeTrainingResponse)
                .toList();

        logger.info("Retrieved {} trainings for trainee: {}", response.size(), username);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerDTO.Response.Unassigned> getUnassignedTrainers(String username, String password) {
        logger.debug("Fetching unassigned trainers for trainee: {}", username);
        authenticationCheck(username, password);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with username: " + username));

        List<Trainer> allTrainers = trainerRepository.findAll();
        Set<Trainer> assignedTrainers = trainee.getTrainers();

        List<Trainer> unassignedTrainers = allTrainers.stream()
                .filter(trainer -> !assignedTrainers.contains(trainer))
                .toList();

        List<TrainerDTO.Response.Unassigned> response = unassignedTrainers.stream()
                .map(trainerMapper::toUnassignedResponse)
                .toList();

        logger.info("Found {} trainers not assigned to trainee: {}", response.size(), username);
        return response;
    }

    @Override
    public List<TrainerDTO.Response.InList> updateTraineeTrainers(
            String username, TraineeDTO.Request.UpdateTrainers request, String password) {
        logger.debug("Updating trainee's trainers list for username: {} with {} trainers",
                username, request.trainerUsernames().size());
        authenticationCheck(username, password);

        if (request.trainerUsernames() == null) {
            logger.error("Failed to update trainers list - trainers list is null");
            throw new ValidationException("Trainers list cannot be null");
        }

        Set<Trainer> trainers = request.trainerUsernames().stream()
                .map(trainerUsername -> trainerRepository.findByUserUsername(trainerUsername)
                        .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + trainerUsername)))
                .collect(Collectors.toSet());

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with username: " + username));
        trainee.setTrainers(trainers);
        Trainee updatedTrainee = traineeRepository.save(trainee);

        List<TrainerDTO.Response.InList> response = updatedTrainee.getTrainers().stream()
                .map(trainerMapper::toInListResponse)
                .toList();

        logger.info("Successfully updated trainers list for trainee: {} with {} trainers",
                username, trainers.size());
        return response;
    }

    private void generateCredentials(Trainee trainee) {
        String username = usernameGenerator.generateUsername(trainee.getUser().getFirstName(), trainee.getUser().getLastName(), this::usernameExists);
        trainee.getUser().setUsername(username);

        String password = passwordGenerator.generatePassword();
        trainee.getUser().setPassword(password);

        logger.debug("Generated credentials for trainee - username: {}", username);
    }

    private boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    private void authenticationCheck(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            logger.error("Authentication failed - username or password is null or empty");
            throw new AuthenticationException("Username and password cannot be null or empty");
        }

        if (!traineeRepository.existsByUserUsernameAndUserPassword(username, password)) {
            logger.error("Authentication failed for trainee: {}", username);
            throw new AuthenticationException(
                    "Authentication failed for trainee: " + username);
        }
    }
}

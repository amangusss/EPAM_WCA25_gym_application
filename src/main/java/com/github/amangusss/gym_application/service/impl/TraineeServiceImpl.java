package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.TraineeNotFoundException;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;
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
import com.github.amangusss.gym_application.validation.entity.EntityValidator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TraineeServiceImpl implements TraineeService {

    TraineeRepository traineeRepository;
    TrainerRepository trainerRepository;
    TrainingTypeRepository trainingTypeRepository;
    UserRepository userRepository;
    UsernameGenerator usernameGenerator;
    PasswordGenerator passwordGenerator;
    EntityValidator entityValidator;
    TraineeMapper traineeMapper;
    TrainerMapper trainerMapper;
    TrainingMapper trainingMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public Trainee changeTraineePassword(String username, String oldPassword, String newPassword) {
        log.debug("Changing password for trainee: {}", username);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with username: " + username));

        entityValidator.validatePasswordChange(oldPassword, newPassword, trainee.getUser().getPassword());

        String hashedPassword = passwordEncoder.encode(newPassword);
        trainee.getUser().setPassword(hashedPassword);
        Trainee updatedTrainee = traineeRepository.save(trainee);
        log.info("Successfully changed password for trainee: {}", username);
        return updatedTrainee;
    }

    @Override
    public TraineeDTO.Response.Registered createTrainee(TraineeDTO.Request.Register request) {
        log.debug("Creating trainee profile: {} {}", request.firstName(), request.lastName());

        Trainee trainee = traineeMapper.toEntity(request);
        entityValidator.validateTraineeForCreation(trainee);
        String plainPassword = generateCredentials(trainee);
        trainee.getUser().setActive(true);

        Trainee savedTrainee = traineeRepository.save(trainee);

        return traineeMapper.toRegisteredResponse(savedTrainee, plainPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeDTO.Response.Profile getTraineeProfile(String username) {
        log.debug("Finding trainee by username: {}", username);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with username: " + username));
        log.info("Found trainee with username: {}", username);
        return traineeMapper.toProfileResponse(trainee);
    }

    @Override
    public TraineeDTO.Response.Updated updateTrainee(TraineeDTO.Request.Update request, String username) {
        log.debug("Updating trainee profile: {}", username);

        Trainee trainee = traineeMapper.toUpdateEntity(request);
        entityValidator.validateTraineeForCreation(trainee);

        Trainee existingTrainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with username: " + username));

        existingTrainee.getUser().setFirstName(trainee.getUser().getFirstName());
        existingTrainee.getUser().setLastName(trainee.getUser().getLastName());
        existingTrainee.getUser().setActive(trainee.getUser().isActive());
        existingTrainee.setDateOfBirth(trainee.getDateOfBirth());
        existingTrainee.setAddress(trainee.getAddress());

        Trainee updatedTrainee = traineeRepository.save(existingTrainee);
        log.info("Successfully updated trainee profile: {}", username);
        return traineeMapper.toUpdatedResponse(updatedTrainee);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        log.debug("Deleting trainee profile: {}", username);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with username: " + username));
        traineeRepository.delete(trainee);
        log.info("Successfully deleted trainee profile: {} with cascade deletion of trainings", username);
    }

    @Override
    public void updateTraineeStatus(String username, boolean isActive) {
        log.debug("Updating trainee status for: {} to isActive: {}", username, isActive);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with username: " + username));
        trainee.getUser().setActive(isActive);
        traineeRepository.save(trainee);
        
        log.info("Successfully updated trainee status for: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingDTO.Response.TraineeTraining> getTraineeTrainings(
            String username, LocalDate periodFrom, LocalDate periodTo,
            String trainerName, String trainingType) {
        log.debug("Fetching trainee trainings for username: {} with filters - periodFrom: {}, periodTo: {}, trainerName: {}, trainingType: {}",
                username, periodFrom, periodTo, trainerName, trainingType);
        entityValidator.validateDateRange(periodFrom, periodTo);

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

        log.info("Retrieved {} trainings for trainee: {}", response.size(), username);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerDTO.Response.Unassigned> getUnassignedTrainers(String username) {
        log.debug("Fetching unassigned trainers for trainee: {}", username);

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

        log.info("Found {} trainers not assigned to trainee: {}", response.size(), username);
        return response;
    }

    @Override
    public List<TrainerDTO.Response.InList> updateTraineeTrainers(
            String username, TraineeDTO.Request.UpdateTrainers request) {
        log.debug("Updating trainee's trainers list for username: {} with {} trainers",
                username, request.trainerUsernames().size());

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

        log.info("Successfully updated trainers list for trainee: {} with {} trainers",
                username, trainers.size());
        return response;
    }

    private String generateCredentials(Trainee trainee) {
        String username = usernameGenerator.generateUsername(trainee.getUser().getFirstName(), trainee.getUser().getLastName(), this::usernameExists);
        trainee.getUser().setUsername(username);

        String password = passwordGenerator.generatePassword();
        String hashedPassword = passwordEncoder.encode(password);
        trainee.getUser().setPassword(hashedPassword);

        log.debug("Generated credentials for trainee - username: {}, password hashed", username);
        return password;
    }

    private boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}

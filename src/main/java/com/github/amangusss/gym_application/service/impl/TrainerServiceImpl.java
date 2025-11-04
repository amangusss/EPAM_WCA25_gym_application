package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.AuthenticationException;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;
import com.github.amangusss.gym_application.mapper.TrainerMapper;
import com.github.amangusss.gym_application.mapper.TrainingMapper;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.repository.UserRepository;
import com.github.amangusss.gym_application.service.TrainerService;
import com.github.amangusss.gym_application.service.TrainingTypeService;
import com.github.amangusss.gym_application.util.credentials.PasswordGenerator;
import com.github.amangusss.gym_application.util.credentials.UsernameGenerator;
import com.github.amangusss.gym_application.validation.trainer.TrainerEntityValidation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrainerServiceImpl implements TrainerService {

    TrainerRepository trainerRepository;
    UserRepository userRepository;
    UsernameGenerator usernameGenerator;
    PasswordGenerator passwordGenerator;
    TrainerEntityValidation trainerEntityValidation;
    TrainerMapper trainerMapper;
    TrainingMapper trainingMapper;
    TrainingTypeService trainingTypeService;

    @Override
    @Transactional(readOnly = true)
    public boolean authenticateTrainer(String username, String password) {
        log.debug("Authenticating trainer: {}", username);

        boolean authenticated = trainerRepository.existsByUserUsernameAndUserPassword(username, password);

        if (authenticated) {
            log.info("Trainer authenticated successfully: {}", username);
        } else {
            log.warn("Authentication failed for trainer: {}", username);
        }

        return authenticated;
    }

    @Override
    public Trainer changeTrainerPassword(String username, String oldPassword, String newPassword) {
        log.debug("Changing password for trainer: {}", username);
        authenticationCheck(username, oldPassword);
        trainerEntityValidation.validatePasswordChange(oldPassword, newPassword);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with username: " + username));
        trainer.getUser().setPassword(newPassword);
        Trainer updatedTrainer = trainerRepository.save(trainer);
        log.info("Successfully changed password for trainer: {}", username);
        return updatedTrainer;
    }

    @Override
    public TrainerDTO.Response.Registered registerTrainer(TrainerDTO.Request.Register request) {
        log.debug("Registering new trainer with specialization ID: {}", request.specialization());

        TrainingType specialization = trainingTypeService.findById(request.specialization());
        Trainer trainer = trainerMapper.toEntity(request, specialization);
        trainerEntityValidation.validateTrainerForCreationOrUpdate(trainer);
        generateCredentials(trainer);

        trainer.getUser().setActive(true);

        Trainer savedTrainer = trainerRepository.save(trainer);
        log.info("Successfully created trainer profile with username: {}", savedTrainer.getUser().getUsername());
        
        return trainerMapper.toRegisteredResponse(savedTrainer);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerDTO.Response.Profile getTrainerProfile(String username, String password) {
        log.debug("Fetching trainer profile for username: {}", username);
        authenticationCheck(username, password);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with username: " + username));
        log.info("Found trainer with username: {}", username);
        
        return trainerMapper.toProfileResponse(trainer);
    }

    @Override
    public TrainerDTO.Response.Updated updateTrainerProfile(TrainerDTO.Request.Update request, String username, String password) {
        log.debug("Updating trainer profile for username: {}", username);
        authenticationCheck(username, password);

        TrainingType specialization = trainingTypeService.findById(request.specialization());
        Trainer updateData = trainerMapper.toUpdateEntity(request, specialization);
        trainerEntityValidation.validateTrainerForCreationOrUpdate(updateData);

        Trainer existingTrainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with username: " + username));

        existingTrainer.getUser().setFirstName(updateData.getUser().getFirstName());
        existingTrainer.getUser().setLastName(updateData.getUser().getLastName());
        existingTrainer.setSpecialization(updateData.getSpecialization());

        Trainer updatedTrainer = trainerRepository.save(existingTrainer);
        log.info("Successfully updated trainer profile with username: {}", updatedTrainer.getUser().getUsername());
        
        return trainerMapper.toUpdatedResponse(updatedTrainer);
    }

    @Override
    public void updateTrainerStatus(String username, Boolean isActive, String password) {
        log.debug("Updating trainer status for username: {} to isActive: {}", username, isActive);
        authenticationCheck(username, password);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with username: " + username));
        trainer.getUser().setActive(isActive);
        trainerRepository.save(trainer);
        
        log.info("Successfully updated trainer status for username: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingDTO.Response.TrainerTraining> getTrainerTrainings(
            String username, String password, LocalDate periodFrom, LocalDate periodTo, String traineeName) {
        log.debug("Fetching trainer trainings for username: {} with filters - periodFrom: {}, periodTo: {}, traineeName: {}",
                username, periodFrom, periodTo, traineeName);
        authenticationCheck(username, password);
        trainerEntityValidation.validateDateRange(periodFrom, periodTo);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with username: " + username));
        
        List<Training> trainings = trainer.getTrainings().stream()
                .filter(training -> {
                    if (periodFrom != null && training.getTrainingDate().isBefore(periodFrom)) return false;
                    if (periodTo != null && training.getTrainingDate().isAfter(periodTo)) return false;
                    if (traineeName != null && !traineeName.isEmpty()) {
                        String fullName = training.getTrainee().getUser().getFirstName() + " " + 
                                        training.getTrainee().getUser().getLastName();
                        return fullName.contains(traineeName);
                    }
                    return true;
                })
                .toList();

        List<TrainingDTO.Response.TrainerTraining> response = trainings.stream()
                .map(trainingMapper::toTrainerTrainingResponse)
                .toList();

        log.info("Retrieved {} trainings for trainer: {}", response.size(), username);
        return response;
    }

    private void generateCredentials(Trainer trainer) {
        String username = usernameGenerator.generateUsername(trainer.getUser().getFirstName(), trainer.getUser().getLastName(), this::usernameExists);
        trainer.getUser().setUsername(username);

        String password = passwordGenerator.generatePassword();
        trainer.getUser().setPassword(password);

        log.debug("Generated credentials for trainer - username: {}", username);
    }

    private boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    private void authenticationCheck(String username, String password) {
        if (!trainerRepository.existsByUserUsernameAndUserPassword(username, password)) {
            throw new AuthenticationException("Authentication failed for trainer: " + username);
        }
    }
}

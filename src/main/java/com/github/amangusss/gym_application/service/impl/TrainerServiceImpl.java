package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;
import com.github.amangusss.gym_application.mapper.TrainerMapper;
import com.github.amangusss.gym_application.mapper.TrainingMapper;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.repository.UserRepository;
import com.github.amangusss.gym_application.service.TrainerService;
import com.github.amangusss.gym_application.service.TrainingTypeService;
import com.github.amangusss.gym_application.util.credentials.PasswordGenerator;
import com.github.amangusss.gym_application.util.credentials.UsernameGenerator;
import com.github.amangusss.gym_application.validation.entity.EntityValidator;

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
    EntityValidator entityValidator;
    TrainerMapper trainerMapper;
    TrainingMapper trainingMapper;
    TrainingTypeService trainingTypeService;
    org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public Trainer changeTrainerPassword(String username, String oldPassword, String newPassword) {
        log.debug("Changing password for trainer: {}", username);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with username: " + username));

        entityValidator.validatePasswordChange(oldPassword, newPassword, trainer.getUser().getPassword());

        String hashedPassword = passwordEncoder.encode(newPassword);
        trainer.getUser().setPassword(hashedPassword);
        Trainer updatedTrainer = trainerRepository.save(trainer);
        log.info("Successfully changed password for trainer: {}", username);
        return updatedTrainer;
    }

    @Override
    public TrainerDTO.Response.Registered registerTrainer(TrainerDTO.Request.Register request) {
        log.debug("Registering new trainer with specialization ID: {}", request.specialization());

        TrainingType specialization = trainingTypeService.findById(request.specialization());
        Trainer trainer = trainerMapper.toEntity(request, specialization);
        String plainPassword = generateCredentials(trainer);

        trainer.getUser().setActive(true);

        Trainer savedTrainer = trainerRepository.save(trainer);

        return trainerMapper.toRegisteredResponse(savedTrainer, plainPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerDTO.Response.Profile getTrainerProfile(String username) {
        log.debug("Fetching trainer profile for username: {}", username);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with username: " + username));
        log.info("Found trainer with username: {}", username);
        
        return trainerMapper.toProfileResponse(trainer);
    }

    @Override
    public TrainerDTO.Response.Updated updateTrainerProfile(TrainerDTO.Request.Update request, String username) {
        log.debug("Updating trainer profile for username: {}", username);

        TrainingType specialization = trainingTypeService.findById(request.specialization());
        Trainer updateData = trainerMapper.toUpdateEntity(request, specialization);

        Trainer existingTrainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with username: " + username));

        existingTrainer.getUser().setFirstName(updateData.getUser().getFirstName());
        existingTrainer.getUser().setLastName(updateData.getUser().getLastName());
        existingTrainer.getUser().setActive(updateData.getUser().isActive());
        existingTrainer.setSpecialization(updateData.getSpecialization());

        Trainer updatedTrainer = trainerRepository.save(existingTrainer);

        log.info("Successfully updated trainer profile with username: {}", updatedTrainer.getUser().getUsername());
        
        return trainerMapper.toUpdatedResponse(updatedTrainer);
    }

    @Override
    public void updateTrainerStatus(String username, Boolean isActive) {
        log.debug("Updating trainer isActive for username: {} to isActive: {}", username, isActive);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with username: " + username));
        trainer.getUser().setActive(isActive);
        trainerRepository.save(trainer);

        log.info("Successfully updated trainer isActive for username: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingDTO.Response.TrainerTraining> getTrainerTrainings(
            String username, LocalDate periodFrom, LocalDate periodTo, String traineeName) {
        log.debug("Fetching trainer trainings for username: {} with filters - periodFrom: {}, periodTo: {}, traineeName: {}",
                username, periodFrom, periodTo, traineeName);
        entityValidator.validateDateRange(periodFrom, periodTo);

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

    private String generateCredentials(Trainer trainer) {
        String username = usernameGenerator.generateUsername(trainer.getUser().getFirstName(), trainer.getUser().getLastName(), this::usernameExists);
        trainer.getUser().setUsername(username);

        String password = passwordGenerator.generatePassword();
        String hashedPassword = passwordEncoder.encode(password);
        trainer.getUser().setPassword(hashedPassword);

        log.debug("Generated credentials for trainer - username: {}, password hashed", username);
        return password;
    }

    private boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}

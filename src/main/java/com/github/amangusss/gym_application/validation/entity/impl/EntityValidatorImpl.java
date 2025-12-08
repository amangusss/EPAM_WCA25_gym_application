package com.github.amangusss.gym_application.validation.entity.impl;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.validation.entity.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntityValidatorImpl implements EntityValidator {

    private final PasswordEncoder passwordEncoder;

    @Override
    public void validatePasswordChange(String oldPassword, String newPassword, String currentHashedPassword) {
        log.debug("Validating password change");

        if (!passwordEncoder.matches(oldPassword, currentHashedPassword)) {
            log.error("Password validation failed - old password does not match");
            throw new ValidationException("Invalid old password");
        }

        if (oldPassword.equals(newPassword)) {
            log.error("Password validation failed - new password is the same as old password");
            throw new ValidationException("New password must be different from old password");
        }

        log.debug("Password change validation passed");
    }

    @Override
    public void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            log.error("Date range validation failed - fromDate {} is after toDate {}", fromDate, toDate);
            throw new ValidationException("From date cannot be after to date");
        }

        log.debug("Date range validation passed: fromDate={}, toDate={}", fromDate, toDate);
    }

    @Override
    public void validateTrainee(Trainee trainee) {
        log.debug("Validating trainee entity");

        if (trainee == null) {
            log.error("Validation failed - trainee is null");
            throw new ValidationException("Trainee cannot be null");
        }

        if (trainee.getUser() == null) {
            log.error("Validation failed - trainee user is null");
            throw new ValidationException("Trainee user cannot be null");
        }

        if (!trainee.getUser().isActive()) {
            log.error("Validation failed - trainee is not active: {}", trainee.getUser().getUsername());
            throw new ValidationException("Trainee is not active");
        }

        log.debug("Trainee validation passed");
    }

    @Override
    public void validateTrainer(Trainer trainer) {
        log.debug("Validating trainer entity");

        if (trainer == null) {
            log.error("Validation failed - trainer is null");
            throw new ValidationException("Trainer cannot be null");
        }

        if (trainer.getUser() == null) {
            log.error("Validation failed - trainer user is null");
            throw new ValidationException("Trainer user cannot be null");
        }

        if (!trainer.getUser().isActive()) {
            log.error("Validation failed - trainer is not active: {}", trainer.getUser().getUsername());
            throw new ValidationException("Trainer is not active");
        }

        if (trainer.getSpecialization() == null) {
            log.error("Validation failed - trainer specialization is null");
            throw new ValidationException("Trainer specialization cannot be null");
        }

        log.debug("Trainer validation passed");
    }

    @Override
    public void validateTraining(Training training) {
        log.debug("Validating training entity");

        if (training == null) {
            log.error("Validation failed - training is null");
            throw new ValidationException("Training cannot be null");
        }

        if (training.getTrainee() == null) {
            log.error("Validation failed - training trainee is null");
            throw new ValidationException("Training trainee cannot be null");
        }

        if (training.getTrainer() == null) {
            log.error("Validation failed - training trainer is null");
            throw new ValidationException("Training trainer cannot be null");
        }

        if (StringUtils.isBlank(training.getTrainingName())) {
            log.error("Validation failed - training name is blank");
            throw new ValidationException("Training name cannot be blank");
        }

        if (training.getTrainingType() == null) {
            log.error("Validation failed - training type is null");
            throw new ValidationException("Training type cannot be null");
        }

        if (training.getTrainingDate() == null) {
            log.error("Validation failed - training date is null");
            throw new ValidationException("Training date cannot be null");
        }

        if (training.getTrainingDuration() == null || training.getTrainingDuration() <= 0) {
            log.error("Validation failed - training duration is invalid: {}", training.getTrainingDuration());
            throw new ValidationException("Training duration must be positive");
        }

        log.debug("Training validation passed");
    }

    @Override
    public void validateTraineeForCreation(Trainee trainee) {
        log.debug("Validating trainee for creation");

        if (trainee == null) {
            log.error("Validation failed - trainee is null");
            throw new ValidationException("Trainee cannot be null");
        }

        if (trainee.getUser() == null) {
            log.error("Validation failed - trainee user is null");
            throw new ValidationException("Trainee user cannot be null");
        }

        if (StringUtils.isBlank(trainee.getUser().getFirstName())) {
            log.error("Validation failed - first name is null or empty");
            throw new ValidationException("First name is required");
        }

        if (StringUtils.isBlank(trainee.getUser().getLastName())) {
            log.error("Validation failed - last name is null or empty");
            throw new ValidationException("Last name is required");
        }

        if (StringUtils.isBlank(trainee.getAddress())) {
            log.error("Validation failed - address is null or empty");
            throw new ValidationException("Address is required");
        }

        log.debug("Trainee creation validation passed");
    }

    @Override
    public void validateTrainerForCreation(Trainer trainer) {
        log.debug("Validating trainer for creation");

        if (trainer == null) {
            log.error("Validation failed - trainer is null");
            throw new ValidationException("Trainer cannot be null");
        }

        if (trainer.getUser() == null) {
            log.error("Validation failed - trainer user is null");
            throw new ValidationException("Trainer user cannot be null");
        }

        if (StringUtils.isBlank(trainer.getUser().getFirstName())) {
            log.error("Validation failed - first name is null or empty");
            throw new ValidationException("First name is required");
        }

        if (StringUtils.isBlank(trainer.getUser().getLastName())) {
            log.error("Validation failed - last name is null or empty");
            throw new ValidationException("Last name is required");
        }

        if (trainer.getSpecialization() == null) {
            log.error("Validation failed - specialization is null");
            throw new ValidationException("Specialization is required");
        }

        log.debug("Trainer creation validation passed");
    }
}

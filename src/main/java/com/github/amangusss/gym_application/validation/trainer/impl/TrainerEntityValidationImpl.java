package com.github.amangusss.gym_application.validation.trainer.impl;

import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.validation.trainer.TrainerEntityValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TrainerEntityValidationImpl implements TrainerEntityValidation {

    public static final Logger logger = LoggerFactory.getLogger(TrainerEntityValidationImpl.class);

    @Override
    public void validateTrainerForCreationOrUpdate(Trainer trainer) {
        if (trainer == null) {
            logger.error("Validation failed - trainer is null");
            throw new ValidationException("Trainer cannot be null");
        }

        if (trainer.getUser().getFirstName() == null || trainer.getUser().getFirstName().trim().isEmpty()) {
            logger.error("Validation failed - first name is null or empty");
            throw new ValidationException("First name is required");
        }

        if (trainer.getUser().getLastName() == null || trainer.getUser().getLastName().trim().isEmpty()) {
            logger.error("Validation failed - last name is null or empty");
            throw new ValidationException("Last name is required");
        }

        if (trainer.getSpecialization() == null || trainer.getSpecialization().toString().trim().isEmpty()) {
            logger.error("Validation failed - specialization is null or empty");
            throw new ValidationException("Specialization is required");
        }
    }

    @Override
    public void validatePasswordChange(String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            logger.error("Validation failed - old password is null or empty");
            throw new ValidationException("Old password is required");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            logger.error("Validation failed - new password is null or empty");
            throw new ValidationException("New password is required");
        }

        if (oldPassword.equals(newPassword)) {
            logger.error("Validation failed - new password is the same as old password");
            throw new ValidationException("New password must be different from old password");
        }
    }

    @Override
    public void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            logger.error("Validation failed - fromDate is after toDate");
            throw new ValidationException("From date cannot be after to date");
        }
    }
}

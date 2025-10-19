package com.github.amangusss.gym_application.validation.trainee.impl;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.exception.ValidationException;

import com.github.amangusss.gym_application.validation.trainee.TraineeEntityValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TraineeEntityValidationImpl implements TraineeEntityValidation {

    public static final Logger logger = LoggerFactory.getLogger(TraineeEntityValidationImpl.class);

    @Override
    public void validateTraineeForCreationOrUpdate(Trainee trainee) {
        if (trainee == null) {
            logger.error("Validation failed - trainee is null");
            throw new ValidationException("Trainee cannot be null");
        }

        if (trainee.getFirstName() == null || trainee.getFirstName().trim().isEmpty()) {
            logger.error("Validation failed - first name is null or empty");
            throw new ValidationException("First name is required");
        }

        if (trainee.getLastName() == null || trainee.getLastName().trim().isEmpty()) {
            logger.error("Validation failed - last name is null or empty");
            throw new ValidationException("Last name is required");
        }

        if (trainee.getAddress() == null || trainee.getAddress().trim().isEmpty()) {
            logger.error("Validation failed - address is null or empty");
            throw new ValidationException("Address is required");
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

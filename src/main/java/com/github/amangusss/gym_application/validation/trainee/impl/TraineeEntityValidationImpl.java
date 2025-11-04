package com.github.amangusss.gym_application.validation.trainee.impl;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.validation.trainee.TraineeEntityValidation;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class TraineeEntityValidationImpl implements TraineeEntityValidation {

    @Override
    public void validateTraineeForCreationOrUpdate(Trainee trainee) {
        if (trainee == null) {
            log.error("Validation failed - trainee is null");
            throw new ValidationException("Trainee cannot be null");
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
    }

    @Override
    public void validatePasswordChange(String oldPassword, String newPassword) {
        if (StringUtils.isBlank(oldPassword)) {
            log.error("Validation failed - old password is null or empty");
            throw new ValidationException("Old password is required");
        }

        if (StringUtils.isBlank(newPassword)) {
            log.error("Validation failed - new password is null or empty");
            throw new ValidationException("New password is required");
        }

        if (oldPassword.equals(newPassword)) {
            log.error("Validation failed - new password is the same as old password");
            throw new ValidationException("New password must be different from old password");
        }
    }

    @Override
    public void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            log.error("Validation failed - fromDate is after toDate");
            throw new ValidationException("From date cannot be after to date");
        }
    }
}

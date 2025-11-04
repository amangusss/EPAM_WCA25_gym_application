package com.github.amangusss.gym_application.validation.training.impl;

import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.validation.training.TrainingEntityValidation;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrainingEntityValidationImpl implements TrainingEntityValidation {

    @Override
    public void validateTrainingForAddition(Training training) {
        if (training == null) {
            log.error("Validation failed - training is null");
            throw new ValidationException("Training cannot be null");
        }

        if (StringUtils.isBlank(training.getTrainingName())) {
            log.error("Validation failed - training name is null or empty");
            throw new ValidationException("Training name is required");
        }

        if (StringUtils.isBlank(training.getTrainingType().getTypeName())) {
            log.error("Validation failed - training type is null or empty");
            throw new ValidationException("Training type is required");
        }

        if (training.getTrainingDate() == null) {
            log.error("Validation failed - training date is null");
            throw new ValidationException("Training date is required");
        }

        if (training.getTrainingDuration() <= 0) {
            log.error("Validation failed - training duration is less than or equal to zero");
            throw new ValidationException("Training duration must be greater than zero");
        }

        if (training.getTrainer() == null) {
            log.error("Validation failed - trainer is null");
            throw new ValidationException("Trainer is required");
        }

        if (training.getTrainee() == null) {
            log.error("Validation failed - trainee is null");
            throw new ValidationException("Trainee is required");
        }
    }
}

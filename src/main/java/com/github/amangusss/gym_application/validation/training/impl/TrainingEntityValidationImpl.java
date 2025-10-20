package com.github.amangusss.gym_application.validation.training.impl;

import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.validation.training.TrainingEntityValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TrainingEntityValidationImpl implements TrainingEntityValidation {

    public static final Logger logger = LoggerFactory.getLogger(TrainingEntityValidationImpl.class);

    @Override
    public void validateTrainingForAddition(Training training) {
        if (training == null) {
            logger.error("Validation failed - training is null");
            throw new ValidationException("Training cannot be null");
        }

        if (training.getTrainingName() == null || training.getTrainingName().trim().isEmpty()) {
            logger.error("Validation failed - training name is null or empty");
            throw new ValidationException("Training name is required");
        }

        if (training.getTrainingType() == null || training.getTrainingType().toString().trim().isEmpty()) {
            logger.error("Validation failed - training type is null or empty");
            throw new ValidationException("Training type is required");
        }

        if (training.getTrainingDate() == null) {
            logger.error("Validation failed - training date is null");
            throw new ValidationException("Training date is required");
        }

        if (training.getTrainingDuration() <= 0) {
            logger.error("Validation failed - training duration is less than or equal to zero");
            throw new ValidationException("Training duration must be greater than zero");
        }

        if (training.getTrainer() == null) {
            logger.error("Validation failed - trainer is null");
            throw new ValidationException("Trainer is required");
        }

        if (training.getTrainee() == null) {
            logger.error("Validation failed - trainee is null");
            throw new ValidationException("Trainee is required");
        }
    }
}

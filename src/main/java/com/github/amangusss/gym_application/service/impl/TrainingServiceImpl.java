package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.TraineeNotFoundException;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.repository.TraineeDAO;
import com.github.amangusss.gym_application.repository.TrainerDAO;
import com.github.amangusss.gym_application.repository.TrainingDAO;

import com.github.amangusss.gym_application.service.TrainingService;
import com.github.amangusss.gym_application.util.constants.LoggerConstants;
import com.github.amangusss.gym_application.util.constants.ValidationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    public static final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingDAO trainingDAO;
    private TrainerDAO trainerDAO;
    private TraineeDAO traineeDAO;

    @Autowired
    public void setTrainingDAO(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Override
    public Training createTraining(Training training) {
        if (training == null) {
            throw new ValidationException(ValidationConstants.TRAINING_NULL);
        }

        validateTraining(training);

        logger.debug(LoggerConstants.TRAINING_CREATING, training.getTrainingName());

        Trainee trainee = traineeDAO.findById(training.getTraineeId());
        if (trainee == null) {
            throw new TraineeNotFoundException(String.format(ValidationConstants.TRAINEE_NOT_FOUND_BY_ID, training.getTraineeId()));
        }

        Trainer trainer = trainerDAO.findById(training.getTrainerId());
        if (trainer == null) {
            throw new TrainerNotFoundException(String.format(ValidationConstants.TRAINER_NOT_FOUND_BY_ID, training.getTrainerId()));
        }

        if (!trainee.isActive()) {
            throw new ValidationException(String.format(ValidationConstants.TRAINEE_NOT_ACTIVE, training.getTraineeId()));
        }

        if (!trainer.isActive()) {
            throw new ValidationException(String.format(ValidationConstants.TRAINER_NOT_ACTIVE, training.getTrainerId()));
        }

        Training savedTraining = trainingDAO.save(training);
        logger.info(LoggerConstants.TRAINING_CREATED, savedTraining.getId());
        return savedTraining;
    }

    @Override
    public Training findTraining(Long trainingId) {
        return trainingDAO.findById(trainingId);
    }

    @Override
    public List<Training> findAllTrainings() {
        return trainingDAO.findAll();
    }

    private void validateTraining(Training training) {
        if (training.getTrainerId() == null) {
            throw new ValidationException(ValidationConstants.TRAINER_ID_REQUIRED);
        }

        if (training.getTraineeId() == null) {
            throw new ValidationException(ValidationConstants.TRAINEE_ID_REQUIRED);
        }

        if (training.getTrainingName() == null || training.getTrainingName().trim().isEmpty()) {
            throw new ValidationException(ValidationConstants.TRAINING_NAME_NULL);
        }

        if (training.getTrainingType() == null) {
            throw new ValidationException(ValidationConstants.TRAINING_TYPE_NULL);
        }

        if (training.getTrainingDate() == null) {
            throw new ValidationException(ValidationConstants.TRAINING_DATE_NULL);
        }

        if (training.getTrainingDuration() == null) {
            throw new ValidationException(ValidationConstants.TRAINING_DURATION_INVALID);
        }
    }
}
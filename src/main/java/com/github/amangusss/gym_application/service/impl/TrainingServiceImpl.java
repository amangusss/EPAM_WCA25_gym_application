package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.entity.Trainee;
import com.github.amangusss.gym_application.entity.Trainer;
import com.github.amangusss.gym_application.entity.Training;
import com.github.amangusss.gym_application.repository.dao.TraineeDAO;
import com.github.amangusss.gym_application.repository.dao.TrainerDAO;
import com.github.amangusss.gym_application.repository.dao.TrainingDAO;

import com.github.amangusss.gym_application.service.TrainingService;
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
            throw new IllegalArgumentException("training cannot be null");
        }

        validateTraining(training);

        logger.debug("Creating training: {}", training.getTrainingName());

        Trainee trainee = traineeDAO.findById(training.getTraineeId());
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee with id " + training.getTraineeId() + " not found");
        }

        Trainer trainer = trainerDAO.findById(training.getTrainerId());
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer with id " + training.getTrainerId() + " not found");
        }

        if (!trainee.isActive()) {
            throw new IllegalArgumentException("Trainee with id " + training.getTraineeId() + " is not active");
        }

        if (!trainer.isActive()) {
            throw new IllegalArgumentException("Trainer with id " + training.getTrainerId() + " is not active");
        }

        Training savedTraining = trainingDAO.save(training);
        logger.info("Training created successfully with id: {}", savedTraining.getId());
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
            throw new IllegalArgumentException("Trainer id is required");
        }

        if (training.getTraineeId() == null) {
            throw new IllegalArgumentException("Trainee id is required");
        }

        if (training.getTrainingName() == null || training.getTrainingName().trim().isEmpty()) {
            throw new IllegalArgumentException("Training name is required");
        }

        if (training.getTrainingType() == null) {
            throw new IllegalArgumentException("Training type is required");
        }

        if (training.getTrainingDate() == null) {
            throw new IllegalArgumentException("Training date is required");
        }

        if (training.getTrainingDuration() == null) {
            throw new IllegalArgumentException("Training duration is required");
        }
    }
}
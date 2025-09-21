package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.training.Training;

import java.util.List;

public interface TrainingService {

    Training createTraining(Training training);
    Training findTraining(Long trainingId);
    List<Training> findAllTrainings();
}
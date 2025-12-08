package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.dto.training.TrainingDTO;

public interface TrainingService {

    void addTraining(TrainingDTO.Request.Create request);
    void deleteTraining(Long trainingId);
}
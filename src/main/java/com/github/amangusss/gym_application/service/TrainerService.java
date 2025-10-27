package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.trainer.Trainer;

import java.time.LocalDate;
import java.util.List;

public interface TrainerService {

    boolean authenticateTrainer(String username, String password);
    Trainer changeTrainerPassword(String username, String oldPassword, String newPassword);
    
    TrainerDTO.Response.Registered registerTrainer(TrainerDTO.Request.Register request);
    TrainerDTO.Response.Profile getTrainerProfile(String username, String password);
    TrainerDTO.Response.Updated updateTrainerProfile(TrainerDTO.Request.Update request, String username, String password);
    void updateTrainerStatus(String username, Boolean isActive, String password);
    List<TrainingDTO.Response.TrainerTraining> getTrainerTrainings(
            String username, String password, LocalDate periodFrom, LocalDate periodTo, String traineeName);
}

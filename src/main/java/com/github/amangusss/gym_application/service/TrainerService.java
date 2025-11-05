package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.trainer.Trainer;

import java.time.LocalDate;
import java.util.List;

public interface TrainerService {

    Trainer changeTrainerPassword(String username, String oldPassword, String newPassword);
    
    TrainerDTO.Response.Registered registerTrainer(TrainerDTO.Request.Register request);
    TrainerDTO.Response.Profile getTrainerProfile(String username);
    TrainerDTO.Response.Updated updateTrainerProfile(TrainerDTO.Request.Update request, String username);
    void updateTrainerStatus(String username, Boolean isActive);
    List<TrainingDTO.Response.TrainerTraining> getTrainerTrainings(
            String username, LocalDate periodFrom, LocalDate periodTo, String traineeName);
}

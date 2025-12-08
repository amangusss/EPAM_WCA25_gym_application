package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.trainee.Trainee;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService {

    Trainee changeTraineePassword(String username, String oldPassword, String newPassword);
    
    TraineeDTO.Response.Registered createTrainee(TraineeDTO.Request.Register request);
    TraineeDTO.Response.Profile getTraineeProfile(String username);
    TraineeDTO.Response.Updated updateTrainee(TraineeDTO.Request.Update request, String username);
    void deleteTraineeByUsername(String username);
    void updateTraineeStatus(String username, boolean isActive);
    List<TrainingDTO.Response.TraineeTraining> getTraineeTrainings(
            String username, LocalDate periodFrom, LocalDate periodTo,
            String trainerName, String trainingType);
    List<TrainerDTO.Response.Unassigned> getUnassignedTrainers(String username);
    List<TrainerDTO.Response.InList> updateTraineeTrainers(
            String username, TraineeDTO.Request.UpdateTrainers request);
}

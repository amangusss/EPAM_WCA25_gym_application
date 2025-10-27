package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.trainee.Trainee;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService {

    boolean authenticateTrainee(String username, String password);
    Trainee changeTraineePassword(String username, String oldPassword, String newPassword);
    
    TraineeDTO.Response.Registered createTrainee(TraineeDTO.Request.Register request);
    TraineeDTO.Response.Profile findTraineeByUsername(String username, String password);
    TraineeDTO.Response.Updated updateTrainee(TraineeDTO.Request.Update request, String username, String password);
    void deleteTraineeByUsername(String username, String password);
    void updateTraineeStatus(String username, String password, boolean isActive);
    List<TrainingDTO.Response.TraineeTraining> getTraineeTrainings(
            String username, String password, LocalDate periodFrom, LocalDate periodTo,
            String trainerName, String trainingType);
    List<TrainerDTO.Response.Unassigned> getUnassignedTrainers(String username, String password);
    List<TrainerDTO.Response.InList> updateTraineeTrainers(
            String username, TraineeDTO.Request.UpdateTrainers request, String password);
}

package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface TraineeService {

    Trainee createTrainee(Trainee trainee);
    Trainee findTraineeByUsername(String username, String password);
    Trainee updateTrainee(String username, String password, Trainee trainee);
    void deleteTraineeByUsername(String username, String password);
    boolean authenticateTrainee(String username, String password);
    Trainee changeTraineePassword(String username, String oldPassword, String newPassword);
    Trainee activateTrainee(String username, String password);
    Trainee deactivateTrainee(String username, String password);
    List<Training> getTraineeTrainingsList(String username, String password, LocalDate fromDate, LocalDate toDate,
                                           String trainerName, TrainingType trainingType);
    List<Trainer> getTrainersNotAssignedToTrainee(String username, String password);
    Trainee updateTraineeTrainersList(String username, String password, Set<Trainer> trainers);
}
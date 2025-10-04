package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface TraineeRepository {

    Trainee save(Trainee trainee);
    boolean existsByUsernameAndPassword(String username, String password);
    Trainee findByUsername(String username);
    Trainee updatePasswordByUsername(String username, String oldPassword, String newPassword);
    Trainee update(Trainee trainee);
    Trainee updateActiveStatusByUsername(String username, boolean isActive);
    void deleteByUsername(String username);
    List<Training> findTrainingsByUsername(String username, LocalDate fromDate, LocalDate toDate,
                                           String trainerName, TrainingType trainingType);
    List<Trainer> findTrainersNotAssignedOnTraineeByUsername(String username);
    Trainee updateTrainersListByUsername(String username, Set<Trainer> trainers);
}
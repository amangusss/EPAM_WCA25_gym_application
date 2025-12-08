package com.github.amangusss.gym_application.validation.entity;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;

import java.time.LocalDate;

public interface EntityValidator {

    void validatePasswordChange(String oldPassword, String newPassword, String currentHashedPassword);
    void validateDateRange(LocalDate fromDate, LocalDate toDate);
    void validateTrainee(Trainee trainee);
    void validateTrainer(Trainer trainer);
    void validateTraining(Training training);
    void validateTraineeForCreation(Trainee trainee);
    void validateTrainerForCreation(Trainer trainer);
}

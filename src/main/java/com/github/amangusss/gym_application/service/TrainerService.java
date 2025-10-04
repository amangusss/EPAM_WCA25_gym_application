package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainerService {

    Trainer createTrainer(Trainer trainer);
    Trainer findTrainerByUsername(String username, String password);
    Trainer updateTrainer(String username, String password, Trainer trainer);
    boolean authenticateTrainer(String username, String password);
    Trainer changeTrainerPassword(String username, String oldPassword, String newPassword);
    Trainer activateTrainer(String username, String password);
    Trainer deactivateTrainer(String username, String password);
    List<Training> getTrainerTrainingsList(String username, String password, LocalDate fromDate, LocalDate toDate,
                                           String traineeName);
}
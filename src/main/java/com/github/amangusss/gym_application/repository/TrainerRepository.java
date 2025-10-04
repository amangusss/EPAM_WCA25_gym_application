package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainerRepository {

    Trainer save(Trainer trainer);
    boolean existsByUsernameAndPassword(String username, String password);
    Trainer findByUsername(String username);
    Trainer updatePasswordByUsername(String username, String oldPassword, String newPassword);
    Trainer update(Trainer trainer);
    Trainer updateActiveStatusByUsername(String username, boolean isActive);
    List<Training> findTrainingsByUsername(String username, LocalDate fromDate, LocalDate toDate, String traineeName);
}
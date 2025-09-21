package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.Trainer;

import java.util.List;

public interface TrainerService {

    Trainer createTrainer(Trainer trainer);
    Trainer updateTrainer(Trainer trainer);
    Trainer findTrainerById(Long id);
    List<Trainer> findAllTrainers();
}

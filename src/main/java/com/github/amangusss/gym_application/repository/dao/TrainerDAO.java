package com.github.amangusss.gym_application.repository.dao;

import com.github.amangusss.gym_application.entity.Trainer;
import com.github.amangusss.gym_application.entity.TrainingType;

public interface TrainerDAO {
    Trainer save(Trainer trainer);
    Trainer update(Trainer trainer);
    Trainer findById(Long id);
    Iterable<Trainer> findAll();
    Trainer findByUsername(String username);
    Iterable<Trainer> findBySpecialization(TrainingType specialization);
    Iterable<Trainer> findActiveTrainers();
    boolean existsByUsername(String username);
}
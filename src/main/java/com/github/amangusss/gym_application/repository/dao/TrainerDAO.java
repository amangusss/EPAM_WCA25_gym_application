package com.github.amangusss.gym_application.repository.dao;

import com.github.amangusss.gym_application.entity.Trainer;
import com.github.amangusss.gym_application.entity.TrainingType;

import java.util.List;

public interface TrainerDAO {
    Trainer save(Trainer trainer);
    Trainer update(Trainer trainer);
    Trainer findById(Long id);
    List<Trainer> findAll();
    Trainer findByUsername(String username);
    List<Trainer> findBySpecialization(TrainingType specialization);
    List<Trainer> findActiveTrainers();
    boolean existsByUsername(String username);
}
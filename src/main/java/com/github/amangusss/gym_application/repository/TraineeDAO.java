package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.trainee.Trainee;

import java.util.List;

public interface TraineeDAO {
    Trainee save(Trainee trainee);
    Trainee update(Trainee trainee);
    boolean deleteById(Long id);
    Trainee findById(Long id);
    List<Trainee> findAll();
    Trainee findByUsername(String username);
    List<Trainee> findActiveTrainees();
    boolean existsByUsername(String username);
}
package com.github.amangusss.gym_application.repository.dao;

import com.github.amangusss.gym_application.entity.Trainee;

public interface TraineeDAO {
    Trainee save(Trainee trainee);
    Trainee update(Trainee trainee);
    boolean deleteById(Long id);
    Trainee findById(Long id);
    Iterable<Trainee> findAll();
    Trainee findByUsername(String username);
    Iterable<Trainee> findActiveTrainees();
    boolean existsByUsername(String username);
}
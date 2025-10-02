package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.trainee.Trainee;

import java.util.List;

public interface TraineeService {

    Trainee createTrainee(Trainee trainee);
    Trainee updateTrainee(Trainee trainee);
    boolean deleteTrainee(Long id);
    Trainee findTraineeById(Long id);
    List<Trainee> findAllTrainees();
}

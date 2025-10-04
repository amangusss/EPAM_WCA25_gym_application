package com.github.amangusss.gym_application.repository;

import com.github.amangusss.gym_application.entity.training.Training;

public interface TrainingRepository {

    Training save(Training training);
}
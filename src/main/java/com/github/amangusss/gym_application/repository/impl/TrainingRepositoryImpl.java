package com.github.amangusss.gym_application.repository.impl;

import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.repository.TrainingRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Training save(Training training) {

        //TODO: create dto and mapper
        if (training.getTrainee() == null ||
            training.getTrainer() == null ||
            training.getTrainingName() == null ||
            training.getTrainingType() == null ||
            training.getTrainingDate() == null ||
            training.getTrainingDuration() == null) {

            throw new ValidationException("All required fields must be set for Training");
        }

        entityManager.persist(training);
        return training;
    }
}

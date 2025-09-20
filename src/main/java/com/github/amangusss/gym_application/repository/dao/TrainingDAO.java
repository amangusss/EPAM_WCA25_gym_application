package com.github.amangusss.gym_application.repository.dao;

import com.github.amangusss.gym_application.entity.Training;
import com.github.amangusss.gym_application.entity.TrainingType;

import java.time.LocalDate;

public interface TrainingDAO {
    Training save(Training training);
    Training findById(Long id);
    Iterable<Training> findAll();
    Iterable<Training> findByTrainerId(Long trainerId);
    Iterable<Training> findByTraineeId(Long traineeId);
    Iterable<Training> findByTrainingType(TrainingType trainingType);
    Iterable<Training> findByDateRange(LocalDate startDate, LocalDate endDate);
}

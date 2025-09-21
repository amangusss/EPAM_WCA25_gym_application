package com.github.amangusss.gym_application.repository.dao;

import com.github.amangusss.gym_application.entity.Training;
import com.github.amangusss.gym_application.entity.TrainingType;

import java.time.LocalDate;
import java.util.List;

public interface TrainingDAO {
    Training save(Training training);
    Training findById(Long id);
    List<Training> findAll();
    List<Training> findByTrainerId(Long trainerId);
    List<Training> findByTraineeId(Long traineeId);
    List<Training> findByTrainingType(TrainingType trainingType);
    List<Training> findByDateRange(LocalDate startDate, LocalDate endDate);
}

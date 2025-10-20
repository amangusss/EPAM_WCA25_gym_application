package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.entity.TrainingType;

import java.util.List;

public interface TrainingTypeService {

    List<TrainingType> getAllTrainingTypes();
    TrainingType findById(Long id);
}

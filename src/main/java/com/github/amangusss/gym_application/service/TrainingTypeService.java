package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.dto.trainingtype.TrainingTypeDTO;
import com.github.amangusss.gym_application.entity.TrainingType;

import java.util.List;

public interface TrainingTypeService {

    TrainingType findById(Long id);
    List<TrainingTypeDTO.Response.TrainingType> getAllTrainingTypes();
}

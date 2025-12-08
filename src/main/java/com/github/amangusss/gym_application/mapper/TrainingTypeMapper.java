package com.github.amangusss.gym_application.mapper;

import com.github.amangusss.gym_application.dto.trainingType.TrainingTypeDTO;
import com.github.amangusss.gym_application.entity.TrainingType;

import org.springframework.stereotype.Component;

@Component
public class TrainingTypeMapper {

    public TrainingTypeDTO.Response.TrainingType toResponse(TrainingType trainingType) {
        return new TrainingTypeDTO.Response.TrainingType(
                trainingType.getId(),
                trainingType.getTypeName()
        );
    }
}

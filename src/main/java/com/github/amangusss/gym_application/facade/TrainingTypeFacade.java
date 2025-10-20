package com.github.amangusss.gym_application.facade;

import com.github.amangusss.gym_application.dto.trainingtype.TrainingTypeDTO;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.mapper.TrainingTypeMapper;
import com.github.amangusss.gym_application.service.TrainingTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingTypeFacade {

    private final TrainingTypeService trainingTypeService;
    private final TrainingTypeMapper trainingTypeMapper;

    public List<TrainingTypeDTO.Response.TrainingType> getAllTrainingTypes() {
        log.debug("Fetching all training types");

        List<TrainingType> trainingTypes = trainingTypeService.getAllTrainingTypes();

        List<TrainingTypeDTO.Response.TrainingType> response = trainingTypes.stream()
                .map(trainingTypeMapper::toResponse)
                .toList();

        log.debug("Retrieved {} training types", response.size());
        return response;
    }
}

package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.dto.trainingtype.TrainingTypeDTO;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.exception.TrainingTypeNotFoundException;
import com.github.amangusss.gym_application.mapper.TrainingTypeMapper;
import com.github.amangusss.gym_application.repository.TrainingTypeRepository;
import com.github.amangusss.gym_application.service.TrainingTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    @Transactional(readOnly = true)
    public TrainingType findById(Long id) {
        log.debug("Finding training type by id: {}", id);
        return trainingTypeRepository.findById(id)
                .orElseThrow(() -> new TrainingTypeNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingTypeDTO.Response.TrainingType> getAllTrainingTypes() {
        log.debug("Fetching all training types");

        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();

        List<TrainingTypeDTO.Response.TrainingType> response = trainingTypes.stream()
                .map(trainingTypeMapper::toResponse)
                .toList();

        log.info("Retrieved {} training types", response.size());
        return response;
    }
}

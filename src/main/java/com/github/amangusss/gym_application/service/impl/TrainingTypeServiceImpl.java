package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.exception.TrainingTypeNotFoundException;
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

    @Override
    @Transactional(readOnly = true)
    public List<TrainingType> getAllTrainingTypes() {
        log.info("Fetching all training types");
        return trainingTypeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingType findById(Long id) {
        log.info("Finding training type by id: {}", id);
        return trainingTypeRepository.findById(id)
                .orElseThrow(() -> new TrainingTypeNotFoundException(id));
    }
}

package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.repository.TrainingRepository;
import com.github.amangusss.gym_application.service.TrainingService;
import com.github.amangusss.gym_application.validation.training.TrainingEntityValidation;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    public static final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingRepository trainingRepository;
    private final TrainingEntityValidation trainingEntityValidation;

    @Override
    public Training addTraining(Training training) {
        logger.debug("Adding training: {}", training);
        trainingEntityValidation.validateTrainingForAddition(training);

        Training savedTraining = trainingRepository.save(training);
        logger.info("Successfully added training with id: {}", savedTraining.getId());
        return savedTraining;
    }
}

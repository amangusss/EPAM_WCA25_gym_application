package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.TraineeNotFoundException;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;
import com.github.amangusss.gym_application.repository.TraineeRepository;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.repository.TrainingRepository;
import com.github.amangusss.gym_application.service.TrainingService;
import com.github.amangusss.gym_application.validation.entity.impl.EntityValidatorImpl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrainingServiceImpl implements TrainingService {

    TrainingRepository trainingRepository;
    TraineeRepository traineeRepository;
    TrainerRepository trainerRepository;
    EntityValidatorImpl entityValidator;

    @Override
    public void addTraining(TrainingDTO.Request.Create request) {
        log.debug("Adding new training: {} for trainee: {} and trainer: {}",
                request.trainingName(), request.traineeUsername(), request.trainerUsername());

        Trainee trainee = traineeRepository.findByUserUsername(request.traineeUsername())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found: " + request.traineeUsername()));

        entityValidator.validateTrainee(trainee);

        Trainer trainer = trainerRepository.findByUserUsername(request.trainerUsername())
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + request.trainerUsername()));

        entityValidator.validateTrainer(trainer);

        Training training = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName(request.trainingName())
                .trainingType(trainer.getSpecialization())
                .trainingDate(request.trainingDate())
                .trainingDuration(request.trainingDuration())
                .build();

        entityValidator.validateTraining(training);

        trainingRepository.save(training);

        log.info("Successfully added training: {} for trainee: {} and trainer: {}",
                training.getTrainingName(), trainee.getUser().getUsername(), trainer.getUser().getUsername());
    }
}

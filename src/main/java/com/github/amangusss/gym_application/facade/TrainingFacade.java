package com.github.amangusss.gym_application.facade;

import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.TraineeNotFoundException;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;
import com.github.amangusss.gym_application.repository.TraineeRepository;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.service.TrainingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingFacade {

    private final TrainingService trainingService;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public void addTraining(TrainingDTO.Request.Create request) {
        log.debug("Adding new training: {} for trainee: {} and trainer: {}",
                request.trainingName(), request.traineeUsername(), request.trainerUsername());

        Trainee trainee = traineeRepository.findByUserUsername(request.traineeUsername())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found: " + request.traineeUsername()));

        Trainer trainer = trainerRepository.findByUserUsername(request.trainerUsername())
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + request.trainerUsername()));

        Training training = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName(request.trainingName())
                .trainingType(trainer.getSpecialization())
                .trainingDate(request.trainingDate())
                .trainingDuration(request.trainingDuration())
                .build();

        trainingService.addTraining(training);

        log.debug("Training added successfully: {}", request.trainingName());
    }
}

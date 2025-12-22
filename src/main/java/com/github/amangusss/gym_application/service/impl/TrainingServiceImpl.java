package com.github.amangusss.gym_application.service.impl;

import com.github.amangusss.gym_application.client.WorkloadServiceClient;
import com.github.amangusss.gym_application.client.dto.workload.WorkloadDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.exception.TraineeNotFoundException;
import com.github.amangusss.gym_application.exception.TrainerNotFoundException;
import com.github.amangusss.gym_application.exception.TrainingNotFoundException;
import com.github.amangusss.gym_application.repository.TraineeRepository;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.repository.TrainingRepository;
import com.github.amangusss.gym_application.service.TrainingService;
import com.github.amangusss.gym_application.validation.entity.EntityValidator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
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
    EntityValidator entityValidator;
    WorkloadServiceClient workloadServiceClient;

    @Override
    public void addTraining(TrainingDTO.Request.Create request) {
        String transactionId = MDC.get("transactionId");

        log.debug("[{}] Adding new training: {} for trainee: {} and trainer: {}",
                transactionId, request.trainingName(), request.traineeUsername(), request.trainerUsername());

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

        sendWorkload(trainer, training, "ADD", transactionId);

        log.info("Successfully added training: {} for trainee: {} and trainer: {}",
                training.getTrainingName(), trainee.getUser().getUsername(), trainer.getUser().getUsername());
    }

    @Override
    public void deleteTraining(Long trainingId) {
        String transactionId = MDC.get("transactionId");

        log.debug("[{}] Deleting training with id: {}", transactionId, trainingId);

        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new TrainingNotFoundException("Training not found with id: " + trainingId));

        Trainer trainer = training.getTrainer();

        sendWorkload(trainer, training, "DELETE", transactionId);

        trainingRepository.delete(training);

        log.info("[{}] Successfully deleted training: {} (id={})",
                transactionId, training.getTrainingName(), trainingId);
    }

    private void sendWorkload(Trainer trainer, Training training, String actionType, String transactionId) {
        WorkloadDTO.Request.Workload workloadRequest = WorkloadDTO.Request.Workload.builder()
                .username(trainer.getUser().getUsername())
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .status(trainer.getUser().isActive() ? "active" : "inactive")
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .actionType(actionType)
                .build();

        workloadServiceClient.sendWorkload(workloadRequest, transactionId);
    }
}

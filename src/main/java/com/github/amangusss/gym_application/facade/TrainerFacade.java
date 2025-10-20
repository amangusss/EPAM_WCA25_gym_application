package com.github.amangusss.gym_application.facade;

import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.mapper.TrainerMapper;
import com.github.amangusss.gym_application.mapper.TrainingMapper;
import com.github.amangusss.gym_application.service.TrainerService;
import com.github.amangusss.gym_application.service.TrainingTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerFacade {

    private final TrainerService trainerService;
    private final TrainingTypeService trainingTypeService;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;

    public TrainerDTO.Response.Registered registerTrainer(TrainerDTO.Request.Register request) {
        log.debug("Registering new trainer with specialization ID: {}", request.specialization());

        TrainingType specialization = trainingTypeService.findById(request.specialization());
        Trainer trainer = trainerMapper.toEntity(request, specialization);
        Trainer created = trainerService.createTrainer(trainer);

        log.debug("Trainer registered successfully with username: {}", created.getUser().getUsername());
        return trainerMapper.toRegisteredResponse(created);
    }

    public TrainerDTO.Response.Profile getTrainerProfile(String username, String password) {
        log.debug("Fetching trainer profile for username: {}", username);

        Trainer trainer = trainerService.findTrainerByUsername(username, password);

        log.debug("Trainer profile retrieved successfully for username: {}", username);
        return trainerMapper.toProfileResponse(trainer);
    }

    public TrainerDTO.Response.Updated updateTrainer(TrainerDTO.Request.Update request, String password) {
        log.debug("Updating trainer profile for username: {}", request.username());

        Trainer updateData = Trainer.builder()
                .user(com.github.amangusss.gym_application.entity.User.builder()
                        .firstName(request.firstName())
                        .lastName(request.lastName())
                        .isActive(request.isActive())
                        .build())
                .build();

        Trainer updated = trainerService.updateTrainer(request.username(), password, updateData);

        log.debug("Trainer profile updated successfully for username: {}", request.username());
        return trainerMapper.toUpdatedResponse(updated);
    }

    public void updateTrainerStatus(String username, Boolean isActive, String password) {
        log.debug("Updating trainer status for username: {} to isActive: {}", username, isActive);

        if (isActive) {
            trainerService.activateTrainer(username, password);
        } else {
            trainerService.deactivateTrainer(username, password);
        }

        log.debug("Trainer status updated successfully for username: {}", username);
    }

    public List<TrainingDTO.Response.TrainerTraining> getTrainerTrainings(
            String username, String password, LocalDate periodFrom, LocalDate periodTo, String traineeName) {
        log.debug("Fetching trainer trainings for username: {} with filters - periodFrom: {}, periodTo: {}, traineeName: {}",
                username, periodFrom, periodTo, traineeName);

        List<Training> trainings = trainerService.getTrainerTrainingsList(
                username, password, periodFrom, periodTo, traineeName);

        List<TrainingDTO.Response.TrainerTraining> response = trainings.stream()
                .map(trainingMapper::toTrainerTrainingResponse)
                .toList();

        log.debug("Retrieved {} trainings for trainer: {}", response.size(), username);
        return response;
    }
}

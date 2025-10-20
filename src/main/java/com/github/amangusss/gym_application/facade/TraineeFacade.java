package com.github.amangusss.gym_application.facade;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.trainer.UpdateTrainersDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.mapper.TraineeMapper;
import com.github.amangusss.gym_application.mapper.TrainerMapper;
import com.github.amangusss.gym_application.mapper.TrainingMapper;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.repository.TrainingTypeRepository;
import com.github.amangusss.gym_application.service.TraineeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TraineeFacade {

    private final TraineeService traineeService;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    public TraineeDTO.Response.Registered registerTrainee(TraineeDTO.Request.Register request) {
        log.debug("Registering new trainee");

        Trainee trainee = traineeMapper.toEntity(request);
        Trainee created = traineeService.createTrainee(trainee);

        log.debug("Trainee registered successfully with username: {}", created.getUser().getUsername());
        return traineeMapper.toRegisteredResponse(created);
    }

    public TraineeDTO.Response.Profile getTraineeProfile(String username, String password) {
        log.debug("Fetching trainee profile for username: {}", username);

        Trainee trainee = traineeService.findTraineeByUsername(username, password);

        log.debug("Trainee profile retrieved successfully for username: {}", username);
        return traineeMapper.toProfileResponse(trainee);
    }

    public TraineeDTO.Response.Updated updateTrainee(TraineeDTO.Request.Update request, String password) {
        log.debug("Updating trainee profile for username: {}", request.username());

        Trainee updateData = Trainee.builder()
                .user(com.github.amangusss.gym_application.entity.User.builder()
                        .firstName(request.firstName())
                        .lastName(request.lastName())
                        .isActive(request.isActive())
                        .build())
                .dateOfBirth(request.dateOfBirth())
                .address(request.address())
                .build();

        Trainee updated = traineeService.updateTrainee(request.username(), password, updateData);

        log.debug("Trainee profile updated successfully for username: {}", request.username());
        return traineeMapper.toUpdatedResponse(updated);
    }

    public void deleteTrainee(String username, String password) {
        log.debug("Deleting trainee profile for username: {}", username);

        traineeService.deleteTraineeByUsername(username, password);

        log.debug("Trainee profile deleted successfully for username: {}", username);
    }

    public void updateTraineeStatus(String username, Boolean isActive, String password) {
        log.debug("Updating trainee status for username: {} to isActive: {}", username, isActive);

        if (isActive) {
            traineeService.activateTrainee(username, password);
        } else {
            traineeService.deactivateTrainee(username, password);
        }

        log.debug("Trainee status updated successfully for username: {}", username);
    }

    public List<TrainingDTO.Response.TraineeTraining> getTraineeTrainings(
            String username, String password, LocalDate periodFrom, LocalDate periodTo,
            String trainerName, String trainingType) {
        log.debug("Fetching trainee trainings for username: {} with filters - periodFrom: {}, periodTo: {}, trainerName: {}, trainingType: {}",
                username, periodFrom, periodTo, trainerName, trainingType);

        TrainingType type = trainingType != null ?
                trainingTypeRepository.findByTypeName(trainingType).orElse(null) : null;

        List<Training> trainings = traineeService.getTraineeTrainingsList(
                username, password, periodFrom, periodTo, trainerName, type);

        List<TrainingDTO.Response.TraineeTraining> response = trainings.stream()
                .map(trainingMapper::toTraineeTrainingResponse)
                .toList();

        log.debug("Retrieved {} trainings for trainee: {}", response.size(), username);
        return response;
    }

    public List<TrainerDTO.Response.Unassigned> getUnassignedTrainers(String username, String password) {
        log.debug("Fetching unassigned trainers for trainee: {}", username);

        List<Trainer> trainers = traineeService.getTrainersNotAssignedToTrainee(username, password);

        List<TrainerDTO.Response.Unassigned> response = trainers.stream()
                .map(trainerMapper::toUnassignedResponse)
                .toList();

        log.debug("Retrieved {} unassigned trainers for trainee: {}", response.size(), username);
        return response;
    }

    public List<TrainerDTO.Response.InList> updateTraineeTrainers(
            String username, UpdateTrainersDTO.Request.Update request, String password) {
        log.debug("Updating trainee's trainers list for username: {} with {} trainers",
                username, request.trainerUsernames().size());

        Set<Trainer> trainers = request.trainerUsernames().stream()
                .map(trainerUsername -> trainerRepository.findByUserUsername(trainerUsername)
                        .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + trainerUsername)))
                .collect(Collectors.toSet());

        Trainee updated = traineeService.updateTraineeTrainersList(username, password, trainers);

        List<TrainerDTO.Response.InList> response = updated.getTrainers().stream()
                .map(trainerMapper::toInListResponse)
                .toList();

        log.debug("Trainee's trainers list updated successfully for username: {}", username);
        return response;
    }
}

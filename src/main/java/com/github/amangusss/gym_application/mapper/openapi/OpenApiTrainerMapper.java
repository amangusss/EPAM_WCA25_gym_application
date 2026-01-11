package com.github.amangusss.gym_application.mapper.openapi;

import com.github.amangusss.dto.generated.TraineeBasicInfo;
import com.github.amangusss.dto.generated.TrainerProfileResponse;
import com.github.amangusss.dto.generated.TrainerRegistrationRequest;
import com.github.amangusss.dto.generated.TrainerRegistrationResponse;
import com.github.amangusss.dto.generated.TrainerUpdateRequest;
import com.github.amangusss.dto.generated.TrainingResponse;
import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OpenApiTrainerMapper {

    public TrainerDTO.Request.Register toInternalTrainerCreate(TrainerRegistrationRequest request) {
        return new TrainerDTO.Request.Register(
                request.getFirstName(),
                request.getLastName(),
                request.getSpecializationId()
        );
    }

    public TrainerRegistrationResponse toGeneratedTrainerRegistered(TrainerDTO.Response.Registered registered) {
        return TrainerRegistrationResponse.builder()
                .username(registered.username())
                .password(registered.password())
                .build();
    }

    public TrainerDTO.Request.Update toInternalTrainerUpdate(TrainerUpdateRequest request) {
        return new TrainerDTO.Request.Update(
                request.getFirstName(),
                request.getLastName(),
                request.getSpecializationId(),
                request.getIsActive()
        );
    }

    public TrainerProfileResponse toGeneratedTrainerProfile(TrainerDTO.Response.Profile profile) {
        return TrainerProfileResponse.builder()
                .firstName(profile.firstName())
                .lastName(profile.lastName())
                .specialization(profile.specializationName())
                .isActive(profile.isActive())
                .trainees(profile.trainees().stream()
                        .map(this::toGeneratedTraineeBasicInfo)
                        .collect(Collectors.toList()))
                .build();
    }

    public TrainerProfileResponse toGeneratedTrainerUpdated(TrainerDTO.Response.Updated updated) {
        return TrainerProfileResponse.builder()
                .firstName(updated.firstName())
                .lastName(updated.lastName())
                .specialization(updated.specializationName())
                .isActive(updated.isActive())
                .trainees(updated.trainees().stream()
                        .map(this::toGeneratedTraineeBasicInfo)
                        .collect(Collectors.toList()))
                .build();
    }

    private TraineeBasicInfo toGeneratedTraineeBasicInfo(TraineeDTO.Response.InList info) {
        return TraineeBasicInfo.builder()
                .username(info.username())
                .firstName(info.firstName())
                .lastName(info.lastName())
                .build();
    }

    public TrainingResponse toGeneratedTrainingResponse(TrainingDTO.Response.TrainerTraining training) {
        return TrainingResponse.builder()
                .trainingName(training.trainingName())
                .trainingDate(training.trainingDate())
                .trainingType(training.trainingType())
                .trainingDuration(training.trainingDuration())
                .trainerName(training.traineeName())
                .build();
    }
}

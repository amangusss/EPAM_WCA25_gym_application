package com.github.amangusss.gym_application.mapper.openapi;

import com.github.amangusss.dto.generated.TraineeProfileResponse;
import com.github.amangusss.dto.generated.TraineeRegistrationRequest;
import com.github.amangusss.dto.generated.TraineeRegistrationResponse;
import com.github.amangusss.dto.generated.TraineeUpdateRequest;
import com.github.amangusss.dto.generated.TrainerBasicInfo;
import com.github.amangusss.dto.generated.TrainerUnassignedResponse;
import com.github.amangusss.dto.generated.TrainingResponse;
import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OpenApiTraineeMapper {

    public TraineeDTO.Request.Register toInternalTraineeCreate(TraineeRegistrationRequest request) {
        return new TraineeDTO.Request.Register(
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress()
        );
    }

    public TraineeRegistrationResponse toGeneratedTraineeRegistered(TraineeDTO.Response.Registered registered) {
        return TraineeRegistrationResponse.builder()
                .username(registered.username())
                .password(registered.password())
                .build();
    }

    public TraineeDTO.Request.Update toInternalTraineeUpdate(TraineeUpdateRequest request) {
        return new TraineeDTO.Request.Update(
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress(),
                request.getIsActive()
        );
    }

    public TraineeProfileResponse toGeneratedTraineeProfile(TraineeDTO.Response.Profile profile) {
        return TraineeProfileResponse.builder()
                .firstName(profile.firstName())
                .lastName(profile.lastName())
                .dateOfBirth(profile.dateOfBirth())
                .address(profile.address())
                .isActive(profile.isActive())
                .trainers(profile.trainers().stream()
                        .map(this::toGeneratedTrainerBasicInfo)
                        .collect(Collectors.toList()))
                .build();
    }

    public TraineeProfileResponse toGeneratedTraineeUpdated(TraineeDTO.Response.Updated updated) {
        return TraineeProfileResponse.builder()
                .firstName(updated.firstName())
                .lastName(updated.lastName())
                .dateOfBirth(updated.dateOfBirth())
                .address(updated.address())
                .isActive(updated.isActive())
                .trainers(updated.trainers().stream()
                        .map(this::toGeneratedTrainerBasicInfo)
                        .collect(Collectors.toList()))
                .build();
    }

    private TrainerBasicInfo toGeneratedTrainerBasicInfo(TrainerDTO.Response.InList info) {
        return TrainerBasicInfo.builder()
                .username(info.username())
                .firstName(info.firstName())
                .lastName(info.lastName())
                .specialization(info.specializationName())
                .build();
    }

    public TrainingResponse toGeneratedTrainingResponse(TrainingDTO.Response.TraineeTraining training) {
        return TrainingResponse.builder()
                .trainingName(training.trainingName())
                .trainingDate(training.trainingDate())
                .trainingType(training.trainingType())
                .trainingDuration(training.trainingDuration())
                .trainerName(training.trainerName())
                .build();
    }

    public TrainerUnassignedResponse toGeneratedTrainerUnassigned(TrainerDTO.Response.Unassigned trainer) {
        return TrainerUnassignedResponse.builder()
                .username(trainer.username())
                .firstName(trainer.firstName())
                .lastName(trainer.lastName())
                .specialization(trainer.specializationName())
                .build();
    }

    public TrainerBasicInfo toGeneratedTrainerBasicInfoFromInList(TrainerDTO.Response.InList trainer) {
        return TrainerBasicInfo.builder()
                .username(trainer.username())
                .firstName(trainer.firstName())
                .lastName(trainer.lastName())
                .specialization(trainer.specializationName())
                .build();
    }
}

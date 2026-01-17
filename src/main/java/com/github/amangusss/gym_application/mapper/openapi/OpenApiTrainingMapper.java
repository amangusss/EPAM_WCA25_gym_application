package com.github.amangusss.gym_application.mapper.openapi;

import com.github.amangusss.dto.generated.TrainingCreateRequest;
import com.github.amangusss.dto.generated.TrainingResponse;
import com.github.amangusss.dto.generated.UserCredentialsResponse;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import org.springframework.stereotype.Component;

@Component
public class OpenApiTrainingMapper {

    public TrainingDTO.Request.Create toInternalTrainingCreate(TrainingCreateRequest request) {
        return new TrainingDTO.Request.Create(
                request.getTraineeUsername(),
                request.getTrainerUsername(),
                request.getTrainingName(),
                request.getTrainingDate(),
                request.getTrainingDuration()
        );
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

    public TrainingResponse toGeneratedTrainingResponseFromTrainer(TrainingDTO.Response.TrainerTraining training) {
        return TrainingResponse.builder()
                .trainingName(training.trainingName())
                .trainingDate(training.trainingDate())
                .trainingType(training.trainingType())
                .trainingDuration(training.trainingDuration())
                .trainerName(training.traineeName())
                .build();
    }

    public UserCredentialsResponse toGeneratedCredentials(String username, String password) {
        return UserCredentialsResponse.builder()
                .username(username)
                .password(password)
                .build();
    }
}

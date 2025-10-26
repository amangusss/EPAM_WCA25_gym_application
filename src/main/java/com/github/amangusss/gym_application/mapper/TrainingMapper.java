package com.github.amangusss.gym_application.mapper;

import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.entity.training.Training;

import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {

    public TrainingDTO.Response.TraineeTraining toTraineeTrainingResponse(Training training) {
        return new TrainingDTO.Response.TraineeTraining(
                training.getTrainingName(),
                training.getTrainingDate(),
                training.getTrainingType().getTypeName(),
                training.getTrainingDuration(),
                training.getTrainer().getUser().getFirstName() + " " + training.getTrainer().getUser().getLastName()
        );
    }

    public TrainingDTO.Response.TrainerTraining toTrainerTrainingResponse(Training training) {
        return new TrainingDTO.Response.TrainerTraining(
                training.getTrainingName(),
                training.getTrainingDate(),
                training.getTrainingType().getTypeName(),
                training.getTrainingDuration(),
                training.getTrainee().getUser().getFirstName() + " " + training.getTrainee().getUser().getLastName()
        );
    }
}

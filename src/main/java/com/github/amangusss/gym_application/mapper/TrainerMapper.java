package com.github.amangusss.gym_application.mapper;

import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.User;
import com.github.amangusss.gym_application.entity.trainer.Trainer;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TrainerMapper {

    private final TraineeMapper traineeMapper;

    public TrainerMapper(@Lazy TraineeMapper traineeMapper) {
        this.traineeMapper = traineeMapper;
    }

    public TrainerDTO.Response.Registered toRegisteredResponse(Trainer trainer) {
        return new TrainerDTO.Response.Registered(
                trainer.getUser().getUsername(),
                trainer.getUser().getPassword()
        );
    }

    public TrainerDTO.Response.Profile toProfileResponse(Trainer trainer) {
        return new TrainerDTO.Response.Profile(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecialization().getTypeName(),
                trainer.getUser().isActive(),
                trainer.getTrainees().stream()
                        .map(traineeMapper::toInListResponse)
                        .collect(Collectors.toList())
        );
    }

    public TrainerDTO.Response.Updated toUpdatedResponse(Trainer trainer) {
        return new TrainerDTO.Response.Updated(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecialization().getTypeName(),
                trainer.getUser().isActive(),
                trainer.getTrainees().stream()
                        .map(traineeMapper::toInListResponse)
                        .collect(Collectors.toList())
        );
    }

    public TrainerDTO.Response.InList toInListResponse(Trainer trainer) {
        return new TrainerDTO.Response.InList(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecialization().getTypeName()
        );
    }

    public TrainerDTO.Response.Unassigned toUnassignedResponse(Trainer trainer) {
        return new TrainerDTO.Response.Unassigned(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecialization().getTypeName()
        );
    }

    public Trainer toEntity(TrainerDTO.Request.Register request, TrainingType specialization) {
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .isActive(false)
                .build();

        return Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();
    }

    public Trainer toUpdateEntity(TrainerDTO.Request.Update request, TrainingType specialization) {
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .isActive(request.isActive())
                .build();

        return Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();
    }
}

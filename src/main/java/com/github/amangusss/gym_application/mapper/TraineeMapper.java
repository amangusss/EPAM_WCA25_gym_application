package com.github.amangusss.gym_application.mapper;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.entity.CustomUser;
import com.github.amangusss.gym_application.entity.trainee.Trainee;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TraineeMapper {

    private final TrainerMapper trainerMapper;

    public TraineeMapper(@Lazy TrainerMapper trainerMapper) {
        this.trainerMapper = trainerMapper;
    }

    public TraineeDTO.Response.Registered toRegisteredResponse(Trainee trainee, String plainPassword) {
        return new TraineeDTO.Response.Registered(
                trainee.getUser().getUsername(),
                plainPassword
        );
    }

    public TraineeDTO.Response.Profile toProfileResponse(Trainee trainee) {
        return new TraineeDTO.Response.Profile(
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().isActive(),
                trainee.getTrainers().stream()
                        .map(trainerMapper::toInListResponse)
                        .collect(Collectors.toList())
        );
    }

    public TraineeDTO.Response.Updated toUpdatedResponse(Trainee trainee) {
        return new TraineeDTO.Response.Updated(
                trainee.getUser().getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().isActive(),
                trainee.getTrainers().stream()
                        .map(trainerMapper::toInListResponse)
                        .collect(Collectors.toList())
        );
    }

    public TraineeDTO.Response.InList toInListResponse(Trainee trainee) {
        return new TraineeDTO.Response.InList(
                trainee.getUser().getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName()
        );
    }

    public Trainee toUpdateEntity(TraineeDTO.Request.Update request) {
        CustomUser user = CustomUser.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .isActive(request.isActive())
                .build();

        return Trainee.builder()
                .user(user)
                .dateOfBirth(request.dateOfBirth())
                .address(request.address())
                .build();
    }

    public Trainee toEntity(TraineeDTO.Request.Register request) {
        CustomUser user = CustomUser.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .isActive(false)
                .build();

        return Trainee.builder()
                .user(user)
                .dateOfBirth(request.dateOfBirth())
                .address(request.address())
                .build();
    }
}

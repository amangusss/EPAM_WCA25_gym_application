package com.github.amangusss.gym_application.facade;

import com.github.amangusss.gym_application.dto.auth.AuthDTO;
import com.github.amangusss.gym_application.service.TraineeService;
import com.github.amangusss.gym_application.service.TrainerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public boolean login(AuthDTO.Request.Login request) {
        log.debug("Authenticating user: {}", request.username());

        boolean authenticated = traineeService.authenticateTrainee(request.username(), request.password()) ||
                               trainerService.authenticateTrainer(request.username(), request.password());

        log.debug("Authentication result for user {}: {}", request.username(), authenticated);
        return authenticated;
    }

    public boolean changePassword(AuthDTO.Request.ChangePassword request) {
        log.debug("Changing password for user: {}", request.username());

        try {
            traineeService.changeTraineePassword(
                    request.username(), request.oldPassword(), request.newPassword());
            log.debug("Password changed successfully for trainee: {}", request.username());
            return true;
        } catch (Exception e) {
            try {
                trainerService.changeTrainerPassword(
                        request.username(), request.oldPassword(), request.newPassword());
                log.debug("Password changed successfully for trainer: {}", request.username());
                return true;
            } catch (Exception ex) {
                log.debug("Password change failed for user: {}", request.username());
                return false;
            }
        }
    }
}

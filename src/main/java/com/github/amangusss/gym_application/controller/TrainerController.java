package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.metrics.MetricsExecutor;
import com.github.amangusss.gym_application.metrics.TrainerMetrics;
import com.github.amangusss.gym_application.service.TrainerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Trainer", description = "Trainer management APIs")
public class TrainerController {

    TrainerService trainerService;
    TrainerMetrics trainerMetrics;
    MetricsExecutor metricsExecutor;

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Register new trainer", description = "Creates a new trainer profile and generates username and password")
    public ResponseEntity<TrainerDTO.Response.Registered> registerTrainer(
            @Valid @RequestBody TrainerDTO.Request.Register request) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] POST /api/trainers/register", transactionId);

        TrainerDTO.Response.Registered response = metricsExecutor.executeWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("register_trainer")
                        .endpoint("/api/trainers/register")
                        .method("POST")
                        .build(),
                () -> trainerService.registerTrainer(request),
                result -> {
                    trainerMetrics.incrementTrainerRegistered();
                    trainerMetrics.recordTrainerBySpecialization(request.specialization());
                },
                ex -> trainerMetrics.incrementTrainerOperationFailed("register")
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/profile", produces = "application/json")
    @Operation(summary = "Get current trainer profile",
            description = "Retrieves the profile of the currently authenticated trainer. Username is taken from JWT token.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<TrainerDTO.Response.Profile> getCurrentTrainerProfile(
            Authentication authentication) {
        String transactionId = UUID.randomUUID().toString();
        String username = authentication.getName();
        log.info("[Transaction: {}] GET /api/trainers/profile", transactionId);

        TrainerDTO.Response.Profile response = metricsExecutor.executeWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("get_current_trainer_profile")
                        .endpoint("/api/trainers/profile")
                        .method("GET")
                        .build(),
                () -> trainerService.getTrainerProfile(username),
                result -> trainerMetrics.recordTrainerProfileView(username),
                ex -> trainerMetrics.incrementTrainerOperationFailed("get_profile")
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{username}", produces = "application/json")
    @Operation(summary = "Get trainer profile", description = "Retrieves trainer profile by username. Requires JWT authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<TrainerDTO.Response.Profile> getTrainerProfile(
            @Parameter(description = "Trainer username") @PathVariable String username,
            Authentication authentication) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainers/{}", transactionId, username);

        if (!authentication.getName().equals(username)) {
            throw new AccessDeniedException("Cannot access other user's profile");
        }

        TrainerDTO.Response.Profile response = metricsExecutor.executeWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("get_trainer_profile")
                        .endpoint("/api/trainers/{username}")
                        .method("GET")
                        .build(),
                () -> trainerService.getTrainerProfile(username),
                result -> trainerMetrics.recordTrainerProfileView(username),
                null
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{username}", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update trainer profile", description = "Updates trainer profile information. Requires JWT authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<TrainerDTO.Response.Updated> updateTrainer(
            @Valid @RequestBody TrainerDTO.Request.Update request,
            @Parameter(description = "Trainer username") @PathVariable String username,
            Authentication authentication) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PUT /api/trainers/{}", transactionId, username);

        if (!authentication.getName().equals(username)) {
            throw new AccessDeniedException("Cannot update other user's profile");
        }

        TrainerDTO.Response.Updated response = metricsExecutor.executeWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("update_trainer")
                        .endpoint("/api/trainers/{username}")
                        .method("PUT")
                        .build(),
                () -> trainerService.updateTrainerProfile(request, username),
                result -> trainerMetrics.incrementTrainerUpdated(),
                ex -> trainerMetrics.incrementTrainerOperationFailed("update")
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{username}/activate", consumes = "application/json")
    @Operation(summary = "Activate/Deactivate trainer", description = "Changes trainer active isActive. Requires JWT authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Void> updateTrainerStatus(
            @Parameter(description = "Trainer username") @PathVariable String username,
            @Valid @RequestBody TrainerDTO.Request.UpdateStatus request,
            Authentication authentication) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PATCH /api/trainers/{}/activate", transactionId, username);

        if (!authentication.getName().equals(username)) {
            throw new AccessDeniedException("Cannot update other user's isActive");
        }

        metricsExecutor.executeVoidWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("update_trainer_status")
                        .endpoint("/api/trainers/{username}/activate")
                        .method("PATCH")
                        .build(),
                () -> trainerService.updateTrainerStatus(username, request.isActive()),
                () -> trainerMetrics.recordTrainerActivation(request.isActive()),
                null
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{username}/trainings", produces = "application/json")
    @Operation(summary = "Get trainer trainings list", description = "Retrieves list of trainings for a trainer with optional filters. Requires JWT authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<List<TrainingDTO.Response.TrainerTraining>> getTrainerTrainings(
            @Parameter(description = "Trainer username") @PathVariable String username,
            @Parameter(description = "Training filters") TrainingDTO.Request.TrainerTrainingsFilter filter,
            Authentication authentication) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainers/{}/trainings", transactionId, username);

        if (!authentication.getName().equals(username)) {
            throw new AccessDeniedException("Cannot access other user's trainings");
        }

        List<TrainingDTO.Response.TrainerTraining> response = metricsExecutor.executeWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("get_trainer_trainings")
                        .endpoint("/api/trainers/{username}/trainings")
                        .method("GET")
                        .build(),
                () -> trainerService.getTrainerTrainings(
                        username, filter.periodFrom(), filter.periodTo(), filter.traineeName()),
                result -> trainerMetrics.recordTrainerTrainingsQuery(username),
                null
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }
}

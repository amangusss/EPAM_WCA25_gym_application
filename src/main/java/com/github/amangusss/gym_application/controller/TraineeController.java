package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.metrics.MetricsExecutor;
import com.github.amangusss.gym_application.metrics.TraineeMetrics;
import com.github.amangusss.gym_application.service.TraineeService;

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
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Trainee", description = "Trainee management APIs")
public class TraineeController {

    TraineeService traineeService;
    TraineeMetrics traineeMetrics;
    MetricsExecutor metricsExecutor;

    @PostMapping("/register")
    @Operation(summary = "Register new trainee", description = "Creates a new trainee profile and generates username and password")
    public ResponseEntity<TraineeDTO.Response.Registered> registerTrainee(
            @Valid @RequestBody TraineeDTO.Request.Register request) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] POST /api/trainees/register", transactionId);

        TraineeDTO.Response.Registered response = metricsExecutor.executeWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("register_trainee")
                        .endpoint("/api/trainees/register")
                        .method("POST")
                        .build(),
                () -> traineeService.createTrainee(request),
                result -> traineeMetrics.incrementTraineeRegistered(),
                ex -> traineeMetrics.incrementTraineeOperationFailed("register")
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/profile", produces = "application/json")
    @Operation(summary = "Get current trainee profile",
            description = "Retrieves the profile of the currently authenticated trainee. Username is taken from JWT token.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<TraineeDTO.Response.Profile> getCurrentTraineeProfile(
            Authentication authentication) {
        String transactionId = UUID.randomUUID().toString();
        String username = authentication.getName();
        log.info("[Transaction: {}] GET /api/trainees/profile", transactionId);

        TraineeDTO.Response.Profile response = metricsExecutor.executeWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("get_current_trainer_profile")
                        .endpoint("/api/trainers/profile")
                        .method("GET")
                        .build(),
                () -> traineeService.getTraineeProfile(username),
                result -> traineeMetrics.recordTraineeProfileView(username),
                ex -> traineeMetrics.incrementTraineeOperationFailed("get_profile")
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{username}", produces = "application/json")
    @Operation(summary = "Get trainee profile", description = "Retrieves trainee profile by username. Requires JWT authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<TraineeDTO.Response.Profile> getTraineeProfile(
            @Parameter(description = "Trainee username") @PathVariable String username,
            Authentication authentication) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainees/{}", transactionId, username);

        if (!authentication.getName().equals(username)) {
            log.warn("[Transaction: {}] Access denied: user {} tried to access profile of {}",
                    transactionId, authentication.getName(), username);
            throw new AccessDeniedException("Cannot access other user's profile");
        }

        TraineeDTO.Response.Profile response = metricsExecutor.executeWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("get_trainee_profile")
                        .endpoint("/api/trainees/{username}")
                        .method("GET")
                        .build(),
                () -> traineeService.getTraineeProfile(username),
                result -> traineeMetrics.recordTraineeProfileView(username),
                null
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{username}/trainings", produces = "application/json")
    @Operation(summary = "Get trainee trainings list", description = "Retrieves list of trainings for a trainee with optional filters. Requires JWT authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<List<TrainingDTO.Response.TraineeTraining>> getTraineeTrainings(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Parameter(description = "Training filters") TrainingDTO.Request.TraineeTrainingsFilter filter,
            Authentication authentication) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainees/{}/trainings", transactionId, username);

        if (!authentication.getName().equals(username)) {
            log.warn("[Transaction: {}] Access denied: user {} tried to access trainings of {}",
                    transactionId, authentication.getName(), username);
            throw new AccessDeniedException("Cannot access other user's trainings");
        }

        List<TrainingDTO.Response.TraineeTraining> response = metricsExecutor.executeWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("get_trainee_trainings")
                        .endpoint("/api/trainees/{username}/trainings")
                        .method("GET")
                        .build(),
                () -> traineeService.getTraineeTrainings(
                        username, filter.periodFrom(), filter.periodTo(), filter.trainerName(), filter.trainingType()),
                result -> traineeMetrics.recordTraineeTrainingsQuery(username),
                null
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{username}/trainers/unassigned", produces = "application/json")
    @Operation(summary = "Get unassigned trainers", description = "Retrieves list of active trainers not assigned to the trainee. Requires JWT authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<List<TrainerDTO.Response.Unassigned>> getUnassignedTrainers(
            @Parameter(description = "Trainee username") @PathVariable String username,
            Authentication authentication) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainees/{}/trainers/unassigned", transactionId, username);

        if (!authentication.getName().equals(username)) {
            throw new AccessDeniedException("Cannot access other user's data");
        }

        List<TrainerDTO.Response.Unassigned> response = metricsExecutor.executeWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("get_unassigned_trainers")
                        .endpoint("/api/trainees/{username}/trainers/unassigned")
                        .method("GET")
                        .build(),
                () -> traineeService.getUnassignedTrainers(username),
                result -> traineeMetrics.recordUnassignedTrainersQuery(username),
                null
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{username}", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update trainee profile", description = "Updates trainee profile information. Requires JWT authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<TraineeDTO.Response.Updated> updateTrainee(
            @Valid @RequestBody TraineeDTO.Request.Update request,
            @Parameter(description = "Trainee username") @PathVariable String username,
            Authentication authentication) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PUT /api/trainees/{}", transactionId, username);

        if (!authentication.getName().equals(username)) {
            throw new AccessDeniedException("Cannot update other user's profile");
        }

        TraineeDTO.Response.Updated response = metricsExecutor.executeWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("update_trainee")
                        .endpoint("/api/trainees/{username}")
                        .method("PUT")
                        .build(),
                () -> traineeService.updateTrainee(request, username),
                result -> traineeMetrics.incrementTraineeUpdated(),
                ex -> traineeMetrics.incrementTraineeOperationFailed("update")
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{username}/activate", consumes = "application/json")
    @Operation(summary = "Activate/Deactivate trainee", description = "Changes trainee active status. Requires JWT authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Void> updateTraineeStatus(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Valid @RequestBody TraineeDTO.Request.UpdateStatus request,
            Authentication authentication) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PATCH /api/trainees/{}/activate", transactionId, username);

        if (!authentication.getName().equals(username)) {
            throw new AccessDeniedException("Cannot update other user's status");
        }

        metricsExecutor.executeVoidWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("update_trainee_status")
                        .endpoint("/api/trainees/{username}/activate")
                        .method("PATCH")
                        .build(),
                () -> traineeService.updateTraineeStatus(username, request.isActive()),
                () -> traineeMetrics.recordTraineeActivation(request.isActive()),
                null
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{username}/trainers", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update trainee's trainers list", description = "Updates the list of trainers assigned to the trainee. Requires JWT authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<List<TrainerDTO.Response.InList>> updateTraineeTrainers(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Valid @RequestBody TraineeDTO.Request.UpdateTrainers request,
            Authentication authentication) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PUT /api/trainees/{}/trainers", transactionId, username);

        if (!authentication.getName().equals(username)) {
            throw new AccessDeniedException("Cannot update other user's trainers");
        }

        List<TrainerDTO.Response.InList> response = metricsExecutor.executeWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("update_trainee_trainers")
                        .endpoint("/api/trainees/{username}/trainers")
                        .method("PUT")
                        .build(),
                () -> traineeService.updateTraineeTrainers(username, request),
                result -> traineeMetrics.recordTraineeTrainersUpdate(username, request.trainerUsernames().size()),
                null
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete trainee profile", description = "Deletes trainee profile and all associated trainings. Requires JWT authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Void> deleteTrainee(
            @Parameter(description = "Trainee username") @PathVariable String username,
            Authentication authentication) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] DELETE /api/trainees/{}", transactionId, username);

        if (!authentication.getName().equals(username)) {
            throw new AccessDeniedException("Cannot delete other user's profile");
        }

        metricsExecutor.executeVoidWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("delete_trainee")
                        .endpoint("/api/trainees/{username}")
                        .method("DELETE")
                        .build(),
                () -> traineeService.deleteTraineeByUsername(username),
                traineeMetrics::incrementTraineeDeleted,
                ex -> traineeMetrics.incrementTraineeOperationFailed("delete")
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok().build();
    }
}

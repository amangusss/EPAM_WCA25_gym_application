package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.metrics.ApiPerformanceMetrics;
import com.github.amangusss.gym_application.metrics.TrainerMetrics;
import com.github.amangusss.gym_application.service.TrainerService;

import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    ApiPerformanceMetrics apiPerformanceMetrics;

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Register new trainer", description = "Creates a new trainer profile and generates username and password")
    public ResponseEntity<TrainerDTO.Response.Registered> registerTrainer(
            @Valid @RequestBody TrainerDTO.Request.Register request) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] POST /api/trainers/register", transactionId);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/trainers/register", "POST");

        try {
            TrainerDTO.Response.Registered response = trainerService.registerTrainer(request);

            trainerMetrics.incrementTrainerRegistered();
            trainerMetrics.recordTrainerBySpecialization(request.specialization());

            apiPerformanceMetrics.stopTimerSuccess(sample, "register_trainer", "/api/trainers/register", "POST");
            apiPerformanceMetrics.recordResponse("/api/trainers/register", "POST", 200);

            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            trainerMetrics.incrementTrainerOperationFailed("register");
            apiPerformanceMetrics.stopTimerFailure(sample, "register_trainer", "/api/trainers/register", "POST");
            apiPerformanceMetrics.recordResponse("/api/trainers/register", "POST", 500);
            throw e;
        }
    }

    @GetMapping(value = "/{username}", produces = "application/json")
    @Operation(summary = "Get trainer profile", description = "Retrieves trainer profile by username")
    public ResponseEntity<TrainerDTO.Response.Profile> getTrainerProfile(
            @Parameter(description = "Trainer username") @PathVariable String username,
            @Parameter(description = "Trainer password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainers/{}", transactionId, username);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/trainers/" + username, "GET");

        try {
            TrainerDTO.Response.Profile response = trainerService.getTrainerProfile(username, password);

            trainerMetrics.recordTrainerProfileView(username);

            apiPerformanceMetrics.stopTimerSuccess(sample, "get_trainer_profile", "/api/trainers/{username}", "GET");
            apiPerformanceMetrics.recordResponse("/api/trainers/" + username, "GET", 200);

            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            apiPerformanceMetrics.stopTimerFailure(sample, "get_trainer_profile", "/api/trainers/{username}", "GET");
            apiPerformanceMetrics.recordResponse("/api/trainers/" + username, "GET", 500);
            throw e;
        }
    }

    @PutMapping(value = "/{username}", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update trainer profile", description = "Updates trainer profile information")
    public ResponseEntity<TrainerDTO.Response.Updated> updateTrainer(
            @Valid @RequestBody TrainerDTO.Request.Update request,
            @Parameter(description = "Trainer username") @PathVariable String username,
            @Parameter(description = "Trainer password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PUT /api/trainers/{}", transactionId, username);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/trainers/" + username, "PUT");

        try {
            TrainerDTO.Response.Updated response = trainerService.updateTrainerProfile(request, username, password);

            trainerMetrics.incrementTrainerUpdated();

            apiPerformanceMetrics.stopTimerSuccess(sample, "update_trainer", "/api/trainers/{username}", "PUT");
            apiPerformanceMetrics.recordResponse("/api/trainers/" + username, "PUT", 200);

            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            trainerMetrics.incrementTrainerOperationFailed("update");
            apiPerformanceMetrics.stopTimerFailure(sample, "update_trainer", "/api/trainers/{username}", "PUT");
            apiPerformanceMetrics.recordResponse("/api/trainers/" + username, "PUT", 500);
            throw e;
        }
    }

    @PatchMapping(value = "/{username}/activate", consumes = "application/json")
    @Operation(summary = "Activate/Deactivate trainer", description = "Changes trainer active status")
    public ResponseEntity<Void> updateTrainerStatus(
            @Parameter(description = "Trainer username") @PathVariable String username,
            @Valid @RequestBody TrainerDTO.Request.UpdateStatus request,
            @Parameter(description = "Trainer password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PATCH /api/trainers/{}/activate", transactionId, username);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/trainers/" + username + "/activate", "PATCH");

        try {
            trainerService.updateTrainerStatus(username, request.isActive(), password);

            trainerMetrics.recordTrainerActivation(request.isActive());

            apiPerformanceMetrics.stopTimerSuccess(sample, "update_trainer_status", "/api/trainers/{username}/activate", "PATCH");
            apiPerformanceMetrics.recordResponse("/api/trainers/" + username + "/activate", "PATCH", 200);

            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            apiPerformanceMetrics.stopTimerFailure(sample, "update_trainer_status", "/api/trainers/{username}/activate", "PATCH");
            apiPerformanceMetrics.recordResponse("/api/trainers/" + username + "/activate", "PATCH", 500);
            throw e;
        }
    }

    @GetMapping(value = "/{username}/trainings", produces = "application/json")
    @Operation(summary = "Get trainer trainings list", description = "Retrieves list of trainings for a trainer with optional filters")
    public ResponseEntity<List<TrainingDTO.Response.TrainerTraining>> getTrainerTrainings(
            @Parameter(description = "Trainer username") @PathVariable String username,
            @Parameter(description = "Trainer password") @RequestParam String password,
            @Parameter(description = "Training filters") TrainingDTO.Request.TrainerTrainingsFilter filter) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainers/{}/trainings", transactionId, username);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/trainers/" + username + "/trainings", "GET");

        try {
            List<TrainingDTO.Response.TrainerTraining> response = trainerService.getTrainerTrainings(
                    username, password, filter.periodFrom(), filter.periodTo(), filter.traineeName());

            trainerMetrics.recordTrainerTrainingsQuery(username);

            apiPerformanceMetrics.stopTimerSuccess(sample, "get_trainer_trainings", "/api/trainers/{username}/trainings", "GET");
            apiPerformanceMetrics.recordResponse("/api/trainers/" + username + "/trainings", "GET", 200);

            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            apiPerformanceMetrics.stopTimerFailure(sample, "get_trainer_trainings", "/api/trainers/{username}/trainings", "GET");
            apiPerformanceMetrics.recordResponse("/api/trainers/" + username + "/trainings", "GET", 500);
            throw e;
        }
    }
}

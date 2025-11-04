package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.metrics.ApiPerformanceMetrics;
import com.github.amangusss.gym_application.metrics.TraineeMetrics;
import com.github.amangusss.gym_application.service.TraineeService;

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
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Trainee", description = "Trainee management APIs")
public class TraineeController {

    TraineeService traineeService;
    TraineeMetrics traineeMetrics;
    ApiPerformanceMetrics apiPerformanceMetrics;

    @PostMapping("/register")
    @Operation(summary = "Register new trainee", description = "Creates a new trainee profile and generates username and password")
    public ResponseEntity<TraineeDTO.Response.Registered> registerTrainee(
            @Valid @RequestBody TraineeDTO.Request.Register request) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] POST /api/trainees/register", transactionId);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/trainees/register", "POST");

        try {
            TraineeDTO.Response.Registered response = traineeService.createTrainee(request);

            traineeMetrics.incrementTraineeRegistered();

            apiPerformanceMetrics.stopTimerSuccess(sample, "register_trainee", "/api/trainees/register", "POST");
            apiPerformanceMetrics.recordResponse("/api/trainees/register", "POST", 200);

            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            traineeMetrics.incrementTraineeOperationFailed("register");
            apiPerformanceMetrics.stopTimerFailure(sample, "register_trainee", "/api/trainees/register", "POST");
            apiPerformanceMetrics.recordResponse("/api/trainees/register", "POST", 500);
            throw e;
        }
    }

    @GetMapping(value = "/{username}", produces = "application/json")
    @Operation(summary = "Get trainee profile", description = "Retrieves trainee profile by username")
    public ResponseEntity<TraineeDTO.Response.Profile> getTraineeProfile(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Parameter(description = "Trainee password") @RequestParam String password) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainees/{}", transactionId, username);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/trainees/" + username, "GET");

        try {
            TraineeDTO.Response.Profile response = traineeService.findTraineeByUsername(username, password);

            traineeMetrics.recordTraineeProfileView(username);

            apiPerformanceMetrics.stopTimerSuccess(sample, "get_trainee_profile", "/api/trainees/{username}", "GET");
            apiPerformanceMetrics.recordResponse("/api/trainees/" + username, "GET", 200);

            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            apiPerformanceMetrics.stopTimerFailure(sample, "get_trainee_profile", "/api/trainees/{username}", "GET");
            apiPerformanceMetrics.recordResponse("/api/trainees/" + username, "GET", 500);
            throw e;
        }
    }

    @GetMapping(value = "/{username}/trainings", produces = "application/json")
    @Operation(summary = "Get trainee trainings list", description = "Retrieves list of trainings for a trainee with optional filters")
    public ResponseEntity<List<TrainingDTO.Response.TraineeTraining>> getTraineeTrainings(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Parameter(description = "Trainee password") @RequestParam String password,
            @Parameter(description = "Training filters") TrainingDTO.Request.TraineeTrainingsFilter filter) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainees/{}/trainings", transactionId, username);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/trainees/" + username + "/trainings", "GET");

        try {
            List<TrainingDTO.Response.TraineeTraining> response = traineeService.getTraineeTrainings(
                    username, password, filter.periodFrom(), filter.periodTo(), filter.trainerName(), filter.trainingType());

            traineeMetrics.recordTraineeTrainingsQuery(username);

            apiPerformanceMetrics.stopTimerSuccess(sample, "get_trainee_trainings", "/api/trainees/{username}/trainings", "GET");
            apiPerformanceMetrics.recordResponse("/api/trainees/" + username + "/trainings", "GET", 200);

            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            apiPerformanceMetrics.stopTimerFailure(sample, "get_trainee_trainings", "/api/trainees/{username}/trainings", "GET");
            apiPerformanceMetrics.recordResponse("/api/trainees/" + username + "/trainings", "GET", 500);
            throw e;
        }
    }

    @GetMapping(value = "/{username}/trainers/unassigned", produces = "application/json")
    @Operation(summary = "Get unassigned trainers", description = "Retrieves list of active trainers not assigned to the trainee")
    public ResponseEntity<List<TrainerDTO.Response.Unassigned>> getUnassignedTrainers(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Parameter(description = "Trainee password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainees/{}/trainers/unassigned", transactionId, username);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/trainees/" + username + "/trainers/unassigned", "GET");

        try {
            List<TrainerDTO.Response.Unassigned> response = traineeService.getUnassignedTrainers(username, password);

            traineeMetrics.recordUnassignedTrainersQuery(username);

            apiPerformanceMetrics.stopTimerSuccess(sample, "get_unassigned_trainers", "/api/trainees/{username}/trainers/unassigned", "GET");
            apiPerformanceMetrics.recordResponse("/api/trainees/" + username + "/trainers/unassigned", "GET", 200);

            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            apiPerformanceMetrics.stopTimerFailure(sample, "get_unassigned_trainers", "/api/trainees/{username}/trainers/unassigned", "GET");
            apiPerformanceMetrics.recordResponse("/api/trainees/" + username + "/trainers/unassigned", "GET", 500);
            throw e;
        }
    }

    @PutMapping(value = "/{username}", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update trainee profile", description = "Updates trainee profile information")
    public ResponseEntity<TraineeDTO.Response.Updated> updateTrainee(
            @Valid @RequestBody TraineeDTO.Request.Update request,
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Parameter(description = "Trainee password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PUT /api/trainees/{}", transactionId, username);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/trainees/" + username, "PUT");

        try {
            TraineeDTO.Response.Updated response = traineeService.updateTrainee(request, username, password);

            traineeMetrics.incrementTraineeUpdated();

            apiPerformanceMetrics.stopTimerSuccess(sample, "update_trainee", "/api/trainees/{username}", "PUT");
            apiPerformanceMetrics.recordResponse("/api/trainees/" + username, "PUT", 200);

            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            traineeMetrics.incrementTraineeOperationFailed("update");
            apiPerformanceMetrics.stopTimerFailure(sample, "update_trainee", "/api/trainees/{username}", "PUT");
            apiPerformanceMetrics.recordResponse("/api/trainees/" + username, "PUT", 500);
            throw e;
        }
    }

    @PatchMapping(value = "/{username}/activate", consumes = "application/json")
    @Operation(summary = "Activate/Deactivate trainee", description = "Changes trainee active status")
    public ResponseEntity<Void> updateTraineeStatus(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Valid @RequestBody TraineeDTO.Request.UpdateStatus request,
            @Parameter(description = "Trainee password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PATCH /api/trainees/{}/activate", transactionId, username);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/trainees/" + username + "/activate", "PATCH");

        try {
            traineeService.updateTraineeStatus(username, password, request.isActive());

            traineeMetrics.recordTraineeActivation(request.isActive());

            apiPerformanceMetrics.stopTimerSuccess(sample, "update_trainee_status", "/api/trainees/{username}/activate", "PATCH");
            apiPerformanceMetrics.recordResponse("/api/trainees/" + username + "/activate", "PATCH", 200);

            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            apiPerformanceMetrics.stopTimerFailure(sample, "update_trainee_status", "/api/trainees/{username}/activate", "PATCH");
            apiPerformanceMetrics.recordResponse("/api/trainees/" + username + "/activate", "PATCH", 500);
            throw e;
        }
    }

    @PutMapping(value = "/{username}/trainers", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update trainee's trainers list", description = "Updates the list of trainers assigned to the trainee")
    public ResponseEntity<List<TrainerDTO.Response.InList>> updateTraineeTrainers(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Valid @RequestBody TraineeDTO.Request.UpdateTrainers request,
            @Parameter(description = "Trainee password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PUT /api/trainees/{}/trainers", transactionId, username);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/trainees/" + username + "/trainers", "PUT");

        try {
            List<TrainerDTO.Response.InList> response = traineeService.updateTraineeTrainers(username, request, password);

            traineeMetrics.recordTraineeTrainersUpdate(username, request.trainerUsernames().size());

            apiPerformanceMetrics.stopTimerSuccess(sample, "update_trainee_trainers", "/api/trainees/{username}/trainers", "PUT");
            apiPerformanceMetrics.recordResponse("/api/trainees/" + username + "/trainers", "PUT", 200);

            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            apiPerformanceMetrics.stopTimerFailure(sample, "update_trainee_trainers", "/api/trainees/{username}/trainers", "PUT");
            apiPerformanceMetrics.recordResponse("/api/trainees/" + username + "/trainers", "PUT", 500);
            throw e;
        }
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete trainee profile", description = "Deletes trainee profile and all associated trainings")
    public ResponseEntity<Void> deleteTrainee(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Parameter(description = "Trainee password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] DELETE /api/trainees/{}", transactionId, username);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/trainees/" + username, "DELETE");

        try {
            traineeService.deleteTraineeByUsername(username, password);

            traineeMetrics.incrementTraineeDeleted();

            apiPerformanceMetrics.stopTimerSuccess(sample, "delete_trainee", "/api/trainees/{username}", "DELETE");
            apiPerformanceMetrics.recordResponse("/api/trainees/" + username, "DELETE", 200);


            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            traineeMetrics.incrementTraineeOperationFailed("delete");
            apiPerformanceMetrics.stopTimerFailure(sample, "delete_trainee", "/api/trainees/{username}", "DELETE");
            apiPerformanceMetrics.recordResponse("/api/trainees/" + username, "DELETE", 500);
            throw e;
        }
    }
}

package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.trainer.UpdateTrainersDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.facade.TraineeFacade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
@Tag(name = "Trainee", description = "Trainee management APIs")
public class TraineeController {

    private final TraineeFacade traineeFacade;

    @PostMapping("/register")
    @Operation(summary = "Register new trainee", description = "Creates a new trainee profile and generates username and password")
    public ResponseEntity<TraineeDTO.Response.Registered> registerTrainee(
            @Valid @RequestBody TraineeDTO.Request.Register request) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] POST /api/trainees/register", transactionId);

        TraineeDTO.Response.Registered response = traineeFacade.registerTrainee(request);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get trainee profile", description = "Retrieves trainee profile by username")
    public ResponseEntity<TraineeDTO.Response.Profile> getTraineeProfile(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Parameter(description = "Trainee password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainees/{}", transactionId, username);

        TraineeDTO.Response.Profile response = traineeFacade.getTraineeProfile(username, password);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Operation(summary = "Update trainee profile", description = "Updates trainee profile information")
    public ResponseEntity<TraineeDTO.Response.Updated> updateTrainee(
            @Valid @RequestBody TraineeDTO.Request.Update request,
            @Parameter(description = "Trainee password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PUT /api/trainees", transactionId);

        TraineeDTO.Response.Updated response = traineeFacade.updateTrainee(request, password);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete trainee profile", description = "Deletes trainee profile and all associated trainings")
    public ResponseEntity<Void> deleteTrainee(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Parameter(description = "Trainee password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] DELETE /api/trainees/{}", transactionId, username);

        traineeFacade.deleteTrainee(username, password);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{username}/activate")
    @Operation(summary = "Activate/Deactivate trainee", description = "Changes trainee active status")
    public ResponseEntity<Void> updateTraineeStatus(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Valid @RequestBody TraineeDTO.Request.UpdateStatus request,
            @Parameter(description = "Trainee password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PATCH /api/trainees/{}/activate", transactionId, username);

        traineeFacade.updateTraineeStatus(username, request.isActive(), password);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainee trainings list", description = "Retrieves list of trainings for a trainee with optional filters")
    public ResponseEntity<List<TrainingDTO.Response.TraineeTraining>> getTraineeTrainings(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Parameter(description = "Trainee password") @RequestParam String password,
            @Parameter(description = "Period from date") @RequestParam(required = false) LocalDate periodFrom,
            @Parameter(description = "Period to date") @RequestParam(required = false) LocalDate periodTo,
            @Parameter(description = "Trainer name") @RequestParam(required = false) String trainerName,
            @Parameter(description = "Training type") @RequestParam(required = false) String trainingType) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainees/{}/trainings", transactionId, username);

        List<TrainingDTO.Response.TraineeTraining> response = traineeFacade.getTraineeTrainings(
                username, password, periodFrom, periodTo, trainerName, trainingType);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/trainers/unassigned")
    @Operation(summary = "Get unassigned trainers", description = "Retrieves list of active trainers not assigned to the trainee")
    public ResponseEntity<List<TrainerDTO.Response.Unassigned>> getUnassignedTrainers(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Parameter(description = "Trainee password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainees/{}/trainers/unassigned", transactionId, username);

        List<TrainerDTO.Response.Unassigned> response = traineeFacade.getUnassignedTrainers(username, password);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}/trainers")
    @Operation(summary = "Update trainee's trainers list", description = "Updates the list of trainers assigned to the trainee")
    public ResponseEntity<List<TrainerDTO.Response.InList>> updateTraineeTrainers(
            @Parameter(description = "Trainee username") @PathVariable String username,
            @Valid @RequestBody UpdateTrainersDTO.Request.Update request,
            @Parameter(description = "Trainee password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PUT /api/trainees/{}/trainers", transactionId, username);

        List<TrainerDTO.Response.InList> response = traineeFacade.updateTraineeTrainers(username, request, password);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }
}

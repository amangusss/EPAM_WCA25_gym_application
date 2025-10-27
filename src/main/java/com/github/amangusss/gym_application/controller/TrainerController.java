package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.service.TrainerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
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
@Tag(name = "Trainer", description = "Trainer management APIs")
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Register new trainer", description = "Creates a new trainer profile and generates username and password")
    public ResponseEntity<TrainerDTO.Response.Registered> registerTrainer(
            @Valid @RequestBody TrainerDTO.Request.Register request) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] POST /api/trainers/register", transactionId);

        TrainerDTO.Response.Registered response = trainerService.registerTrainer(request);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{username}", produces = "application/json")
    @Operation(summary = "Get trainer profile", description = "Retrieves trainer profile by username")
    public ResponseEntity<TrainerDTO.Response.Profile> getTrainerProfile(
            @Parameter(description = "Trainer username") @PathVariable String username,
            @Parameter(description = "Trainer password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainers/{}", transactionId, username);

        TrainerDTO.Response.Profile response = trainerService.getTrainerProfile(username, password);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{username}", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update trainer profile", description = "Updates trainer profile information")
    public ResponseEntity<TrainerDTO.Response.Updated> updateTrainer(
            @Valid @RequestBody TrainerDTO.Request.Update request,
            @Parameter(description = "Trainer username") @PathVariable String username,
            @Parameter(description = "Trainer password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PUT /api/trainers/{}", transactionId, username);

        TrainerDTO.Response.Updated response = trainerService.updateTrainerProfile(request, username, password);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{username}/activate", consumes = "application/json")
    @Operation(summary = "Activate/Deactivate trainer", description = "Changes trainer active status")
    public ResponseEntity<Void> updateTrainerStatus(
            @Parameter(description = "Trainer username") @PathVariable String username,
            @Valid @RequestBody TrainerDTO.Request.UpdateStatus request,
            @Parameter(description = "Trainer password") @RequestParam String password) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] PATCH /api/trainers/{}/activate", transactionId, username);

        trainerService.updateTrainerStatus(username, request.isActive(), password);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{username}/trainings", produces = "application/json")
    @Operation(summary = "Get trainer trainings list", description = "Retrieves list of trainings for a trainer with optional filters")
    public ResponseEntity<List<TrainingDTO.Response.TrainerTraining>> getTrainerTrainings(
            @Parameter(description = "Trainer username") @PathVariable String username,
            @Parameter(description = "Trainer password") @RequestParam String password,
            @Parameter(description = "Training filters") TrainingDTO.Request.TrainerTrainingsFilter filter) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/trainers/{}/trainings", transactionId, username);

        List<TrainingDTO.Response.TrainerTraining> response = trainerService.getTrainerTrainings(
                username, password, filter.periodFrom(), filter.periodTo(), filter.traineeName());

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }
}

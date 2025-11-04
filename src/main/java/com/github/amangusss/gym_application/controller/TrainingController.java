package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.service.TrainingService;
import com.github.amangusss.gym_application.metrics.TrainingMetrics;
import com.github.amangusss.gym_application.metrics.ApiPerformanceMetrics;

import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Training", description = "Training management APIs")
public class TrainingController {

    TrainingService trainingService;
    TrainingMetrics trainingMetrics;
    ApiPerformanceMetrics apiPerformanceMetrics;

    @PostMapping(consumes = "application/json")
    @Operation(summary = "Add new training", description = "Creates a new training session")
    public ResponseEntity<Void> addTraining(@Valid @RequestBody TrainingDTO.Request.Create request) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] POST /api/trainings", transactionId);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/trainings", "POST");

        try {
            trainingService.addTraining(request);

            trainingMetrics.incrementTrainingCreated();
            trainingMetrics.recordTrainingByTrainer(request.trainerUsername());
            trainingMetrics.recordTrainingByTrainee(request.traineeUsername());
            trainingMetrics.recordTrainingDuration(request.trainingDuration(), request.trainingName());

            apiPerformanceMetrics.stopTimerSuccess(sample, "create_training", "/api/trainings", "POST");
            apiPerformanceMetrics.recordResponse("/api/trainings", "POST", 200);

            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            trainingMetrics.incrementTrainingFailed();
            trainingMetrics.incrementTrainingFailedByReason(e.getClass().getSimpleName());

            apiPerformanceMetrics.stopTimerFailure(sample, "create_training", "/api/trainings", "POST");
            apiPerformanceMetrics.recordResponse("/api/trainings", "POST", 500);

            log.error("[Transaction: {}] Failed to create training: {}",
                    transactionId, e.getMessage());
            throw e;
        }
    }
}

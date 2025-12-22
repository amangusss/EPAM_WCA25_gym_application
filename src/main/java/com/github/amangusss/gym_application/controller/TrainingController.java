package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.service.TrainingService;
import com.github.amangusss.gym_application.metrics.TrainingMetrics;
import com.github.amangusss.gym_application.metrics.MetricsExecutor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Training", description = "Training management APIs")
public class TrainingController {

    TrainingService trainingService;
    TrainingMetrics trainingMetrics;
    MetricsExecutor metricsExecutor;

    @PostMapping(consumes = "application/json")
    @Operation(summary = "Add new training", description = "Creates a new training session")
    public ResponseEntity<Void> addTraining(@Valid @RequestBody TrainingDTO.Request.Create request) {
        String transactionId = MDC.get("transactionId");
        log.info("[Transaction: {}] POST /api/trainings", transactionId);

        metricsExecutor.executeVoidWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("create_training")
                        .endpoint("/api/trainings")
                        .method("POST")
                        .build(),
                () -> trainingService.addTraining(request),
                () -> {
                    trainingMetrics.incrementTrainingCreated();
                    trainingMetrics.recordTrainingByTrainer(request.trainerUsername());
                    trainingMetrics.recordTrainingByTrainee(request.traineeUsername());
                    trainingMetrics.recordTrainingDuration(request.trainingDuration(), request.trainingName());
                },
                ex -> {
                    trainingMetrics.incrementTrainingFailed();
                    trainingMetrics.incrementTrainingFailedByReason(ex.getClass().getSimpleName());
                }
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{trainingId}")
    @Operation(summary = "Delete training", description = "Deletes a training session by ID")
    public ResponseEntity<Void> deleteTraining(@PathVariable Long trainingId) {
        String transactionId = MDC.get("transactionId");
        log.info("[Transaction: {}] DELETE /api/trainings/{}", transactionId, trainingId);

        metricsExecutor.executeVoidWithMetrics(
                MetricsExecutor.MetricsContext.builder()
                        .operation("delete_training")
                        .endpoint("/api/trainings/{trainingId}")
                        .method("DELETE")
                        .build(),
                () -> trainingService.deleteTraining(trainingId),
                trainingMetrics::incrementTrainingDeleted,
                ex -> {
                    trainingMetrics.incrementTrainingFailed();
                    trainingMetrics.incrementTrainingFailedByReason(ex.getClass().getSimpleName());
                }
        );

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok().build();
    }
}

package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.service.TrainingService;
import com.github.amangusss.gym_application.metrics.TrainingMetrics;
import com.github.amangusss.gym_application.metrics.ApiPerformanceMetrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static io.micrometer.core.instrument.Timer.start;

@Slf4j
@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
@Tag(name = "Training", description = "Training management APIs")
public class TrainingController {

    private final TrainingService trainingService;
    private final TrainingMetrics trainingMetrics;
    private final ApiPerformanceMetrics apiPerformanceMetrics;
    private final MeterRegistry meterRegistry;

    @PostMapping(consumes = "application/json")
    @Operation(summary = "Add training", description = "Creates a new training session")
    public ResponseEntity<Void> addTraining(@Valid @RequestBody TrainingDTO.Request.Create request) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] POST /api/trainings", transactionId);

        Instant start = Instant.now();
        Timer.Sample sample = start(meterRegistry);

        try {
            trainingService.addTraining(request);

            trainingMetrics.incrementTrainingCreated();

            Duration duration = Duration.between(start, Instant.now());
            apiPerformanceMetrics.recordTrainingCreationTime(duration);
            sample.stop(meterRegistry.timer("api.training.creation.duration"));

            log.info("[Transaction: {}] Response: 200 OK, duration: {} ms",
                    transactionId, duration.toMillis());
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            trainingMetrics.incrementTrainingFailed();

            log.error("[Transaction: {}] Failed to create training: {}",
                    transactionId, e.getMessage());
            throw e;
        }
    }
}

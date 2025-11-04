package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.trainingtype.TrainingTypeDTO;
import com.github.amangusss.gym_application.metrics.ApiPerformanceMetrics;
import com.github.amangusss.gym_application.metrics.TrainingTypeMetrics;
import com.github.amangusss.gym_application.service.TrainingTypeService;

import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/training-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Training Type", description = "Training type APIs")
public class TrainingTypeController {

    TrainingTypeService trainingTypeService;
    TrainingTypeMetrics trainingTypeMetrics;
    ApiPerformanceMetrics apiPerformanceMetrics;

    @GetMapping(produces = "application/json")
    @Operation(summary = "Get training types", description = "Retrieves all available training types")
    public ResponseEntity<List<TrainingTypeDTO.Response.TrainingType>> getAllTrainingTypes() {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/training-types", transactionId);

        Timer.Sample sample = apiPerformanceMetrics.startTimer();
        apiPerformanceMetrics.recordRequest("/api/training-types", "GET");

        try {
            List<TrainingTypeDTO.Response.TrainingType> response = trainingTypeService.getAllTrainingTypes();

            trainingTypeMetrics.incrementTrainingTypeQuery();

            apiPerformanceMetrics.stopTimerSuccess(sample, "get_training_types", "/api/training-types", "GET");
            apiPerformanceMetrics.recordResponse("/api/training-types", "GET", 200);

            log.info("[Transaction: {}] Response: 200 OK", transactionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            apiPerformanceMetrics.stopTimerFailure(sample, "get_training_types", "/api/training-types", "GET");
            apiPerformanceMetrics.recordResponse("/api/training-types", "GET", 500);
            throw e;
        }
    }
}

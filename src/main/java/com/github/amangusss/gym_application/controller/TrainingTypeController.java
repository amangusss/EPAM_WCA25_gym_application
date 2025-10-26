package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.trainingtype.TrainingTypeDTO;
import com.github.amangusss.gym_application.facade.TrainingTypeFacade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
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
@Tag(name = "Training Type", description = "Training type APIs")
public class TrainingTypeController {

    private final TrainingTypeFacade trainingTypeFacade;

    @GetMapping(produces = "application/json")
    @Operation(summary = "Get training types", description = "Retrieves all available training types")
    public ResponseEntity<List<TrainingTypeDTO.Response.TrainingType>> getAllTrainingTypes() {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] GET /api/training-types", transactionId);

        List<TrainingTypeDTO.Response.TrainingType> response = trainingTypeFacade.getAllTrainingTypes();

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok(response);
    }
}

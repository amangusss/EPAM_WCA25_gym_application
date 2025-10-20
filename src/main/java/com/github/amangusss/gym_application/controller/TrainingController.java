package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.facade.TrainingFacade;

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

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
@Tag(name = "Training", description = "Training management APIs")
public class TrainingController {

    private final TrainingFacade trainingFacade;

    @PostMapping
    @Operation(summary = "Add training", description = "Creates a new training session")
    public ResponseEntity<Void> addTraining(@Valid @RequestBody TrainingDTO.Request.Create request) {

        String transactionId = UUID.randomUUID().toString();
        log.info("[Transaction: {}] POST /api/trainings", transactionId);

        trainingFacade.addTraining(request);

        log.info("[Transaction: {}] Response: 200 OK", transactionId);
        return ResponseEntity.ok().build();
    }
}

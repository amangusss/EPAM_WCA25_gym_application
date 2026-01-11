package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.workload.WorkloadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/test")
@Profile("dev")
@RequiredArgsConstructor
public class TestJmsController {

    private final JmsTemplate jmsTemplate;

    @Value("${app.jms.queue.workload}")
    private String workloadQueue;

    @PostMapping("/send-invalid-workload")
    public ResponseEntity<String> sendInvalidWorkload() {
        WorkloadDTO.Request.Workload invalidWorkload = WorkloadDTO.Request.Workload.builder()
                .username(null)
                .firstName("Test")
                .lastName("User")
                .isActive(true)
                .trainingDate(LocalDate.now())
                .trainingDuration(2.5)
                .actionType(WorkloadDTO.ActionType.ADD)
                .build();

        jmsTemplate.convertAndSend(workloadQueue, invalidWorkload, message -> {
            message.setStringProperty("transactionId", "DLQ-TEST-" + System.currentTimeMillis());
            return message;
        });

        return ResponseEntity.ok("Invalid message sent to queue");
    }
}
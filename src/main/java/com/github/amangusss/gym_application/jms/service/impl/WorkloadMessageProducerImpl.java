package com.github.amangusss.gym_application.jms.service.impl;

import com.github.amangusss.gym_application.dto.workload.WorkloadDTO;
import com.github.amangusss.gym_application.jms.service.WorkloadMessageProducer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkloadMessageProducerImpl implements WorkloadMessageProducer {

    final JmsTemplate jmsTemplate;

    @Value("${app.jms.queue.workload}")
    String workloadQueue;

    @Override
    public void sendWorkloadMessage(WorkloadDTO.Request.Workload workload, String transactionId) {
        log.info("Sending workload message to queue {}: username={}, action={}",
                workloadQueue, workload.username(), workload.actionType());

        jmsTemplate.convertAndSend(workloadQueue, workload, message -> {
            message.setStringProperty("transactionId", transactionId);
            return message;
        });

        log.info("Workload message sent successfully to queue {}", workloadQueue);
    }
}

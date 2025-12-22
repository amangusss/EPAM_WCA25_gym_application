package com.github.amangusss.gym_application.jms.listener;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorkloadDlqListener {

    @JmsListener(destination = "${app.jms.queue.workload-dlq}")
    public void handleDeadLetter(Message message) {
        try {
            String transactionId = message.getStringProperty("transactionId");
            String errorReason = message.getStringProperty("errorReason");
            String effectiveTransactionId = transactionId != null ? transactionId : "N/A";

            String body = "unknown";
            if (message instanceof TextMessage textMessage) {
                body = textMessage.getText();
            }

            log.error("[{}] Message moved to DLQ. Reason: {}. Body: {}",
                    effectiveTransactionId, errorReason, body);

        } catch (JMSException e) {
            log.error("Error reading DLQ message: {}", e.getMessage(), e);
        }
    }
}

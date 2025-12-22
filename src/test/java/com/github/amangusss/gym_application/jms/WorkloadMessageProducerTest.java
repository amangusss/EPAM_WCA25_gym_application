package com.github.amangusss.gym_application.jms;

import com.github.amangusss.gym_application.dto.workload.WorkloadDTO;
import com.github.amangusss.gym_application.jms.listener.WorkloadDlqListener;
import com.github.amangusss.gym_application.jms.service.WorkloadMessageProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.activemq.ArtemisContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class WorkloadMessageProducerTest {

    @Container
    static ArtemisContainer artemisContainer = new ArtemisContainer("apache/activemq-artemis")
            .withUser("Artemis");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.activemq.broker-url", artemisContainer::getBrokerUrl);
        registry.add("spring.activemq.user", artemisContainer::getUser);
        registry.add("spring.activemq.password", artemisContainer::getPassword);
    }

    @MockitoBean
    private WorkloadDlqListener workloadDlqListener;

    @Autowired
    private WorkloadMessageProducer workloadMessageProducer;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${app.jms.queue.workload}")
    private String workloadQueue;

    @Test
    void shouldSendWorkloadMessage() {
        WorkloadDTO.Request.Workload workload = WorkloadDTO.Request.Workload.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate(java.time.LocalDate.now())
                .trainingDuration(60.0)
                .actionType(WorkloadDTO.ActionType.ADD)
                .build();

        workloadMessageProducer.sendWorkloadMessage(workload, "test-transaction-id");

        jmsTemplate.setReceiveTimeout(5000);
        Object receivedMessage = jmsTemplate.receiveAndConvert(workloadQueue);

        assertThat(receivedMessage).isNotNull();
        assertThat(receivedMessage).isInstanceOf(WorkloadDTO.Request.Workload.class);
    }
}

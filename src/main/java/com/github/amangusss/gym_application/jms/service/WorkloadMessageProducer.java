package com.github.amangusss.gym_application.jms.service;

import com.github.amangusss.gym_application.dto.workload.WorkloadDTO;

public interface WorkloadMessageProducer {

    void sendWorkloadMessage(WorkloadDTO.Request.Workload workload, String transactionId);
}

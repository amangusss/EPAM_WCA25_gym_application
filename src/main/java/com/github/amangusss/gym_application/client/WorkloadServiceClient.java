package com.github.amangusss.gym_application.client;

import com.github.amangusss.gym_application.client.dto.workload.WorkloadDTO;

public interface WorkloadServiceClient {
    void sendWorkload(WorkloadDTO.Request.Workload request, String transactionId);
}

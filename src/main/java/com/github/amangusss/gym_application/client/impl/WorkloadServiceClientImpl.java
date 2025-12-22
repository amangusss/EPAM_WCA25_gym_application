package com.github.amangusss.gym_application.client.impl;

import com.github.amangusss.gym_application.client.WorkloadServiceClient;
import com.github.amangusss.gym_application.client.dto.workload.WorkloadDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkloadServiceClientImpl implements WorkloadServiceClient {

    final RestTemplate restTemplate;

    @Value("${workload-service.url}")
    String workloadServiceUrl;

    @Override
    @CircuitBreaker(name = "workloadService", fallbackMethod = "sendWorkloadFallback")
    public void sendWorkload(WorkloadDTO.Request.Workload request, String transactionId) {
        log.info("[{}] Sending workload update to workload-service: username={}, action={}",
                transactionId, request.username(), request.actionType());
        HttpHeaders headers = createHeaders(transactionId);
        HttpEntity<WorkloadDTO.Request.Workload> entity = new HttpEntity<>(request, headers);

        restTemplate.exchange(workloadServiceUrl + "/api/v1/workload", HttpMethod.POST, entity, Void.class);

        log.info("[{}] Workload update sent successfully", transactionId);
    }


    private HttpHeaders createHeaders(String transactionId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-transaction-id", transactionId);

        String token = extractCurrentToken();
        if (token != null) {
            headers.setBearerAuth(token);
            log.debug("[{}] Bearer token added to request", transactionId);
        } else {
            log.warn("[{}] No token available in SecurityContext", transactionId);
        }

        return headers;
    }

    private String extractCurrentToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.debug("Authentication is null");
            return null;
        }

        log.debug("Authentication type: {}, credentials type: {}",
                authentication.getClass().getSimpleName(),
                authentication.getCredentials() != null ? authentication.getCredentials().getClass().getSimpleName() : "null");

        if (authentication.getCredentials() instanceof String token) {
            return token;
        }
        return null;
    }

    private void sendWorkloadFallback(WorkloadDTO.Request.Workload request, String transactionId, Throwable t) {
        log.error("[{}] Circuit breaker activated for workload-service. Request: username={}, error={}",
                transactionId, request.username(), t.getMessage());
    }
}

package com.github.amangusss.gym_application.controller;

import com.github.amangusss.dto.generated.TrainingCreateRequest;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.jwt.JwtUtils;
import com.github.amangusss.gym_application.mapper.openapi.OpenApiTrainingMapper;
import com.github.amangusss.gym_application.metrics.ApiPerformanceMetrics;
import com.github.amangusss.gym_application.metrics.MetricsExecutor;
import com.github.amangusss.gym_application.metrics.TrainingMetrics;
import com.github.amangusss.gym_application.service.BruteForceProtectionService;
import com.github.amangusss.gym_application.service.TrainingService;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(
        controllers = TrainingController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("TrainingController Tests")
class TrainingControllerTest {

    private static final String TRAINER_USERNAME = "Aman.Nazarkulov";
    private static final String TRAINEE_USERNAME = "Dastan.Ibraimov";
    private static final String TRAINING_NAME = "Morning Yoga";
    private static final LocalDate TRAINING_DATE = LocalDate.of(2025, 10, 28);
    private static final double TRAINING_DURATION = 60;

    private static final String TRAININGS_ENDPOINT = "/api/trainings";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainingService trainingService;

    @MockitoBean(name = "jwtUtils")
    private JwtUtils jwtUtils;

    @MockitoBean(name = "customUserDetailsService")
    private UserDetailsService userDetailsService;

    @MockitoBean(name = "bruteForceProtectionService")
    private BruteForceProtectionService bruteForceProtectionService;

    @MockitoBean
    private TrainingMetrics trainingMetrics;

    @MockitoBean
    private ApiPerformanceMetrics apiPerformanceMetrics;

    @MockitoBean
    private MetricsExecutor metricsExecutor;

    @MockitoBean
    private OpenApiTrainingMapper openApiTrainingMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    @BeforeEach
    void setUp() {
        reset(trainingService, trainingMetrics, apiPerformanceMetrics, metricsExecutor, openApiTrainingMapper);

        when(metricsExecutor.executeWithMetrics(any(), any(), any(), any()))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(1);
                    return supplier.get();
                });

        doAnswer(invocation -> {
            Runnable businessLogic = invocation.getArgument(1);
            Runnable onSuccess = invocation.getArgument(2);

            businessLogic.run();
            if (onSuccess != null) {
                onSuccess.run();
            }
            return null;
        }).when(metricsExecutor).executeVoidWithMetrics(any(), any(), any(), any());

        when(openApiTrainingMapper.toInternalTrainingCreate(any()))
                .thenAnswer(invocation -> {
                    com.github.amangusss.dto.generated.TrainingCreateRequest request = invocation.getArgument(0);
                    return new TrainingDTO.Request.Create(
                            request.getTraineeUsername(),
                            request.getTrainerUsername(),
                            request.getTrainingName(),
                            request.getTrainingDate(),
                            request.getTrainingDuration()
                    );
                });
    }

    @Test
    @DisplayName("Should return 200 OK when adding training successfully")
    void shouldReturnOkWhenAddingTrainingSuccessfully() throws Exception {
        TrainingCreateRequest createRequest = createValidTrainingRequest();
        doNothing().when(trainingService).addTraining(any(TrainingDTO.Request.Create.class));
        doNothing().when(trainingMetrics).incrementTrainingCreated();
        doNothing().when(trainingMetrics).recordTrainingByTrainer(any(String.class));
        doNothing().when(trainingMetrics).recordTrainingByTrainee(any(String.class));
        doNothing().when(trainingMetrics).recordTrainingDuration(any(Double.class), any(String.class));

        mockMvc.perform(post(TRAININGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        verify(trainingService, times(1)).addTraining(any(TrainingDTO.Request.Create.class));
        verify(trainingMetrics, times(1)).incrementTrainingCreated();
        verify(trainingMetrics, times(1)).recordTrainingByTrainer(TRAINER_USERNAME);
        verify(trainingMetrics, times(1)).recordTrainingByTrainee(TRAINEE_USERNAME);
        verify(trainingMetrics, times(1)).recordTrainingDuration(TRAINING_DURATION, TRAINING_NAME);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when trainer username is null")
    void shouldReturnBadRequestWhenTrainerUsernameIsNull() throws Exception {
        TrainingCreateRequest invalidRequest = createTrainingRequestWithNullTrainerUsername();

        mockMvc.perform(post(TRAININGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(trainingService, never()).addTraining(any(TrainingDTO.Request.Create.class));
        verify(trainingMetrics, never()).incrementTrainingCreated();
    }

    @Test
    @DisplayName("Should return 400 Bad Request when trainee username is null")
    void shouldReturnBadRequestWhenTraineeUsernameIsNull() throws Exception {
        TrainingCreateRequest invalidRequest = createTrainingRequestWithNullTraineeUsername();

        mockMvc.perform(post(TRAININGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(trainingService, never()).addTraining(any(TrainingDTO.Request.Create.class));
        verify(trainingMetrics, never()).incrementTrainingCreated();
    }

    private TrainingCreateRequest createValidTrainingRequest() {
        return new TrainingCreateRequest(
                TRAINEE_USERNAME,
                TRAINER_USERNAME,
                TRAINING_NAME,
                TRAINING_DATE,
                TRAINING_DURATION
        );
    }

    private TrainingCreateRequest createTrainingRequestWithNullTrainerUsername() {
        return new TrainingCreateRequest(
                TRAINEE_USERNAME,
                null,
                TRAINING_NAME,
                TRAINING_DATE,
                TRAINING_DURATION
        );
    }

    private TrainingCreateRequest createTrainingRequestWithNullTraineeUsername() {
        return new TrainingCreateRequest(
                null,
                TRAINER_USERNAME,
                TRAINING_NAME,
                TRAINING_DATE,
                TRAINING_DURATION
        );
    }
}

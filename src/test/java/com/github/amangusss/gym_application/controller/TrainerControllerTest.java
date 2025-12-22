package com.github.amangusss.gym_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.jms.listener.WorkloadDlqListener;
import com.github.amangusss.gym_application.jms.service.WorkloadMessageProducer;
import com.github.amangusss.gym_application.jwt.JwtUtils;
import com.github.amangusss.gym_application.metrics.ApiPerformanceMetrics;
import com.github.amangusss.gym_application.metrics.MetricsExecutor;
import com.github.amangusss.gym_application.metrics.TrainerMetrics;
import com.github.amangusss.gym_application.service.BruteForceProtectionService;
import com.github.amangusss.gym_application.service.TrainerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "Aman.Nazarkulov")
@DisplayName("TrainerController Tests")
class TrainerControllerTest {

    private static final String TRAINER_FIRST_NAME = "Aman";
    private static final String TRAINER_LAST_NAME = "Nazarkulov";
    private static final String TRAINER_USERNAME = "Aman.Nazarkulov";
    private static final String VALID_PASSWORD = "password123";
    private static final Long SPECIALIZATION_ID = 1L;
    private static final String SPECIALIZATION_NAME = "YOGA";
    private static final String TRAINEE_NAME = "Dastan";
    private static final LocalDate PERIOD_FROM = LocalDate.parse("2025-01-01");
    private static final LocalDate PERIOD_TO = LocalDate.parse("2025-12-31");

    private static final String REGISTER_ENDPOINT = "/api/trainers/register";
    private static final String TRAINER_BY_USERNAME_ENDPOINT = "/api/trainers/{username}";
    private static final String ACTIVATE_ENDPOINT = "/api/trainers/{username}/activate";
    private static final String TRAININGS_ENDPOINT = "/api/trainers/{username}/trainings";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean(name = "metricsExecutor")
    private MetricsExecutor metricsExecutor;

    @MockitoBean(name = "trainerMetrics")
    private TrainerMetrics trainerMetrics;

    @MockitoBean(name = "apiPerformanceMetrics")
    private ApiPerformanceMetrics apiMetrics;

    @MockitoBean(name = "jwtUtils")
    private JwtUtils jwtUtils;

    @MockitoBean(name = "customUserDetailsService")
    private UserDetailsService userDetailsService;

    @MockitoBean(name = "bruteForceProtectionService")
    private BruteForceProtectionService bruteForceProtectionService;

    @MockitoBean
    private WorkloadMessageProducer workloadMessageProducer;

    @MockitoBean
    private WorkloadDlqListener workloadDlqListener;

    @BeforeEach
    void setUp() {
        reset(trainerService, trainerMetrics, apiMetrics, metricsExecutor);

        when(metricsExecutor.executeWithMetrics(any(), any(), any(), any()))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(1);
                    return supplier.get();
                });

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(1);
            if (runnable != null) runnable.run();
            return null;
        }).when(metricsExecutor).executeVoidWithMetrics(any(), any(), any(), any());
    }

    private Authentication createAuthentication(String username) {
        return new UsernamePasswordAuthenticationToken(
                username, null, java.util.Collections.emptyList());
    }

    @Test
    @DisplayName("Should return 200 OK and registered response when trainer registers successfully")
    void shouldReturnOkAndRegisteredResponseWhenTrainerRegistersSuccessfully() throws Exception {
        TrainerDTO.Request.Register registerRequest = createRegisterRequest();
        TrainerDTO.Response.Registered expectedResponse = createRegisteredResponse();
        when(trainerService.registerTrainer(any(TrainerDTO.Request.Register.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TRAINER_USERNAME))
                .andExpect(jsonPath("$.password").value(VALID_PASSWORD));

        verify(trainerService, times(1)).registerTrainer(any(TrainerDTO.Request.Register.class));
    }

    @Test
    @DisplayName("Should return 200 OK and profile when getting trainer profile")
    void shouldReturnOkAndProfileWhenGettingTrainerProfile() throws Exception {
        TrainerDTO.Response.Profile expectedProfile = createTrainerProfile();
        when(trainerService.getTrainerProfile(TRAINER_USERNAME)).thenReturn(expectedProfile);

        mockMvc.perform(get(TRAINER_BY_USERNAME_ENDPOINT, TRAINER_USERNAME)
                        .with(authentication(createAuthentication(TRAINER_USERNAME))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(TRAINER_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(TRAINER_LAST_NAME))
                .andExpect(jsonPath("$.specializationName").value(SPECIALIZATION_NAME));

        verify(trainerService, times(1)).getTrainerProfile(TRAINER_USERNAME);
    }

    @Test
    @DisplayName("Should return 200 OK and updated response when trainer updates successfully")
    void shouldReturnOkAndUpdatedResponseWhenTrainerUpdatesSuccessfully() throws Exception {
        TrainerDTO.Request.Update updateRequest = createUpdateRequest();
        TrainerDTO.Response.Updated expectedResponse = createUpdatedResponse();
        when(trainerService.updateTrainerProfile(any(TrainerDTO.Request.Update.class), eq(TRAINER_USERNAME)))
                .thenReturn(expectedResponse);

        mockMvc.perform(put(TRAINER_BY_USERNAME_ENDPOINT, TRAINER_USERNAME)
                        .with(authentication(createAuthentication(TRAINER_USERNAME)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TRAINER_USERNAME));

        verify(trainerService, times(1)).updateTrainerProfile(any(TrainerDTO.Request.Update.class), eq(TRAINER_USERNAME));
    }

    @Test
    @DisplayName("Should return 200 OK when updating trainer isActive successfully")
    void shouldReturnOkWhenUpdatingTrainerStatusSuccessfully() throws Exception {
        boolean newStatus = true;
        TrainerDTO.Request.UpdateStatus statusRequest = new TrainerDTO.Request.UpdateStatus(newStatus);

        mockMvc.perform(patch(ACTIVATE_ENDPOINT, TRAINER_USERNAME)
                        .with(authentication(createAuthentication(TRAINER_USERNAME)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());

        verify(trainerService, times(1)).updateTrainerStatus(TRAINER_USERNAME, newStatus);
    }

    @Test
    @DisplayName("Should return 200 OK and trainings list when getting trainer trainings")
    void shouldReturnOkAndTrainingsListWhenGettingTrainerTrainings() throws Exception {
        List<TrainingDTO.Response.TrainerTraining> expectedTrainings = Collections.emptyList();
        when(trainerService.getTrainerTrainings(
                TRAINER_USERNAME, PERIOD_FROM, PERIOD_TO, TRAINEE_NAME))
                .thenReturn(expectedTrainings);

        mockMvc.perform(get(TRAININGS_ENDPOINT, TRAINER_USERNAME)
                        .with(authentication(createAuthentication(TRAINER_USERNAME)))
                        .param("periodFrom", PERIOD_FROM.toString())
                        .param("periodTo", PERIOD_TO.toString())
                        .param("traineeName", TRAINEE_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(trainerService, times(1)).getTrainerTrainings(
                TRAINER_USERNAME, PERIOD_FROM, PERIOD_TO, TRAINEE_NAME);
    }

    private TrainerDTO.Request.Register createRegisterRequest() {
        return new TrainerDTO.Request.Register(
                TRAINER_FIRST_NAME, TRAINER_LAST_NAME, SPECIALIZATION_ID
        );
    }

    private TrainerDTO.Response.Registered createRegisteredResponse() {
        return new TrainerDTO.Response.Registered(TRAINER_USERNAME, VALID_PASSWORD);
    }

    private TrainerDTO.Response.Profile createTrainerProfile() {
        return new TrainerDTO.Response.Profile(
                TRAINER_FIRST_NAME, TRAINER_LAST_NAME, SPECIALIZATION_NAME, true, Collections.emptyList()
        );
    }

    private TrainerDTO.Request.Update createUpdateRequest() {
        return new TrainerDTO.Request.Update(
                TRAINER_FIRST_NAME, TRAINER_LAST_NAME, SPECIALIZATION_ID, true
        );
    }

    private TrainerDTO.Response.Updated createUpdatedResponse() {
        return new TrainerDTO.Response.Updated(
                TRAINER_USERNAME, TRAINER_FIRST_NAME, TRAINER_LAST_NAME,
                SPECIALIZATION_NAME, true, Collections.emptyList()
        );
    }
}

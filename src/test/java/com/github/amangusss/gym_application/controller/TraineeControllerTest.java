package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.jms.listener.WorkloadDlqListener;
import com.github.amangusss.gym_application.jms.service.WorkloadMessageProducer;
import com.github.amangusss.gym_application.jwt.JwtUtils;
import com.github.amangusss.gym_application.metrics.ApiPerformanceMetrics;
import com.github.amangusss.gym_application.metrics.TraineeMetrics;
import com.github.amangusss.gym_application.service.BruteForceProtectionService;
import com.github.amangusss.gym_application.service.TraineeService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amangusss.gym_application.metrics.MetricsExecutor;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("test")
@WithMockUser(username = "Dastan.Ibraimov")
@DisplayName("TraineeController Tests")
class TraineeControllerTest {

    private static final String TRAINEE_FIRST_NAME = "Dastan";
    private static final String TRAINEE_LAST_NAME = "Ibraimov";
    private static final String TRAINEE_USERNAME = "Dastan.Ibraimov";
    private static final String VALID_PASSWORD = "password123";
    private static final LocalDate BIRTH_DATE = LocalDate.of(2004, 8, 14);
    private static final String ADDRESS = "Panfilov St, 46A";
    private static final String NEW_ADDRESS = "New Address";
    private static final String TRAINER_USERNAME = "Aman.Nazarkulov";
    private static final String TRAINER_NAME = "Aman";
    private static final String TRAINING_TYPE = "YOGA";
    private static final LocalDate PERIOD_FROM = LocalDate.parse("2025-01-01");
    private static final LocalDate PERIOD_TO = LocalDate.parse("2025-12-31");

    private static final String REGISTER_ENDPOINT = "/api/trainees/register";
    private static final String TRAINEE_BY_USERNAME_ENDPOINT = "/api/trainees/{username}";
    private static final String ACTIVATE_ENDPOINT = "/api/trainees/{username}/activate";
    private static final String TRAININGS_ENDPOINT = "/api/trainees/{username}/trainings";
    private static final String UNASSIGNED_TRAINERS_ENDPOINT = "/api/trainees/{username}/trainers/unassigned";
    private static final String UPDATE_TRAINERS_ENDPOINT = "/api/trainees/{username}/trainers";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TraineeService traineeService;

    @MockitoBean
    private MetricsExecutor metricsExecutor;

    @MockitoBean
    private TraineeMetrics traineeMetrics;

    @MockitoBean
    private ApiPerformanceMetrics apiPerformanceMetrics;

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
        reset(traineeService, traineeMetrics, apiPerformanceMetrics, metricsExecutor);

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

    private Authentication createAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                TraineeControllerTest.TRAINEE_USERNAME, null, Collections.emptyList());
    }

    @Test
    @DisplayName("Should return 200 OK and registered response when trainee registers successfully")
    void shouldReturnOkAndRegisteredResponseWhenTraineeRegistersSuccessfully() throws Exception {
        TraineeDTO.Request.Register registerRequest = createRegisterRequest();
        TraineeDTO.Response.Registered expectedResponse = createRegisteredResponse();
        when(traineeService.createTrainee(any(TraineeDTO.Request.Register.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TRAINEE_USERNAME))
                .andExpect(jsonPath("$.password").value(VALID_PASSWORD));

        verify(traineeService, times(1)).createTrainee(any(TraineeDTO.Request.Register.class));
    }

    @Test
    @DisplayName("Should return 200 OK and profile when getting trainee profile")
    void shouldReturnOkAndProfileWhenGettingTraineeProfile() throws Exception {
        TraineeDTO.Response.Profile expectedProfile = createTraineeProfile();
        when(traineeService.getTraineeProfile(TRAINEE_USERNAME)).thenReturn(expectedProfile);

        mockMvc.perform(get(TRAINEE_BY_USERNAME_ENDPOINT, TRAINEE_USERNAME)
                        .with(authentication(createAuthentication())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(TRAINEE_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(TRAINEE_LAST_NAME))
                .andExpect(jsonPath("$.dateOfBirth").value(BIRTH_DATE.toString()))
                .andExpect(jsonPath("$.address").value(ADDRESS));

        verify(traineeService, times(1)).getTraineeProfile(TRAINEE_USERNAME);
    }

    @Test
    @DisplayName("Should return 200 OK and updated response when trainee updates successfully")
    void shouldReturnOkAndUpdatedResponseWhenTraineeUpdatesSuccessfully() throws Exception {
        TraineeDTO.Request.Update updateRequest = createUpdateRequest();
        TraineeDTO.Response.Updated expectedResponse = createUpdatedResponse();
        when(traineeService.updateTrainee(any(TraineeDTO.Request.Update.class), eq(TRAINEE_USERNAME)))
                .thenReturn(expectedResponse);

        mockMvc.perform(put(TRAINEE_BY_USERNAME_ENDPOINT, TRAINEE_USERNAME)
                        .with(authentication(createAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(NEW_ADDRESS));

        verify(traineeService, times(1)).updateTrainee(any(TraineeDTO.Request.Update.class), eq(TRAINEE_USERNAME));
    }

    @Test
    @DisplayName("Should return 200 OK when deleting trainee successfully")
    void shouldReturnOkWhenDeletingTraineeSuccessfully() throws Exception {
        doNothing().when(traineeService).deleteTraineeByUsername(TRAINEE_USERNAME);

        mockMvc.perform(delete(TRAINEE_BY_USERNAME_ENDPOINT, TRAINEE_USERNAME)
                        .with(authentication(createAuthentication())))
                .andExpect(status().isOk());

        verify(traineeService, times(1)).deleteTraineeByUsername(TRAINEE_USERNAME);
    }

    @Test
    @DisplayName("Should return 200 OK when updating trainee isActive successfully")
    void shouldReturnOkWhenUpdatingTraineeStatusSuccessfully() throws Exception {
        boolean newStatus = false;
        TraineeDTO.Request.UpdateStatus statusRequest = new TraineeDTO.Request.UpdateStatus(newStatus);
        doNothing().when(traineeService).updateTraineeStatus(TRAINEE_USERNAME, newStatus);

        mockMvc.perform(patch(ACTIVATE_ENDPOINT, TRAINEE_USERNAME)
                        .with(authentication(createAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());

        verify(traineeService, times(1)).updateTraineeStatus(TRAINEE_USERNAME, newStatus);
    }

    @Test
    @DisplayName("Should return 200 OK and trainings list when getting trainee trainings")
    void shouldReturnOkAndTrainingsListWhenGettingTraineeTrainings() throws Exception {
        List<TrainingDTO.Response.TraineeTraining> expectedTrainings = Collections.emptyList();
        when(traineeService.getTraineeTrainings(
                TRAINEE_USERNAME, PERIOD_FROM, PERIOD_TO, TRAINER_NAME, TRAINING_TYPE))
                .thenReturn(expectedTrainings);

        mockMvc.perform(get(TRAININGS_ENDPOINT, TRAINEE_USERNAME)
                        .with(authentication(createAuthentication()))
                        .param("periodFrom", PERIOD_FROM.toString())
                        .param("periodTo", PERIOD_TO.toString())
                        .param("trainerName", TRAINER_NAME)
                        .param("trainingType", TRAINING_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(traineeService, times(1)).getTraineeTrainings(
                TRAINEE_USERNAME, PERIOD_FROM, PERIOD_TO, TRAINER_NAME, TRAINING_TYPE);
    }

    @Test
    @DisplayName("Should return 200 OK and unassigned trainers list when getting unassigned trainers")
    void shouldReturnOkAndUnassignedTrainersListWhenGettingUnassignedTrainers() throws Exception {
        List<TrainerDTO.Response.Unassigned> expectedTrainers = Collections.emptyList();
        when(traineeService.getUnassignedTrainers(TRAINEE_USERNAME)).thenReturn(expectedTrainers);

        mockMvc.perform(get(UNASSIGNED_TRAINERS_ENDPOINT, TRAINEE_USERNAME)
                        .with(authentication(createAuthentication())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(traineeService, times(1)).getUnassignedTrainers(TRAINEE_USERNAME);
    }

    @Test
    @DisplayName("Should return 200 OK and trainers list when updating trainee trainers")
    void shouldReturnOkAndTrainersListWhenUpdatingTraineeTrainers() throws Exception {
        TraineeDTO.Request.UpdateTrainers updateTrainersRequest = createUpdateTrainersRequest();
        List<TrainerDTO.Response.InList> expectedTrainers = Collections.emptyList();
        when(traineeService.updateTraineeTrainers(eq(TRAINEE_USERNAME), any(TraineeDTO.Request.UpdateTrainers.class)))
                .thenReturn(expectedTrainers);

        mockMvc.perform(put(UPDATE_TRAINERS_ENDPOINT, TRAINEE_USERNAME)
                        .with(authentication(createAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTrainersRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(traineeService, times(1)).updateTraineeTrainers(eq(TRAINEE_USERNAME), any(TraineeDTO.Request.UpdateTrainers.class));
    }

    private TraineeDTO.Request.Register createRegisterRequest() {
        return new TraineeDTO.Request.Register(
                TRAINEE_FIRST_NAME, TRAINEE_LAST_NAME, BIRTH_DATE, ADDRESS
        );
    }

    private TraineeDTO.Response.Registered createRegisteredResponse() {
        return new TraineeDTO.Response.Registered(TRAINEE_USERNAME, VALID_PASSWORD);
    }

    private TraineeDTO.Response.Profile createTraineeProfile() {
        return new TraineeDTO.Response.Profile(
                TRAINEE_FIRST_NAME, TRAINEE_LAST_NAME, BIRTH_DATE, ADDRESS, true, Collections.emptyList()
        );
    }

    private TraineeDTO.Request.Update createUpdateRequest() {
        return new TraineeDTO.Request.Update(
                TRAINEE_FIRST_NAME, TRAINEE_LAST_NAME, BIRTH_DATE, NEW_ADDRESS, true
        );
    }

    private TraineeDTO.Response.Updated createUpdatedResponse() {
        return new TraineeDTO.Response.Updated(
                TRAINEE_USERNAME, TRAINEE_FIRST_NAME, TRAINEE_LAST_NAME,
                BIRTH_DATE, NEW_ADDRESS, true, Collections.emptyList()
        );
    }

    private TraineeDTO.Request.UpdateTrainers createUpdateTrainersRequest() {
        return new TraineeDTO.Request.UpdateTrainers(
                List.of(TRAINER_USERNAME)
        );
    }
}

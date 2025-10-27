package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.service.TrainerService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TrainerController.class)
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

    @BeforeEach
    void setUp() {
        reset(trainerService);
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
        when(trainerService.getTrainerProfile(TRAINER_USERNAME, VALID_PASSWORD)).thenReturn(expectedProfile);

        mockMvc.perform(get(TRAINER_BY_USERNAME_ENDPOINT, TRAINER_USERNAME)
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(TRAINER_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(TRAINER_LAST_NAME))
                .andExpect(jsonPath("$.specializationName").value(SPECIALIZATION_NAME));

        verify(trainerService, times(1)).getTrainerProfile(TRAINER_USERNAME, VALID_PASSWORD);
    }

    @Test
    @DisplayName("Should return 200 OK and updated response when trainer updates successfully")
    void shouldReturnOkAndUpdatedResponseWhenTrainerUpdatesSuccessfully() throws Exception {
        TrainerDTO.Request.Update updateRequest = createUpdateRequest();
        TrainerDTO.Response.Updated expectedResponse = createUpdatedResponse();
        when(trainerService.updateTrainerProfile(any(TrainerDTO.Request.Update.class), any(), eq(VALID_PASSWORD)))
                .thenReturn(expectedResponse);

        mockMvc.perform(put(TRAINER_BY_USERNAME_ENDPOINT, TRAINER_USERNAME)
                        .param("password", VALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TRAINER_USERNAME));

        verify(trainerService, times(1)).updateTrainerProfile(any(TrainerDTO.Request.Update.class), any(), eq(VALID_PASSWORD));
    }

    @Test
    @DisplayName("Should return 200 OK when updating trainer status successfully")
    void shouldReturnOkWhenUpdatingTrainerStatusSuccessfully() throws Exception {
        boolean newStatus = true;
        TrainerDTO.Request.UpdateStatus statusRequest = new TrainerDTO.Request.UpdateStatus(newStatus);
        doNothing().when(trainerService).updateTrainerStatus(TRAINER_USERNAME, newStatus, VALID_PASSWORD);

        mockMvc.perform(patch(ACTIVATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", VALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());

        verify(trainerService, times(1)).updateTrainerStatus(TRAINER_USERNAME, newStatus, VALID_PASSWORD);
    }

    @Test
    @DisplayName("Should return 200 OK and trainings list when getting trainer trainings")
    void shouldReturnOkAndTrainingsListWhenGettingTrainerTrainings() throws Exception {
        List<TrainingDTO.Response.TrainerTraining> expectedTrainings = Collections.emptyList();
        when(trainerService.getTrainerTrainings(
                TRAINER_USERNAME, VALID_PASSWORD, PERIOD_FROM, PERIOD_TO, TRAINEE_NAME))
                .thenReturn(expectedTrainings);

        mockMvc.perform(get(TRAININGS_ENDPOINT, TRAINER_USERNAME)
                        .param("password", VALID_PASSWORD)
                        .param("periodFrom", PERIOD_FROM.toString())
                        .param("periodTo", PERIOD_TO.toString())
                        .param("traineeName", TRAINEE_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(trainerService, times(1)).getTrainerTrainings(
                TRAINER_USERNAME, VALID_PASSWORD, PERIOD_FROM, PERIOD_TO, TRAINEE_NAME);
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

package com.github.amangusss.gym_application.controller;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.amangusss.gym_application.facade.TraineeFacade;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TraineeController.class)
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
    private TraineeFacade traineeFacade;

    @BeforeEach
    void setUp() {
        reset(traineeFacade);
    }

    @Test
    @DisplayName("Should return 200 OK and registered response when trainee registers successfully")
    void shouldReturnOkAndRegisteredResponseWhenTraineeRegistersSuccessfully() throws Exception {
        TraineeDTO.Request.Register registerRequest = createRegisterRequest();
        TraineeDTO.Response.Registered expectedResponse = createRegisteredResponse();
        when(traineeFacade.registerTrainee(any(TraineeDTO.Request.Register.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TRAINEE_USERNAME))
                .andExpect(jsonPath("$.password").value(VALID_PASSWORD));

        verify(traineeFacade, times(1)).registerTrainee(any(TraineeDTO.Request.Register.class));
    }

    @Test
    @DisplayName("Should return 200 OK and profile when getting trainee profile")
    void shouldReturnOkAndProfileWhenGettingTraineeProfile() throws Exception {
        TraineeDTO.Response.Profile expectedProfile = createTraineeProfile();
        when(traineeFacade.getTraineeProfile(TRAINEE_USERNAME, VALID_PASSWORD)).thenReturn(expectedProfile);

        mockMvc.perform(get(TRAINEE_BY_USERNAME_ENDPOINT, TRAINEE_USERNAME)
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(TRAINEE_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(TRAINEE_LAST_NAME))
                .andExpect(jsonPath("$.dateOfBirth").value(BIRTH_DATE.toString()))
                .andExpect(jsonPath("$.address").value(ADDRESS));

        verify(traineeFacade, times(1)).getTraineeProfile(TRAINEE_USERNAME, VALID_PASSWORD);
    }

    @Test
    @DisplayName("Should return 200 OK and updated response when trainee updates successfully")
    void shouldReturnOkAndUpdatedResponseWhenTraineeUpdatesSuccessfully() throws Exception {
        TraineeDTO.Request.Update updateRequest = createUpdateRequest();
        TraineeDTO.Response.Updated expectedResponse = createUpdatedResponse();
        when(traineeFacade.updateTrainee(any(TraineeDTO.Request.Update.class), any(), eq(VALID_PASSWORD)))
                .thenReturn(expectedResponse);

        mockMvc.perform(put(TRAINEE_BY_USERNAME_ENDPOINT, TRAINEE_USERNAME)
                        .param("password", VALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TRAINEE_USERNAME))
                .andExpect(jsonPath("$.address").value(NEW_ADDRESS));

        verify(traineeFacade, times(1)).updateTrainee(any(TraineeDTO.Request.Update.class), any(), eq(VALID_PASSWORD));
    }

    @Test
    @DisplayName("Should return 200 OK when deleting trainee successfully")
    void shouldReturnOkWhenDeletingTraineeSuccessfully() throws Exception {
        doNothing().when(traineeFacade).deleteTrainee(TRAINEE_USERNAME, VALID_PASSWORD);

        mockMvc.perform(delete(TRAINEE_BY_USERNAME_ENDPOINT, TRAINEE_USERNAME)
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isOk());

        verify(traineeFacade, times(1)).deleteTrainee(TRAINEE_USERNAME, VALID_PASSWORD);
    }

    @Test
    @DisplayName("Should return 200 OK when updating trainee status successfully")
    void shouldReturnOkWhenUpdatingTraineeStatusSuccessfully() throws Exception {
        boolean newStatus = false;
        TraineeDTO.Request.UpdateStatus statusRequest = new TraineeDTO.Request.UpdateStatus(TRAINEE_USERNAME, newStatus);
        doNothing().when(traineeFacade).updateTraineeStatus(TRAINEE_USERNAME, newStatus, VALID_PASSWORD);

        mockMvc.perform(patch(ACTIVATE_ENDPOINT, TRAINEE_USERNAME)
                        .param("password", VALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());

        verify(traineeFacade, times(1)).updateTraineeStatus(TRAINEE_USERNAME, newStatus, VALID_PASSWORD);
    }

    @Test
    @DisplayName("Should return 200 OK and trainings list when getting trainee trainings")
    void shouldReturnOkAndTrainingsListWhenGettingTraineeTrainings() throws Exception {
        List<TrainingDTO.Response.TraineeTraining> expectedTrainings = Collections.emptyList();
        when(traineeFacade.getTraineeTrainings(
                TRAINEE_USERNAME, VALID_PASSWORD, PERIOD_FROM, PERIOD_TO, TRAINER_NAME, TRAINING_TYPE))
                .thenReturn(expectedTrainings);

        mockMvc.perform(get(TRAININGS_ENDPOINT, TRAINEE_USERNAME)
                        .param("password", VALID_PASSWORD)
                        .param("periodFrom", PERIOD_FROM.toString())
                        .param("periodTo", PERIOD_TO.toString())
                        .param("trainerName", TRAINER_NAME)
                        .param("trainingType", TRAINING_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(traineeFacade, times(1)).getTraineeTrainings(
                TRAINEE_USERNAME, VALID_PASSWORD, PERIOD_FROM, PERIOD_TO, TRAINER_NAME, TRAINING_TYPE);
    }

    @Test
    @DisplayName("Should return 200 OK and unassigned trainers list when getting unassigned trainers")
    void shouldReturnOkAndUnassignedTrainersListWhenGettingUnassignedTrainers() throws Exception {
        List<TrainerDTO.Response.Unassigned> expectedTrainers = Collections.emptyList();
        when(traineeFacade.getUnassignedTrainers(TRAINEE_USERNAME, VALID_PASSWORD)).thenReturn(expectedTrainers);

        mockMvc.perform(get(UNASSIGNED_TRAINERS_ENDPOINT, TRAINEE_USERNAME)
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(traineeFacade, times(1)).getUnassignedTrainers(TRAINEE_USERNAME, VALID_PASSWORD);
    }

    @Test
    @DisplayName("Should return 200 OK and trainers list when updating trainee trainers")
    void shouldReturnOkAndTrainersListWhenUpdatingTraineeTrainers() throws Exception {
        TraineeDTO.Request.UpdateTrainers updateTrainersRequest = createUpdateTrainersRequest();
        List<TrainerDTO.Response.InList> expectedTrainers = Collections.emptyList();
        when(traineeFacade.updateTraineeTrainers(eq(TRAINEE_USERNAME), any(), eq(VALID_PASSWORD)))
                .thenReturn(expectedTrainers);

        mockMvc.perform(put(UPDATE_TRAINERS_ENDPOINT, TRAINEE_USERNAME)
                        .param("password", VALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTrainersRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(traineeFacade, times(1)).updateTraineeTrainers(eq(TRAINEE_USERNAME), any(), eq(VALID_PASSWORD));
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
                TRAINEE_USERNAME, List.of(TRAINER_USERNAME)
        );
    }
}

package com.github.amangusss.gym_application.integration.controller.training.create;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amangusss.gym_application.dto.training.TrainingDTO;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WireMockTest(httpPort = 8089)
@DisplayName("TrainingController WireMock Integration Tests for Create Operations")
public class TrainingControllerCreateWireMockTest {

    private static final String TRAININGS_ENDPOINT = "/api/trainings";
    private static final String TRAINER_USERNAME = "John.Doe";
    private static final String TRAINEE_USERNAME = "Jane.Smith";
    private static final String TRAINING_NAME = "Morning Cardio";
    private static final LocalDate TRAINING_DATE = LocalDate.now().plusDays(1);
    private static final int TRAINING_DURATION = 60;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        setupExternalServiceStubs();
    }

    @Test
    @DisplayName("Should create training successfully when all data is valid")
    void shouldCreateTrainingSuccessfullyWhenAllDataIsValid() throws Exception {
        TrainingDTO.Request.Create request = createValidTrainingRequest();

        mockMvc.perform(post(TRAININGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when trainer username is null")
    void shouldReturnBadRequestWhenTrainerUsernameIsNull() throws Exception {
        TrainingDTO.Request.Create request = new TrainingDTO.Request.Create(
                TRAINEE_USERNAME,
                null,
                TRAINING_NAME,
                TRAINING_DATE,
                TRAINING_DURATION
        );

        mockMvc.perform(post(TRAININGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when trainee username is null")
    void shouldReturnBadRequestWhenTraineeUsernameIsNull() throws Exception {
        TrainingDTO.Request.Create request = new TrainingDTO.Request.Create(
                null,
                TRAINER_USERNAME,
                TRAINING_NAME,
                TRAINING_DATE,
                TRAINING_DURATION
        );

        mockMvc.perform(post(TRAININGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when training name is null")
    void shouldReturnBadRequestWhenTrainingNameIsNull() throws Exception {
        TrainingDTO.Request.Create request = new TrainingDTO.Request.Create(
                TRAINER_USERNAME,
                TRAINEE_USERNAME,
                null,
                TRAINING_DATE,
                TRAINING_DURATION
        );

        mockMvc.perform(post(TRAININGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when training date is null")
    void shouldReturnBadRequestWhenTrainingDateIsNull() throws Exception {
        TrainingDTO.Request.Create request = new TrainingDTO.Request.Create(
                TRAINER_USERNAME,
                TRAINEE_USERNAME,
                TRAINING_NAME,
                null,
                TRAINING_DURATION
        );

        mockMvc.perform(post(TRAININGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when training duration is negative")
    void shouldReturnBadRequestWhenTrainingDurationIsNegative() throws Exception {
        TrainingDTO.Request.Create request = new TrainingDTO.Request.Create(
                TRAINER_USERNAME,
                TRAINEE_USERNAME,
                TRAINING_NAME,
                TRAINING_DATE,
                -10
        );

        mockMvc.perform(post(TRAININGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private void setupExternalServiceStubs() {
        stubFor(WireMock.post(urlEqualTo("/external/validate"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"valid\": true}")));

        stubFor(WireMock.post(urlEqualTo("/external/notify"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"sent\": true}")));
    }

    private TrainingDTO.Request.Create createValidTrainingRequest() {
        return new TrainingDTO.Request.Create(
                TRAINEE_USERNAME,
                TRAINER_USERNAME,
                TRAINING_NAME,
                TRAINING_DATE,
                TRAINING_DURATION
        );
    }
}

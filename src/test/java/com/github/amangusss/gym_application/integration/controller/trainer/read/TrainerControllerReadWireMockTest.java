package com.github.amangusss.gym_application.integration.controller.trainer.read;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WireMockTest(httpPort = 8089)
@DisplayName("TrainerController WireMock Integration Tests for Read Operations")
public class TrainerControllerReadWireMockTest {

    private static final String TRAINER_BY_USERNAME_ENDPOINT = "/api/trainers/{username}";
    private static final String TRAINER_TRAININGS_ENDPOINT = "/api/trainers/{username}/trainings";

    private static final String TRAINER_USERNAME = "John.Doe";
    private static final String TRAINER_PASSWORD = "password123";
    private static final String TRAINER_FIRST_NAME = "John";
    private static final String TRAINER_LAST_NAME = "Doe";

    private static final String NON_EXISTENT_USERNAME = "NonExistent.User";
    private static final String WRONG_PASSWORD = "wrongpassword";

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        setupExternalServiceStubs();
    }

    @Test
    @DisplayName("Should return 200 OK and trainer profile when valid credentials provided")
    void shouldReturnOkAndTrainerProfileWhenValidCredentials() throws Exception {
        mockMvc.perform(get(TRAINER_BY_USERNAME_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(TRAINER_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(TRAINER_LAST_NAME))
                .andExpect(jsonPath("$.specializationName").exists())
                .andExpect(jsonPath("$.isActive").exists());
    }

    @Test
    @DisplayName("Should return 401 when trainer username does not exist")
    void shouldReturn404WhenTrainerUsernameDoesNotExist() throws Exception {
        mockMvc.perform(get(TRAINER_BY_USERNAME_ENDPOINT, NON_EXISTENT_USERNAME)
                        .param("password", TRAINER_PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication Error"));
    }

    @Test
    @DisplayName("Should return 401 when password is incorrect")
    void shouldReturn401WhenPasswordIsIncorrect() throws Exception {
        mockMvc.perform(get(TRAINER_BY_USERNAME_ENDPOINT, TRAINER_USERNAME)
                        .param("password", WRONG_PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication Error"));
    }

    @Test
    @DisplayName("Should return 400 when password parameter is missing")
    void shouldReturn400WhenPasswordParameterIsMissing() throws Exception {
        mockMvc.perform(get(TRAINER_BY_USERNAME_ENDPOINT, TRAINER_USERNAME))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when username is empty")
    void shouldReturn400WhenUsernameIsEmpty() throws Exception {
        mockMvc.perform(get(TRAINER_BY_USERNAME_ENDPOINT, "")
                        .param("password", TRAINER_PASSWORD))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 200 OK and list of trainings when trainer exists")
    void shouldReturnOkAndTrainingsListWhenTrainerExists() throws Exception {
        mockMvc.perform(get(TRAINER_TRAININGS_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should return 200 OK and filtered trainings by date range")
    void shouldReturnOkAndFilteredTrainingsByDateRange() throws Exception {
        mockMvc.perform(get(TRAINER_TRAININGS_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .param("periodFrom", "2025-01-01")
                        .param("periodTo", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should return 200 OK and filtered trainings by trainee name")
    void shouldReturnOkAndFilteredTrainingsByTraineeName() throws Exception {
        mockMvc.perform(get(TRAINER_TRAININGS_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .param("traineeName", "Jane.Smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should return 200 OK and filtered trainings by all filters")
    void shouldReturnOkAndFilteredTrainingsByAllFilters() throws Exception {
        mockMvc.perform(get(TRAINER_TRAININGS_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .param("periodFrom", "2025-01-01")
                        .param("periodTo", "2025-12-31")
                        .param("traineeName", "Jane.Smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should return 401 when getting trainings for non-existent trainer")
    void shouldReturn404WhenGettingTrainingsForNonExistentTrainer() throws Exception {
        mockMvc.perform(get(TRAINER_TRAININGS_ENDPOINT, NON_EXISTENT_USERNAME)
                        .param("password", TRAINER_PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication Error"));
    }

    @Test
    @DisplayName("Should return 401 when getting trainings with incorrect password")
    void shouldReturn401WhenGettingTrainingsWithIncorrectPassword() throws Exception {
        mockMvc.perform(get(TRAINER_TRAININGS_ENDPOINT, TRAINER_USERNAME)
                        .param("password", WRONG_PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication Error"));
    }

    @Test
    @DisplayName("Should return 400 when password parameter is missing for trainings")
    void shouldReturn400WhenPasswordParameterIsMissingForTrainings() throws Exception {
        mockMvc.perform(get(TRAINER_TRAININGS_ENDPOINT, TRAINER_USERNAME))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when periodFrom is invalid date format")
    void shouldReturn400WhenPeriodFromIsInvalidDateFormat() throws Exception {
        mockMvc.perform(get(TRAINER_TRAININGS_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .param("periodFrom", "invalid-date"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when periodTo is invalid date format")
    void shouldReturn400WhenPeriodToIsInvalidDateFormat() throws Exception {
        mockMvc.perform(get(TRAINER_TRAININGS_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .param("periodTo", "invalid-date"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return empty list when no trainings match filters")
    void shouldReturnEmptyListWhenNoTrainingsMatchFilters() throws Exception {
        mockMvc.perform(get(TRAINER_TRAININGS_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .param("periodFrom", "2099-01-01")
                        .param("periodTo", "2099-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
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
}

package com.github.amangusss.gym_application.integration.controller.trainer.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WireMockTest(httpPort = 8089)
@DisplayName("TrainerController WireMock Integration Tests for Update Operations")
public class TrainerControllerUpdateWireMockTest {

    private static final String TRAINER_UPDATE_ENDPOINT = "/api/trainers/{username}";
    private static final String TRAINER_ACTIVATE_ENDPOINT = "/api/trainers/{username}/activate";

    private static final String TRAINER_USERNAME = "John.Doe";
    private static final String TRAINER_PASSWORD = "password123";
    private static final String TRAINER_FIRST_NAME = "John";
    private static final String TRAINER_LAST_NAME = "Doe";
    private static final String UPDATED_FIRST_NAME = "Johnny";
    private static final String UPDATED_LAST_NAME = "Doel";
    private static final Long TRAINER_SPECIALIZATION = 1L;
    private static final Long UPDATED_SPECIALIZATION = 2L;

    private static final String NON_EXISTENT_USERNAME = "NonExistent.User";
    private static final String WRONG_PASSWORD = "wrongpassword";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        setupExternalServiceStubs();
    }

    @Test
    @DisplayName("Should return 200 OK and updated profile when valid update request")
    void shouldReturnOkAndUpdatedProfileWhenValidUpdateRequest() throws Exception {
        TrainerDTO.Request.Update updateRequest = createValidUpdateRequest();

        mockMvc.perform(put(TRAINER_UPDATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TRAINER_USERNAME))
                .andExpect(jsonPath("$.firstName").value(UPDATED_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(UPDATED_LAST_NAME))
                .andExpect(jsonPath("$.specializationName").exists())
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    @DisplayName("Should return 200 OK when updating only first name")
    void shouldReturnOkWhenUpdatingOnlyFirstName() throws Exception {
        TrainerDTO.Request.Update updateRequest = createUpdateRequestWithOnlyFirstName();

        mockMvc.perform(put(TRAINER_UPDATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(UPDATED_FIRST_NAME));
    }

    @Test
    @DisplayName("Should return 200 OK when updating only last name")
    void shouldReturnOkWhenUpdatingOnlyLastName() throws Exception {
        TrainerDTO.Request.Update updateRequest = createUpdateRequestWithOnlyLastName();

        mockMvc.perform(put(TRAINER_UPDATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value(UPDATED_LAST_NAME));
    }

    @Test
    @DisplayName("Should return 200 OK when updating specialization")
    void shouldReturnOkWhenUpdatingSpecialization() throws Exception {
        TrainerDTO.Request.Update updateRequest = createUpdateRequestWithNewSpecialization();

        mockMvc.perform(put(TRAINER_UPDATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specializationName").exists());
    }

    @Test
    @DisplayName("Should return 200 OK when updating active status to false")
    void shouldReturnOkWhenUpdatingActiveStatusToFalse() throws Exception {
        TrainerDTO.Request.Update updateRequest = createUpdateRequestWithActiveStatusFalse();

        mockMvc.perform(put(TRAINER_UPDATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when first name is null")
    void shouldReturnBadRequestWhenFirstNameIsNull() throws Exception {
        TrainerDTO.Request.Update updateRequest = createUpdateRequestWithNullFirstName();

        mockMvc.perform(put(TRAINER_UPDATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when last name is null")
    void shouldReturnBadRequestWhenLastNameIsNull() throws Exception {
        TrainerDTO.Request.Update updateRequest = createUpdateRequestWithNullLastName();

        mockMvc.perform(put(TRAINER_UPDATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when specialization is null")
    void shouldReturnBadRequestWhenSpecializationIsNull() throws Exception {
        TrainerDTO.Request.Update updateRequest = createUpdateRequestWithNullSpecialization();

        mockMvc.perform(put(TRAINER_UPDATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when first name is blank")
    void shouldReturnBadRequestWhenFirstNameIsBlank() throws Exception {
        TrainerDTO.Request.Update updateRequest = createUpdateRequestWithBlankFirstName();

        mockMvc.perform(put(TRAINER_UPDATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when last name is blank")
    void shouldReturnBadRequestWhenLastNameIsBlank() throws Exception {
        TrainerDTO.Request.Update updateRequest = createUpdateRequestWithBlankLastName();

        mockMvc.perform(put(TRAINER_UPDATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 401 when updating non-existent trainer")
    void shouldReturn404WhenUpdatingNonExistentTrainer() throws Exception {
        TrainerDTO.Request.Update updateRequest = createValidUpdateRequest();

        mockMvc.perform(put(TRAINER_UPDATE_ENDPOINT, NON_EXISTENT_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication Error"));
    }

    @Test
    @DisplayName("Should return 401 when updating with incorrect password")
    void shouldReturn401WhenUpdatingWithIncorrectPassword() throws Exception {
        TrainerDTO.Request.Update updateRequest = createValidUpdateRequest();

        mockMvc.perform(put(TRAINER_UPDATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", WRONG_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication Error"));
    }

    @Test
    @DisplayName("Should return 400 when password parameter is missing")
    void shouldReturn400WhenPasswordParameterIsMissing() throws Exception {
        TrainerDTO.Request.Update updateRequest = createValidUpdateRequest();

        mockMvc.perform(put(TRAINER_UPDATE_ENDPOINT, TRAINER_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 200 OK when activating trainer")
    void shouldReturnOkWhenActivatingTrainer() throws Exception {
        TrainerDTO.Request.UpdateStatus statusRequest = createStatusRequestWithActiveTrue();

        mockMvc.perform(patch(TRAINER_ACTIVATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 200 OK when deactivating trainer")
    void shouldReturnOkWhenDeactivatingTrainer() throws Exception {
        TrainerDTO.Request.UpdateStatus statusRequest = createStatusRequestWithActiveFalse();

        mockMvc.perform(patch(TRAINER_ACTIVATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 401 when activating non-existent trainer")
    void shouldReturn404WhenActivatingNonExistentTrainer() throws Exception {
        TrainerDTO.Request.UpdateStatus statusRequest = createStatusRequestWithActiveTrue();

        mockMvc.perform(patch(TRAINER_ACTIVATE_ENDPOINT, NON_EXISTENT_USERNAME)
                        .param("password", TRAINER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication Error"));
    }

    @Test
    @DisplayName("Should return 401 when activating with incorrect password")
    void shouldReturn401WhenActivatingWithIncorrectPassword() throws Exception {
        TrainerDTO.Request.UpdateStatus statusRequest = createStatusRequestWithActiveTrue();

        mockMvc.perform(patch(TRAINER_ACTIVATE_ENDPOINT, TRAINER_USERNAME)
                        .param("password", WRONG_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication Error"));
    }

    @Test
    @DisplayName("Should return 400 when password parameter is missing for activation")
    void shouldReturn400WhenPasswordParameterIsMissingForActivation() throws Exception {
        TrainerDTO.Request.UpdateStatus statusRequest = createStatusRequestWithActiveTrue();

        mockMvc.perform(patch(TRAINER_ACTIVATE_ENDPOINT, TRAINER_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isBadRequest());
    }

    private TrainerDTO.Request.Update createValidUpdateRequest() {
        return new TrainerDTO.Request.Update(
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                TRAINER_SPECIALIZATION,
                true
        );
    }

    private TrainerDTO.Request.Update createUpdateRequestWithOnlyFirstName() {
        return new TrainerDTO.Request.Update(
                UPDATED_FIRST_NAME,
                TRAINER_LAST_NAME,
                TRAINER_SPECIALIZATION,
                true
        );
    }

    private TrainerDTO.Request.Update createUpdateRequestWithOnlyLastName() {
        return new TrainerDTO.Request.Update(
                TRAINER_FIRST_NAME,
                UPDATED_LAST_NAME,
                TRAINER_SPECIALIZATION,
                true
        );
    }

    private TrainerDTO.Request.Update createUpdateRequestWithNewSpecialization() {
        return new TrainerDTO.Request.Update(
                TRAINER_FIRST_NAME,
                TRAINER_LAST_NAME,
                UPDATED_SPECIALIZATION,
                true
        );
    }

    private TrainerDTO.Request.Update createUpdateRequestWithActiveStatusFalse() {
        return new TrainerDTO.Request.Update(
                TRAINER_FIRST_NAME,
                TRAINER_LAST_NAME,
                TRAINER_SPECIALIZATION,
                false
        );
    }

    private TrainerDTO.Request.Update createUpdateRequestWithNullFirstName() {
        return new TrainerDTO.Request.Update(
                null,
                TRAINER_LAST_NAME,
                TRAINER_SPECIALIZATION,
                true
        );
    }

    private TrainerDTO.Request.Update createUpdateRequestWithNullLastName() {
        return new TrainerDTO.Request.Update(
                TRAINER_FIRST_NAME,
                null,
                TRAINER_SPECIALIZATION,
                true
        );
    }

    private TrainerDTO.Request.Update createUpdateRequestWithNullSpecialization() {
        return new TrainerDTO.Request.Update(
                TRAINER_FIRST_NAME,
                TRAINER_LAST_NAME,
                null,
                true
        );
    }

    private TrainerDTO.Request.Update createUpdateRequestWithBlankFirstName() {
        return new TrainerDTO.Request.Update(
                "   ",
                TRAINER_LAST_NAME,
                TRAINER_SPECIALIZATION,
                true
        );
    }

    private TrainerDTO.Request.Update createUpdateRequestWithBlankLastName() {
        return new TrainerDTO.Request.Update(
                TRAINER_FIRST_NAME,
                "   ",
                TRAINER_SPECIALIZATION,
                true
        );
    }

    private TrainerDTO.Request.UpdateStatus createStatusRequestWithActiveTrue() {
        return new TrainerDTO.Request.UpdateStatus(true);
    }

    private TrainerDTO.Request.UpdateStatus createStatusRequestWithActiveFalse() {
        return new TrainerDTO.Request.UpdateStatus(false);
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

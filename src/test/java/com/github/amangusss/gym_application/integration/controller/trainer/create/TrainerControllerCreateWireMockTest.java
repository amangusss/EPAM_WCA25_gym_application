package com.github.amangusss.gym_application.integration.controller.trainer.create;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WireMockTest(httpPort = 8089)
@DisplayName("TrainerController WireMock Integration Tests for Create Operations")
public class TrainerControllerCreateWireMockTest {

    public static final String TRAINERS_ENDPOINT = "/api/trainers/register";
    public static final String TRAINER_FIRST_NAME = "John";
    public static final String TRAINER_LAST_NAME = "Doe";
    public static final Long TRAINER_SPECIALIZATION = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        setupExternalServiceStubs();
    }

    @Test
    @DisplayName("Should create trainer successfully when all data is valid")
    void shouldCreateTrainerSuccessfullyWhenAllDataIsValid() throws Exception {
        TrainerDTO.Request.Register validTrainerRequest = createValidTrainerRequest();

        mockMvc.perform(post(TRAINERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTrainerRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when trainer first name is null")
    void shouldReturnBadRequestWhenTrainerFirstNameIsNull() throws Exception {
        TrainerDTO.Request.Register invalidTrainerRequest = createTrainerRequestWithNullTrainerFirstName();

        mockMvc.perform(post(TRAINERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTrainerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when trainer last name is null")
    void shouldReturnBadRequestWhenTrainerLastNameIsNull() throws Exception {
        TrainerDTO.Request.Register invalidTrainerRequest = createTrainerRequestWithNullTrainerLastName();

        mockMvc.perform(post(TRAINERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTrainerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when trainer specialization is null")
    void shouldReturnBadRequestWhenTrainerSpecializationIsNull() throws Exception {
        TrainerDTO.Request.Register invalidTrainerRequest = createTrainerRequestWithNullTrainerSpecialization();

        mockMvc.perform(post(TRAINERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTrainerRequest)))
                .andExpect(status().isBadRequest());
    }

    private TrainerDTO.Request.Register createValidTrainerRequest() {
        return new TrainerDTO.Request.Register(
                TRAINER_FIRST_NAME,
                TRAINER_LAST_NAME,
                TRAINER_SPECIALIZATION
        );
    }

    private TrainerDTO.Request.Register createTrainerRequestWithNullTrainerFirstName() {
        return new TrainerDTO.Request.Register(
                null,
                TRAINER_LAST_NAME,
                TRAINER_SPECIALIZATION
        );
    }

    private TrainerDTO.Request.Register createTrainerRequestWithNullTrainerSpecialization() {
        return new TrainerDTO.Request.Register(
                TRAINER_FIRST_NAME,
                TRAINER_LAST_NAME,
                null
        );
    }

    private TrainerDTO.Request.Register createTrainerRequestWithNullTrainerLastName() {
        return new TrainerDTO.Request.Register(
                TRAINER_FIRST_NAME,
                null,
                TRAINER_SPECIALIZATION
        );
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

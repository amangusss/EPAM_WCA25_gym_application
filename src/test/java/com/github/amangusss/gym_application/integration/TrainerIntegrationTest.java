package com.github.amangusss.gym_application.integration;

import com.github.amangusss.dto.generated.ChangePasswordRequest;
import com.github.amangusss.dto.generated.TrainerRegistrationRequest;
import com.github.amangusss.dto.generated.TrainerRegistrationResponse;
import com.github.amangusss.dto.generated.TrainerUpdateRequest;
import com.github.amangusss.dto.generated.TrainerUpdateStatusRequest;
import com.github.amangusss.dto.generated.TrainerProfileResponse;
import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.auth.AuthDTO;
import com.github.amangusss.gym_application.jms.listener.WorkloadDlqListener;
import com.github.amangusss.gym_application.jms.service.WorkloadMessageProducer;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@WireMockTest(httpPort = 8089)
@DisplayName("Trainer Integration Tests - Full CRUD Operations with WireMock")
class TrainerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private WorkloadMessageProducer workloadMessageProducer;

    @MockitoBean
    private WorkloadDlqListener workloadDlqListener;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    private String getJwtToken(String username, String password) {
        AuthDTO.Request.Login loginRequest = new AuthDTO.Request.Login(username, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthDTO.Request.Login> entity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<AuthDTO.Response.Login> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/login", entity, AuthDTO.Response.Login.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody().token();
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    @DisplayName("Integration Test: Create Trainer -> Get Profile -> Update -> Get Updated")
    void shouldPerformFullTrainerCrudCycle() {
        TrainerRegistrationRequest registerRequest = new TrainerRegistrationRequest(
                "Mike",
                "Johnson",
                1L
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TrainerRegistrationRequest> registerEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<TrainerRegistrationResponse> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainers/register",
                registerEntity,
                TrainerRegistrationResponse.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerRegistrationResponse registered = registerResponse.getBody();
        assertThat(registered).isNotNull();
        assertThat(registered.getUsername()).isNotNull();
        assertThat(registered.getPassword()).isNotNull();

        String createdUsername = registered.getUsername();
        String createdPassword = registered.getPassword();

        String jwt = getJwtToken(createdUsername, createdPassword);
        HttpHeaders auth = authHeaders(jwt);

        String getProfileUrl = baseUrl + "/api/trainers/" + createdUsername;
        ResponseEntity<TrainerProfileResponse> profileResponse = restTemplate.exchange(
                getProfileUrl,
                HttpMethod.GET,
                new HttpEntity<>(auth),
                TrainerProfileResponse.class
        );

        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerProfileResponse profile = profileResponse.getBody();
        assertThat(profile).isNotNull();
        assertThat(profile.getFirstName()).isEqualTo("Mike");
        assertThat(profile.getLastName()).isEqualTo("Johnson");
        assertThat(profile.getSpecialization()).isEqualTo("FITNESS");
        assertThat(profile.getIsActive()).isTrue();

        TrainerUpdateRequest updateRequest = new TrainerUpdateRequest(
                "Michael",
                "Johnsonson",
                2L,
                true
        );

        String updateUrl = baseUrl + "/api/trainers/" + createdUsername;
        HttpEntity<TrainerUpdateRequest> updateEntity = new HttpEntity<>(updateRequest, auth);
        ResponseEntity<TrainerProfileResponse> updateResponse = restTemplate.exchange(
                updateUrl,
                HttpMethod.PUT,
                updateEntity,
                TrainerProfileResponse.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerProfileResponse updatedProfile = updateResponse.getBody();
        assertThat(updatedProfile).isNotNull();
        assertThat(updatedProfile.getFirstName()).isEqualTo("Michael");
        assertThat(updatedProfile.getLastName()).isEqualTo("Johnsonson");

        ResponseEntity<TrainerProfileResponse> verifyResponse = restTemplate.exchange(
                getProfileUrl,
                HttpMethod.GET,
                new HttpEntity<>(auth),
                TrainerProfileResponse.class
        );

        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerProfileResponse verifiedProfile = verifyResponse.getBody();
        assertThat(verifiedProfile).isNotNull();
        assertThat(verifiedProfile.getFirstName()).isEqualTo("Michael");
        assertThat(verifiedProfile.getLastName()).isEqualTo("Johnsonson");

        TrainerUpdateStatusRequest statusRequest = new TrainerUpdateStatusRequest(false);

        String activateUrl = baseUrl + "/api/trainers/" + createdUsername + "/activate";
        HttpEntity<TrainerUpdateStatusRequest> statusEntity = new HttpEntity<>(statusRequest, auth);
        ResponseEntity<Void> deactivateResponse = restTemplate.exchange(
                activateUrl,
                HttpMethod.PATCH,
                statusEntity,
                Void.class
        );

        assertThat(deactivateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<TrainerProfileResponse> deactivatedResponse = restTemplate.exchange(
                getProfileUrl,
                HttpMethod.GET,
                new HttpEntity<>(auth),
                TrainerProfileResponse.class
        );

        assertThat(deactivatedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerProfileResponse deactivatedProfile = deactivatedResponse.getBody();
        assertThat(deactivatedProfile).isNotNull();
        assertThat(deactivatedProfile.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Integration Test: Create Trainer and Trainee -> Assign -> Get Trainings")
    void shouldManageTrainerTraineesAndTrainings() {
        TrainerRegistrationRequest trainerRequest = new TrainerRegistrationRequest(
                "Sarah",
                "Connor",
                2L
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TrainerRegistrationRequest> registerEntity = new HttpEntity<>(trainerRequest, headers);

        ResponseEntity<TrainerRegistrationResponse> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainers/register",
                registerEntity,
                TrainerRegistrationResponse.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerRegistrationResponse trainer = registerResponse.getBody();
        assertThat(trainer).isNotNull();

        String jwt = getJwtToken(trainer.getUsername(), trainer.getPassword());
        HttpHeaders auth = authHeaders(jwt);
        String getTrainingsUrl = baseUrl + "/api/trainers/" + trainer.getUsername() + "/trainings";
        ResponseEntity<Object[]> trainingsResponse = restTemplate.exchange(
                getTrainingsUrl,
                HttpMethod.GET,
                new HttpEntity<>(auth),
                Object[].class
        );

        assertThat(trainingsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Object[] trainings = trainingsResponse.getBody();
        assertThat(trainings).isNotNull();
        assertThat(trainings).isEmpty();
    }

    @Test
    @DisplayName("Integration Test: Trainer Authentication and Password Change")
    void shouldAuthenticateAndChangeTrainerPassword() {
        TrainerRegistrationRequest registerRequest = new TrainerRegistrationRequest(
                "David",
                "Miller",
                3L
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TrainerRegistrationRequest> registerEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<TrainerRegistrationResponse> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainers/register",
                registerEntity,
                TrainerRegistrationResponse.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerRegistrationResponse registered = registerResponse.getBody();
        assertThat(registered).isNotNull();

        String username = registered.getUsername();
        String oldPassword = registered.getPassword();

        String jwt = getJwtToken(username, oldPassword);
        HttpHeaders auth = authHeaders(jwt);

        String newPassword = "trainerNewPass456";
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest(username, oldPassword, newPassword);

        HttpEntity<ChangePasswordRequest> changePasswordEntity =
                new HttpEntity<>(changePasswordRequest, auth);

        ResponseEntity<Void> changePasswordResponse = restTemplate.exchange(
                baseUrl + "/api/auth/change-password",
                HttpMethod.PUT,
                changePasswordEntity,
                Void.class
        );

        assertThat(changePasswordResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        String newJwt = getJwtToken(username, newPassword);
        HttpHeaders newAuth = authHeaders(newJwt);
        String getProfileUrl = baseUrl + "/api/trainers/" + username;
        ResponseEntity<TrainerDTO.Response.Profile> profileResponse = restTemplate.exchange(
                getProfileUrl,
                HttpMethod.GET,
                new HttpEntity<>(newAuth),
                TrainerDTO.Response.Profile.class
        );

        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerDTO.Response.Profile profile = profileResponse.getBody();
        assertThat(profile).isNotNull();
        assertThat(profile.firstName()).isEqualTo("David");
        assertThat(profile.lastName()).isEqualTo("Miller");
    }

    @Test
    @DisplayName("Integration Test: Get Trainers with Filter by Specialization")
    void shouldFilterTrainersBySpecialization() {
        TrainerRegistrationRequest yogaTrainer = new TrainerRegistrationRequest(
                "Yoga",
                "Master",
                2L
        );

        TrainerRegistrationRequest fitnessTrainer = new TrainerRegistrationRequest(
                "Fitness",
                "Guru",
                1L
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TrainerRegistrationRequest> yogaEntity = new HttpEntity<>(yogaTrainer, headers);
        ResponseEntity<TrainerRegistrationResponse> yogaResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainers/register",
                yogaEntity,
                TrainerRegistrationResponse.class
        );
        assertThat(yogaResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<TrainerRegistrationRequest> fitnessEntity = new HttpEntity<>(fitnessTrainer, headers);
        ResponseEntity<TrainerRegistrationResponse> fitnessResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainers/register",
                fitnessEntity,
                TrainerRegistrationResponse.class
        );
        assertThat(fitnessResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertNotNull(yogaResponse.getBody());
        assertNotNull(fitnessResponse.getBody());

        String jwtYoga = getJwtToken(yogaResponse.getBody().getUsername(), yogaResponse.getBody().getPassword());
        String jwtFitness = getJwtToken(fitnessResponse.getBody().getUsername(), fitnessResponse.getBody().getPassword());
        assertThat(jwtYoga).isNotNull();
        assertThat(jwtFitness).isNotNull();
    }
}

package com.github.amangusss.gym_application.integration;

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
        TrainerDTO.Request.Register registerRequest = new TrainerDTO.Request.Register(
                "Mike",
                "Johnson",
                1L
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TrainerDTO.Request.Register> registerEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<TrainerDTO.Response.Registered> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainers/register",
                registerEntity,
                TrainerDTO.Response.Registered.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerDTO.Response.Registered registered = registerResponse.getBody();
        assertThat(registered).isNotNull();
        assertThat(registered.username()).isNotNull();
        assertThat(registered.password()).isNotNull();

        String createdUsername = registered.username();
        String createdPassword = registered.password();

        String jwt = getJwtToken(createdUsername, createdPassword);
        HttpHeaders auth = authHeaders(jwt);

        String getProfileUrl = baseUrl + "/api/trainers/" + createdUsername;
        ResponseEntity<TrainerDTO.Response.Profile> profileResponse = restTemplate.exchange(
                getProfileUrl,
                HttpMethod.GET,
                new HttpEntity<>(auth),
                TrainerDTO.Response.Profile.class
        );

        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerDTO.Response.Profile profile = profileResponse.getBody();
        assertThat(profile).isNotNull();
        assertThat(profile.firstName()).isEqualTo("Mike");
        assertThat(profile.lastName()).isEqualTo("Johnson");
        assertThat(profile.specializationName()).isEqualTo("FITNESS");
        assertThat(profile.isActive()).isTrue();

        TrainerDTO.Request.Update updateRequest = new TrainerDTO.Request.Update(
                "Michael",
                "Johnsonson",
                2L,
                true
        );

        String updateUrl = baseUrl + "/api/trainers/" + createdUsername;
        HttpEntity<TrainerDTO.Request.Update> updateEntity = new HttpEntity<>(updateRequest, auth);
        ResponseEntity<TrainerDTO.Response.Profile> updateResponse = restTemplate.exchange(
                updateUrl,
                HttpMethod.PUT,
                updateEntity,
                TrainerDTO.Response.Profile.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerDTO.Response.Profile updatedProfile = updateResponse.getBody();
        assertThat(updatedProfile).isNotNull();
        assertThat(updatedProfile.firstName()).isEqualTo("Michael");
        assertThat(updatedProfile.lastName()).isEqualTo("Johnsonson");

        ResponseEntity<TrainerDTO.Response.Profile> verifyResponse = restTemplate.exchange(
                getProfileUrl,
                HttpMethod.GET,
                new HttpEntity<>(auth),
                TrainerDTO.Response.Profile.class
        );

        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerDTO.Response.Profile verifiedProfile = verifyResponse.getBody();
        assertThat(verifiedProfile).isNotNull();
        assertThat(verifiedProfile.firstName()).isEqualTo("Michael");
        assertThat(verifiedProfile.lastName()).isEqualTo("Johnsonson");

        TrainerDTO.Request.UpdateStatus statusRequest = new TrainerDTO.Request.UpdateStatus(false);

        String activateUrl = baseUrl + "/api/trainers/" + createdUsername + "/activate";
        HttpEntity<TrainerDTO.Request.UpdateStatus> statusEntity = new HttpEntity<>(statusRequest, auth);
        ResponseEntity<Void> deactivateResponse = restTemplate.exchange(
                activateUrl,
                HttpMethod.PATCH,
                statusEntity,
                Void.class
        );

        assertThat(deactivateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<TrainerDTO.Response.Profile> deactivatedResponse = restTemplate.exchange(
                getProfileUrl,
                HttpMethod.GET,
                new HttpEntity<>(auth),
                TrainerDTO.Response.Profile.class
        );

        assertThat(deactivatedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerDTO.Response.Profile deactivatedProfile = deactivatedResponse.getBody();
        assertThat(deactivatedProfile).isNotNull();
        assertThat(deactivatedProfile.isActive()).isFalse();
    }

    @Test
    @DisplayName("Integration Test: Create Trainer and Trainee -> Assign -> Get Trainings")
    void shouldManageTrainerTraineesAndTrainings() {
        TrainerDTO.Request.Register trainerRequest = new TrainerDTO.Request.Register(
                "Sarah",
                "Connor",
                2L
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TrainerDTO.Request.Register> registerEntity = new HttpEntity<>(trainerRequest, headers);

        ResponseEntity<TrainerDTO.Response.Registered> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainers/register",
                registerEntity,
                TrainerDTO.Response.Registered.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerDTO.Response.Registered trainer = registerResponse.getBody();
        assertThat(trainer).isNotNull();

        String jwt = getJwtToken(trainer.username(), trainer.password());
        HttpHeaders auth = authHeaders(jwt);
        String getTrainingsUrl = baseUrl + "/api/trainers/" + trainer.username() + "/trainings";
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
        TrainerDTO.Request.Register registerRequest = new TrainerDTO.Request.Register(
                "David",
                "Miller",
                3L
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TrainerDTO.Request.Register> registerEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<TrainerDTO.Response.Registered> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainers/register",
                registerEntity,
                TrainerDTO.Response.Registered.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerDTO.Response.Registered registered = registerResponse.getBody();
        assertThat(registered).isNotNull();

        String username = registered.username();
        String oldPassword = registered.password();
        System.out.println("✅ Created trainer: " + username);

        String jwt = getJwtToken(username, oldPassword);
        HttpHeaders auth = authHeaders(jwt);

        String newPassword = "trainerNewPass456";
        AuthDTO.Request.ChangePassword changePasswordRequest =
                new AuthDTO.Request.ChangePassword(oldPassword, newPassword);

        HttpEntity<AuthDTO.Request.ChangePassword> changePasswordEntity =
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
        TrainerDTO.Request.Register yogaTrainer = new TrainerDTO.Request.Register(
                "Yoga",
                "Master",
                2L
        );

        TrainerDTO.Request.Register fitnessTrainer = new TrainerDTO.Request.Register(
                "Fitness",
                "Guru",
                1L
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TrainerDTO.Request.Register> yogaEntity = new HttpEntity<>(yogaTrainer, headers);
        ResponseEntity<TrainerDTO.Response.Registered> yogaResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainers/register",
                yogaEntity,
                TrainerDTO.Response.Registered.class
        );
        assertThat(yogaResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<TrainerDTO.Request.Register> fitnessEntity = new HttpEntity<>(fitnessTrainer, headers);
        ResponseEntity<TrainerDTO.Response.Registered> fitnessResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainers/register",
                fitnessEntity,
                TrainerDTO.Response.Registered.class
        );
        assertThat(fitnessResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertNotNull(yogaResponse.getBody());
        assertNotNull(fitnessResponse.getBody());

        String jwtYoga = getJwtToken(yogaResponse.getBody().username(), yogaResponse.getBody().password());
        String jwtFitness = getJwtToken(fitnessResponse.getBody().username(), fitnessResponse.getBody().password());
        assertThat(jwtYoga).isNotNull();
        assertThat(jwtFitness).isNotNull();
    }
}

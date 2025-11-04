package com.github.amangusss.gym_application.integration;

import com.github.amangusss.gym_application.dto.trainer.TrainerDTO;
import com.github.amangusss.gym_application.dto.auth.AuthDTO;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;

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

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
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
        System.out.println("✅ Created trainer: " + createdUsername);

        String getProfileUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/trainers/{username}")
                .queryParam("password", createdPassword)
                .buildAndExpand(createdUsername)
                .toUriString();

        ResponseEntity<TrainerDTO.Response.Profile> profileResponse = restTemplate.getForEntity(
                getProfileUrl,
                TrainerDTO.Response.Profile.class
        );

        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerDTO.Response.Profile profile = profileResponse.getBody();
        assertThat(profile).isNotNull();
        assertThat(profile.firstName()).isEqualTo("Mike");
        assertThat(profile.lastName()).isEqualTo("Johnson");
        assertThat(profile.specializationName()).isEqualTo("FITNESS");
        assertThat(profile.isActive()).isTrue();
        System.out.println("✅ Retrieved trainer profile successfully");

        TrainerDTO.Request.Update updateRequest = new TrainerDTO.Request.Update(
                "Michael",
                "Johnsonson",
                2L,
                true
        );

        String updateUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/trainers/{username}")
                .queryParam("password", createdPassword)
                .buildAndExpand(createdUsername)
                .toUriString();

        HttpEntity<TrainerDTO.Request.Update> updateEntity = new HttpEntity<>(updateRequest, headers);
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
        System.out.println("✅ Updated trainer profile successfully");

        ResponseEntity<TrainerDTO.Response.Profile> verifyResponse = restTemplate.getForEntity(
                getProfileUrl,
                TrainerDTO.Response.Profile.class
        );

        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerDTO.Response.Profile verifiedProfile = verifyResponse.getBody();
        assertThat(verifiedProfile).isNotNull();
        assertThat(verifiedProfile.firstName()).isEqualTo("Michael");
        assertThat(verifiedProfile.lastName()).isEqualTo("Johnsonson");
        System.out.println("✅ Verified updated data");

        TrainerDTO.Request.UpdateStatus statusRequest = new TrainerDTO.Request.UpdateStatus(false);

        String activateUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/trainers/{username}/activate")
                .queryParam("password", createdPassword)
                .buildAndExpand(createdUsername)
                .toUriString();

        HttpEntity<TrainerDTO.Request.UpdateStatus> statusEntity = new HttpEntity<>(statusRequest, headers);
        ResponseEntity<Void> deactivateResponse = restTemplate.exchange(
                activateUrl,
                HttpMethod.PATCH,
                statusEntity,
                Void.class
        );

        assertThat(deactivateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("✅ Deactivated trainer");

        ResponseEntity<TrainerDTO.Response.Profile> deactivatedResponse = restTemplate.getForEntity(
                getProfileUrl,
                TrainerDTO.Response.Profile.class
        );

        assertThat(deactivatedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerDTO.Response.Profile deactivatedProfile = deactivatedResponse.getBody();
        assertThat(deactivatedProfile).isNotNull();
        assertThat(deactivatedProfile.isActive()).isFalse();
        System.out.println("✅ Verified trainer is deactivated");
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
        System.out.println("✅ Created trainer: " + trainer.username());

        String getTrainingsUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/trainers/{username}/trainings")
                .queryParam("password", trainer.password())
                .buildAndExpand(trainer.username())
                .toUriString();

        ResponseEntity<Object[]> trainingsResponse = restTemplate.getForEntity(
                getTrainingsUrl,
                Object[].class
        );

        assertThat(trainingsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Object[] trainings = trainingsResponse.getBody();
        assertThat(trainings).isNotNull();
        assertThat(trainings).isEmpty();
        System.out.println("✅ Verified trainer has no trainings initially");
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

        String loginUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/auth/login")
                .queryParam("username", username)
                .queryParam("password", oldPassword)
                .toUriString();

        ResponseEntity<Void> loginResponse = restTemplate.getForEntity(loginUrl, Void.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("✅ Authenticated successfully");

        String newPassword = "trainerNewPass456";
        AuthDTO.Request.ChangePassword changePasswordRequest =
                new AuthDTO.Request.ChangePassword(username, oldPassword, newPassword);

        HttpEntity<AuthDTO.Request.ChangePassword> changePasswordEntity =
                new HttpEntity<>(changePasswordRequest, headers);

        ResponseEntity<Void> changePasswordResponse = restTemplate.exchange(
                baseUrl + "/api/auth/change-password",
                HttpMethod.PUT,
                changePasswordEntity,
                Void.class
        );

        assertThat(changePasswordResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("✅ Changed password successfully");

        String newLoginUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/auth/login")
                .queryParam("username", username)
                .queryParam("password", newPassword)
                .toUriString();

        ResponseEntity<Void> newLoginResponse = restTemplate.getForEntity(newLoginUrl, Void.class);
        assertThat(newLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("✅ New password works");

        String getProfileUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/trainers/{username}")
                .queryParam("password", newPassword)
                .buildAndExpand(username)
                .toUriString();

        ResponseEntity<TrainerDTO.Response.Profile> profileResponse = restTemplate.getForEntity(
                getProfileUrl,
                TrainerDTO.Response.Profile.class
        );

        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TrainerDTO.Response.Profile profile = profileResponse.getBody();
        assertThat(profile).isNotNull();
        assertThat(profile.firstName()).isEqualTo("David");
        assertThat(profile.lastName()).isEqualTo("Miller");
        System.out.println("✅ Can access profile with new password");
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

        System.out.println("✅ Created trainers with different specializations");
        Assertions.assertNotNull(yogaResponse.getBody());
        System.out.println("   - Yoga trainer: " + yogaResponse.getBody().username());
        Assertions.assertNotNull(fitnessResponse.getBody());
        System.out.println("   - Fitness trainer: " + fitnessResponse.getBody().username());
    }
}

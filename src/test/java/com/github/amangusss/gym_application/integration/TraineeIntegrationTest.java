package com.github.amangusss.gym_application.integration;

import com.github.amangusss.gym_application.dto.trainee.TraineeDTO;
import com.github.amangusss.gym_application.dto.auth.AuthDTO;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@WireMockTest(httpPort = 8089)
@DisplayName("Trainee Integration Tests - Full CRUD Operations with WireMock")
class TraineeIntegrationTest {

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
    @DisplayName("Integration Test: Create Trainee -> Get Profile -> Update -> Delete")
    void shouldPerformFullTraineeCrudCycle() {
        TraineeDTO.Request.Register registerRequest = new TraineeDTO.Request.Register(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main Street"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TraineeDTO.Request.Register> registerEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<TraineeDTO.Response.Registered> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainees/register",
                registerEntity,
                TraineeDTO.Response.Registered.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeDTO.Response.Registered registered = registerResponse.getBody();
        assertThat(registered).isNotNull();
        assertThat(registered.username()).isNotNull();
        assertThat(registered.password()).isNotNull();

        String createdUsername = registered.username();
        String createdPassword = registered.password();
        System.out.println("Created trainee: " + createdUsername);

        String getProfileUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/trainees/{username}")
                .queryParam("password", createdPassword)
                .buildAndExpand(createdUsername)
                .toUriString();

        ResponseEntity<TraineeDTO.Response.Profile> profileResponse = restTemplate.getForEntity(
                getProfileUrl,
                TraineeDTO.Response.Profile.class
        );

        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeDTO.Response.Profile profile = profileResponse.getBody();
        assertThat(profile).isNotNull();
        assertThat(profile.firstName()).isEqualTo("John");
        assertThat(profile.lastName()).isEqualTo("Doe");
        assertThat(profile.dateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(profile.address()).isEqualTo("123 Main Street");
        assertThat(profile.isActive()).isTrue();
        System.out.println("Retrieved trainee profile successfully");

        TraineeDTO.Request.Update updateRequest = new TraineeDTO.Request.Update(
                "Johnny",
                "Doeson",
                LocalDate.of(1990, 1, 1),
                "456 New Avenue",
                true
        );

        String updateUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/trainees/{username}")
                .queryParam("password", createdPassword)
                .buildAndExpand(createdUsername)
                .toUriString();

        HttpEntity<TraineeDTO.Request.Update> updateEntity = new HttpEntity<>(updateRequest, headers);
        ResponseEntity<TraineeDTO.Response.Profile> updateResponse = restTemplate.exchange(
                updateUrl,
                HttpMethod.PUT,
                updateEntity,
                TraineeDTO.Response.Profile.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeDTO.Response.Profile updatedProfile = updateResponse.getBody();
        assertThat(updatedProfile).isNotNull();
        assertThat(updatedProfile.firstName()).isEqualTo("Johnny");
        assertThat(updatedProfile.lastName()).isEqualTo("Doeson");
        System.out.println("Updated trainee profile successfully");

        String deleteUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/trainees/{username}")
                .queryParam("password", createdPassword)
                .buildAndExpand(createdUsername)
                .toUriString();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                deleteUrl,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("Deleted trainee successfully");

        ResponseEntity<Object> verifyResponse = restTemplate.getForEntity(getProfileUrl, Object.class);
        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        System.out.println("Verified trainee is deleted");
    }

    @Test
    @DisplayName("Integration Test: Authentication and Password Change Flow")
    void shouldAuthenticateAndChangePassword() {
        TraineeDTO.Request.Register registerRequest = new TraineeDTO.Request.Register(
                "Bob",
                "Wilson",
                LocalDate.of(1988, 3, 20),
                "321 Gym Street"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TraineeDTO.Request.Register> registerEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<TraineeDTO.Response.Registered> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainees/register",
                registerEntity,
                TraineeDTO.Response.Registered.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeDTO.Response.Registered registered = registerResponse.getBody();
        assertThat(registered).isNotNull();

        String username = registered.username();
        String oldPassword = registered.password();
        System.out.println("Created trainee: " + username);

        String loginUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/auth/login")
                .queryParam("username", username)
                .queryParam("password", oldPassword)
                .toUriString();

        ResponseEntity<Void> loginResponse = restTemplate.getForEntity(loginUrl, Void.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("Authenticated successfully");

        String newPassword = "newSecurePassword123";
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
        System.out.println("Changed password successfully");

        String newLoginUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/auth/login")
                .queryParam("username", username)
                .queryParam("password", newPassword)
                .toUriString();

        ResponseEntity<Void> newLoginResponse = restTemplate.getForEntity(newLoginUrl, Void.class);
        assertThat(newLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("New password works");

        String getProfileUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/trainees/{username}")
                .queryParam("password", newPassword)
                .buildAndExpand(username)
                .toUriString();

        ResponseEntity<TraineeDTO.Response.Profile> profileResponse = restTemplate.getForEntity(
                getProfileUrl,
                TraineeDTO.Response.Profile.class
        );

        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeDTO.Response.Profile profile = profileResponse.getBody();
        assertThat(profile).isNotNull();
        assertThat(profile.firstName()).isEqualTo("Bob");
        assertThat(profile.lastName()).isEqualTo("Wilson");
        System.out.println("Can access profile with new password");
    }

    @Test
    @DisplayName("Integration Test: Manage Trainee Trainers Relationship")
    void shouldManageTraineeTrainersRelationship() {
        TraineeDTO.Request.Register traineeRequest = new TraineeDTO.Request.Register(
                "Alice",
                "Smith",
                LocalDate.of(1995, 5, 15),
                "789 Fitness Lane"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TraineeDTO.Request.Register> registerEntity = new HttpEntity<>(traineeRequest, headers);

        ResponseEntity<TraineeDTO.Response.Registered> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/trainees/register",
                registerEntity,
                TraineeDTO.Response.Registered.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TraineeDTO.Response.Registered trainee = registerResponse.getBody();
        assertThat(trainee).isNotNull();
        System.out.println("Created trainee: " + trainee.username());

        String unassignedUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/trainees/{username}/trainers/unassigned")
                .queryParam("password", trainee.password())
                .buildAndExpand(trainee.username())
                .toUriString();

        ResponseEntity<Object[]> unassignedResponse = restTemplate.getForEntity(unassignedUrl, Object[].class);
        assertThat(unassignedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("Retrieved unassigned trainers");
    }
}

package com.github.amangusss.gym_application.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amangusss.gym_application.bdd.context.SharedTestContext;
import com.github.amangusss.gym_application.entity.CustomUser;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.repository.TrainingTypeRepository;
import com.github.amangusss.gym_application.repository.UserRepository;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class TrainerSteps {

    SharedTestContext context;
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    UserRepository userRepository;
    TrainerRepository trainerRepository;
    TrainingTypeRepository trainingTypeRepository;
    PasswordEncoder passwordEncoder;

    @Given("a trainer exists with username {string} and password {string}")
    public void aTrainerExistsWithUsernameAndPassword(String username, String password) {
        TrainingType trainingType = trainingTypeRepository.findByTypeName("FITNESS")
                .orElseGet(() -> trainingTypeRepository.save(TrainingType.builder().typeName("FITNESS").build()));

        CustomUser user = CustomUser.builder()
                .firstName(username.split("\\.")[0])
                .lastName(username.contains(".") ? username.split("\\.")[1] : "Trainer")
                .username(username)
                .password(passwordEncoder.encode(password))
                .isActive(true)
                .build();

        Trainer trainer = Trainer.builder()
                .user(user)
                .specialization(trainingType)
                .build();
        trainerRepository.save(trainer);

        log.info("Created trainer with username: {}", username);
    }

    @Given("an inactive trainer exists with username {string}")
    public void anInactiveTrainerExistsWithUsername(String username) {
        TrainingType trainingType = trainingTypeRepository.findByTypeName("FITNESS")
                .orElseGet(() -> trainingTypeRepository.save(TrainingType.builder().typeName("FITNESS").build()));

        CustomUser user = CustomUser.builder()
                .firstName(username.split("\\.")[0])
                .lastName(username.contains(".") ? username.split("\\.")[1] : "Trainer")
                .username(username)
                .password(passwordEncoder.encode("password123"))
                .isActive(false)
                .build();

        Trainer trainer = Trainer.builder()
                .user(user)
                .specialization(trainingType)
                .build();
        trainerRepository.save(trainer);

        log.info("Created inactive trainer with username: {}", username);
    }

    @When("I register a new trainer with firstName {string} and lastName {string} and specialization {string}")
    public void iRegisterANewTrainerWithFirstNameAndLastNameAndSpecialization(String firstName, String lastName, String specialization) throws Exception {
        TrainingType trainingType = trainingTypeRepository.findByTypeName(specialization)
                .orElseGet(() -> trainingTypeRepository.save(TrainingType.builder().typeName(specialization).build()));

        String requestJson = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"specializationId\":%d}",
                firstName, lastName, trainingType.getId()
        );

        context.setResultActions(mockMvc.perform(post("/api/trainers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)));
    }

    @When("I request my trainer profile")
    public void iRequestMyTrainerProfile() throws Exception {
        context.setResultActions(mockMvc.perform(get("/api/trainers/profile")
                .header("Authorization", "Bearer " + context.getJwtToken())));
    }

    @When("I update my trainer profile with firstName {string}")
    public void iUpdateMyTrainerProfileWithFirstName(String firstName) throws Exception {
        String username = context.getCurrentUsername();

        String requestJson = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"Smith\",\"specializationId\":1,\"isActive\":true}",
                firstName
        );

        context.setResultActions(mockMvc.perform(put("/api/trainers/" + username)
                .header("Authorization", "Bearer " + context.getJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)));
    }

    @When("I activate my trainer profile")
    public void iActivateMyTrainerProfile() throws Exception {
        String username = context.getCurrentUsername();

        String requestJson = "{\"isActive\":true}";

        context.setResultActions(mockMvc.perform(patch("/api/trainers/" + username + "/activate")
                .header("Authorization", "Bearer " + context.getJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)));
    }

    @When("I deactivate my trainer profile")
    public void iDeactivateMyTrainerProfile() throws Exception {
        String username = context.getCurrentUsername();

        String requestJson = "{\"isActive\":false}";

        context.setResultActions(mockMvc.perform(patch("/api/trainers/" + username + "/activate")
                .header("Authorization", "Bearer " + context.getJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)));
    }

    @When("I request my trainer trainings list")
    public void iRequestMyTrainerTrainingsList() throws Exception {
        String username = context.getCurrentUsername();

        context.setResultActions(mockMvc.perform(get("/api/trainers/" + username + "/trainings")
                .header("Authorization", "Bearer " + context.getJwtToken())));
    }
}

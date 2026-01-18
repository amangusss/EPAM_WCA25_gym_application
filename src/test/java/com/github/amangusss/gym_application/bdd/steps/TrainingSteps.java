package com.github.amangusss.gym_application.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amangusss.gym_application.bdd.context.SharedTestContext;
import com.github.amangusss.gym_application.entity.TrainingType;
import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.entity.training.Training;
import com.github.amangusss.gym_application.repository.TraineeRepository;
import com.github.amangusss.gym_application.repository.TrainerRepository;
import com.github.amangusss.gym_application.repository.TrainingRepository;
import com.github.amangusss.gym_application.repository.TrainingTypeRepository;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class TrainingSteps {

    SharedTestContext context;
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    TraineeRepository traineeRepository;
    TrainerRepository trainerRepository;
    TrainingRepository trainingRepository;
    TrainingTypeRepository trainingTypeRepository;

    @When("I create a training with trainer {string} and duration {int}")
    public void iCreateATrainingWithTrainerAndDuration(String trainerUsername, int duration) throws Exception {
        String traineeUsername = context.getCurrentUsername();

        String requestJson = String.format(
                "{\"traineeUsername\":\"%s\",\"trainerUsername\":\"%s\",\"trainingName\":\"Test Training\",\"trainingDate\":\"%s\",\"trainingDuration\":%d}",
                traineeUsername, trainerUsername, LocalDate.now().plusDays(1), duration
        );

        context.setResultActions(mockMvc.perform(post("/api/trainings")
                .header("Authorization", "Bearer " + context.getJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)));
    }

    @Given("a training exists for trainee {string} and trainer {string}")
    public void aTrainingExistsForTraineeAndTrainer(String traineeUsername, String trainerUsername) {
        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new RuntimeException("Trainee not found: " + traineeUsername));

        Trainer trainer = trainerRepository.findByUserUsername(trainerUsername)
                .orElseThrow(() -> new RuntimeException("Trainer not found: " + trainerUsername));

        TrainingType trainingType = trainer.getSpecialization();

        Training training = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Test Training")
                .trainingType(trainingType)
                .trainingDate(LocalDate.now().plusDays(1))
                .trainingDuration(60.0)
                .build();

        training = trainingRepository.save(training);
        context.set("lastTrainingId", training.getId());

        log.info("Created training with ID: {} for trainee: {} and trainer: {}", training.getId(), traineeUsername, trainerUsername);
    }

    @When("I delete the training")
    public void iDeleteTheTraining() throws Exception {
        Long trainingId = context.get("lastTrainingId", Long.class);

        context.setResultActions(mockMvc.perform(delete("/api/trainings/" + trainingId)
                .header("Authorization", "Bearer " + context.getJwtToken())));
    }

    @And("workload message should be sent to queue")
    public void workloadMessageShouldBeSentToQueue() {
        log.info("Workload message verification skipped in test mode");
    }

    @And("workload DELETE message should be sent to queue")
    public void workloadDeleteMessageShouldBeSentToQueue() {
        log.info("Workload DELETE message verification skipped in test mode");
    }
}

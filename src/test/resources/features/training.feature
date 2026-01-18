Feature: Training Management
  As a trainee
  I want to manage my training sessions
  So that I can track my workout history

  Background:
    Given the system is ready

  Scenario: Create training successfully
    Given a trainee exists with username "John.Doe" and password "password123"
    And a trainer exists with username "Mike.Smith" and password "password123"
    And I am logged in as "John.Doe" with password "password123"
    When I create a training with trainer "Mike.Smith" and duration 60
    Then the response status should be 200
    And workload message should be sent to queue

  Scenario: Create training with invalid duration
    Given a trainee exists with username "John.Doe" and password "password123"
    And a trainer exists with username "Mike.Smith" and password "password123"
    And I am logged in as "John.Doe" with password "password123"
    When I create a training with trainer "Mike.Smith" and duration -10
    Then the response status should be 400

  Scenario: Create training with non-existent trainer
    Given a trainee exists with username "John.Doe" and password "password123"
    And I am logged in as "John.Doe" with password "password123"
    When I create a training with trainer "NonExistent.Trainer" and duration 60
    Then the response status should be 404

  Scenario: Delete training successfully
    Given a trainee exists with username "John.Doe" and password "password123"
    And a trainer exists with username "Mike.Smith" and password "password123"
    And a training exists for trainee "John.Doe" and trainer "Mike.Smith"
    And I am logged in as "John.Doe" with password "password123"
    When I delete the training
    Then the response status should be 200
    And workload DELETE message should be sent to queue

  Scenario: Get trainings list with filters
    Given a trainee exists with username "John.Doe" and password "password123"
    And I am logged in as "John.Doe" with password "password123"
    When I request my trainings list
    Then the response status should be 200
    And I should receive a list of trainings

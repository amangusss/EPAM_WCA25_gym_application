Feature: Trainee Management
  As a trainee
  I want to manage my profile and trainers
  So that I can organize my training sessions

  Background:
    Given the system is ready

  Scenario: Register new trainee successfully
    When I register a new trainee with firstName "John" and lastName "Doe"
    Then the response status should be 200
    And I should receive username and password
    And username should start with "John.Doe"

  Scenario: Get trainee profile
    Given a trainee exists with username "John.Doe" and password "password123"
    And I am logged in as "John.Doe" with password "password123"
    When I request my trainee profile
    Then the response status should be 200
    And the profile should contain username "John.Doe"

  Scenario: Update trainee profile
    Given a trainee exists with username "John.Doe" and password "password123"
    And I am logged in as "John.Doe" with password "password123"
    When I update my profile with firstName "Johnny" and lastName "Doe"
    Then the response status should be 200
    And the profile should be updated

  Scenario: Delete trainee profile
    Given a trainee exists with username "John.Doe" and password "password123"
    And I am logged in as "John.Doe" with password "password123"
    When I delete my trainee profile
    Then the response status should be 200

  Scenario: Deactivate and activate trainee profile
    Given a trainee exists with username "John.Doe" and password "password123"
    And I am logged in as "John.Doe" with password "password123"
    When I deactivate my trainee profile
    Then the response status should be 200

  Scenario: Deactivate trainee profile
    Given a trainee exists with username "John.Doe" and password "password123"
    And I am logged in as "John.Doe" with password "password123"
    When I deactivate my trainee profile
    Then the response status should be 200
    And the profile should be inactive

  Scenario: Update trainers list
    Given a trainee exists with username "John.Doe" and password "password123"
    And a trainer exists with username "Trainer.Smith"
    And I am logged in as "John.Doe" with password "password123"
    When I update my trainers list with "Trainer.Smith"
    Then the response status should be 200
    And my trainers list should contain "Trainer.Smith"

  Scenario: Cannot access another trainee profile
    Given a trainee exists with username "John.Doe" and password "password123"
    And a trainee exists with username "Jane.Smith" and password "password123"
    And I am logged in as "John.Doe" with password "password123"
    When I request profile for username "Jane.Smith"
    Then the response status should be 403

Feature: Trainer Management
  As a trainer
  I want to manage my profile and view my trainings
  So that I can track my coaching activities

  Background:
    Given the system is ready

  Scenario: Register new trainer successfully
    When I register a new trainer with firstName "Mike" and lastName "Smith" and specialization "FITNESS"
    Then the response status should be 200
    And I should receive username and password
    And username should start with "Mike.Smith"

  Scenario: Get trainer profile
    Given a trainer exists with username "Mike.Smith" and password "password123"
    And I am logged in as "Mike.Smith" with password "password123"
    When I request my trainer profile
    Then the response status should be 200
    And the profile should contain username "Mike.Smith"

  Scenario: Update trainer profile
    Given a trainer exists with username "Mike.Smith" and password "password123"
    And I am logged in as "Mike.Smith" with password "password123"
    When I update my trainer profile with firstName "Michael"
    Then the response status should be 200

  Scenario: Deactivate and activate trainer profile
    Given a trainer exists with username "Mike.Smith" and password "password123"
    And I am logged in as "Mike.Smith" with password "password123"
    When I deactivate my trainer profile
    Then the response status should be 200

  Scenario: Get trainer trainings list
    Given a trainer exists with username "Mike.Smith" and password "password123"
    And I am logged in as "Mike.Smith" with password "password123"
    When I request my trainer trainings list
    Then the response status should be 200
    And I should receive a list of trainings

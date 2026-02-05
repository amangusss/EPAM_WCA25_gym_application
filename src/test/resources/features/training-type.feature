Feature: Training Type Management
  As a user
  I want to view available training types
  So that I can choose appropriate training specialization

  Background:
    Given the system is ready

  Scenario: Get all training types
    Given a trainee exists with username "John.Doe" and password "password123"
    And I am logged in as "John.Doe" with password "password123"
    When I request all training types
    Then the response status should be 200
    And I should receive a list of training types
    And the list should contain "FITNESS"
    And the list should contain "YOGA"

  Scenario: Training types are cached
    Given a trainee exists with username "John.Doe" and password "password123"
    And I am logged in as "John.Doe" with password "password123"
    When I request all training types
    And I request all training types again
    Then both requests should return the same data
    And cache should be used

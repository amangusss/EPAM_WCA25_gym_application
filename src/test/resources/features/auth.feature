Feature: Authentication
  As a user
  I want to authenticate and manage my credentials
  So that I can access the system securely

  Background:
    Given the system is ready

  Scenario: Successful login with valid credentials
    Given a trainee exists with username "John.Doe" and password "password123"
    When I login with username "John.Doe" and password "password123"
    Then the response status should be 200
    And I should receive a JWT token

  Scenario: Failed login with invalid password
    Given a trainee exists with username "John.Doe" and password "password123"
    When I login with username "John.Doe" and password "wrongpassword"
    Then the response status should be 401
    And I should not receive a JWT token

  Scenario: Failed login with non-existent username
    When I login with username "NonExistent.User" and password "password123"
    Then the response status should be 401

  Scenario: Change password successfully
    Given a trainee exists with username "John.Doe" and password "oldPassword"
    And I am logged in as "John.Doe" with password "oldPassword"
    When I change password from "oldPassword" to "newPassword123"
    Then the response status should be 200

  Scenario: Change password with wrong old password
    Given a trainee exists with username "John.Doe" and password "password123"
    And I am logged in as "John.Doe" with password "password123"
    When I change password from "wrongOld" to "newPassword123"
    Then the response status should be 401

  Scenario: Brute force protection after multiple failed attempts
    Given a trainee exists with username "John.Doe" and password "password123"
    When I login with username "John.Doe" and password "wrong" 3 times
    Then the user "John.Doe" should be blocked
    And login attempt with correct password should fail

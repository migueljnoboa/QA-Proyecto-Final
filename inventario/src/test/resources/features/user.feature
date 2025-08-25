Feature: User API

  Scenario: Create a new user
    Given a user payload with username "qa_user", password "s3cret!", email "qa@example.com"
    When I create the user
    Then the response user should have username "qa_user"

  Scenario: Get all users with pagination
    Given a user payload with username "page_user1", password "pass", email "p1@example.com"
    And I create the user
    And a user payload with username "page_user2", password "pass", email "p2@example.com"
    And I create the user
    When I list users page 0 size 2
    Then the users list should have page 0 and pageSize 2
    And the users list should contain at least 2 elements

  Scenario: Get a user by ID
    Given a user payload with username "lookup_id", password "pass", email "lookup_id@example.com"
    And I create the user
    When I get the user by the last created ID
    Then the response user should have username "lookup_id"

  Scenario: Get a user by username
    Given a user payload with username "lookup_username", password "pass", email "lookup_username@example.com"
    And I create the user
    When I get the user by username "lookup_username"
    Then the response user should have username "lookup_username"

  Scenario: Update a user
    Given a user payload with username "update_me", password "oldpass", email "update@example.com"
    And I create the user
    When I update the user to username "updated_user" password "newpass" email "updated@example.com"
    Then the response user should have username "updated_user"

  Scenario: Delete a user (soft delete) and then enable again
    Given a user payload with username "delete_me", password "pass", email "delete@example.com"
    And I create the user
    When I delete the user by the last created ID
    Then the user should be disabled
    When I enable the user by the last created ID
    Then the user should be enabled

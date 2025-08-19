Feature: Role API

  Background:
    Given default permits exist

  Scenario: Create a new role
    Given a role payload with name "QA_ROLE", description "Role for QA", permits "DASHBOARD_MENU;PRODUCTS_MENU;PRODUCT_VIEW"
    When I create the role
    Then the response role should have name "QA_ROLE"

  Scenario: Get all roles with pagination
    Given a role payload with name "Paged Role 1", description "R1", permits "DASHBOARD_MENU;PRODUCTS_MENU"
    And I create the role
    And a role payload with name "Paged Role 2", description "R2", permits "DASHBOARD_MENU;SUPPLIERS_MENU"
    And I create the role
    When I list roles page 0 size 2
    Then the roles list should have page 0 and pageSize 2
    And the roles list should contain at least 2 elements

  Scenario: Get a role by ID
    Given a role payload with name "Lookup Role", description "Lookup", permits "DASHBOARD_MENU;PRODUCTS_MENU"
    And I create the role
    When I get the role by the last created ID
    Then the response role should have name "Lookup Role"

  Scenario: Update a role
    Given a role payload with name "Update Me Role", description "Initial", permits "DASHBOARD_MENU;PRODUCTS_MENU"
    And I create the role
    When I update the role name to "Updated Role"
    Then the response role should have name "Updated Role"

  Scenario: Delete a role (soft delete)
    Given a role payload with name "Delete Me Role", description "To delete", permits "DASHBOARD_MENU;PRODUCTS_MENU"
    And I create the role
    When I delete the role by the last created ID
    Then the role should be marked as disabled

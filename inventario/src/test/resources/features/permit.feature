Feature: Permit API

  Scenario: List permits after seeding defaults
    Given default permits exist
    When I list permits
    Then the permits list should contain at least 5 elements
    And the permits should include "DASHBOARD_MENU"
    And the permits should include "SUPPLIERS_MENU"
    And the permits should include "PRODUCTS_MENU"
    And the permits should include "USERS_MENU"
    And the permits should include "ROLES_MENU"

  Scenario: Each returned permit has an id and a name
    Given default permits exist
    When I list permits
    Then each permit should have a non-null id and a non-empty name

  Scenario: List permits without authentication should fail
    When I list permits without authentication
    Then the response status should be 401
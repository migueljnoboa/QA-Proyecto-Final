Feature: Supplier API

  Scenario: Create a new supplier
    Given a supplier payload with name "Supplier A", contact info "Contact A", address "Address A", email "a@example.com", and phone "123456789"
    When I create the supplier
    Then the response supplier should have name "Supplier A"

  Scenario: Get all suppliers with pagination
    Given a supplier payload with name "Paged One", contact info "C1", address "Addr1", email "p1@example.com", and phone "111111"
    And I create the supplier
    And a supplier payload with name "Paged Two", contact info "C2", address "Addr2", email "p2@example.com", and phone "222222"
    And I create the supplier
    When I list suppliers page 0 size 2
    Then the suppliers list should have page 0 and pageSize 2
    And the suppliers list should contain at least 2 elements

  Scenario: Get a supplier by ID
    Given a supplier payload with name "Lookup Me", contact info "Contact L", address "Addr L", email "lookup@example.com", and phone "555555"
    And I create the supplier
    When I get the supplier by the last created ID
    Then the response supplier should have name "Lookup Me"

  Scenario: Update a supplier
    Given a supplier payload with name "Update Me", contact info "Contact U", address "Addr U", email "update@example.com", and phone "777777"
    And I create the supplier
    When I update the supplier name to "Updated Supplier"
    Then the response supplier should have name "Updated Supplier"

  Scenario: Delete a supplier (soft delete)
    Given a supplier payload with name "Delete Me", contact info "Contact D", address "Addr D", email "delete@example.com", and phone "888888"
    And I create the supplier
    When I delete the supplier by the last created ID
    Then the supplier should be marked as disabled

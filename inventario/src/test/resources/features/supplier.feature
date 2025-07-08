Feature: Supplier Management

  Scenario: Create a new supplier
    Given a supplier with name "Supplier A", contact info "Contact A", address "Address A", email "a@example.com", and phone "123456789"
    When the supplier is created
    Then the supplier should be saved and returned with name "Supplier A"

  Scenario: Get all suppliers
    Given suppliers exist in the database
    When I request all suppliers
    Then I should receive a list of suppliers

  Scenario: Get a supplier by ID
    Given a supplier with ID 1 exists
    When I request the supplier by ID 1
    Then I should get the supplier with ID 1

  Scenario: Delete a supplier
    Given a supplier with ID 2 exists
    When I delete the supplier with ID 2
    Then the supplier should be marked as disabled

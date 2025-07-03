Feature: Supplier management

  Scenario: Create a new supplier
    Given a new supplier with name "Supplier A", contactInfo "John Doe", address "123 St", email "a@example.com", phoneNumber "1234567890"
    When I send a create supplier request
    Then the response supplier should have name "Supplier A"
    And the supplier ID should not be null

  Scenario: Get supplier by ID
    Given a supplier exists with name "Supplier B", contactInfo "Jane Doe", address "456 Ave", email "b@example.com", phoneNumber "0987654321"
    When I get the supplier by ID
    Then the response supplier should have name "Supplier B"

  Scenario: Delete a supplier
    Given a supplier exists with name "Supplier C", contactInfo "Jim Beam", address "789 Blvd", email "c@example.com", phoneNumber "1122334455"
    When I delete the supplier by ID
    Then getting the supplier by ID should return an error "Supplier not found"

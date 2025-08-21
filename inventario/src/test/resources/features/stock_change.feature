Feature: StockChangeController - getById and findAll via Product stock updates
  As a client of the API
  I want stock changes to be generated when a product's stock is updated
  So I can retrieve them through the StockChangeController endpoints

  Scenario: Update stock (increase) and retrieve the change by ID
    Given a test supplier exists
    And a product named "QA-INC" priced 10.00 in category "MISC" with initial stock 10 using that supplier
    When I service-update the last product stock to 16
    And I capture the latest stock change id for the last product
    And I GET the stock change by that id
    Then the response stock change should have increased true and amount 6
    And the response stock change should reference the last product

  Scenario: Update stock (decrease) and verify it appears in the list
    Given a test supplier exists
    And a product named "QA-DEC" priced 5.00 in category "MISC" with initial stock 20 using that supplier
    When I service-update the last product stock to 14
    And I list stock changes page 0 size 50
    Then the list should contain a change for the last product with increased false and amount 6

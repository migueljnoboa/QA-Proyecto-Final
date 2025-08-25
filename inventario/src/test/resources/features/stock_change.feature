Feature: StockChangeController - getById, getAll, and search via Product stock updates
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

  # ---------------------- SEARCH SCENARIOS ----------------------

  Scenario: Search by productId + increased + amount range
    Given a test supplier exists
    And a product named "QA-SRCH-1" priced 12.00 in category "MISC" with initial stock 10 using that supplier
    When I service-update the last product stock to 11
    And I service-update the last product stock to 15
    And I service-update the last product stock to 25
    And I SEARCH stock changes page 0 size 50 with increased true and minAmount 3 and maxAmount 6 for the last product
    Then the search result should contain exactly 1 changes for the last product
    And the search result should include amounts "4"

  Scenario: Search by date window isolates rows created in this test
    Given a test supplier exists
    And a product named "QA-SRCH-DATE" priced 9.00 in category "MISC" with initial stock 30 using that supplier
    When I start a time window
    And I service-update the last product stock to 33
    And I end the time window
    And I SEARCH stock changes page 0 size 50 for the last product within the captured window
    Then the search result should contain at least 1 changes for the last product
    And the search result should include amounts "3"

  Scenario: Search with swapped amount bounds (max < min) still works
    Given a test supplier exists
    And a product named "QA-SRCH-SWAP" priced 7.50 in category "MISC" with initial stock 50 using that supplier
    When I service-update the last product stock to 51
    And I service-update the last product stock to 55
    And I service-update the last product stock to 65
    And I start a time window
    And I end the time window
    And I SEARCH stock changes page 0 size 50 with increased true and minAmount 10 and maxAmount 1 for the last product within the captured window
    Then the search result should contain exactly 3 changes for the last product
    And the search result should include amounts "1,4,10"

  Scenario: Search with only max bound
    Given a test supplier exists
    And a product named "QA-SRCH-MAX" priced 8.00 in category "MISC" with initial stock 40 using that supplier
    When I service-update the last product stock to 41
    And I service-update the last product stock to 44
    And I service-update the last product stock to 50
    And I start a time window
    And I end the time window
    And I SEARCH stock changes page 0 size 50 with increased true and minAmount null and maxAmount 5 for the last product within the captured window
    Then the search result should contain exactly 2 changes for the last product
    And the search result should include amounts "1,3"

  Scenario: Search with only min bound
    Given a test supplier exists
    And a product named "QA-SRCH-MIN" priced 8.00 in category "MISC" with initial stock 40 using that supplier
    When I service-update the last product stock to 45
    And I service-update the last product stock to 55
    And I start a time window
    And I end the time window
    And I SEARCH stock changes page 0 size 50 with increased true and minAmount 5 and maxAmount null for the last product within the captured window
    Then the search result should contain exactly 2 changes for the last product
    And the search result should include amounts "5,10"

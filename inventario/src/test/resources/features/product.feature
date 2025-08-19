Feature: Product API

  Scenario: Create a product
    Given a supplier exists with id 1
    When I create a product with name "Product A", description "Desc A", category "ELECTRONICS", price 100.00, stock 10, minStock 2, supplier Id 1
    Then the response product should have name "Product A"

  Scenario: Get product by ID
    Given a product exists with name "Product B", description "Desc B", category "ELECTRONICS", price 200.00, stock 30, minStock 3, supplier Id 1
    When I get the product by ID
    Then the response product should have name "Product B"

  Scenario: Update a product
    Given a product exists with name "Update Me", description "Desc B", category "ELECTRONICS", price 200.00, stock 30, minStock 3, supplier Id 1
    When I update the product name to "Updated Product"
    Then the response product should have name "Updated Product"

  Scenario: Delete a product (soft delete)
    Given a product exists with name "Delete Me", description "Desc B", category "ELECTRONICS", price 200.00, stock 30, minStock 3, supplier Id 1
    When I delete the product
    Then the product should be marked as disabled

  Scenario: List products with pagination
    Given a product exists with name "Paged One", description "D1", category "ELECTRONICS", price 50.00, stock 5, minStock 1, supplier Id 1
    And a product exists with name "Paged Two", description "D2", category "ELECTRONICS", price 60.00, stock 6, minStock 1, supplier Id 1
    When I list products page 0 size 2
    Then the list response should contain at least 2 elements
    And the list page should be 0 and pageSize should be 2
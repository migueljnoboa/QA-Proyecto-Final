Feature: Product management

  Scenario: Get product by ID
    Given a product exists with name "Product B", description "Desc B", category "ELECTRONICS", price 200.00, stock 30, minStock 3, supplier Id 1
    When I get the product by ID
    Then the response product should have name "Product B"

  Scenario: Update a product
    Given a product exists with name "Update Me", description "Desc B", category "ELECTRONICS", price 200.00, stock 30, minStock 3, supplier Id 1
    When I update the product name to "Updated Product"
    Then the response product should have name "Updated Product"

  Scenario: Delete a product
    Given a product exists with name "Delete Me", description "Desc B", category "ELECTRONICS", price 200.00, stock 30, minStock 3, supplier Id 1
    When I delete the product
    Then the product should be marked as disabled
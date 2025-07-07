Feature: Product management

  Scenario: Get product by ID
    Given a product exists with name "Product B", description "Desc B", category "ELECTRONICS", price 200.00, stock 30, minStock 3, supplier Id 1
    When I get the product by ID
    Then the response product should have name "Product B"
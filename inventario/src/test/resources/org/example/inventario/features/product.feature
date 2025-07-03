Feature: Product management

  Scenario: Create a new product
    Given a new product with name "Product A", description "Desc A", price 100.00, stock 50, minStock 5, supplier id 1
    When I send a create product request
    Then the response product should have name "Product A"
    And the product ID should not be null

  Scenario: Get product by ID
    Given a product exists with name "Product B", description "Desc B", price 200.00, stock 30, minStock 3, supplier id 1
    When I get the product by ID
    Then the response product should have name "Product B"

  Scenario: Delete a product
    Given a product exists with name "Product C", description "Desc C", price 300.00, stock 20, minStock 2, supplier id 1
    When I delete the product by ID
    Then getting the product by ID should return an error "Product not found"

-- Create test supplier

INSERT INTO supplier (created_at, enabled, update_at, version, address, contact_info, email, name, phone_number)
VALUES (NOW(), b'1', NOW(), 1, '123 Elm St', 'John Doe', 'supplier1@example.com', 'Tech Supply Co.', '555-1234');

INSERT INTO supplier (created_at, enabled, update_at, version, address, contact_info, email, name, phone_number)
VALUES (NOW(), b'1', NOW(), 1, '456 Oak Ave', 'Jane Smith', 'supplier2@example.com', 'Global Parts Ltd.', '555-5678');

INSERT INTO supplier (created_at, enabled, update_at, version, address, contact_info, email, name, phone_number)
VALUES (NOW(), b'1', NOW(), 1, '789 Pine Rd', 'Michael Chan', 'supplier3@example.com', 'Mega Components Inc.', '555-9101');

INSERT INTO supplier (created_at, enabled, update_at, version, address, contact_info, email, name, phone_number)
VALUES (NOW(), b'1', NOW(), 1, '321 Maple Blvd', 'Ana Rivera', 'supplier4@example.com', 'Industrial Suppliers SA', '555-3456');

INSERT INTO supplier (created_at, enabled, update_at, version, address, contact_info, email, name, phone_number)
VALUES (NOW(), b'1', NOW(), 1, '654 Birch Ln', 'Luis Gomez', 'supplier5@example.com', 'Precision Supply LLC', '555-7890');


-- Create test Products

INSERT INTO product (created_at, enabled, update_at, version, category, description, image, min_stock, name, price, stock, supplier_id)
VALUES (NOW(), b'1', NOW(), 1, 'ELECTRONICS', 'High-performance graphics card', NULL, 10, 'NVIDIA RTX 4080', 1199.99, 25, 1);

INSERT INTO product (created_at, enabled, update_at, version, category, description, image, min_stock, name, price, stock, supplier_id)
VALUES (NOW(), b'1', NOW(), 1, 'FURNITURE', 'Heavy-duty industrial drill', NULL, 5, 'Bosch PowerDrill X200', 349.50, 12, 2);

INSERT INTO product (created_at, enabled, update_at, version, category, description, image, min_stock, name, price, stock, supplier_id)
VALUES (NOW(), b'1', NOW(), 1, 'CLOTHING', 'Ergonomic wireless keyboard', NULL, 15, 'Logitech Ergo K860', 99.95, 40, 3);

INSERT INTO product (created_at, enabled, update_at, version, category, description, image, min_stock, name, price, stock, supplier_id)
VALUES (NOW(), b'1', NOW(), 1, 'FOOD', '500GB NVMe SSD drive', NULL, 20, 'Samsung 980 PRO', 129.99, 60, 4);

INSERT INTO product (created_at, enabled, update_at, version, category, description, image, min_stock, name, price, stock, supplier_id)
VALUES (NOW(), b'1', NOW(), 1, 'BOOKS', 'Wi-Fi 6 router with mesh support', NULL, 8, 'TP-Link AX3000', 89.00, 18, 5);

-- USER
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       created_at DATETIME,
                       updated_at DATETIME,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255)
);

-- ROLE
CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       created_at DATETIME,
                       updated_at DATETIME,
                       name VARCHAR(255),
                       description VARCHAR(255)
);

-- Table: permits
CREATE TABLE permits (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         created_at DATETIME,
                         updated_at DATETIME,
                         name VARCHAR(255) UNIQUE
);

-- Table: user_roles (join table)
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            roles_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, roles_id),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (roles_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Table: role_permits (join table)
CREATE TABLE role_permits (
                              role_id BIGINT NOT NULL,
                              permits_id BIGINT NOT NULL,
                              PRIMARY KEY (role_id, permits_id),
                              FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
                              FOREIGN KEY (permits_id) REFERENCES permits(id) ON DELETE CASCADE
);

-- Table: supplier
CREATE TABLE supplier (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          created_at DATETIME,
                          updated_at DATETIME,
                          name VARCHAR(255),
                          contact_info VARCHAR(255),
                          address VARCHAR(255),
                          email VARCHAR(255),
                          phone_number VARCHAR(255)
);

-- Table: product
CREATE TABLE product (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         created_at DATETIME,
                         updated_at DATETIME,
                         name VARCHAR(255),
                         description TEXT,
                         category VARCHAR(255),
                         price DECIMAL(19,2),
                         stock INT,
                         min_stock INT,
                         image TEXT,
                         supplier_id BIGINT,
                         FOREIGN KEY (supplier_id) REFERENCES supplier(id)
);

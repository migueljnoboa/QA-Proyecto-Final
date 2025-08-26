-- revinfo definition

CREATE TABLE `revinfo`
(
    `rev`      int NOT NULL AUTO_INCREMENT,
    `revtstmp` bigint DEFAULT NULL,
    PRIMARY KEY (`rev`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 25
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- permits definition

CREATE TABLE `permits`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `created_by`         varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `created_date`       datetime(6)                             DEFAULT NULL,
    `enabled`            bit(1) NOT NULL,
    `last_modified_by`   varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `last_modified_date` datetime(6)                             DEFAULT NULL,
    `version`            bigint                                  DEFAULT NULL,
    `name`               varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKjkqq0lo5gti77y8qrrsp0ak3q` (`name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 20
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- permits_aud definition

CREATE TABLE `permits_aud`
(
    `id`        bigint NOT NULL,
    `rev`       int    NOT NULL,
    `revtype`   tinyint                                 DEFAULT NULL,
    `name`      varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name_mod`  bit(1)                                  DEFAULT NULL,
    `roles_mod` bit(1)                                  DEFAULT NULL,
    PRIMARY KEY (`rev`, `id`),
    CONSTRAINT `FK2xvkghwbbiaomofyio374l3tm` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- roles definition

CREATE TABLE `roles`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `created_by`         varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `created_date`       datetime(6)                             DEFAULT NULL,
    `enabled`            bit(1) NOT NULL,
    `last_modified_by`   varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `last_modified_date` datetime(6)                             DEFAULT NULL,
    `version`            bigint                                  DEFAULT NULL,
    `description`        varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name`               varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- roles_aud definition

CREATE TABLE `roles_aud`
(
    `id`              bigint NOT NULL,
    `rev`             int    NOT NULL,
    `revtype`         tinyint                                 DEFAULT NULL,
    `description`     varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `description_mod` bit(1)                                  DEFAULT NULL,
    `name`            varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name_mod`        bit(1)                                  DEFAULT NULL,
    `permits_mod`     bit(1)                                  DEFAULT NULL,
    `users_mod`       bit(1)                                  DEFAULT NULL,
    PRIMARY KEY (`rev`, `id`),
    CONSTRAINT `FKt0mnl3rej2p0h9gxnbalf2kdd` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- roles_permits definition

CREATE TABLE `roles_permits`
(
    `roles_id`   bigint NOT NULL,
    `permits_id` bigint NOT NULL,
    KEY          `FKbr5xd3omh6bkhyxcn2j6wbcdc` (`permits_id`),
    KEY          `FK362qj8xl57wes9pocllv1q7v8` (`roles_id`),
    CONSTRAINT `FK362qj8xl57wes9pocllv1q7v8` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`),
    CONSTRAINT `FKbr5xd3omh6bkhyxcn2j6wbcdc` FOREIGN KEY (`permits_id`) REFERENCES `permits` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- roles_permits_aud definition

CREATE TABLE `roles_permits_aud`
(
    `rev`        int    NOT NULL,
    `roles_id`   bigint NOT NULL,
    `permits_id` bigint NOT NULL,
    `revtype`    tinyint DEFAULT NULL,
    PRIMARY KEY (`rev`, `roles_id`, `permits_id`),
    CONSTRAINT `FK22tlmfhh3cpqlyta3yr5js4yq` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- users definition

CREATE TABLE `users`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `created_by`         varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `created_date`       datetime(6)                             DEFAULT NULL,
    `enabled`            bit(1) NOT NULL,
    `last_modified_by`   varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `last_modified_date` datetime(6)                             DEFAULT NULL,
    `version`            bigint                                  DEFAULT NULL,
    `email`              varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `password`           varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `username`           varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY                  `idx_user_username` (`username`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- users_aud definition

CREATE TABLE `users_aud`
(
    `id`           bigint NOT NULL,
    `rev`          int    NOT NULL,
    `revtype`      tinyint                                 DEFAULT NULL,
    `email`        varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `email_mod`    bit(1)                                  DEFAULT NULL,
    `password`     varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `password_mod` bit(1)                                  DEFAULT NULL,
    `username`     varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `username_mod` bit(1)                                  DEFAULT NULL,
    `roles_mod`    bit(1)                                  DEFAULT NULL,
    PRIMARY KEY (`rev`, `id`),
    CONSTRAINT `FKc4vk4tui2la36415jpgm9leoq` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- users_roles definition

CREATE TABLE `users_roles`
(
    `users_id` bigint NOT NULL,
    `roles_id` bigint NOT NULL,
    KEY        `FKa62j07k5mhgifpp955h37ponj` (`roles_id`),
    KEY        `FKml90kef4w2jy7oxyqv742tsfc` (`users_id`),
    CONSTRAINT `FKa62j07k5mhgifpp955h37ponj` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`),
    CONSTRAINT `FKml90kef4w2jy7oxyqv742tsfc` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- users_roles_aud definition

CREATE TABLE `users_roles_aud`
(
    `rev`      int    NOT NULL,
    `users_id` bigint NOT NULL,
    `roles_id` bigint NOT NULL,
    `revtype`  tinyint DEFAULT NULL,
    PRIMARY KEY (`rev`, `users_id`, `roles_id`),
    CONSTRAINT `FKktxqr55ntd0j2i228uj8sq6j9` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- supplier definition

CREATE TABLE `supplier`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `created_by`         varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `created_date`       datetime(6)                             DEFAULT NULL,
    `enabled`            bit(1) NOT NULL,
    `last_modified_by`   varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `last_modified_date` datetime(6)                             DEFAULT NULL,
    `version`            bigint                                  DEFAULT NULL,
    `address`            varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `contact_info`       varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `email`              varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name`               varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `phone_number`       varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- supplier_aud definition

CREATE TABLE `supplier_aud`
(
    `id`               bigint NOT NULL,
    `rev`              int    NOT NULL,
    `revtype`          tinyint                                 DEFAULT NULL,
    `address`          varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `address_mod`      bit(1)                                  DEFAULT NULL,
    `contact_info`     varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `contact_info_mod` bit(1)                                  DEFAULT NULL,
    `email`            varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `email_mod`        bit(1)                                  DEFAULT NULL,
    `name`             varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name_mod`         bit(1)                                  DEFAULT NULL,
    `phone_number`     varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `phone_number_mod` bit(1)                                  DEFAULT NULL,
    PRIMARY KEY (`rev`, `id`),
    CONSTRAINT `FKd8mhbb2j0c9woft7uaik3opek` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- product definition

CREATE TABLE `product`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `created_by`         varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `created_date`       datetime(6)                                                                                                                                                                                                                                       DEFAULT NULL,
    `enabled`            bit(1) NOT NULL,
    `last_modified_by`   varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `last_modified_date` datetime(6)                                                                                                                                                                                                                                       DEFAULT NULL,
    `version`            bigint                                  DEFAULT NULL,
    `category`           enum ('AUTOMOTIVE','BEAUTY_PRODUCTS','BOOKS','CLOTHING','ELECTRONICS','FOOD','FURNITURE','GARDENING_SUPPLIES','HEALTHCARE','JEWELRY','MUSICAL_INSTRUMENTS','OFFICE_SUPPLIES','PET_SUPPLIES','SPORTS_EQUIPMENT','TOYS') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `description`        longtext COLLATE utf8mb4_unicode_ci,
    `image`              longtext COLLATE utf8mb4_unicode_ci,
    `min_stock`          int                                     DEFAULT NULL,
    `name`               varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `price`              decimal(38, 2)                          DEFAULT NULL,
    `stock`              int                                     DEFAULT NULL,
    `supplier_id`        bigint                                  DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY                  `FK2kxvbr72tmtscjvyp9yqb12by` (`supplier_id`),
    CONSTRAINT `FK2kxvbr72tmtscjvyp9yqb12by` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- product_aud definition

CREATE TABLE `product_aud`
(
    `id`              bigint NOT NULL,
    `rev`             int    NOT NULL,
    `revtype`         tinyint                                 DEFAULT NULL,
    `category`        enum ('AUTOMOTIVE','BEAUTY_PRODUCTS','BOOKS','CLOTHING','ELECTRONICS','FOOD','FURNITURE','GARDENING_SUPPLIES','HEALTHCARE','JEWELRY','MUSICAL_INSTRUMENTS','OFFICE_SUPPLIES','PET_SUPPLIES','SPORTS_EQUIPMENT','TOYS') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `category_mod`    bit(1)                                  DEFAULT NULL,
    `description`     longtext COLLATE utf8mb4_unicode_ci     DEFAULT NULL,
    `description_mod` bit(1)                                  DEFAULT NULL,
    `image`           longtext COLLATE utf8mb4_unicode_ci     DEFAULT NULL,
    `image_mod`       bit(1)                                  DEFAULT NULL,
    `min_stock`       int                                     DEFAULT NULL,
    `min_stock_mod`   bit(1)                                  DEFAULT NULL,
    `name`            varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name_mod`        bit(1)                                  DEFAULT NULL,
    `price`           decimal(38, 2)                          DEFAULT NULL,
    `price_mod`       bit(1)                                  DEFAULT NULL,
    `stock`           int                                     DEFAULT NULL,
    `stock_mod`       bit(1)                                  DEFAULT NULL,
    `supplier_id`     bigint                                  DEFAULT NULL,
    `supplier_mod`    bit(1)                                  DEFAULT NULL,
    PRIMARY KEY (`rev`, `id`),
    CONSTRAINT `FK9vwllld6jlw5xys1ay911oh1x` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;



CREATE TABLE `product_stock_change`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `amount`     int    NOT NULL,
    `created_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `date`       datetime(6) DEFAULT NULL,
    `increased`  bit(1) NOT NULL,
    `product_id` bigint                                  DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY          `FKfr7yw1f3sheemv3864v70ygjx` (`product_id`),
    CONSTRAINT `FKfr7yw1f3sheemv3864v70ygjx` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# INSERT PERMITS

INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(1, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'DASHBOARD_MENU');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(2, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'SUPPLIERS_MENU');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(3, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'SUPPLIER_CREATE');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(4, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'SUPPLIER_EDIT');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(5, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'SUPPLIER_DELETE');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(6, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'SUPPLIER_VIEW');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(7, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'PRODUCTS_MENU');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(8, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'PRODUCT_CREATE');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(9, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'PRODUCT_EDIT');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(10, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'PRODUCT_DELETE');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(11, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'PRODUCT_VIEW');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(12, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'PRODUCT_STOCK_CHANGE');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(13, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'USERS_MENU');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(14, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'USER_CREATE');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(15, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'USER_EDIT');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(16, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'USER_DELETE');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(17, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'USER_VIEW');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(18, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'ROLES_MENU');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(19, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'ROLE_CREATE');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(20, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'ROLE_EDIT');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(21, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'ROLE_DELETE');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(22, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'ROLE_VIEW');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(23, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'PERMIT_MENU');
INSERT INTO permits
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, name)
VALUES(24, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'PERMIT_VIEW');



# INSERT ROLES

INSERT INTO roles
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, description, name)
VALUES(1, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'Administrator role with all permissions', 'ADMIN');
INSERT INTO roles
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, description, name)
VALUES(2, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'Employee, access to Create, Edit and View Products and view Stock Change History', 'EMPLOYEE');
INSERT INTO roles
(id, created_by, created_date, enabled, last_modified_by, last_modified_date, version, description, name)
VALUES(3, 'system', '2025-08-25 00:00:00.000000', 1, 'system', '2025-08-25 00:00:00.000000', 0, 'Invited / User, can View Product Information', 'USER');



# INSERT ROLES PERMIT

INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 1);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 3);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 12);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 21);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 6);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 18);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 19);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 2);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 5);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 7);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 11);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 16);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 4);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 24);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 17);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 15);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 14);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 8);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 23);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 9);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 13);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 20);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 22);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(1, 10);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(2, 7);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(2, 11);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(2, 1);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(2, 3);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(2, 12);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(2, 4);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(2, 6);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(2, 8);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(2, 9);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(2, 2);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(3, 7);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(3, 11);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(3, 1);
INSERT INTO roles_permits
(roles_id, permits_id)
VALUES(3, 6);

# INSERT BASIC USERS
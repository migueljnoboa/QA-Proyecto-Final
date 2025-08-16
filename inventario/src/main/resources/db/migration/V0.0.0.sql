-- inventariodb.revinfo definition
CREATE TABLE `revinfo`
(
    `rev`      int NOT NULL AUTO_INCREMENT,
    `revtstmp` bigint DEFAULT NULL,
    PRIMARY KEY (`rev`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 25
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- inventariodb.permits definition
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

-- inventariodb.permits_aud definition
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


-- inventariodb.roles definition

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

-- inventariodb.roles_aud definition

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


-- inventariodb.roles_permits definition

CREATE TABLE `roles_permits`
(
    `roles_id`   bigint NOT NULL,
    `permits_id` bigint NOT NULL,
    KEY `FKbr5xd3omh6bkhyxcn2j6wbcdc` (`permits_id`),
    KEY `FK362qj8xl57wes9pocllv1q7v8` (`roles_id`),
    CONSTRAINT `FK362qj8xl57wes9pocllv1q7v8` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`),
    CONSTRAINT `FKbr5xd3omh6bkhyxcn2j6wbcdc` FOREIGN KEY (`permits_id`) REFERENCES `permits` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- inventariodb.roles_permits_aud definition

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


-- inventariodb.users definition

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
    KEY `idx_user_username` (`username`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- inventariodb.users_aud definition

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


-- inventariodb.users_roles definition

CREATE TABLE `users_roles`
(
    `users_id` bigint NOT NULL,
    `roles_id` bigint NOT NULL,
    KEY `FKa62j07k5mhgifpp955h37ponj` (`roles_id`),
    KEY `FKml90kef4w2jy7oxyqv742tsfc` (`users_id`),
    CONSTRAINT `FKa62j07k5mhgifpp955h37ponj` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`),
    CONSTRAINT `FKml90kef4w2jy7oxyqv742tsfc` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- inventariodb.users_roles_aud definition

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


-- inventariodb.supplier definition

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

-- inventariodb.supplier_aud definition

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


-- inventariodb.product definition

CREATE TABLE `product`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `created_by`         varchar(255) COLLATE utf8mb4_unicode_ci                                                                                                                                                                                                           DEFAULT NULL,
    `created_date`       datetime(6)                                                                                                                                                                                                                                       DEFAULT NULL,
    `enabled`            bit(1) NOT NULL,
    `last_modified_by`   varchar(255) COLLATE utf8mb4_unicode_ci                                                                                                                                                                                                           DEFAULT NULL,
    `last_modified_date` datetime(6)                                                                                                                                                                                                                                       DEFAULT NULL,
    `version`            bigint                                                                                                                                                                                                                                            DEFAULT NULL,
    `category`           varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `description`        longtext COLLATE utf8mb4_unicode_ci,
    `image`              longtext COLLATE utf8mb4_unicode_ci,
    `min_stock`          int                                                                                                                                                                                                                                               DEFAULT NULL,
    `name`               varchar(255) COLLATE utf8mb4_unicode_ci                                                                                                                                                                                                           DEFAULT NULL,
    `price`              decimal(38, 2)                                                                                                                                                                                                                                    DEFAULT NULL,
    `stock`              int                                                                                                                                                                                                                                               DEFAULT NULL,
    `supplier_id`        bigint                                                                                                                                                                                                                                            DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK2kxvbr72tmtscjvyp9yqb12by` (`supplier_id`),
    CONSTRAINT `FK2kxvbr72tmtscjvyp9yqb12by` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- inventariodb.product_aud definition

CREATE TABLE `product_aud`
(
    `id`              bigint NOT NULL,
    `rev`             int    NOT NULL,
    `revtype`         tinyint                                                                                                                                                                                                                                           DEFAULT NULL,
    `category`        varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `category_mod`    bit(1)                                                                                                                                                                                                                                            DEFAULT NULL,
    `description`     varchar(255) COLLATE utf8mb4_unicode_ci                                                                                                                                                                                                           DEFAULT NULL,
    `description_mod` bit(1)                                                                                                                                                                                                                                            DEFAULT NULL,
    `image`           varchar(255) COLLATE utf8mb4_unicode_ci                                                                                                                                                                                                           DEFAULT NULL,
    `image_mod`       bit(1)                                                                                                                                                                                                                                            DEFAULT NULL,
    `min_stock`       int                                                                                                                                                                                                                                               DEFAULT NULL,
    `min_stock_mod`   bit(1)                                                                                                                                                                                                                                            DEFAULT NULL,
    `name`            varchar(255) COLLATE utf8mb4_unicode_ci                                                                                                                                                                                                           DEFAULT NULL,
    `name_mod`        bit(1)                                                                                                                                                                                                                                            DEFAULT NULL,
    `price`           decimal(38, 2)                                                                                                                                                                                                                                    DEFAULT NULL,
    `price_mod`       bit(1)                                                                                                                                                                                                                                            DEFAULT NULL,
    `stock`           int                                                                                                                                                                                                                                               DEFAULT NULL,
    `stock_mod`       bit(1)                                                                                                                                                                                                                                            DEFAULT NULL,
    `supplier_id`     bigint                                                                                                                                                                                                                                            DEFAULT NULL,
    `supplier_mod`    bit(1)                                                                                                                                                                                                                                            DEFAULT NULL,
    PRIMARY KEY (`rev`, `id`),
    CONSTRAINT `FK9vwllld6jlw5xys1ay911oh1x` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;



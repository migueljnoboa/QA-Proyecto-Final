-- inventariodb.roles definition
CREATE TABLE `roles`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `name`        varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `created_at`  datetime(6) DEFAULT NULL,
    `enabled`     bit(1) NOT NULL,
    `update_at`   datetime(6) DEFAULT NULL,
    `version`     bigint                                  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- inventariodb.permits definition
CREATE TABLE `permits`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `enabled`    bit(1) NOT NULL,
    `update_at`  datetime(6) DEFAULT NULL,
    `version`    bigint                                  DEFAULT NULL,
    `name`       varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKjkqq0lo5gti77y8qrrsp0ak3q` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- inventariodb.roles_permits definition
CREATE TABLE `roles_permits`
(
    `roles_id`   bigint NOT NULL,
    `permits_id` bigint NOT NULL,
    KEY          `FKbr5xd3omh6bkhyxcn2j6wbcdc` (`permits_id`),
    KEY          `FK362qj8xl57wes9pocllv1q7v8` (`roles_id`),
    CONSTRAINT `FK362qj8xl57wes9pocllv1q7v8` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`),
    CONSTRAINT `FKbr5xd3omh6bkhyxcn2j6wbcdc` FOREIGN KEY (`permits_id`) REFERENCES `permits` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- inventariodb.users definition
CREATE TABLE `users`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `enabled`    bit(1) NOT NULL,
    `update_at`  datetime(6) DEFAULT NULL,
    `version`    bigint                                  DEFAULT NULL,
    `email`      varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `password`   varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `username`   varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- inventariodb.users_roles definition
CREATE TABLE `users_roles`
(
    `users_id` bigint NOT NULL,
    `roles_id` bigint NOT NULL,
    KEY        `FKa62j07k5mhgifpp955h37ponj` (`roles_id`),
    KEY        `FKml90kef4w2jy7oxyqv742tsfc` (`users_id`),
    CONSTRAINT `FKa62j07k5mhgifpp955h37ponj` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`),
    CONSTRAINT `FKml90kef4w2jy7oxyqv742tsfc` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- inventariodb.supplier definition
CREATE TABLE `supplier`
(
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `created_at`   datetime(6) DEFAULT NULL,
    `enabled`      bit(1) NOT NULL,
    `update_at`    datetime(6) DEFAULT NULL,
    `version`      bigint                                  DEFAULT NULL,
    `address`      varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `contact_info` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `email`        varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name`         varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `phone_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- inventariodb.product definition
CREATE TABLE `product`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `created_at`  datetime(6) DEFAULT NULL,
    `enabled`     bit(1) NOT NULL,
    `update_at`   datetime(6) DEFAULT NULL,
    `version`     bigint                                  DEFAULT NULL,
    `category`    varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `description` longtext COLLATE utf8mb4_unicode_ci,
    `image`       longtext COLLATE utf8mb4_unicode_ci,
    `min_stock`   int                                     DEFAULT NULL,
    `name`        varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `price`       decimal(38, 2)                          DEFAULT NULL,
    `stock`       int                                     DEFAULT NULL,
    `supplier_id` bigint                                  DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY           `FK2kxvbr72tmtscjvyp9yqb12by` (`supplier_id`),
    CONSTRAINT `FK2kxvbr72tmtscjvyp9yqb12by` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

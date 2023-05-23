-- liquibase formatted sql

-- changeset fisherman:init-tables

CREATE TABLE if not exists users
(
    id       INT UNSIGNED           NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255)           NOT NULL UNIQUE,
    password VARCHAR(255)           NOT NULL,
    avatar   VARCHAR(255),
    bio      TEXT,
    location VARCHAR(255),
    contacts VARCHAR(255),
    role     ENUM ('user', 'admin') NOT NULL DEFAULT 'user'
);

CREATE TABLE if not exists categories
(
    id   INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE if not exists posts
(
    id           INT UNSIGNED   NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id      INT UNSIGNED   NOT NULL,
    category_id  INT UNSIGNED   NULL,
    title        VARCHAR(255)   NULL,
    description  TEXT           NULL,
    start_date   TIMESTAMP      NULL,
    latitude     DECIMAL(10, 8) NULL,
    longitude    DECIMAL(11, 8) NULL,
    contact_info TEXT,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (category_id) REFERENCES categories (id),
    INDEX (latitude, longitude, start_date)
);

CREATE TABLE if not exists requests
(
    id      INT UNSIGNED                             NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED                             NOT NULL,
    post_id INT UNSIGNED                             NOT NULL,
    comment TEXT                                     NULL,
    status  ENUM ('pending', 'accepted', 'declined') NOT NULL DEFAULT 'pending',
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (post_id) REFERENCES posts (id)
);

CREATE TABLE if not exists ratings
(
    id       INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id  INT UNSIGNED NOT NULL,
    post_id  INT UNSIGNED NULL,
    rated_by INT UNSIGNED NULL,
    rating   INT          NULL,
    comment  TEXT         NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

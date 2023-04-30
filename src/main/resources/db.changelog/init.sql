-- liquibase formatted sql

-- changeset fisherman:init-tables

CREATE TABLE if not exists users
(
    id       INT UNSIGNED           NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255)           NOT NULL UNIQUE,
    email    VARCHAR(255)           NOT NULL UNIQUE,
    password VARCHAR(255)           NOT NULL,
    role     ENUM ('user', 'admin') NOT NULL DEFAULT 'user'
);

CREATE TABLE if not exists profiles
(
    id        INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id   INT UNSIGNED NOT NULL UNIQUE,
    full_name VARCHAR(255),
    avatar    VARCHAR(255),
    bio       TEXT,
    location  VARCHAR(255),
    website   VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE if not exists categories
(
    id   INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE if not exists posts
(
    id           INT UNSIGNED            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id      INT UNSIGNED            NOT NULL,
    category_id  INT UNSIGNED            NULL,
    title        VARCHAR(255)            NULL,
    description  TEXT                    NULL,
    start_date   DATETIME                NULL,
    coordinates  VARCHAR(255),
    contact_info TEXT,
    status       ENUM ('open', 'closed') NOT NULL DEFAULT 'open',
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE if not exists requests
(
    id      INT UNSIGNED                                         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED                                         NOT NULL,
    post_id INT UNSIGNED                                         NOT NULL,
    status  ENUM ('pending', 'accepted', 'canceled', 'declined') NOT NULL DEFAULT 'pending',
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (post_id) REFERENCES posts (id)
);

CREATE TABLE if not exists ratings
(
    id      INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    post_id INT UNSIGNED NOT NULL,
    rating  INT          NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (post_id) REFERENCES posts (id)
);

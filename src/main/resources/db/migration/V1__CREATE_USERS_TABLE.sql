CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE users
(
    id         BIGINT       NOT NULL,
    uuid       UUID,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    updated_on TIMESTAMP WITHOUT TIME ZONE,
    version    BIGINT,
    username   VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);
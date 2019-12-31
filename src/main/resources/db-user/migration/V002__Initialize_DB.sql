CREATE TABLE auth_user
(
    id           BIGSERIAL PRIMARY KEY,
    user_uuid    VARCHAR(64) NOT NULL,
    provider_id  VARCHAR(32) NOT NULL,
    provider_key VARCHAR(96) NOT NULL,
    first_name   VARCHAR(32) NULL,
    last_name    VARCHAR(32) NULL,
    full_name    VARCHAR(64) NULL,
    email        VARCHAR(96) NULL,
    avatar_url   VARCHAR(96) NULL,
    activated    BOOLEAN     NOT NULL
);

CREATE UNIQUE INDEX ON auth_user (user_uuid);
CREATE UNIQUE INDEX ON auth_user (provider_id, provider_key);

CREATE TABLE password_info
(
    id       BIGSERIAL   PRIMARY KEY,
    user_id  BIGINT      NOT NULL REFERENCES auth_user (id),
    hasher   VARCHAR(32) NOT NULL,
    password VARCHAR(96) NOT NULL,
    salt     VARCHAR(64) NULL
);

CREATE UNIQUE INDEX ON password_info (user_id);

CREATE TABLE auth_token
(
    id        BIGSERIAL PRIMARY KEY,
    uuid      VARCHAR(64) NOT NULL,
    user_uuid VARCHAR(64) NOT NULL,
    expiry    TIMESTAMP   NOT NULL
);

CREATE UNIQUE INDEX ON auth_token (uuid);

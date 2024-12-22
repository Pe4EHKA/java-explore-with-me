CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name  VARCHAR(256)                        NOT NULL,
    email VARCHAR(256)                        NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT unique_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(256)                        NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id),
    CONSTRAINT unique_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    annotation         VARCHAR(2048)                       NOT NULL,
    category_id        BIGINT                              NOT NULL,
    confirmed_requests BIGINT,
    created_on         TIMESTAMP WITHOUT TIME ZONE         NOT NULL,
    description        VARCHAR(8192)                       NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE         NOT NULL,
    initiator_id       BIGINT                              NOT NULL,
    lat                REAL                                NOT NULL,
    lon                REAL                                NOT NULL,
    paid               BOOLEAN                             NOT NULL,
    participant_limit  BIGINT,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN,
    state              VARCHAR(64)                         NOT NULL,
    title              VARCHAR(128)                        NOT NULL,
    CONSTRAINT pk_event PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE,
    FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    event_id     BIGINT                              NOT NULL,
    requester_id BIGINT                              NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE         NOT NULL,
    status       VARCHAR(16)                         NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT unique_event_and_requester UNIQUE (event_id, requester_id),
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    title  VARCHAR(128)                        NOT NULL,
    pinned BOOLEAN                             NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (id),
    CONSTRAINT unique_compilation_title UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS compilations_events
(
    compilation_id BIGINT NOT NULL,
    event_id       BIGINT NOT NULL,
    CONSTRAINT pk_compilations_events PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT unique_compilation_and_event UNIQUE (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);
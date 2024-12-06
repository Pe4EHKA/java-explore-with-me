CREATE TABLE IF NOT EXISTS hits
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY,
    app       VARCHAR(64)                 NOT NULL,
    uri       VARCHAR(128)                NOT NULL,
    ip        VARCHAR(16)                 NOT NULL,
    timestamp TIMESTAMP without time zone NOT NULL,
    CONSTRAINT pk_hit PRIMARY KEY (id)
);
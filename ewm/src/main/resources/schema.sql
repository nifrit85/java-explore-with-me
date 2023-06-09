DROP TABLE IF EXISTS categories CASCADE;
CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50) UNIQUE                      NOT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    email VARCHAR(254) UNIQUE                     NOT NULL,
    name  varchar(250)                            NOT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS locations CASCADE;
CREATE TABLE IF NOT EXISTS locations
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat         REAL                                    NOT NULL,
    lon         REAL                                    NOT NULL,
    name        VARCHAR(120),
    description VARCHAR(7000),
    PRIMARY KEY (id),
    UNIQUE (lat, lon)
);

DROP TABLE IF EXISTS events CASCADE;
create table IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation         VARCHAR(2000)                           NOT NULL,
    category_id        BIGINT REFERENCES categories (id)       NOT NULL,
    created_on         TIMESTAMP                               NOT NULL,
    description        VARCHAR(7000)                           NOT NULL,
    event_date         TIMESTAMP                               NOT NULL,
    initiator_id       BIGINT REFERENCES users (ID)            NOT NULL,
    location_id        BIGINT REFERENCES locations (ID)        NOT NULL,
    paid               BOOLEAN                                 NOT NULL,
    participant_limit  INTEGER                                 NOT NULL,
    published_on       TIMESTAMP,
    request_moderation BOOLEAN                                 NOT NULL,
    state              VARCHAR(255)                            NOT NULL,
    title              VARCHAR(120)                            NOT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS requests CASCADE;
CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY,
    created      TIMESTAMP    NOT NULL,
    event_id     BIGINT REFERENCES events (id),
    requester_id BIGINT REFERENCES users (id),
    status       VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS compilations CASCADE;
CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY,
    pinned BOOLEAN     NOT NULL,
    title  VARCHAR(50) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS compilations_events CASCADE;
CREATE TABLE IF NOT EXISTS compilations_events
(
    event_id       BIGINT REFERENCES events (id),
    compilation_id BIGINT REFERENCES compilations (id),
    PRIMARY KEY (event_id, compilation_id)
);

CREATE OR REPLACE FUNCTION distance(lat1 float, lon1 float, lat2 float, lon2 float)
    RETURNS float
AS
'
declare
    dist float = 0;
    rad_lat1 float;
    rad_lat2 float;
    theta float;
    rad_theta float;
BEGIN
    IF lat1 = lat2 AND lon1 = lon2
    THEN
        RETURN dist;
    ELSE
        -- переводим градусы широты в радианы
        rad_lat1 = pi() * lat1 / 180;
        -- переводим градусы долготы в радианы
        rad_lat2 = pi() * lat2 / 180;
        -- находим разность долгот
        theta = lon1 - lon2;
        -- переводим градусы в радианы
        rad_theta = pi() * theta / 180;
        -- находим длину ортодромии
        dist = sin(rad_lat1) * sin(rad_lat2) + cos(rad_lat1) * cos(rad_lat2) * cos(rad_theta);

        IF dist > 1
            THEN dist = 1;
        END IF;

        dist = acos(dist);
        -- переводим радианы в градусы
        dist = dist * 180 / pi();
        -- переводим градусы в километры
        dist = dist * 60 * 1.8524;

        RETURN dist;
    END IF;
END;
'
LANGUAGE PLPGSQL;
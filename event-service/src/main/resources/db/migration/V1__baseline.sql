CREATE TABLE IF NOT EXISTS events (
                                      id          BIGSERIAL PRIMARY KEY,
                                      name        VARCHAR(255) NOT NULL,
    description TEXT,
    location    VARCHAR(255) NOT NULL,
    start_time  TIMESTAMP NOT NULL,
    end_time    TIMESTAMP NOT NULL,
    capacity    INTEGER NOT NULL CHECK (capacity > 0),
    price       NUMERIC(10,2) NOT NULL CHECK (price >= 0)
    );

CREATE INDEX IF NOT EXISTS idx_events_start_time ON events(start_time);

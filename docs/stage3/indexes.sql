CREATE INDEX IF NOT EXISTS idx_users_email
    ON application_users (email);

CREATE INDEX IF NOT EXISTS idx_users_username
    ON application_users (username);

CREATE INDEX IF NOT EXISTS idx_event_id
    ON event (id);

CREATE INDEX IF NOT EXISTS idx_event_status
    ON event (status);

CREATE INDEX IF NOT EXISTS idx_outcome_event_id
    ON outcome (event_id);


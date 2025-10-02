CREATE TABLE token (
    id SERIAL PRIMARY KEY,
    token TEXT NOT NULL UNIQUE,
    is_expired BOOLEAN,
    is_revoked BOOLEAN,
    user_id uuid NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

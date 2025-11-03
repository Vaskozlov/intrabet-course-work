-- CREATE DATABASE university_bets;

-- \c university_bets;

CREATE TYPE user_role AS ENUM ('student', 'teacher', 'admin');
CREATE TYPE event_status AS ENUM ('planned', 'ongoing', 'completed');
CREATE TYPE participant_type AS ENUM ('team', 'individual');
CREATE TYPE bet_status AS ENUM ('active', 'settled');
CREATE TYPE transaction_type AS ENUM ('deposit', 'withdrawal', 'bet', 'win');
CREATE TYPE payment_type AS ENUM ('card', 'bank_transfer');

CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL CHECK (end_time > start_time),
    status event_status NOT NULL DEFAULT 'planned',
    category_id INT REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE participant_events (
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    event_id INT REFERENCES events(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, event_id)
);

CREATE TABLE outcomes (
    id SERIAL PRIMARY KEY,
    event_id INT REFERENCES events(id) ON DELETE CASCADE,
    description VARCHAR(100) NOT NULL,
    is_winner BOOLEAN DEFAULT FALSE
);

CREATE TABLE wallets (
    id SERIAL PRIMARY KEY,
    user_id INT UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    balance DECIMAL(10,2) DEFAULT 0 CHECK (balance >= 0),
    currency VARCHAR(3) DEFAULT 'RUB'
);

CREATE TABLE bets (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    event_id INT REFERENCES events(id) ON DELETE CASCADE,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    placement_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status bet_status NOT NULL DEFAULT 'active',
    outcome_id INT REFERENCES outcomes(id) ON DELETE SET NULL
);

CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    wallet_id INT REFERENCES wallets(id) ON DELETE CASCADE,
    amount DECIMAL(10,2) NOT NULL,
    type transaction_type NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE admin_logs (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details TEXT
);

CREATE TABLE payment_methods (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    type payment_type NOT NULL,
    details JSONB
);

-- Триггер для автоматического создания кошелька при регистрации пользователя
CREATE OR REPLACE FUNCTION create_wallet() RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO wallets (user_id) VALUES (NEW.id);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trig_create_wallet
AFTER INSERT ON users
FOR EACH ROW EXECUTE FUNCTION create_wallet();

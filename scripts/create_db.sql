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
    INSERT INTO wallets (user_id, balance) VALUES (NEW.id, 1000.00);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trig_create_wallet
AFTER INSERT ON users
FOR EACH ROW EXECUTE FUNCTION create_wallet();

-- Триггер для обновления баланса при размещении ставки
CREATE OR REPLACE FUNCTION update_wallet_on_bet() RETURNS TRIGGER AS $$
BEGIN
    UPDATE wallets
    SET balance = balance - NEW.amount
    WHERE user_id = NEW.user_id;

    -- Создаем транзакцию
    INSERT INTO transactions (wallet_id, amount, type)
    SELECT id, -NEW.amount, 'bet'
    FROM wallets
    WHERE user_id = NEW.user_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trig_update_wallet_on_bet
AFTER INSERT ON bets
FOR EACH ROW EXECUTE FUNCTION update_wallet_on_bet();

-- Триггер для начисления выигрыша
CREATE OR REPLACE FUNCTION process_bet_winnings() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_winner = TRUE AND OLD.is_winner = FALSE THEN
        -- Начисляем выигрыш всем ставкам на этот outcome
        UPDATE wallets w
        SET balance = balance + (b.amount * 2)
        FROM bets b
        WHERE b.outcome_id = NEW.id
          AND b.user_id = w.user_id
          AND b.status = 'active';

        -- Создаем транзакции выигрышей
        INSERT INTO transactions (wallet_id, amount, type)
        SELECT w.id, (b.amount * 2), 'win'
        FROM bets b
        JOIN wallets w ON w.user_id = b.user_id
        WHERE b.outcome_id = NEW.id
          AND b.status = 'active';

        -- Обновляем статус ставок
        UPDATE bets
        SET status = 'settled'
        WHERE outcome_id = NEW.id
          AND status = 'active';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trig_process_bet_winnings
AFTER UPDATE ON outcomes
FOR EACH ROW EXECUTE FUNCTION process_bet_winnings();

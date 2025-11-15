#### Сущности и их атрибуты:
1. **User** (Пользователь): id (PK), username, email, password_hash, role (student/teacher/admin), registration_date.
2. **Event** (Событие): id (PK), title, description, start_time, end_time, status (planned/ongoing/completed/cancelled).
3. **Category** (Категория события): id (PK), name (sports/academic/cultural), description.
4. **Participant** (Участник): id (PK), name, type (team/individual), university_affiliation.
5. **Bet** (Ставка): id (PK), amount, placement_time, status (active/settled).
6. **Outcome** (Исход): id (PK), description, is_winner (boolean).
7. **Wallet** (Кошелек): id (PK), balance, currency.
8. **Transaction** (Транзакция): id (PK), amount, type (deposit/withdrawal/bet/win), timestamp.
9. **AdminLog** (Журнал администратора): id (PK), action, timestamp, details.
10. **PaymentMethod** (Метод платежа): id (PK), type (card/bank_transfer), details.

#### Связи:
- **User 1:1 Wallet** (один пользователь имеет один кошелек, кошелек принадлежит одному пользователю).
- **User 1:N Bet** (пользователь размещает много ставок).
- **Event 1:N Bet** (событие имеет много ставок).
- **Event 1:N Outcome** (событие имеет несколько возможных исходов).
- **Event 1:1 Category** (событие относится к одной категории).
- **Wallet 1:N Transaction** (кошелек имеет много транзакций).
- **User 1:N AdminLog** (администратор логирует действия; только для ролей admin).
- **User 1:N PaymentMethod** (пользователь имеет несколько методов платежа).
- **Participant N:M Event** (многие-ко-многим: участник может быть в нескольких событиях, событие имеет нескольких участников; реализуется через промежуточную сущность ParticipantEvent).
- **Bet 1:1 Outcome** (ставка связана с конкретным исходом).

![alt text](https://github.com/Vaskozlov/is-course-work/blob/main/docs/data_base_uml.png)

### Построение даталогической модели

#### Таблицы:
1. **users**: id SERIAL PRIMARY KEY, username VARCHAR(50) UNIQUE, email VARCHAR(100) UNIQUE, password_hash VARCHAR(255), role ENUM('student', 'teacher', 'admin'), registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP.
2. **events**: id SERIAL PRIMARY KEY, name VARCHAR(100), description TEXT, start_time TIMESTAMP, end_time TIMESTAMP, status ENUM('planned', 'ongoing', 'completed'), category_id INT REFERENCES categories(id).
3. **categories**: id SERIAL PRIMARY KEY, name VARCHAR(50), description TEXT.
4. **participants**: id SERIAL PRIMARY KEY, name VARCHAR(100), type ENUM('team', 'individual'), university_affiliation VARCHAR(100).
5. **bets**: id SERIAL PRIMARY KEY, user_id INT REFERENCES users(id), event_id INT REFERENCES events(id), amount DECIMAL(10,2), placement_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, status ENUM('active', 'settled'), outcome_id INT REFERENCES outcomes(id).
6. **outcomes**: id SERIAL PRIMARY KEY, event_id INT REFERENCES events(id), description VARCHAR(100), is_winner BOOLEAN.
7. **wallets**: id SERIAL PRIMARY KEY, user_id INT UNIQUE REFERENCES users(id), balance DECIMAL(10,2) DEFAULT 0, currency VARCHAR(3) DEFAULT 'RUB'.
8. **transactions**: id SERIAL PRIMARY KEY, wallet_id INT REFERENCES wallets(id), amount DECIMAL(10,2), type ENUM('deposit', 'withdrawal', 'bet', 'win'), timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP.
9. **admin_logs**: id SERIAL PRIMARY KEY, user_id INT REFERENCES users(id), action VARCHAR(100), timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, details TEXT.
10. **payment_methods**: id SERIAL PRIMARY KEY, user_id INT REFERENCES users(id), type ENUM('card', 'bank_transfer'), details JSONB.
11. **participant_events** (для N:M): participant_id INT REFERENCES participants(id), event_id INT REFERENCES events(id), PRIMARY KEY (participant_id, event_id).

### Реализация в PostgreSQL

#### Скрипты DDL

Скрипт для создания БД и таблиц (create_db.sql):

```sql
CREATE DATABASE university_bets;

\c university_bets;

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
```

Скрипт для удаления БД (drop_db.sql):

```sql
DROP DATABASE IF EXISTS university_bets;
```

Скрипт для заполнения тестовыми данными (insert_data.sql):

```sql
\c university_bets;

INSERT INTO categories (name, description) VALUES 
('Sports', 'University sports events'),
('Academic', 'Contests and quizzes'),
('Cultural', 'Festivals and performances');

INSERT INTO users (username, email, password_hash, role) VALUES 
('student1', 'student1@itmo.ru', 'hash1', 'student'),
('teacher1', 'teacher1@itmo.ru', 'hash2', 'teacher'),
('admin1', 'admin1@itmo.ru', 'hash3', 'admin');

-- Кошельки создаются триггером автоматически

INSERT INTO events (name, description, start_time, end_time, status, category_id) VALUES 
('Football Match', 'ITMO vs SPbU', '2025-10-20 14:00:00', '2025-10-20 16:00:00', 'planned', 1),
('Quiz Contest', 'Programming quiz', '2025-10-21 10:00:00', '2025-10-21 12:00:00', 'planned', 2);

INSERT INTO participants (name, type, university_affiliation) VALUES 
('ITMO Team', 'team', 'ITMO'),
('SPbU Team', 'team', 'SPbU'),
('Student A', 'individual', 'ITMO');

INSERT INTO participant_events (participant_id, event_id) VALUES 
(1, 1), (2, 1), (3, 2);

INSERT INTO outcomes (event_id, description, is_winner) VALUES 
(1, 'ITMO wins', FALSE),
(1, 'SPbU wins', FALSE),
(2, 'Student A wins', FALSE);

INSERT INTO bets (user_id, event_id, amount, outcome_id) VALUES 
(1, 1, 100.00, 1),
(2, 2, 50.00, 3);

INSERT INTO transactions (wallet_id, amount, type) VALUES 
(1, 200.00, 'deposit'),
(2, 100.00, 'deposit');

INSERT INTO payment_methods (user_id, type, details) VALUES 
(1, 'card', '{"card_number": "1234-5678"}');

INSERT INTO admin_logs (user_id, action, details) VALUES 
(3, 'Created event', 'Football Match');
```

### PL/pgSQL функции и процедуры

1. **Функция для размещения ставки** (place_bet_func):

```sql
CREATE OR REPLACE FUNCTION place_bet(
    p_user_id INT, 
    p_event_id INT, 
    p_amount DECIMAL, 
    p_outcome_id INT
) RETURNS VOID AS $$
BEGIN
    IF (SELECT balance FROM wallets WHERE user_id = p_user_id) < p_amount THEN
        RAISE EXCEPTION 'Insufficient balance';
    END IF;
    INSERT INTO bets (user_id, event_id, amount, outcome_id) 
    VALUES (p_user_id, p_event_id, p_amount, p_outcome_id);
    -- Триггер обработает обновление баланса
END;
$$ LANGUAGE plpgsql;
```

2. **Процедура для расчета выигрышей по событию** (settle_event_proc):

```sql
CREATE OR REPLACE PROCEDURE settle_event(p_event_id INT, p_winning_outcome_id INT)
AS $$
BEGIN
    UPDATE outcomes SET is_winner = FALSE WHERE event_id = p_event_id;
    UPDATE outcomes SET is_winner = TRUE WHERE id = p_winning_outcome_id;
    UPDATE events SET status = 'completed' WHERE id = p_event_id;
    -- Триггер обработает ставки
END;
$$ LANGUAGE plpgsql;
```

3. **Функция для просмотра баланса** (get_balance_func):

```sql
CREATE OR REPLACE FUNCTION get_balance(p_user_id INT) 
RETURNS DECIMAL AS $$
SELECT balance FROM wallets WHERE user_id = p_user_id;
$$ LANGUAGE sql;
```

4. **Процедура для логирования админ-действий** (log_admin_action_proc):

```sql
CREATE OR REPLACE PROCEDURE log_admin_action(
    p_user_id INT, 
    p_action VARCHAR, 
    p_details TEXT
)
AS $$
BEGIN
    IF (SELECT role FROM users WHERE id = p_user_id) != 'admin' THEN
        RAISE EXCEPTION 'Not an admin';
    END IF;
    INSERT INTO admin_logs (user_id, action, details) VALUES (p_user_id, p_action, p_details);
END;
$$ LANGUAGE plpgsql;
```

### Создание индексов и обоснование

На основе прецедентов (регистрация, размещение/просмотр ставок, поиск событий, расчет выигрышей, админ-отчеты). Анализ: частые SELECT по user_id, event_id; JOIN по foreign keys; фильтры по status, timestamp.

Созданные индексы (в DDL добавить после таблиц):

```sql
CREATE INDEX idx_users_email ON users(email);  -- Для быстрой аутентификации (прецедент: логин).
CREATE INDEX idx_events_status ON events(status);  -- Для поиска активных событий (прецедент: просмотр событий).
CREATE INDEX idx_events_start_time ON events(start_time);  -- Для сортировки по времени (прецедент: расписание).
CREATE INDEX idx_bets_user_id ON bets(user_id);  -- Для просмотра ставок пользователя (прецедент: история ставок).
CREATE INDEX idx_bets_event_id ON bets(event_id);  -- Для расчета по событию (прецедент: завершение события).
CREATE INDEX idx_transactions_wallet_id ON transactions(wallet_id);  -- Для истории транзакций (прецедент: финансовый отчет).
CREATE INDEX idx_outcomes_event_id ON outcomes(event_id);  -- Для быстрого нахождения исходов (прецедент: расчет выигрыша).
```

Обоснование:
- **idx_users_email**: Ускоряет логин/регистрацию (O(1) вместо O(n)), критично для пользовательского опыта в прецеденте аутентификации.
- **idx_events_status и idx_events_start_time**: Оптимизируют запросы на список событий (например, SELECT * FROM events WHERE status = 'planned' ORDER BY start_time), полезно для бизнес-процесса просмотра и ставок на предстоящие события.
- **idx_bets_user_id и idx_bets_event_id**: Ускоряют JOIN и GROUP BY в прецедентах истории ставок и расчета выигрышей (например, SELECT SUM(amount) FROM bets WHERE user_id = ?), снижая время на частые операции.
- **idx_transactions_wallet_id**: Для отчетов по финансам (прецедент: просмотр баланса/транзакций), где много записей.
- **idx_outcomes_event_id**: Критично для прецедента завершения события, где нужно быстро обновлять и рассчитывать (UPDATE/SELECT по event_id).

Эти индексы балансируют чтение/запись, фокусируясь на read-heavy прецедентах (просмотр > запись). В будущем можно мониторить с EXPLAIN ANALYZE.

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

INSERT INTO participant_events (user_id, event_id) VALUES 
(1, 1), (2, 1);

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
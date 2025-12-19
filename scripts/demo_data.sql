-- ===================================================
-- ДЕМОНСТРАЦИОННЫЕ ДАННЫЕ ДЛЯ ЗАЩИТЫ ЛАБОРАТОРНОЙ РАБОТЫ
-- IntraBets - Платформа для ставок на университетские события
-- ===================================================

-- Очистка существующих данных (осторожно!)
TRUNCATE TABLE admin_log CASCADE;
TRUNCATE TABLE bet CASCADE;
TRUNCATE TABLE transaction CASCADE;
TRUNCATE TABLE outcome CASCADE;
TRUNCATE TABLE event CASCADE;
TRUNCATE TABLE user_event CASCADE;
TRUNCATE TABLE wallet CASCADE;
TRUNCATE TABLE category CASCADE;
TRUNCATE TABLE application_users CASCADE;

-- ===================================================
-- 1. КАТЕГОРИИ СОБЫТИЙ
-- ===================================================

INSERT INTO category (name, description) VALUES
('Спорт', 'Спортивные соревнования между факультетами и командами ИТМО'),
('Киберспорт', 'Турниры по Dota 2, CS:GO, League of Legends'),
('Культура', 'Фестивали, концерты, творческие мероприятия'),
('Академия', 'Хакатоны, олимпиады, научные конференции'),
('Общественная жизнь', 'Выборы в студсовет, общественные голосования');

-- ===================================================
-- 2. ПОЛЬЗОВАТЕЛИ
-- ===================================================

-- Пароль для всех: password123 (хеш будет сгенерирован Spring Security)
-- Временно используем простые пароли для демонстрации

INSERT INTO application_users (username, email, password, role) VALUES
-- Студенты
('ivan_petrov', 'ivan.petrov@student.itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 0),  -- password123
('maria_ivanova', 'maria.ivanova@student.itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 0),
('alex_smirnov', 'alex.smirnov@student.itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 0),
('dmitry_kozlov', 'dmitry.kozlov@student.itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 0),
('olga_sokolova', 'olga.sokolova@student.itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 0),

-- Преподаватели
('prof_kuznetsov', 'prof.kuznetsov@itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 1),
('doc_fedorova', 'doc.fedorova@itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 1),

-- Администратор
('admin', 'admin@itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 2);  -- admin123

-- ===================================================
-- 3. ПОПОЛНЕНИЕ БАЛАНСОВ (через триггер создаются кошельки)
-- ===================================================

-- Даём пользователям начальный баланс для демонстрации
UPDATE wallet SET balance = 5000.0 WHERE user_id = 1; -- ivan_petrov
UPDATE wallet SET balance = 3500.0 WHERE user_id = 2; -- maria_ivanova
UPDATE wallet SET balance = 4200.0 WHERE user_id = 3; -- alex_smirnov
UPDATE wallet SET balance = 2800.0 WHERE user_id = 4; -- dmitry_kozlov
UPDATE wallet SET balance = 6100.0 WHERE user_id = 5; -- olga_sokolova
UPDATE wallet SET balance = 8000.0 WHERE user_id = 6; -- prof_kuznetsov
UPDATE wallet SET balance = 7500.0 WHERE user_id = 7; -- doc_fedorova
UPDATE wallet SET balance = 10000.0 WHERE user_id = 8; -- admin

-- ===================================================
-- 4. АКТИВНЫЕ СОБЫТИЯ (для демонстрации размещения ставок)
-- ===================================================

INSERT INTO event (title, description, starts_at, ends_at, status, category_id, user_id, created_at, updated_at) VALUES
-- PLANNED (можно делать ставки)
(
    'Футбол: ФИТиП vs ФПМИ',
    'Финальный матч межфакультетского турнира по футболу. Команда ФИТиП против ФПМИ.',
    '2025-12-20 15:00:00+03',
    '2025-12-20 17:00:00+03',
    0, -- PLANNED
    1, -- Спорт
    8, -- admin
    NOW(),
    NOW()
),
(
    'CS:GO Tournament: Финал',
    'Финальный матч турнира по CS:GO между командами "ITMO Eagles" и "SPbU Legends"',
    '2025-12-21 18:00:00+03',
    '2025-12-21 21:00:00+03',
    0, -- PLANNED
    2, -- Киберспорт
    8,
    NOW(),
    NOW()
),
(
    'Хакатон "AI Challenge 2025"',
    'Какая команда займет первое место на хакатоне по искусственному интеллекту?',
    '2025-12-22 10:00:00+03',
    '2025-12-23 18:00:00+03',
    0, -- PLANNED
    4, -- Академия
    8,
    NOW(),
    NOW()
),

-- ONGOING (события в процессе)
(
    'Dota 2: Полуфинал',
    'Полуфинальный матч турнира по Dota 2. Team Alpha vs Team Beta',
    '2025-12-19 19:00:00+03',
    '2025-12-19 23:00:00+03',
    1, -- ONGOING
    2, -- Киберспорт
    8,
    NOW() - INTERVAL '2 hours',
    NOW()
),
(
    'КВН: 1/4 финала',
    'Команда КИТа против команды ФТМ в четвертьфинале КВН ИТМО',
    '2025-12-19 20:00:00+03',
    '2025-12-19 22:30:00+03',
    1, -- ONGOING
    3, -- Культура
    8,
    NOW() - INTERVAL '1 hour',
    NOW()
);

-- ===================================================
-- 5. ЗАВЕРШЕННЫЕ СОБЫТИЯ (для демонстрации выигрышей)
-- ===================================================

INSERT INTO event (title, description, starts_at, ends_at, status, category_id, user_id, created_at, updated_at, closed_at) VALUES
(
    'Волейбол: ФИКиТ vs ФТиПиМ',
    'Матч по волейболу в рамках спартакиады ИТМО',
    '2025-12-18 14:00:00+03',
    '2025-12-18 16:00:00+03',
    2, -- COMPLETED
    1, -- Спорт
    8,
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '1 day',
    NOW() - INTERVAL '1 day'
),
(
    'Конкурс стартапов "ITMO Ideas"',
    'Финал конкурса стартап-проектов студентов ИТМО',
    '2025-12-17 10:00:00+03',
    '2025-12-17 18:00:00+03',
    2, -- COMPLETED
    4, -- Академия
    8,
    NOW() - INTERVAL '3 days',
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '2 days'
),
(
    'League of Legends: Групповая стадия',
    'Групповой этап турнира по League of Legends',
    '2025-12-16 17:00:00+03',
    '2025-12-16 22:00:00+03',
    2, -- COMPLETED
    2, -- Киберспорт
    8,
    NOW() - INTERVAL '4 days',
    NOW() - INTERVAL '3 days',
    NOW() - INTERVAL '3 days'
);

-- ===================================================
-- 6. ИСХОДЫ ДЛЯ СОБЫТИЙ
-- ===================================================

-- Событие 1: Футбол ФИТиП vs ФПМИ (PLANNED)
INSERT INTO outcome (event_id, description, odds, is_winner) VALUES
(1, 'Победа ФИТиП', 1.85, FALSE),
(1, 'Ничья', 3.20, FALSE),
(1, 'Победа ФПМИ', 2.10, FALSE);

-- Событие 2: CS:GO Финал (PLANNED)
INSERT INTO outcome (event_id, description, odds, is_winner) VALUES
(2, 'ITMO Eagles 2:0', 2.50, FALSE),
(2, 'ITMO Eagles 2:1', 3.00, FALSE),
(2, 'SPbU Legends 2:1', 3.50, FALSE),
(2, 'SPbU Legends 2:0', 2.80, FALSE);

-- Событие 3: Хакатон (PLANNED)
INSERT INTO outcome (event_id, description, odds, is_winner) VALUES
(3, 'Команда "AI Masters"', 2.20, FALSE),
(3, 'Команда "Neural Network"', 2.50, FALSE),
(3, 'Команда "Deep Learning Pro"', 2.80, FALSE),
(3, 'Другая команда', 4.00, FALSE);

-- Событие 4: Dota 2 Полуфинал (ONGOING)
INSERT INTO outcome (event_id, description, odds, is_winner) VALUES
(4, 'Team Alpha побеждает', 1.75, FALSE),
(4, 'Team Beta побеждает', 2.15, FALSE);

-- Событие 5: КВН (ONGOING)
INSERT INTO outcome (event_id, description, odds, is_winner) VALUES
(5, 'Победа команды КИТа', 1.90, FALSE),
(5, 'Победа команды ФТМ', 2.00, FALSE);

-- Событие 6: Волейбол (COMPLETED) - ФИКиТ выиграл
INSERT INTO outcome (event_id, description, odds, is_winner) VALUES
(6, 'Победа ФИКиТ 3:0', 2.00, TRUE),  -- ВЫИГРЫШНЫЙ ИСХОД
(6, 'Победа ФИКиТ 3:1', 2.50, FALSE),
(6, 'Победа ФТиПиМ 3:1', 3.00, FALSE),
(6, 'Победа ФТиПиМ 3:0', 3.50, FALSE);

-- Событие 7: Конкурс стартапов (COMPLETED) - AI Masters выиграли
INSERT INTO outcome (event_id, description, odds, is_winner) VALUES
(7, 'Проект "EcoTech"', 2.20, FALSE),
(7, 'Проект "SmartCity AI"', 1.80, TRUE),  -- ВЫИГРЫШНЫЙ ИСХОД
(7, 'Проект "HealthMonitor"', 2.80, FALSE);

-- Событие 8: LoL (COMPLETED) - Team Phoenix выиграли
INSERT INTO outcome (event_id, description, odds, is_winner) VALUES
(8, 'Team Phoenix', 1.70, TRUE),  -- ВЫИГРЫШНЫЙ ИСХОД
(8, 'Team Dragon', 2.30, FALSE);

-- ===================================================
-- 7. СТАВКИ НА АКТИВНЫЕ СОБЫТИЯ
-- ===================================================

-- Ставки на Футбол (событие 1)
INSERT INTO bet (user_id, outcome_id, amount, placed_at) VALUES
(1, 1, 500.0, NOW() - INTERVAL '2 hours'),  -- ivan_petrov на победу ФИТиП
(2, 3, 300.0, NOW() - INTERVAL '1.5 hours'), -- maria_ivanova на победу ФПМИ
(3, 2, 200.0, NOW() - INTERVAL '1 hour'),    -- alex_smirnov на ничью
(5, 1, 450.0, NOW() - INTERVAL '30 minutes'); -- olga_sokolova на победу ФИТиП

-- Ставки на CS:GO (событие 2)
INSERT INTO bet (user_id, outcome_id, amount, placed_at) VALUES
(1, 4, 300.0, NOW() - INTERVAL '3 hours'),   -- ivan_petrov на ITMO Eagles 2:0
(4, 6, 400.0, NOW() - INTERVAL '2 hours'),   -- dmitry_kozlov на SPbU 2:1
(6, 4, 1000.0, NOW() - INTERVAL '1 hour');   -- prof_kuznetsov на ITMO Eagles 2:0

-- Ставки на Хакатон (событие 3)
INSERT INTO bet (user_id, outcome_id, amount, placed_at) VALUES
(2, 8, 250.0, NOW() - INTERVAL '4 hours'),   -- maria_ivanova на AI Masters
(3, 9, 350.0, NOW() - INTERVAL '3 hours'),   -- alex_smirnov на Neural Network
(7, 10, 500.0, NOW() - INTERVAL '2 hours');  -- doc_fedorova на Deep Learning Pro

-- Ставки на Dota 2 (событие 4 - ONGOING)
INSERT INTO bet (user_id, outcome_id, amount, placed_at) VALUES
(1, 12, 400.0, NOW() - INTERVAL '3 hours'),  -- ivan_petrov на Team Alpha
(5, 13, 600.0, NOW() - INTERVAL '2.5 hours'); -- olga_sokolova на Team Beta

-- Ставки на КВН (событие 5 - ONGOING)
INSERT INTO bet (user_id, outcome_id, amount, placed_at) VALUES
(2, 14, 200.0, NOW() - INTERVAL '2 hours'),  -- maria_ivanova на КИТ
(4, 15, 300.0, NOW() - INTERVAL '1.5 hours'); -- dmitry_kozlov на ФТМ

-- ===================================================
-- 8. СТАВКИ НА ЗАВЕРШЕННЫЕ СОБЫТИЯ (С ВЫИГРЫШАМИ)
-- ===================================================

-- Волейбол (событие 6) - выиграл исход 16 (ФИКиТ 3:0, коэфф 2.0)
INSERT INTO bet (user_id, outcome_id, amount, placed_at) VALUES
(1, 16, 500.0, NOW() - INTERVAL '2 days'),   -- ivan_petrov ВЫИГРАЛ (500 * 2.0 = 1000)
(3, 17, 300.0, NOW() - INTERVAL '2 days'),   -- alex_smirnov проиграл
(5, 16, 400.0, NOW() - INTERVAL '2 days');   -- olga_sokolova ВЫИГРАЛА (400 * 2.0 = 800)

-- Конкурс стартапов (событие 7) - выиграл исход 21 (SmartCity AI, коэфф 1.8)
INSERT INTO bet (user_id, outcome_id, amount, placed_at) VALUES
(2, 21, 600.0, NOW() - INTERVAL '3 days'),   -- maria_ivanova ВЫИГРАЛА (600 * 1.8 = 1080)
(4, 20, 400.0, NOW() - INTERVAL '3 days'),   -- dmitry_kozlov проиграл
(6, 21, 1200.0, NOW() - INTERVAL '3 days');  -- prof_kuznetsov ВЫИГРАЛ (1200 * 1.8 = 2160)

-- LoL (событие 8) - выиграл исход 23 (Team Phoenix, коэфф 1.7)
INSERT INTO bet (user_id, outcome_id, amount, placed_at) VALUES
(1, 23, 300.0, NOW() - INTERVAL '4 days'),   -- ivan_petrov ВЫИГРАЛ (300 * 1.7 = 510)
(3, 24, 500.0, NOW() - INTERVAL '4 days'),   -- alex_smirnov проиграл
(7, 23, 800.0, NOW() - INTERVAL '4 days');   -- doc_fedorova ВЫИГРАЛА (800 * 1.7 = 1360)

-- ===================================================
-- 9. ТРАНЗАКЦИИ
-- ===================================================

-- Транзакции пополнения
INSERT INTO transaction (wallet_id, amount, type, timestamp) VALUES
(1, 5000.0, 0, NOW() - INTERVAL '5 days'),  -- ivan_petrov пополнил
(2, 3500.0, 0, NOW() - INTERVAL '5 days'),  -- maria_ivanova пополнила
(3, 4200.0, 0, NOW() - INTERVAL '5 days'),  -- alex_smirnov пополнил
(4, 2800.0, 0, NOW() - INTERVAL '5 days'),  -- dmitry_kozlov пополнил
(5, 6100.0, 0, NOW() - INTERVAL '5 days'),  -- olga_sokolova пополнила
(6, 8000.0, 0, NOW() - INTERVAL '5 days'),  -- prof_kuznetsov пополнил
(7, 7500.0, 0, NOW() - INTERVAL '5 days'),  -- doc_fedorova пополнила
(8, 10000.0, 0, NOW() - INTERVAL '5 days'); -- admin пополнил

-- Транзакции ставок (автоматически создаются при размещении ставок в реальной системе)
-- Здесь добавляем вручную для демонстрации истории

-- Транзакции выигрышей для завершенных событий
INSERT INTO transaction (wallet_id, amount, type, timestamp) VALUES
-- Волейбол - выигрыши
(1, 1000.0, 3, NOW() - INTERVAL '1 day'),   -- ivan_petrov выиграл
(5, 800.0, 3, NOW() - INTERVAL '1 day'),    -- olga_sokolova выиграла

-- Конкурс стартапов - выигрыши
(2, 1080.0, 3, NOW() - INTERVAL '2 days'),  -- maria_ivanova выиграла
(6, 2160.0, 3, NOW() - INTERVAL '2 days'),  -- prof_kuznetsov выиграл

-- LoL - выигрыши
(1, 510.0, 3, NOW() - INTERVAL '3 days'),   -- ivan_petrov выиграл
(7, 1360.0, 3, NOW() - INTERVAL '3 days');  -- doc_fedorova выиграла

-- Обновляем балансы с учетом выигрышей
UPDATE wallet SET balance = balance + 1000.0 + 510.0 WHERE user_id = 1; -- ivan_petrov +1510
UPDATE wallet SET balance = balance + 1080.0 WHERE user_id = 2;           -- maria_ivanova +1080
UPDATE wallet SET balance = balance + 800.0 WHERE user_id = 5;            -- olga_sokolova +800
UPDATE wallet SET balance = balance + 2160.0 WHERE user_id = 6;           -- prof_kuznetsov +2160
UPDATE wallet SET balance = balance + 1360.0 WHERE user_id = 7;           -- doc_fedorova +1360

-- ===================================================
-- 10. ЛОГИ АДМИНИСТРАТОРА
-- ===================================================

INSERT INTO admin_log (user_id, action, timestamp, details) VALUES
(8, 'CREATE_CATEGORY', NOW() - INTERVAL '10 days', 'Создана категория "Спорт"'),
(8, 'CREATE_CATEGORY', NOW() - INTERVAL '10 days', 'Создана категория "Киберспорт"'),
(8, 'CREATE_EVENT', NOW() - INTERVAL '5 days', 'Создано событие "Футбол: ФИТиП vs ФПМИ"'),
(8, 'CREATE_EVENT', NOW() - INTERVAL '5 days', 'Создано событие "CS:GO Tournament: Финал"'),
(8, 'CREATE_EVENT', NOW() - INTERVAL '4 days', 'Создано событие "Хакатон AI Challenge 2025"'),
(8, 'FINISH_EVENT', NOW() - INTERVAL '1 day', 'Завершено событие "Волейбол: ФИКиТ vs ФТиПиМ"'),
(8, 'FINISH_EVENT', NOW() - INTERVAL '2 days', 'Завершено событие "Конкурс стартапов ITMO Ideas"'),
(8, 'FINISH_EVENT', NOW() - INTERVAL '3 days', 'Завершено событие "League of Legends"');

-- ===================================================
-- ИТОГОВАЯ СТАТИСТИКА
-- ===================================================

-- Просмотр статистики
SELECT
    'Всего пользователей' as metric,
    COUNT(*)::text as value
FROM application_users
UNION ALL
SELECT
    'Всего событий',
    COUNT(*)::text
FROM event
UNION ALL
SELECT
    'Активных событий (PLANNED/ONGOING)',
    COUNT(*)::text
FROM event WHERE status IN (0, 1)
UNION ALL
SELECT
    'Завершенных событий',
    COUNT(*)::text
FROM event WHERE status = 2
UNION ALL
SELECT
    'Всего ставок',
    COUNT(*)::text
FROM bet
UNION ALL
SELECT
    'Общая сумма ставок',
    ROUND(SUM(amount), 2)::text || ' ₽'
FROM bet
UNION ALL
SELECT
    'Общий баланс пользователей',
    ROUND(SUM(balance), 2)::text || ' ₽'
FROM wallet;

-- ===================================================
-- ГОТОВО! База данных подготовлена для демонстрации
-- ===================================================

-- Учетные данные для демонстрации:
-- Студенты: ivan_petrov, maria_ivanova, alex_smirnov, dmitry_kozlov, olga_sokolova
-- Преподаватели: prof_kuznetsov, doc_fedorova
-- Администратор: admin
-- Пароль для всех: password123

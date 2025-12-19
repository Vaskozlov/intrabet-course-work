-- ===================================================
-- ДЕМОНСТРАЦИОННЫЕ ДАННЫЕ ДЛЯ ЗАЩИТЫ ЛАБОРАТОРНОЙ РАБОТЫ
-- IntraBets - Платформа для ставок на университетские события
-- Версия 2: совместимо с реальной схемой БД
-- ===================================================

-- Очистка существующих данных
DELETE FROM bet;
DELETE FROM outcome;
DELETE FROM event;
DELETE FROM wallet WHERE user_id NOT IN (SELECT id FROM application_users WHERE role = 2);
DELETE FROM application_users WHERE role != 2;
DELETE FROM category;

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

-- Пароль для всех: password123
-- BCrypt хеш: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu

INSERT INTO application_users (username, email, password_hash, role, created_at) VALUES
-- Студенты (role = 0)
('ivan_petrov', 'ivan.petrov@student.itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 0, NOW() - INTERVAL '30 days'),
('maria_ivanova', 'maria.ivanova@student.itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 0, NOW() - INTERVAL '28 days'),
('alex_smirnov', 'alex.smirnov@student.itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 0, NOW() - INTERVAL '25 days'),
('dmitry_kozlov', 'dmitry.kozlov@student.itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 0, NOW() - INTERVAL '22 days'),
('olga_sokolova', 'olga.sokolova@student.itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 0, NOW() - INTERVAL '20 days'),

-- Преподаватели (role = 1)
('prof_kuznetsov', 'prof.kuznetsov@itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 1, NOW() - INTERVAL '60 days'),
('doc_fedorova', 'doc.fedorova@itmo.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 1, NOW() - INTERVAL '55 days');

-- ===================================================
-- 3. ПОПОЛНЕНИЕ БАЛАНСОВ
-- ===================================================

-- Обновляем балансы (кошельки создаются триггером автоматически)
UPDATE wallet SET balance = 5000.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'ivan_petrov');
UPDATE wallet SET balance = 3500.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'maria_ivanova');
UPDATE wallet SET balance = 4200.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'alex_smirnov');
UPDATE wallet SET balance = 2800.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'dmitry_kozlov');
UPDATE wallet SET balance = 6100.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'olga_sokolova');
UPDATE wallet SET balance = 8000.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'prof_kuznetsov');
UPDATE wallet SET balance = 7500.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'doc_fedorova');

-- ===================================================
-- 4. СОБЫТИЯ
-- ===================================================

-- Получаем ID админа для создания событий
DO $$
DECLARE
    admin_id bigint;
    sport_cat_id bigint;
    cyber_cat_id bigint;
    culture_cat_id bigint;
    academic_cat_id bigint;
    event1_id bigint;
    event2_id bigint;
    event3_id bigint;
    event4_id bigint;
    event5_id bigint;
    event6_id bigint;
    event7_id bigint;
    event8_id bigint;
BEGIN
    -- Получаем ID админа
    SELECT id INTO admin_id FROM application_users WHERE role = 2 LIMIT 1;

    -- Получаем ID категорий
    SELECT id INTO sport_cat_id FROM category WHERE name = 'Спорт';
    SELECT id INTO cyber_cat_id FROM category WHERE name = 'Киберспорт';
    SELECT id INTO culture_cat_id FROM category WHERE name = 'Культура';
    SELECT id INTO academic_cat_id FROM category WHERE name = 'Академия';

    -- АКТИВНЫЕ СОБЫТИЯ (PLANNED - status = 0)
    INSERT INTO event (title, description, starts_at, ends_at, status, category_id, user_id, created_at, updated_at)
    VALUES (
        'Футбол: ФИТиП vs ФПМИ',
        'Финальный матч межфакультетского турнира по футболу. Команда ФИТиП против ФПМИ.',
        NOW() + INTERVAL '1 day',
        NOW() + INTERVAL '1 day' + INTERVAL '2 hours',
        0,  -- PLANNED
        sport_cat_id,
        admin_id,
        NOW() - INTERVAL '3 days',
        NOW() - INTERVAL '3 days'
    ) RETURNING id INTO event1_id;

    INSERT INTO event (title, description, starts_at, ends_at, status, category_id, user_id, created_at, updated_at)
    VALUES (
        'CS:GO Tournament: Финал',
        'Финальный матч турнира по CS:GO между командами "ITMO Eagles" и "SPbU Legends"',
        NOW() + INTERVAL '2 days',
        NOW() + INTERVAL '2 days' + INTERVAL '3 hours',
        0,  -- PLANNED
        cyber_cat_id,
        admin_id,
        NOW() - INTERVAL '4 days',
        NOW() - INTERVAL '4 days'
    ) RETURNING id INTO event2_id;

    INSERT INTO event (title, description, starts_at, ends_at, status, category_id, user_id, created_at, updated_at)
    VALUES (
        'Хакатон "AI Challenge 2025"',
        'Какая команда займет первое место на хакатоне по искусственному интеллекту?',
        NOW() + INTERVAL '3 days',
        NOW() + INTERVAL '4 days' + INTERVAL '8 hours',
        0,  -- PLANNED
        academic_cat_id,
        admin_id,
        NOW() - INTERVAL '5 days',
        NOW() - INTERVAL '5 days'
    ) RETURNING id INTO event3_id;

    -- СОБЫТИЯ В ПРОЦЕССЕ (ONGOING - status = 1)
    INSERT INTO event (title, description, starts_at, ends_at, status, category_id, user_id, created_at, updated_at)
    VALUES (
        'Dota 2: Полуфинал',
        'Полуфинальный матч турнира по Dota 2. Team Alpha vs Team Beta',
        NOW() - INTERVAL '2 hours',
        NOW() + INTERVAL '2 hours',
        1,  -- ONGOING
        cyber_cat_id,
        admin_id,
        NOW() - INTERVAL '1 day',
        NOW() - INTERVAL '2 hours'
    ) RETURNING id INTO event4_id;

    INSERT INTO event (title, description, starts_at, ends_at, status, category_id, user_id, created_at, updated_at)
    VALUES (
        'КВН: 1/4 финала',
        'Команда КИТа против команды ФТМ в четвертьфинале КВН ИТМО',
        NOW() - INTERVAL '1 hour',
        NOW() + INTERVAL '1.5 hours',
        1,  -- ONGOING
        culture_cat_id,
        admin_id,
        NOW() - INTERVAL '2 days',
        NOW() - INTERVAL '1 hour'
    ) RETURNING id INTO event5_id;

    -- ЗАВЕРШЕННЫЕ СОБЫТИЯ (COMPLETED - status = 2)
    INSERT INTO event (title, description, starts_at, ends_at, status, category_id, user_id, created_at, updated_at, closed_at)
    VALUES (
        'Волейбол: ФИКиТ vs ФТиПиМ',
        'Матч по волейболу в рамках спартакиады ИТМО',
        NOW() - INTERVAL '2 days',
        NOW() - INTERVAL '2 days' + INTERVAL '2 hours',
        2,  -- COMPLETED
        sport_cat_id,
        admin_id,
        NOW() - INTERVAL '5 days',
        NOW() - INTERVAL '2 days',
        NOW() - INTERVAL '2 days'
    ) RETURNING id INTO event6_id;

    INSERT INTO event (title, description, starts_at, ends_at, status, category_id, user_id, created_at, updated_at, closed_at)
    VALUES (
        'Конкурс стартапов "ITMO Ideas"',
        'Финал конкурса стартап-проектов студентов ИТМО',
        NOW() - INTERVAL '4 days',
        NOW() - INTERVAL '4 days' + INTERVAL '8 hours',
        2,  -- COMPLETED
        academic_cat_id,
        admin_id,
        NOW() - INTERVAL '7 days',
        NOW() - INTERVAL '3 days',
        NOW() - INTERVAL '3 days'
    ) RETURNING id INTO event7_id;

    INSERT INTO event (title, description, starts_at, ends_at, status, category_id, user_id, created_at, updated_at, closed_at)
    VALUES (
        'League of Legends: Групповая стадия',
        'Групповой этап турнира по League of Legends',
        NOW() - INTERVAL '5 days',
        NOW() - INTERVAL '5 days' + INTERVAL '5 hours',
        2,  -- COMPLETED
        cyber_cat_id,
        admin_id,
        NOW() - INTERVAL '8 days',
        NOW() - INTERVAL '5 days',
        NOW() - INTERVAL '5 days'
    ) RETURNING id INTO event8_id;

    -- ===================================================
    -- 5. ИСХОДЫ ДЛЯ СОБЫТИЙ
    -- ===================================================

    -- Событие 1: Футбол ФИТиП vs ФПМИ
    INSERT INTO outcome (event_id, description, is_winner) VALUES
    (event1_id, 'Победа ФИТиП', FALSE),
    (event1_id, 'Ничья', FALSE),
    (event1_id, 'Победа ФПМИ', FALSE);

    -- Событие 2: CS:GO Финал
    INSERT INTO outcome (event_id, description, is_winner) VALUES
    (event2_id, 'ITMO Eagles 2:0', FALSE),
    (event2_id, 'ITMO Eagles 2:1', FALSE),
    (event2_id, 'SPbU Legends 2:1', FALSE),
    (event2_id, 'SPbU Legends 2:0', FALSE);

    -- Событие 3: Хакатон
    INSERT INTO outcome (event_id, description, is_winner) VALUES
    (event3_id, 'Команда "AI Masters"', FALSE),
    (event3_id, 'Команда "Neural Network"', FALSE),
    (event3_id, 'Команда "Deep Learning Pro"', FALSE),
    (event3_id, 'Другая команда', FALSE);

    -- Событие 4: Dota 2 (ONGOING)
    INSERT INTO outcome (event_id, description, is_winner) VALUES
    (event4_id, 'Team Alpha побеждает', FALSE),
    (event4_id, 'Team Beta побеждает', FALSE);

    -- Событие 5: КВН (ONGOING)
    INSERT INTO outcome (event_id, description, is_winner) VALUES
    (event5_id, 'Победа команды КИТа', FALSE),
    (event5_id, 'Победа команды ФТМ', FALSE);

    -- Событие 6: Волейбол (COMPLETED с победителем)
    INSERT INTO outcome (event_id, description, is_winner) VALUES
    (event6_id, 'Победа ФИКиТ 3:0', TRUE),   -- ПОБЕДИТЕЛЬ
    (event6_id, 'Победа ФИКиТ 3:1', FALSE),
    (event6_id, 'Победа ФТиПиМ 3:1', FALSE),
    (event6_id, 'Победа ФТиПиМ 3:0', FALSE);

    -- Событие 7: Конкурс стартапов (COMPLETED с победителем)
    INSERT INTO outcome (event_id, description, is_winner) VALUES
    (event7_id, 'Проект "EcoTech"', FALSE),
    (event7_id, 'Проект "SmartCity AI"', TRUE),  -- ПОБЕДИТЕЛЬ
    (event7_id, 'Проект "HealthMonitor"', FALSE);

    -- Событие 8: LoL (COMPLETED с победителем)
    INSERT INTO outcome (event_id, description, is_winner) VALUES
    (event8_id, 'Team Phoenix', TRUE),  -- ПОБЕДИТЕЛЬ
    (event8_id, 'Team Dragon', FALSE);

    -- ===================================================
    -- 6. СТАВКИ
    -- ===================================================

    -- Получаем ID пользователей
    DECLARE
        ivan_id bigint;
        maria_id bigint;
        alex_id bigint;
        dmitry_id bigint;
        olga_id bigint;
        prof_id bigint;
        doc_id bigint;
    BEGIN
        SELECT id INTO ivan_id FROM application_users WHERE username = 'ivan_petrov';
        SELECT id INTO maria_id FROM application_users WHERE username = 'maria_ivanova';
        SELECT id INTO alex_id FROM application_users WHERE username = 'alex_smirnov';
        SELECT id INTO dmitry_id FROM application_users WHERE username = 'dmitry_kozlov';
        SELECT id INTO olga_id FROM application_users WHERE username = 'olga_sokolova';
        SELECT id INTO prof_id FROM application_users WHERE username = 'prof_kuznetsov';
        SELECT id INTO doc_id FROM application_users WHERE username = 'doc_fedorova';

        -- Ставки на активные события
        -- Футбол (событие 1)
        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT ivan_id, id, 500.0, NOW() - INTERVAL '2 hours' FROM outcome WHERE event_id = event1_id AND description = 'Победа ФИТиП';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT maria_id, id, 300.0, NOW() - INTERVAL '1.5 hours' FROM outcome WHERE event_id = event1_id AND description = 'Победа ФПМИ';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT alex_id, id, 200.0, NOW() - INTERVAL '1 hour' FROM outcome WHERE event_id = event1_id AND description = 'Ничья';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT olga_id, id, 450.0, NOW() - INTERVAL '30 minutes' FROM outcome WHERE event_id = event1_id AND description = 'Победа ФИТиП';

        -- CS:GO (событие 2)
        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT ivan_id, id, 300.0, NOW() - INTERVAL '3 hours' FROM outcome WHERE event_id = event2_id AND description = 'ITMO Eagles 2:0';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT dmitry_id, id, 400.0, NOW() - INTERVAL '2 hours' FROM outcome WHERE event_id = event2_id AND description = 'SPbU Legends 2:1';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT prof_id, id, 1000.0, NOW() - INTERVAL '1 hour' FROM outcome WHERE event_id = event2_id AND description = 'ITMO Eagles 2:0';

        -- Хакатон (событие 3)
        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT maria_id, id, 250.0, NOW() - INTERVAL '4 hours' FROM outcome WHERE event_id = event3_id AND description = 'Команда "AI Masters"';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT alex_id, id, 350.0, NOW() - INTERVAL '3 hours' FROM outcome WHERE event_id = event3_id AND description = 'Команда "Neural Network"';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT doc_id, id, 500.0, NOW() - INTERVAL '2 hours' FROM outcome WHERE event_id = event3_id AND description = 'Команда "Deep Learning Pro"';

        -- Dota 2 (ONGOING)
        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT ivan_id, id, 400.0, NOW() - INTERVAL '3 hours' FROM outcome WHERE event_id = event4_id AND description = 'Team Alpha побеждает';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT olga_id, id, 600.0, NOW() - INTERVAL '2.5 hours' FROM outcome WHERE event_id = event4_id AND description = 'Team Beta побеждает';

        -- КВН (ONGOING)
        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT maria_id, id, 200.0, NOW() - INTERVAL '2 hours' FROM outcome WHERE event_id = event5_id AND description = 'Победа команды КИТа';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT dmitry_id, id, 300.0, NOW() - INTERVAL '1.5 hours' FROM outcome WHERE event_id = event5_id AND description = 'Победа команды ФТМ';

        -- Ставки на завершенные события (будут показывать выигрыши/проигрыши)
        -- Волейбол (event6) - выиграл "Победа ФИКиТ 3:0"
        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT ivan_id, id, 500.0, NOW() - INTERVAL '2 days' FROM outcome WHERE event_id = event6_id AND description = 'Победа ФИКиТ 3:0';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT alex_id, id, 300.0, NOW() - INTERVAL '2 days' FROM outcome WHERE event_id = event6_id AND description = 'Победа ФИКиТ 3:1';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT olga_id, id, 400.0, NOW() - INTERVAL '2 days' FROM outcome WHERE event_id = event6_id AND description = 'Победа ФИКиТ 3:0';

        -- Конкурс стартапов (event7) - выиграл "SmartCity AI"
        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT maria_id, id, 600.0, NOW() - INTERVAL '4 days' FROM outcome WHERE event_id = event7_id AND description = 'Проект "SmartCity AI"';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT dmitry_id, id, 400.0, NOW() - INTERVAL '4 days' FROM outcome WHERE event_id = event7_id AND description = 'Проект "EcoTech"';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT prof_id, id, 1200.0, NOW() - INTERVAL '4 days' FROM outcome WHERE event_id = event7_id AND description = 'Проект "SmartCity AI"';

        -- LoL (event8) - выиграл "Team Phoenix"
        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT ivan_id, id, 300.0, NOW() - INTERVAL '5 days' FROM outcome WHERE event_id = event8_id AND description = 'Team Phoenix';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT alex_id, id, 500.0, NOW() - INTERVAL '5 days' FROM outcome WHERE event_id = event8_id AND description = 'Team Dragon';

        INSERT INTO bet (user_id, outcome_id, amount, created_at)
        SELECT doc_id, id, 800.0, NOW() - INTERVAL '5 days' FROM outcome WHERE event_id = event8_id AND description = 'Team Phoenix';
    END;
END $$;

-- ===================================================
-- ИТОГОВАЯ СТАТИСТИКА
-- ===================================================

SELECT
    'Всего пользователей' as metric,
    COUNT(*)::text as value
FROM application_users
UNION ALL
SELECT
    'Студентов',
    COUNT(*)::text
FROM application_users WHERE role = 0
UNION ALL
SELECT
    'Преподавателей',
    COUNT(*)::text
FROM application_users WHERE role = 1
UNION ALL
SELECT
    'Всего событий',
    COUNT(*)::text
FROM event
UNION ALL
SELECT
    'Активных событий (PLANNED)',
    COUNT(*)::text
FROM event WHERE status = 0
UNION ALL
SELECT
    'Событий в процессе (ONGOING)',
    COUNT(*)::text
FROM event WHERE status = 1
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
-- УЧЕТНЫЕ ДАННЫЕ ДЛЯ ДЕМОНСТРАЦИИ
-- ===================================================

SELECT '=======================================' as info
UNION ALL SELECT 'УЧЕТНЫЕ ДАННЫЕ ДЛЯ ВХОДА'
UNION ALL SELECT '======================================='
UNION ALL SELECT ''
UNION ALL SELECT 'Пароль для всех пользователей: password123'
UNION ALL SELECT ''
UNION ALL SELECT 'Студенты:'
UNION ALL SELECT '  - ivan_petrov'
UNION ALL SELECT '  - maria_ivanova'
UNION ALL SELECT '  - alex_smirnov'
UNION ALL SELECT '  - dmitry_kozlov'
UNION ALL SELECT '  - olga_sokolova'
UNION ALL SELECT ''
UNION ALL SELECT 'Преподаватели:'
UNION ALL SELECT '  - prof_kuznetsov'
UNION ALL SELECT '  - doc_fedorova'
UNION ALL SELECT ''
UNION ALL SELECT 'Администратор:'
UNION ALL SELECT '  - admin (используйте существующего админа)'
UNION ALL SELECT '=======================================' ;

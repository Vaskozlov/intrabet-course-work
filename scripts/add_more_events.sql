-- Добавление разнообразных событий для демонстрации

-- Получаем ID администратора и категорий
DO $$
DECLARE
    admin_id bigint;
    sport_cat_id bigint;
    cyber_cat_id bigint;
    culture_cat_id bigint;
    academic_cat_id bigint;
    social_cat_id bigint;
BEGIN
    -- Получаем ID админа
    SELECT id INTO admin_id FROM application_users WHERE role = 2 LIMIT 1;

    -- Получаем ID категорий
    SELECT id INTO sport_cat_id FROM category WHERE name = 'Спорт';
    SELECT id INTO cyber_cat_id FROM category WHERE name = 'Киберспорт';
    SELECT id INTO culture_cat_id FROM category WHERE name = 'Культура';
    SELECT id INTO academic_cat_id FROM category WHERE name = 'Академия';
    SELECT id INTO social_cat_id FROM category WHERE name = 'Общественная жизнь';

    -- ==========================================
    -- АКТИВНЫЕ СОБЫТИЯ (PLANNED - можно делать ставки)
    -- ==========================================

    -- СПОРТ
    INSERT INTO event (title, description, starts_at, ends_at, status, category_id, user_id, created_at, updated_at)
    VALUES
    (
        'Баскетбол: ИТМО vs СПбПУ',
        'Межвузовский турнир по баскетболу. Решающий матч за выход в финал.',
        NOW() + INTERVAL '6 hours',
        NOW() + INTERVAL '8 hours',
        0,
        sport_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),
    (
        'Шахматный турнир: Блиц',
        'Блиц-турнир среди студентов ИТМО. Призовой фонд 10,000₽',
        NOW() + INTERVAL '12 hours',
        NOW() + INTERVAL '16 hours',
        0,
        sport_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),
    (
        'Настольный теннис: Факультетский чемпионат',
        'Финальная игра между представителями ФИТиП и ФПМИ',
        NOW() + INTERVAL '1 day',
        NOW() + INTERVAL '1 day' + INTERVAL '3 hours',
        0,
        sport_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),
    (
        'Бег на 5 км: Весенний забег ИТМО',
        'Традиционный забег вокруг кампуса. Кто придет первым?',
        NOW() + INTERVAL '2 days',
        NOW() + INTERVAL '2 days' + INTERVAL '2 hours',
        0,
        sport_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),

    -- КИБЕРСПОРТ
    (
        'Dota 2: Финал Кубка ИТМО',
        'Грандфинал университетского турнира. Team Legends vs Team Champions',
        NOW() + INTERVAL '8 hours',
        NOW() + INTERVAL '12 hours',
        0,
        cyber_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),
    (
        'Valorant: Отборочный этап',
        'Отбор лучших команд для участия в региональном турнире',
        NOW() + INTERVAL '1 day',
        NOW() + INTERVAL '1 day' + INTERVAL '6 hours',
        0,
        cyber_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),
    (
        'FIFA 24: 1 на 1 турнир',
        'Индивидуальный турнир по FIFA 24. Кто станет чемпионом?',
        NOW() + INTERVAL '18 hours',
        NOW() + INTERVAL '22 hours',
        0,
        cyber_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),

    -- КУЛЬТУРА
    (
        'Битва хоров: Финал',
        'Финальное выступление хоров факультетов',
        NOW() + INTERVAL '1 day',
        NOW() + INTERVAL '1 day' + INTERVAL '3 hours',
        0,
        culture_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),
    (
        'Театральный фестиваль: Лучшая постановка',
        'Конкурс студенческих театральных коллективов',
        NOW() + INTERVAL '2 days',
        NOW() + INTERVAL '2 days' + INTERVAL '5 hours',
        0,
        culture_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),
    (
        'Stand-up батл студентов',
        'Юмористическое соревнование. Кто рассмешит зал?',
        NOW() + INTERVAL '15 hours',
        NOW() + INTERVAL '18 hours',
        0,
        culture_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),

    -- АКАДЕМИЯ
    (
        'CodeForces Contest: University Round',
        'Командное соревнование по программированию на Codeforces',
        NOW() + INTERVAL '10 hours',
        NOW() + INTERVAL '15 hours',
        0,
        academic_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),
    (
        'Pitch Day: Стартап презентации',
        'Финальные презентации проектов перед инвесторами',
        NOW() + INTERVAL '1 day',
        NOW() + INTERVAL '1 day' + INTERVAL '4 hours',
        0,
        academic_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),
    (
        'Робототехника: BattleBots',
        'Соревнование роботов-сумоистов',
        NOW() + INTERVAL '20 hours',
        NOW() + INTERVAL '24 hours',
        0,
        academic_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),
    (
        'Математическая олимпиада',
        'Кто решит больше задач за 3 часа?',
        NOW() + INTERVAL '2 days',
        NOW() + INTERVAL '2 days' + INTERVAL '3 hours',
        0,
        academic_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),

    -- ОБЩЕСТВЕННАЯ ЖИЗНЬ
    (
        'Выборы председателя студсовета',
        'Голосование за нового председателя студенческого совета',
        NOW() + INTERVAL '1 day',
        NOW() + INTERVAL '1 day' + INTERVAL '8 hours',
        0,
        social_cat_id,
        admin_id,
        NOW(),
        NOW()
    ),
    (
        'Конкурс "Лучшая общага ИТМО"',
        'Какое общежитие заслуживает звание лучшего?',
        NOW() + INTERVAL '12 hours',
        NOW() + INTERVAL '3 days',
        0,
        social_cat_id,
        admin_id,
        NOW(),
        NOW()
    );

    -- ==========================================
    -- ДОБАВЛЯЕМ ИСХОДЫ ДЛЯ НОВЫХ СОБЫТИЙ
    -- ==========================================

    -- Баскетбол
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'Победа ИТМО с разницей >10 очков', FALSE FROM event WHERE title = 'Баскетбол: ИТМО vs СПбПУ'
    UNION ALL
    SELECT id, 'Победа ИТМО с разницей <10 очков', FALSE FROM event WHERE title = 'Баскетбол: ИТМО vs СПбПУ'
    UNION ALL
    SELECT id, 'Победа СПбПУ', FALSE FROM event WHERE title = 'Баскетбол: ИТМО vs СПбПУ';

    -- Шахматы
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'Студент 1-2 курса', FALSE FROM event WHERE title = 'Шахматный турнир: Блиц'
    UNION ALL
    SELECT id, 'Студент 3-4 курса', FALSE FROM event WHERE title = 'Шахматный турнир: Блиц'
    UNION ALL
    SELECT id, 'Магистрант/аспирант', FALSE FROM event WHERE title = 'Шахматный турнир: Блиц';

    -- Настольный теннис
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'ФИТиП 3:0', FALSE FROM event WHERE title = 'Настольный теннис: Факультетский чемпионат'
    UNION ALL
    SELECT id, 'ФИТиП 3:1 или 3:2', FALSE FROM event WHERE title = 'Настольный теннис: Факультетский чемпионат'
    UNION ALL
    SELECT id, 'ФПМИ побеждает', FALSE FROM event WHERE title = 'Настольный теннис: Факультетский чемпионат';

    -- Бег
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'Время победителя < 20 минут', FALSE FROM event WHERE title = 'Бег на 5 км: Весенний забег ИТМО'
    UNION ALL
    SELECT id, 'Время победителя 20-25 минут', FALSE FROM event WHERE title = 'Бег на 5 км: Весенний забег ИТМО'
    UNION ALL
    SELECT id, 'Время победителя > 25 минут', FALSE FROM event WHERE title = 'Бег на 5 км: Весенний забег ИТМО';

    -- Dota 2 Финал
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'Team Legends 3:0', FALSE FROM event WHERE title = 'Dota 2: Финал Кубка ИТМО'
    UNION ALL
    SELECT id, 'Team Legends 3:1 или 3:2', FALSE FROM event WHERE title = 'Dota 2: Финал Кубка ИТМО'
    UNION ALL
    SELECT id, 'Team Champions побеждает', FALSE FROM event WHERE title = 'Dota 2: Финал Кубка ИТМО';

    -- Valorant
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'Пройдет команда ФИТиП', FALSE FROM event WHERE title = 'Valorant: Отборочный этап'
    UNION ALL
    SELECT id, 'Пройдет команда ФПМИ', FALSE FROM event WHERE title = 'Valorant: Отборочный этап'
    UNION ALL
    SELECT id, 'Пройдет другая команда', FALSE FROM event WHERE title = 'Valorant: Отборочный этап';

    -- FIFA 24
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'Победитель из ФИТиП', FALSE FROM event WHERE title = 'FIFA 24: 1 на 1 турнир'
    UNION ALL
    SELECT id, 'Победитель из ФПМИ', FALSE FROM event WHERE title = 'FIFA 24: 1 на 1 турнир'
    UNION ALL
    SELECT id, 'Победитель из другого факультета', FALSE FROM event WHERE title = 'FIFA 24: 1 на 1 турнир';

    -- Битва хоров
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'Хор ФИТиП', FALSE FROM event WHERE title = 'Битва хоров: Финал'
    UNION ALL
    SELECT id, 'Хор ФПМИ', FALSE FROM event WHERE title = 'Битва хоров: Финал'
    UNION ALL
    SELECT id, 'Хор другого факультета', FALSE FROM event WHERE title = 'Битва хоров: Финал';

    -- Театр
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'Театр ФИТиП "Гамлет 2.0"', FALSE FROM event WHERE title = 'Театральный фестиваль: Лучшая постановка'
    UNION ALL
    SELECT id, 'Театр ФПМИ "Алгоритм любви"', FALSE FROM event WHERE title = 'Театральный фестиваль: Лучшая постановка'
    UNION ALL
    SELECT id, 'Другая постановка', FALSE FROM event WHERE title = 'Театральный фестиваль: Лучшая постановка';

    -- Stand-up
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'Участник №1', FALSE FROM event WHERE title = 'Stand-up батл студентов'
    UNION ALL
    SELECT id, 'Участник №2', FALSE FROM event WHERE title = 'Stand-up батл студентов'
    UNION ALL
    SELECT id, 'Участник №3', FALSE FROM event WHERE title = 'Stand-up батл студентов'
    UNION ALL
    SELECT id, 'Другой участник', FALSE FROM event WHERE title = 'Stand-up батл студентов';

    -- CodeForces
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'Команда ФИТиП в топ-3', FALSE FROM event WHERE title = 'CodeForces Contest: University Round'
    UNION ALL
    SELECT id, 'Команда ФПМИ в топ-3', FALSE FROM event WHERE title = 'CodeForces Contest: University Round'
    UNION ALL
    SELECT id, 'Другая команда в топ-3', FALSE FROM event WHERE title = 'CodeForces Contest: University Round';

    -- Pitch Day
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'AI-стартап получит инвестиции', FALSE FROM event WHERE title = 'Pitch Day: Стартап презентации'
    UNION ALL
    SELECT id, 'Fintech-стартап получит инвестиции', FALSE FROM event WHERE title = 'Pitch Day: Стартап презентации'
    UNION ALL
    SELECT id, 'Другой стартап получит инвестиции', FALSE FROM event WHERE title = 'Pitch Day: Стартап презентации';

    -- BattleBots
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'Робот от ФИТиП', FALSE FROM event WHERE title = 'Робототехника: BattleBots'
    UNION ALL
    SELECT id, 'Робот от ФПМИ', FALSE FROM event WHERE title = 'Робототехника: BattleBots'
    UNION ALL
    SELECT id, 'Робот от другого факультета', FALSE FROM event WHERE title = 'Робототехника: BattleBots';

    -- Математическая олимпиада
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'Студент 1-2 курса', FALSE FROM event WHERE title = 'Математическая олимпиада'
    UNION ALL
    SELECT id, 'Студент 3-4 курса', FALSE FROM event WHERE title = 'Математическая олимпиада'
    UNION ALL
    SELECT id, 'Магистрант', FALSE FROM event WHERE title = 'Математическая олимпиада';

    -- Выборы
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'Кандидат А (ФИТиП)', FALSE FROM event WHERE title = 'Выборы председателя студсовета'
    UNION ALL
    SELECT id, 'Кандидат Б (ФПМИ)', FALSE FROM event WHERE title = 'Выборы председателя студсовета'
    UNION ALL
    SELECT id, 'Кандидат В (другой факультет)', FALSE FROM event WHERE title = 'Выборы председателя студсовета';

    -- Конкурс общаг
    INSERT INTO outcome (event_id, description, is_winner)
    SELECT id, 'Общежитие на Ломоносова', FALSE FROM event WHERE title = 'Конкурс "Лучшая общага ИТМО"'
    UNION ALL
    SELECT id, 'Общежитие на Вяземском', FALSE FROM event WHERE title = 'Конкурс "Лучшая общага ИТМО"'
    UNION ALL
    SELECT id, 'Общежитие на Чайковского', FALSE FROM event WHERE title = 'Конкурс "Лучшая общага ИТМО"'
    UNION ALL
    SELECT id, 'Другое общежитие', FALSE FROM event WHERE title = 'Конкурс "Лучшая общага ИТМО"';

END $$;

-- Показываем статистику
SELECT
    'Всего событий' as metric,
    COUNT(*)::text as value
FROM event
UNION ALL
SELECT
    'Активных (PLANNED)',
    COUNT(*)::text
FROM event WHERE status = 0
UNION ALL
SELECT
    'В процессе (ONGOING)',
    COUNT(*)::text
FROM event WHERE status = 1
UNION ALL
SELECT
    'Завершенных (COMPLETED)',
    COUNT(*)::text
FROM event WHERE status = 2;

-- Список новых событий
SELECT
    '=== НОВЫЕ АКТИВНЫЕ СОБЫТИЯ ===' as info
UNION ALL
SELECT
    c.name || ': ' || e.title
FROM event e
JOIN category c ON e.category_id = c.id
WHERE e.status = 0
  AND e.created_at > NOW() - INTERVAL '5 minutes'
ORDER BY c.name, e.starts_at;

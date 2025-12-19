#!/bin/bash

# Скрипт для правильной регистрации демо-пользователей через API

API_URL="http://localhost:8080/api"

echo "=== Регистрация демо-пользователей ==="

# Функция регистрации
register_user() {
    local username=$1
    local email=$2
    local password=$3

    echo "Регистрация: $username ($email)"
    curl -s -X POST $API_URL/auth/register \
        -H 'Content-Type: application/json' \
        -d "{\"username\":\"$username\",\"email\":\"$email\",\"password\":\"$password\"}" \
        | python3 -c "import sys, json; d=sys.stdin.read(); print('✓' if 'accessToken' in d else '✗ ' + d)" 2>/dev/null || echo "✗"
}

# Удаляем старых демо-пользователей (кроме net0pyr)
echo "Удаление старых демо-пользователей..."
docker exec is-course-work-db psql -U postgres -d university_bets -c "
DELETE FROM bet WHERE user_id IN (SELECT id FROM application_users WHERE username IN ('ivan_petrov', 'maria_ivanova', 'alex_smirnov', 'dmitry_kozlov', 'olga_sokolova', 'prof_kuznetsov', 'doc_fedorova', 'admin'));
DELETE FROM wallet WHERE user_id IN (SELECT id FROM application_users WHERE username IN ('ivan_petrov', 'maria_ivanova', 'alex_smirnov', 'dmitry_kozlov', 'olga_sokolova', 'prof_kuznetsov', 'doc_fedorova', 'admin'));
DELETE FROM application_users WHERE username IN ('ivan_petrov', 'maria_ivanova', 'alex_smirnov', 'dmitry_kozlov', 'olga_sokolova', 'prof_kuznetsov', 'doc_fedorova', 'admin');
" > /dev/null 2>&1

echo ""
echo "Регистрация новых пользователей..."

# Студенты
register_user "ivan_petrov" "ivan.petrov@student.itmo.ru" "password123"
register_user "maria_ivanova" "maria.ivanova@student.itmo.ru" "password123"
register_user "alex_smirnov" "alex.smirnov@student.itmo.ru" "password123"
register_user "dmitry_kozlov" "dmitry.kozlov@student.itmo.ru" "password123"
register_user "olga_sokolova" "olga.sokolova@student.itmo.ru" "password123"

# Преподаватели
register_user "prof_kuznetsov" "prof.kuznetsov@itmo.ru" "password123"
register_user "doc_fedorova" "doc.fedorova@itmo.ru" "password123"

# Администратор
register_user "admin" "admin@itmo.ru" "admin123"

echo ""
echo "=== Обновление балансов ==="

docker exec is-course-work-db psql -U postgres -d university_bets <<EOF
UPDATE wallet SET balance = 5000.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'ivan_petrov');
UPDATE wallet SET balance = 3500.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'maria_ivanova');
UPDATE wallet SET balance = 4200.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'alex_smirnov');
UPDATE wallet SET balance = 2800.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'dmitry_kozlov');
UPDATE wallet SET balance = 6100.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'olga_sokolova');
UPDATE wallet SET balance = 8000.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'prof_kuznetsov');
UPDATE wallet SET balance = 7500.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'doc_fedorova');
UPDATE wallet SET balance = 10000.0 WHERE user_id = (SELECT id FROM application_users WHERE username = 'admin');

SELECT '✓ Балансы обновлены';
EOF

echo ""
echo "=== Готово! ==="
echo ""
echo "Учетные данные:"
echo "  Студенты: ivan_petrov, maria_ivanova, alex_smirnov, dmitry_kozlov, olga_sokolova"
echo "  Преподаватели: prof_kuznetsov, doc_fedorova"
echo "  Администратор: admin / admin123"
echo "  Пароль для всех студентов и преподавателей: password123"
echo ""

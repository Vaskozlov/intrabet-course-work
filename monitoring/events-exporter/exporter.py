#!/usr/bin/env python3
"""
Events Exporter для мониторинга событий букмекерской системы
Экспортирует метрики из PostgreSQL в Prometheus
"""

import os
import time
import psycopg2
from prometheus_client import start_http_server, Gauge, Counter, Info
from datetime import datetime, timedelta
import logging

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Prometheus метрики
events_total = Gauge('betting_events_total', 'Общее количество событий')
events_by_status = Gauge('betting_events_by_status', 'Количество событий по статусу', ['status'])
events_by_category = Gauge('betting_events_by_category', 'Количество событий по категории', ['category'])

bets_total = Gauge('betting_bets_total', 'Общее количество ставок')
bets_amount_total = Gauge('betting_bets_amount_total', 'Общая сумма ставок')
bets_by_event = Gauge('betting_bets_by_event', 'Количество ставок по событию', ['event_id', 'event_title'])

users_total = Gauge('betting_users_total', 'Общее количество пользователей')
users_balance_total = Gauge('betting_users_balance_total', 'Общий баланс всех пользователей')

active_events_count = Gauge('betting_active_events_count', 'Количество активных событий')
completed_events_last_hour = Gauge('betting_completed_events_last_hour', 'Завершенных событий за последний час')

# Database connection parameters
DB_CONFIG = {
    'host': os.getenv('DB_HOST', 'db'),
    'port': os.getenv('DB_PORT', '5432'),
    'database': os.getenv('DB_NAME', 'university_bets'),
    'user': os.getenv('DB_USER', 'postgres'),
    'password': os.getenv('DB_PASSWORD', 'postgres')
}

def get_db_connection():
    """Создает подключение к БД"""
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        return conn
    except Exception as e:
        logger.error(f"Ошибка подключения к БД: {e}")
        raise

def collect_metrics():
    """Собирает метрики из БД"""
    try:
        conn = get_db_connection()
        cur = conn.cursor()

        # Общее количество событий
        cur.execute("SELECT COUNT(*) FROM event")
        events_total.set(cur.fetchone()[0])

        # События по статусам
        cur.execute("""
            SELECT status, COUNT(*)
            FROM event
            GROUP BY status
        """)
        for status, count in cur.fetchall():
            events_by_status.labels(status=status).set(count)

        # События по категориям
        cur.execute("""
            SELECT c.name, COUNT(e.id)
            FROM category c
            LEFT JOIN event e ON e.category_id = c.id
            GROUP BY c.name
        """)
        for category, count in cur.fetchall():
            events_by_category.labels(category=category).set(count)

        # Активные события (0=PLANNED, 1=ONGOING)
        cur.execute("""
            SELECT COUNT(*) FROM event
            WHERE status IN (0, 1)
        """)
        active_events_count.set(cur.fetchone()[0])

        # Завершенные события за последний час (2=COMPLETED)
        cur.execute("""
            SELECT COUNT(*) FROM event
            WHERE status = 2
            AND updated_at > NOW() - INTERVAL '1 hour'
        """)
        completed_events_last_hour.set(cur.fetchone()[0])

        # Общее количество ставок
        cur.execute("SELECT COUNT(*) FROM bet")
        bets_total.set(cur.fetchone()[0])

        # Общая сумма ставок
        cur.execute("SELECT COALESCE(SUM(amount), 0) FROM bet")
        bets_amount_total.set(float(cur.fetchone()[0]))

        # Ставки по событиям (топ-10) - только активные события (0=PLANNED, 1=ONGOING)
        cur.execute("""
            SELECT e.id, e.title, COUNT(b.id) as bet_count
            FROM event e
            LEFT JOIN outcome o ON o.event_id = e.id
            LEFT JOIN bet b ON b.outcome_id = o.id
            WHERE e.status IN (0, 1)
            GROUP BY e.id, e.title
            ORDER BY bet_count DESC
            LIMIT 10
        """)
        for event_id, event_title, count in cur.fetchall():
            bets_by_event.labels(event_id=str(event_id), event_title=event_title).set(count)

        # Общее количество пользователей
        cur.execute('SELECT COUNT(*) FROM application_users')
        users_total.set(cur.fetchone()[0])

        # Общий баланс пользователей
        cur.execute("SELECT COALESCE(SUM(balance), 0) FROM wallet")
        users_balance_total.set(float(cur.fetchone()[0]))

        cur.close()
        conn.close()

        logger.info("Метрики успешно собраны")

    except Exception as e:
        logger.error(f"Ошибка сбора метрик: {e}")

def main():
    """Основная функция"""
    port = int(os.getenv('EXPORTER_PORT', '9101'))
    interval = int(os.getenv('SCRAPE_INTERVAL', '15'))

    logger.info(f"Запуск Events Exporter на порту {port}")
    logger.info(f"Интервал сбора метрик: {interval} секунд")

    # Запуск HTTP сервера для Prometheus
    start_http_server(port)

    # Бесконечный цикл сбора метрик
    while True:
        try:
            collect_metrics()
        except Exception as e:
            logger.error(f"Ошибка в главном цикле: {e}")

        time.sleep(interval)

if __name__ == '__main__':
    main()

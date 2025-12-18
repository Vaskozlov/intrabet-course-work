FROM python:3.11-alpine

WORKDIR /app

# Установка зависимостей для psycopg2
RUN apk update && \
    apk add --no-cache postgresql-dev gcc musl-dev linux-headers wget && \
    rm -rf /var/cache/apk/*

# Копирование requirements
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Копирование exporter
COPY exporter.py .

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:9101/ || exit 1

EXPOSE 9101

CMD ["python", "exporter.py"]

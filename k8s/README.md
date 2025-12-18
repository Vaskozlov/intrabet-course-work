# Kubernetes манифесты для University Bets

Эта директория содержит все необходимые манифесты для развертывания приложения University Bets в k3s.

## Структура файлов

- `00-namespace.yaml` - Namespace для приложения
- `01-configmap.yaml` - Конфигурация приложения (не чувствительные данные)
- `02-secret.yaml` - Секреты (пароли, JWT ключи)
- `03-postgres-pvc.yaml` - PersistentVolumeClaim для PostgreSQL
- `05-postgres-statefulset.yaml` - StatefulSet для PostgreSQL
- `06-postgres-service.yaml` - Service для PostgreSQL
- `07-backend-deployment.yaml` - Deployment для backend (Spring Boot)
- `08-backend-service.yaml` - Service для backend
- `09-frontend-nginx-configmap.yaml` - ConfigMap с конфигурацией Nginx
- `10-frontend-deployment.yaml` - Deployment для frontend (React + Nginx)
- `11-frontend-service.yaml` - Service для frontend (NodePort для доступа извне)

## Быстрое развертывание

### Создание ConfigMap с SQL скриптами

```bash
kubectl create namespace university-bets

kubectl create configmap postgres-init-scripts \
  --from-file=create_db.sql=../scripts/create_db.sql \
  --from-file=insert_data.sql=../scripts/insert_data.sql \
  --from-file=place_bet.sql=../scripts/place_bet.sql \
  -n university-bets
```

### Применение манифестов

```bash
kubectl apply -f .
```

### Проверка статуса

```bash
kubectl get pods -n university-bets
kubectl get svc -n university-bets
```

## Доступ к приложению

После развертывания приложение будет доступно по адресу:
- `http://<IP-адрес-сервера>:30080`

## Примечания

- Перед применением манифестов убедитесь, что Docker образы собраны и загружены в registry
- По умолчанию используется локальный registry на `localhost:5000`
- Если используете импорт образов напрямую в k3s, измените image в файлах `07-backend-deployment.yaml` и `10-frontend-deployment.yaml`

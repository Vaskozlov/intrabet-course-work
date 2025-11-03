= Rest API

== GET

== /api/auth/login

Принимает два параметра через URL
- login: string
- password: string

Результат работы аналогичен register (только логин и пароль не будет проверяться на соответствие правилу)

== /api/events/list

Требует авторизации?

Принимает два параметра через URL
- date
- type

=== Успех

Возвращает json, содержащий список event

=== Ошибка авторизации?

== /api/bets/history

Опциональные URL параметры:
- user_id
- date
- status

Возвращает историю ставок. По умолчанию для текущего пользователя, если запрос делает администратор, то при помощи user_id можно посмотреть историю ставок любого пользователя.

```json
{
  "bets" : []
}
```

=== Ошибки

Не авторизован, пользователя не существует, нет доступа

== /api/users/list

Возвращает список пользователей, доступен только администраторам

```json
{
  "users": [] 
}
```

#pagebreak()

== POST

== /api/auth/register

Принимает json:
```json
{
  "email": string
  "login": string
  "password": string
}
```

=== Успех

Возвращает json:
```json
{
  "token_type": "bearer"
  "user": "user" | "admin"
}
```

Возвращаемые заголовки:
- Authorization ("Bearer " + token)
- Cache-Control (no-store)
- Pragma (no-cache)
- Access-Control-Expose-Headers (Authorization)

=== Ошибка (пользователь существует)

Код CONFLICT

Возвращает json (в целом им можно не пользоваться):
```json
{
  "reason": string
}
```

=== Ошибка

Случаи: нет логина или пароля, недопустимый логин или пароль, слишком много попыток

Код BAD_REQUEST

Возвращает json:
```json
{
  "reason": string
}
```

== /api/events/create

Принимает json:

```json
{
  "name": string
  "description": string
  "start_time": timestamp
  "end_time": timestamp
  "category_id": category,
  "outcomes"4: []
}
```

=== Ограничения
end_time >= start_time
len(name) < 100

=== В случае успеха 

Код CREATED

Возвращает json

```json
{
  "event_id": int
}
```

== /api/bets/place

Принимает json
```json
{
  "event_id": int,
  "outcome_id": int,
  "wallet_id": int, // do we need this?
  "sum": decimal(10, 2)
}
```

=== Успех
Код OK

=== Ошибка 

Возникает, если события не существует, уже завершено, пользователь является автором события, недостаточно средств на балансе.

Код BAD_REQUEST

Возвращает json:
```json
{
  "reason": string
}
```

== /api/balance/deposit

```json
{
  "user_wallet_id": int,
  "sum": decimal(10, 2),
  "transaction_id": string
}
```

== /api/users/status

Доступен только администраторам. Позволяет изменить статус пользователя.

```json
{
  "status": "blocked" | "none",
  "role": "user" | "admin"
}
```

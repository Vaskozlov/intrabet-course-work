
#set heading(numbering: "1.")
#show heading: it => text(it.body)
#outline()

#pagebreak()

= Rest API

== Structures

=== decimal
decimal(n, m) это просто int, в котором последние m цифр это дробь

=== User

```json
{
  "email": string,
  "login": string,
  "role": "user" | "admin",
  "wallet_id": int
}
```

=== Outcome

```json
{
  "event_id": int,
  "name": string,
  "status": "winner" | "looser" | "none"
}
```

== GET

== /api/events/list

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

Возвращает:

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

== /api/auth/login

Принимает json
```json
{
  "login": string | "email": string
  "password": string
}
```

Результат работы аналогичен register (только логин и пароль не будет проверяться на соответствие правилу)


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
  "user": User
}
```

Токен живет 3 дня.

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
  "outcomes": []
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
  "sum": decimal(10, 2),
  "transaction_id": string
}
```

== /api/users/status

Доступен только администраторам. Позволяет изменить статус пользователя.

```json
{
  "user_id": int,
  "status": "blocked" | "none",
  "role": "user" | "admin"
}
```

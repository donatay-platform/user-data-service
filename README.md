# DonationSpace Backend

User Data Service для Donatay Platform: Java 21, Spring Boot 3, WebFlux, R2DBC, PostgreSQL, Liquibase, OpenAPI.

Сервис отвечает только за профильные и публичные пользовательские данные. Регистрация, логин, JWT, MFA и security audit вынесены в `auth-service`.

## Требования

- JDK 21.
- Docker и Docker Compose — для локального PostgreSQL.

Maven устанавливать отдельно не нужно: в проект добавлен Maven Wrapper.

## Быстрый запуск тестов

```bash
./mvnw test
```

Если на машине установлено несколько JDK, явно укажи JDK 21:

```bash
export JAVA_HOME=/path/to/jdk-21
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw test
```

## Локальная база данных

```bash
cd donation-app
docker compose up -d
```

По умолчанию локальный PostgreSQL доступен на порту `5435`.

На этапе MVP используем одну PostgreSQL-базу для нескольких backend-сервисов, но каждый сервис живёт в своей схеме:

```text
auth-service      -> schema auth_service
user-data-service -> schema user_data_service
```

Этот сервис использует схему `user_data_service`.

## Запуск приложения локально

Сначала подними локальную базу:

```bash
cd donation-app
docker compose up -d
cd ..
```

Потом запусти приложение с профилем `local`:

```bash
./mvnw -pl donation-app spring-boot:run -Dspring-boot.run.profiles=local
```

Локальный профиль использует безопасные dev-значения. Для production нужно явно передать секреты через env-переменные.

Минимальные production-переменные:

```bash
SPRING_LIQUIBASE_URL=jdbc:postgresql://host:5432/donation_db
SPRING_LIQUIBASE_USER=...
SPRING_LIQUIBASE_PASSWORD=...
SPRING_R2DBC_URL=r2dbc:postgresql://host:5432/donation_db
SPRING_R2DBC_USERNAME=...
SPRING_R2DBC_PASSWORD=...
JWT_SECRET=...
APP_CORS_ALLOWED_ORIGINS=https://your-frontend-domain.example
```

Для локальной разработки CORS по умолчанию разрешён только для:

```text
http://localhost:3000
http://localhost:5173
http://127.0.0.1:3000
http://127.0.0.1:5173
```

## JWT contract

`user-data-service` доверяет access token, выпущенному `auth-service`.

Согласованный контракт:

```text
signing secret: JWT_SECRET, общий для backend-сервисов
issuer:         JWT_ISSUER, по умолчанию donatay-auth-service
subject:        user UUID
role claim:     ROLE_USER и будущие роли
TTL:            900 секунд по умолчанию
```

Сервис берёт `subject` из JWT и ищет профиль по UUID.

## API

В этом сервисе остались только user-data endpoints:

```text
GET /api/profile
PUT /api/profile
GET /api/version
```

Auth endpoints находятся в `auth-service`.

## Internal API

Для MVP `auth-service` синхронно создаёт профиль после регистрации через внутренний endpoint:

```text
POST /internal/users
Header: X-Internal-Token: <INTERNAL_SERVICE_TOKEN>
Body: { "uuid": "...", "email": "user@example.com" }
```

Этот endpoint не предназначен для frontend. Значение `INTERNAL_SERVICE_TOKEN` должно совпадать с настройкой в `auth-service`.

Endpoint идемпотентный: повторный запрос с тем же `uuid` считается успешным и не создаёт дубль профиля.

Swagger UI после запуска:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

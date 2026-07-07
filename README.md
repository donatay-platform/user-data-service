# DonationSpace Backend

Backend-сервис DonationSpace: Java 21, Spring Boot 3, WebFlux, R2DBC, PostgreSQL, Liquibase, OpenAPI.

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
```

Swagger UI после запуска:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

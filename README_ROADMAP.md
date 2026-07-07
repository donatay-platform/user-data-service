# 🗺️ DonationSpace — MVP Roadmap

Файл нужен как рабочий чек-лист. После каждого пункта мы будем отмечать прогресс прямо здесь.

Проект: сервис донатов для стримеров.

Текущий фокус: **сначала укрепляем фундамент, потом добавляем донаты и платежи**.

---

## ✅ Правила выполнения задач

Каждая новая задача считается выполненной только если:

- [ ] код написан чисто и без временных костылей;
- [ ] архитектура не ломает разделение слоёв;
- [ ] OpenAPI-спецификация обновлена, если меняется API;
- [ ] Liquibase-миграции добавлены, если меняется БД;
- [ ] новые Liquibase changeset id уникальны и начинаются с первых 10 цифр текущего epoch millis, например `1762500000-create-table`;
- [ ] тесты добавлены или обновлены;
- [ ] покрытие JaCoCo не ниже 70%;
- [ ] проверяется не только line/instruction coverage, но и branch coverage;
- [ ] локально проходят тесты;
- [ ] изменения закоммичены;
- [ ] изменения запушены в нужный GitHub-репозиторий.

---

## 📌 Обозначения

- [x] выполнено;
- [ ] не выполнено;
- `backend` — репозиторий `donatay-labudi-labuday`;
- `frontend` — репозиторий `donatay-client`.

---

# 🎯 MVP v1

MVP — это минимальная версия продукта, в которой стример может зарегистрироваться, настроить публичную страницу, а зритель может отправить тестовый донат через mock-платёж.

Реальные деньги подключаем только после того, как базовая модель донатов и платежей будет стабильной.

---

# 🔎 Контрольные срезы, регрессия и версии

Чтобы не копить ошибки до конца, после крупных блоков делаем отдельный контрольный срез.

## Срез A — Backend foundation

Когда выполняем: после этапа `1.x`.

- [ ] Локально поднять PostgreSQL через Docker Compose.
- [ ] Запустить backend с профилем `local`.
- [ ] Проверить Swagger UI.
- [ ] Пройти ручной happy path:
  - регистрация;
  - логин;
  - получение профиля;
  - обновление профиля.
- [ ] Запустить полный backend regression test suite.
- [ ] Проверить JaCoCo report.
- [ ] Поднять patch-версию backend-сервиса.
- [ ] Зафиксировать результат среза в roadmap.

## Срез B — Auth/MFA hardening

Когда выполняем: после этапов `2.x` и `3.x`.

- [ ] Проверить регистрацию и логин.
- [ ] Проверить защищённые endpoints без токена.
- [ ] Проверить защищённые endpoints с валидным токеном.
- [ ] Проверить MFA happy path.
- [ ] Проверить MFA negative cases.
- [ ] Провести regression по auth/profile API.
- [ ] Поднять minor/patch-версию backend-сервиса по масштабу изменений.

## Срез C — Donation + Mock payments

Когда выполняем: после этапов `4.x` и `5.x`.

- [ ] Локально пройти полный сценарий mock-доната.
- [ ] Проверить идемпотентность webhook-ов.
- [ ] Проверить историю донатов.
- [ ] Проверить статусы платежей.
- [ ] Провести backend regression.
- [ ] Поднять minor-версию backend-сервиса.

## Срез D — Frontend MVP

Когда выполняем: после этапа `6.x`.

- [ ] Локально поднять frontend и backend вместе.
- [ ] Пройти полный UI happy path.
- [ ] Проверить основные ошибки форм.
- [ ] Запустить frontend unit/component tests.
- [ ] Запустить e2e happy path.
- [ ] Поднять версию frontend-приложения.

## Срез E — MVP release candidate

Когда выполняем: перед подключением реальной платёжной системы.

- [ ] Провести полный ручной regression.
- [ ] Провести полный автотестовый regression.
- [ ] Проверить OpenAPI-контракт.
- [ ] Проверить Liquibase-миграции с чистой БД.
- [ ] Проверить security baseline.
- [ ] Поднять версии backend и frontend.
- [ ] Создать Git tag для MVP release candidate.

---

# 0. Стартовая ревизия проекта

## 0.1. Репозитории

- [x] `backend`: репозиторий доступен.
- [x] `frontend`: репозиторий доступен.
- [x] Проведён первичный аудит текущего состояния.
- [x] Выявлено, что проект находится на стадии прототипа.
- [x] Решено сначала укрепить backend-фундамент.

## 0.2. Текущий backend-функционал

- [x] Java 21.
- [x] Spring Boot 3.
- [x] Spring WebFlux.
- [x] Spring Security WebFlux.
- [x] R2DBC.
- [x] PostgreSQL.
- [x] Liquibase.
- [x] OpenAPI contract module.
- [x] JWT-авторизация.
- [x] Регистрация пользователя.
- [x] Логин пользователя.
- [x] Профиль пользователя.
- [x] MFA через Google Authenticator.
- [x] MFA через SMS-симуляцию.
- [x] Базовые unit/web tests.

## 0.3. Текущий frontend-функционал

- [x] Есть HTML-прототип интерфейса.
- [x] Есть регистрация и логин через API.
- [x] Есть экран профиля.
- [x] Есть экран настройки MFA.
- [ ] Нет полноценного frontend-приложения.
- [ ] Нет TypeScript.
- [ ] Нет сборщика.
- [ ] Нет компонентной архитектуры.
- [ ] Нет generated API client.

---

# 1. Backend foundation hardening

Цель этапа: сделать backend безопасной и удобной основой для дальнейшей разработки.

## 1.1. Инструменты проекта

- [x] Добавить Maven Wrapper: `mvnw`, `mvnw.cmd`, `.mvn/wrapper`.
- [x] Проверить, что проект запускается командой `./mvnw test`.
- [x] Добавить/актуализировать `.gitignore` для Java/Maven/IDE.
- [x] Добавить короткую инструкцию запуска backend в README.

## 1.2. Конфигурация и секреты

- [x] Убрать реальные/похожие на реальные секреты из `application.yml`.
- [x] Перевести JWT secret на обязательную env-переменную.
- [x] Разделить профили `local`, `test`, `prod`.
- [x] Добавить безопасные local default-значения только для локальной разработки.
- [x] Проверить, что production-профиль не стартует без обязательных секретов.

## 1.3. CORS и security baseline

- [x] Убрать `allowedOriginPatterns("*")` вместе с credentials.
- [x] Разрешить CORS только для нужных origin-ов через конфигурацию.
- [x] Добавить отдельные настройки CORS для `local` и `prod`.
- [x] Проверить protected endpoints без токена.
- [x] Проверить public endpoints без токена.

## 1.4. JaCoCo и качество покрытия

- [x] Убрать лишние исключения из JaCoCo.
- [x] Оставить исключения только для generated DTO/API, bootstrap-класса и простых моделей при необходимости.
- [x] Добавить проверку `BRANCH` coverage не ниже 70%.
- [x] Добавить отчёт покрытия в стандартный lifecycle.
- [x] Проверить, что тесты реально покрывают use case-и, security и web-слой.

---

# 2. Service split и Clean Architecture refactoring

Цель этапа: разделить backend на сервисы по ответственности и сделать так, чтобы бизнес-логика не зависела от web, JWT и инфраструктуры.

## 2.0. Разделение backend-сервисов

- [x] Создать репозиторий `donatay-platform/auth-service`.
- [x] Настроить доступ агента к `auth-service`.
- [x] Инициализировать базовый Spring Boot 3 + Java 21 каркас `auth-service`.
- [x] Добавить Maven Wrapper в `auth-service`.
- [x] Добавить профили `local`, `test`, `prod` в `auth-service`.
- [x] Добавить базовый `/api/version` в `auth-service`.
- [x] Настроить JaCoCo instruction + branch coverage 70%+ в `auth-service`.
- [x] Добавить первый тест `auth-service` и проверить `./mvnw test`.
- [x] Уточнить целевое имя текущего backend-сервиса: `user-data-service`.
- [x] Создать/перенести репозиторий `user-data-service` в `donatay-platform`.
- [x] Настроить отдельные PostgreSQL-схемы под backend-сервисы в одной БД:
  - `auth_service`;
  - `user_data_service`.
- [x] Разделить OpenAPI-контракт `user-data-service`: убрать auth/MFA endpoints из user-data API.
- [x] Перенести базовую регистрацию/логин/JWT/MFA-login из текущего backend в `auth-service`.
- [x] Перенести setup/verify MFA и SMS-code flow в `auth-service`.
- [x] Перенести security audit в `auth-service`.
- [x] Оставить профиль пользователя и публичные данные в `user-data-service`.
- [x] Удалить старые auth/MFA controllers и use case-и из `user-data-service`.
- [x] Убрать смену email/password из profile API `user-data-service`.
- [x] Настроить взаимодействие сервисов через JWT subject/user UUID.
- [x] Добавить синхронное создание профиля в `user-data-service` после регистрации в `auth-service`.
- [x] Защитить internal profile endpoint через `INTERNAL_SERVICE_TOKEN`.
- [x] Сделать `POST /internal/users` идемпотентным по `uuid` пользователя.
- [ ] Обновить frontend API URLs после разделения сервисов.

## 2.1. Clean Architecture внутри сервисов

Цель этапа: сделать так, чтобы бизнес-логика не зависела от web, JWT и инфраструктуры.

## 2.1. Разделение слоёв

- [ ] Утвердить структуру пакетов:
  - `domain`;
  - `application`;
  - `application.port`;
  - `infrastructure.persistence`;
  - `infrastructure.security`;
  - `infrastructure.payment`;
  - `infrastructure.web`.
- [ ] Перенести use case-и в application-слой.
- [ ] Убрать зависимости use case-ов от generated web DTO.
- [ ] Убрать зависимости use case-ов от конкретного JWT-провайдера.
- [ ] Убрать ручное создание `new GoogleAuthService()` внутри use case-а.

## 2.2. Application ports

- [ ] Добавить порт `TokenService`.
- [ ] Добавить порт `PasswordHasher` или оставить Spring `PasswordEncoder` только за application-границей после оценки.
- [ ] Добавить порт `MfaSecretGenerator`.
- [ ] Добавить порт `MfaCodeVerifier`.
- [ ] Добавить порт `SmsSender`.
- [ ] Реализовать инфраструктурные адаптеры для этих портов.

## 2.3. Domain model cleanup

- [ ] Заменить строковые роли на enum/value object.
- [ ] Заменить строковые типы MFA на enum/value object.
- [ ] Убрать лишнюю изменяемость из domain-моделей там, где это мешает безопасности.
- [ ] Проверить, что domain не зависит от Spring.

---

# 3. Auth и MFA hardening

Цель этапа: сделать авторизацию безопаснее до подключения денег.

## 3.1. JWT

- [x] Использовать `uuid` пользователя как JWT subject вместо email.
- [x] Добавить claims минимального размера.
- [x] Проверить issuer/secret/role contract между `auth-service` и `user-data-service`.
- [x] Проверить expiration.
- [x] Добавить тесты на битый токен и неверный issuer.
- [x] Обновить security filter под UUID subject.

## 3.2. MFA flow

- [ ] Заменить MFA-login по email на flow через `challengeId`.
- [ ] Добавить сущность/таблицу для MFA challenge или временное хранилище с TTL.
- [ ] Добавить TTL для MFA challenge.
- [ ] Добавить ограничение числа попыток ввода MFA-кода.
- [ ] Убрать возврат SMS-кода из HTTP-ответа.
- [ ] В dev-профиле разрешить логировать SMS-код.
- [ ] В prod-профиле запретить логировать SMS-код.

## 3.3. Rate limiting и аудит

- [ ] Добавить ограничение попыток логина.
- [ ] Добавить ограничение отправки SMS-кодов.
- [ ] Добавить аудит важных security-событий:
  - регистрация;
  - логин;
  - неудачный логин;
  - включение MFA;
  - смена email;
  - смена пароля.

---

# 4. Donation domain

Цель этапа: добавить бизнес-сущности донатов без реального платёжного провайдера.

## 4.1. Схема БД

- [ ] Добавить Liquibase changeset для `creator_profiles`.
- [ ] Добавить Liquibase changeset для `donation_pages`.
- [ ] Добавить Liquibase changeset для `donations`.
- [ ] Добавить Liquibase changeset для `payment_attempts`.
- [ ] Добавить Liquibase changeset для `payment_webhook_events`.
- [ ] Добавить индексы по публичным slug/uuid/status/date.

## 4.2. Domain-модели

- [ ] Добавить `CreatorProfile`.
- [ ] Добавить `DonationPage`.
- [ ] Добавить `Donation`.
- [ ] Добавить `PaymentAttempt`.
- [ ] Добавить enum `DonationStatus`.
- [ ] Добавить enum `PaymentStatus`.
- [ ] Деньги хранить как `amountMinor + currency`, не как `double`.

## 4.3. Use cases

- [ ] Создать/обновить профиль стримера.
- [ ] Создать/обновить публичную страницу донатов.
- [ ] Получить публичную страницу по slug.
- [ ] Создать донат.
- [ ] Получить историю донатов стримера.
- [ ] Получить детали доната.

## 4.4. OpenAPI

- [ ] Добавить API публичной страницы стримера.
- [ ] Добавить API создания доната.
- [ ] Добавить API истории донатов в кабинете стримера.
- [ ] Добавить request/response schemas.
- [ ] Добавить ошибки `404`, `400`, `409`, `422` там, где нужно.

---

# 5. Mock payments

Цель этапа: сделать полный сценарий доната без реальных денег.

## 5.1. Payment ports

- [ ] Добавить порт `PaymentProvider`.
- [ ] Добавить команду `CreatePaymentCommand`.
- [ ] Добавить результат `CreatePaymentResult`.
- [ ] Добавить обработку webhook payload через application-слой.

## 5.2. Mock provider

- [ ] Добавить `MockPaymentProvider`.
- [ ] Создавать mock payment URL.
- [ ] Добавить endpoint симуляции успешной оплаты.
- [ ] Добавить endpoint симуляции отказа оплаты.
- [ ] Добавить идемпотентную обработку повторного webhook.

## 5.3. Donation payment flow

- [ ] При создании доната создавать payment attempt.
- [ ] Возвращать ссылку оплаты.
- [ ] После successful webhook переводить донат в `PAID`.
- [ ] После failed webhook переводить донат в `FAILED`.
- [ ] Повторный webhook не должен ломать состояние.

---

# 6. Frontend MVP

Цель этапа: заменить HTML-прототип нормальным frontend-приложением.

## 6.1. Базовая структура

- [ ] Инициализировать Vite + React + TypeScript.
- [ ] Настроить ESLint/Prettier.
- [ ] Настроить env-переменную для API URL.
- [ ] Подключить generated API client из OpenAPI.
- [ ] Разбить код на страницы и компоненты.

## 6.2. Auth UI

- [ ] Экран регистрации.
- [ ] Экран логина.
- [ ] Экран MFA challenge.
- [ ] Безопасное хранение токена после оценки вариантов.
- [ ] Обработка ошибок API понятным текстом.

## 6.3. Profile UI

- [ ] Экран профиля стримера.
- [ ] Редактирование профиля.
- [ ] Настройка публичной страницы донатов.
- [ ] Копирование публичной ссылки.

## 6.4. Public donation UI

- [ ] Публичная страница стримера по slug.
- [ ] Форма доната:
  - ник зрителя;
  - сумма;
  - сообщение;
  - email для чека/уведомлений, если нужен.
- [ ] Переход на mock payment URL.
- [ ] Экран успешной оплаты.
- [ ] Экран неуспешной оплаты.

## 6.5. Frontend tests

- [ ] Unit/component tests для ключевых компонентов.
- [ ] Tests для форм и валидации.
- [ ] Tests для API error states.
- [ ] Минимальный e2e happy path после появления стабильного UI.

---

# 7. Интеграция реальной платёжной системы

Цель этапа: подключить реальные деньги только после mock-flow.

## 7.1. Выбор провайдера

- [ ] Подтвердить юридическую модель проекта:
  - самозанятый;
  - ИП;
  - ООО;
  - платформа/маркетплейс.
- [ ] Проверить требования по фискализации.
- [ ] Проверить необходимость сплитования платежей.
- [ ] Проверить необходимость рекуррентных платежей.
- [ ] Финально выбрать провайдера.

Предварительная рекомендация для MVP: **ЮKassa**.

## 7.2. YooKassa adapter

- [ ] Добавить `YooKassaPaymentProvider`.
- [ ] Создание платежа.
- [ ] Проверка подписи/подлинности webhook, если применимо к выбранной схеме.
- [ ] Идемпотентность запросов к провайдеру.
- [ ] Обработка статусов платежей.
- [ ] Возвраты, если нужны в MVP.
- [ ] Интеграционные тесты через sandbox/test mode.

---

# 8. OBS widgets и real-time — после MVP

- [ ] SSE/WebSocket канал донатов.
- [ ] Виджет для OBS.
- [ ] Уникальный widget token.
- [ ] Анимация доната.
- [ ] Text-To-Speech.
- [ ] Настройки темы виджета.

---

# 9. Dashboard и аналитика — после MVP

- [ ] История донатов.
- [ ] Фильтры по датам/status.
- [ ] Сумма за день/неделю/месяц.
- [ ] Топ донатеров.
- [ ] Экспорт CSV.
- [ ] Графики дохода.

---

# 10. Текущий ближайший порядок работ

Начинаем строго сверху вниз:

- [x] `1.1` Добавить Maven Wrapper и проверить запуск тестов.
- [x] `1.2` Убрать секреты из конфигурации.
- [x] `1.3` Закрыть CORS.
- [x] `1.4` Настроить честное покрытие JaCoCo.
- [x] `2.0` Создать и инициализировать `auth-service`.
- [ ] `2.1` Развязать use case-и от web DTO и infrastructure.
- [x] `3.1` Согласовать JWT contract между `auth-service` и `user-data-service`.
- [x] `3.1.1` Синхронизировать создание user-data профиля при регистрации.
- [ ] `3.2` Переделать MFA flow.
- [ ] `4.x` Добавить домен донатов.
- [ ] `5.x` Добавить mock payments.
- [ ] `6.x` Переделать frontend.

---

## 🧾 Журнал изменений roadmap

- [x] 2026-07-07 — roadmap переписан под MVP-чеклист, добавлены этапы hardening, clean architecture, donation domain, mock payments, frontend MVP и будущая интеграция YooKassa.
- [x] 2026-07-07 — выполнен пункт `1.1`: добавлен Maven Wrapper, README запуска backend, актуализирован `.gitignore`, исправлен web-slice тест авторизации и добавлены тесты exception handler-а для прохождения текущего JaCoCo-порога.
- [x] 2026-07-07 — добавлены контрольные срезы: локальная проверка, regression, обновление версии и release-candidate checkpoints.
- [x] 2026-07-07 — выполнен пункт `1.2`: конфигурация разделена на `local`, `test`, `prod`, секреты убраны из общего конфига, production-секреты требуют env-переменные.
- [x] 2026-07-07 — выполнен пункт `1.3`: CORS закрыт на явный список origin-ов, добавлены настройки origin-ов по профилям и тесты public/protected endpoints + CORS preflight.
- [x] 2026-07-07 — выполнен пункт `1.4`: JaCoCo теперь проверяет instruction и branch coverage 70%+, убраны лишние исключения, добавлены тесты use case, web, security, MFA и version-слоёв.
- [x] 2026-07-07 — создан и инициализирован отдельный репозиторий `donatay-platform/auth-service` для будущего выноса регистрации, логина, JWT, MFA и security audit.
- [x] 2026-07-07 — в `auth-service` перенесены базовые auth endpoints: регистрация, логин, MFA-login, JWT, auth persistence skeleton и Liquibase-схема `auth_users`.
- [x] 2026-07-07 — текущий backend-репозиторий переименован в `donatay-platform/user-data-service`; для backend-сервисов введены отдельные PostgreSQL-схемы `auth_service` и `user_data_service` в одной БД.
- [x] 2026-07-07 — добавлено правило именования новых Liquibase changeset id через первые 10 цифр текущего epoch millis.
- [x] 2026-07-07 — в `auth-service` перенесены setup Google MFA, setup SMS MFA, отправка SMS-кода и verify/enable MFA.
- [x] 2026-07-07 — в `auth-service` добавлен security audit: таблица `auth_service.security_audit_events`, порт `SecurityAuditLog`, persistence adapter и запись событий регистрации, логина и MFA.
- [x] 2026-07-07 — `user-data-service` очищен от старой auth/MFA-логики: удалены auth/MFA controllers/use cases, user-data OpenAPI оставляет только profile/version, profile update больше не меняет email/password.
- [x] 2026-07-07 — согласован JWT contract между сервисами: общий `JWT_SECRET`, общий `JWT_ISSUER`, `subject=userUuid`, claim `role`, TTL 900 секунд по умолчанию; добавлены тесты на issuer, битый и просроченный токен.
- [x] 2026-07-07 — добавлена синхронизация регистрации: `auth-service` после создания auth user вызывает `user-data-service` `POST /internal/users`, endpoint защищён `INTERNAL_SERVICE_TOKEN`.
- [x] 2026-07-07 — `POST /internal/users` сделан идемпотентным: повторный запрос с тем же `uuid` возвращает существующий профиль и не создаёт дубль.

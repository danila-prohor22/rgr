# Sports Inventory REST API

Spring Boot REST API для системы учёта спортивного инвентаря. Проект хранит данные о местах хранения, инвентаре, операциях выдачи/возврата/ремонта/списания, пользователях и загруженных файлах.

---

## Запуск в Docker

### Предварительные требования
- Docker Desktop или Docker Engine + Docker Compose

### Команды

```bash
cd sports-inventory

docker-compose up --build

docker-compose up --build -d

docker-compose down

docker-compose down -v
```

После запуска API доступно на `http://localhost:8080`.

### Параметры подключения к MySQL

| Параметр | Значение |
|----------|----------|
| Host | localhost:3306 |
| Database | sports_inventory |
| User | root |
| Password | rootPass123 |

---

## Аутентификация

API использует JWT Bearer токены.

### Регистрация

```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "name": "Иван Иванов",
  "phone": "+79001234567"
}
```

### Вход

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

Ответ содержит `token`. Используйте его в заголовке:

```http
Authorization: Bearer <token>
```

---

## Места хранения инвентаря

### Список активных мест хранения

```http
GET /api/locations?page=0&size=10
```

### Место хранения по ID

```http
GET /api/locations/{id}
```

### Места хранения по виду спорта

```http
GET /api/locations/sport/Football
```

### Места хранения по минимальной вместимости

```http
GET /api/locations/capacity?minCapacity=50
```

### Создать место хранения, только ADMIN

```http
POST /api/locations
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "name": "Склад футбольного инвентаря",
  "address": "ул. Спортивная, 10",
  "sportType": "Football",
  "capacity": 120,
  "active": true
}
```

---

## Спортивный инвентарь

### Список активного инвентаря

```http
GET /api/equipment?page=0&size=10
```

### Инвентарь по ID

```http
GET /api/equipment/{id}
```

### Инвентарь в конкретном месте хранения

```http
GET /api/equipment/location/{storageLocationId}
```

### Инвентарь по категории

```http
GET /api/equipment/category/Ball
```

### Доступный инвентарь

```http
GET /api/equipment/available
```

### Создать инвентарь, только ADMIN

```http
POST /api/equipment/location/{storageLocationId}
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "name": "Футбольный мяч Adidas",
  "inventoryCode": "BALL-001",
  "description": "Мяч для тренировок",
  "category": "Ball",
  "conditionStatus": "GOOD",
  "quantityTotal": 20,
  "quantityAvailable": 20,
  "replacementCost": 3500.00,
  "active": true
}
```

---

## Операции учёта инвентаря

Типы операций:

```text
ISSUE       — выдача инвентаря
RETURN      — возврат инвентаря
MAINTENANCE — передача в ремонт/обслуживание
WRITE_OFF   — списание
```

Статусы операций:

```text
CREATED → APPROVED → COMPLETED
    ↓
CANCELLED
```

### Создать операцию

```http
POST /api/transactions
Authorization: Bearer <token>
Content-Type: application/json

{
  "storageLocationId": 1,
  "type": "ISSUE",
  "responsiblePerson": "Петров Пётр",
  "comment": "Выдача для тренировки",
  "items": [
    { "equipmentId": 1, "quantity": 5, "notes": "Группа А" },
    { "equipmentId": 2, "quantity": 3, "notes": "Манишки" }
  ]
}
```

### Мои операции

```http
GET /api/transactions/my?page=0&size=10
Authorization: Bearer <token>
```

### Конкретная моя операция

```http
GET /api/transactions/my/{id}
Authorization: Bearer <token>
```

### Операции по статусу, только ADMIN

```http
GET /api/transactions/status/CREATED
Authorization: Bearer <admin_token>
```

### Операции по месту хранения, только ADMIN

```http
GET /api/transactions/location/{storageLocationId}
Authorization: Bearer <admin_token>
```

### Операции за период, только ADMIN

```http
GET /api/transactions/period?from=2024-01-01T00:00:00&to=2024-12-31T23:59:59
Authorization: Bearer <admin_token>
```

### Изменить статус операции, только ADMIN

```http
PATCH /api/transactions/{id}/status?status=APPROVED
Authorization: Bearer <admin_token>
```

### Отменить свою операцию

```http
PATCH /api/transactions/{id}/cancel
Authorization: Bearer <token>
```

---

## Загрузка и обработка файлов

Файлы сохраняются на диск, а метаданные — в таблицу `stored_files`. Имя файла очищается от небезопасных частей, файл получает внутреннее UUID-имя, размер проверяется на лимит 10MB.

### Загрузка файла до 10MB

```http
POST /api/files/upload
Content-Type: multipart/form-data

form-data:
file: <binary>
```

Пример через curl:

```bash
curl -X POST http://localhost:8080/api/files/upload \
  -F "file=@inventory-import.xlsx"
```

### Скачивание файла

```http
GET /api/files/{id}
```

### Удаление файла

```http
DELETE /api/files/{id}
```

---

## Основные сущности

| Сущность | Описание |
|----------|----------|
| `User` | Пользователь системы, роли `USER` и `ADMIN` |
| `StorageLocation` | Место хранения спортивного инвентаря |
| `Equipment` | Единица или группа спортивного инвентаря |
| `InventoryTransaction` | Операция учёта: выдача, возврат, ремонт, списание |
| `InventoryTransactionItem` | Строка операции с количеством конкретного инвентаря |
| `StoredFile` | Метаданные загруженного файла |

---

## Важные настройки

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

app:
  files:
    upload-dir: ${FILE_UPLOAD_DIR:uploads}
```


# WalletX

A production-grade **distributed digital wallet system** built with Spring Boot microservices. Covers the full backend engineering spectrum — authentication, financial transactions, event-driven architecture, API gateway, resilience patterns, and distributed observability.

---

## Architecture

```
                        ┌─────────────────────────────────────┐
                        │           Client / Postman           │
                        └──────────────┬──────────────────────┘
                                       │ HTTP
                                       ▼
                        ┌─────────────────────────────────────┐
                        │           API Gateway :8080          │
                        │   JWT Validation · CORS · Routing   │
                        │   Circuit Breaker (Resilience4j)    │
                        └────┬─────────────────┬──────────────┘
                             │                 │
               /api/auth/**  │                 │  /api/wallet/**
                             ▼                 ▼
              ┌──────────────────┐   ┌──────────────────────┐
              │  Auth Service    │   │   Wallet Service      │
              │     :8081        │   │       :8082           │
              │                  │   │                       │
              │ · Signup/Login   │   │ · Deposit/Withdraw    │
              │ · JWT generation │   │ · Transfer            │
              │ · Refresh tokens │   │ · Transaction history │
              │ · BCrypt hashing │   │ · Redis caching       │
              └────────┬─────────┘   └──────────┬────────────┘
                       │                        │
                       ▼                        │ Kafka Event
              ┌──────────────┐                  ▼
              │  MySQL       │       ┌─────────────────────┐
              │  auth_db     │       │  Notification Svc   │
              └──────────────┘       │      :8083          │
                                     │                     │
              ┌──────────────┐       │ · Kafka consumer    │
              │  MySQL       │◄──────│ · Email sending     │
              │  wallet_db   │       │ · Dedup logging     │
              └──────────────┘       └──────────┬──────────┘
                                                │
              ┌──────────────┐       ┌──────────┴──────────┐
              │  Redis       │       │  MySQL              │
              │  Cache       │       │  notification_db    │
              └──────────────┘       └─────────────────────┘

              ┌──────────────────────────────────────────────┐
              │              Infrastructure                   │
              │  Kafka · Zookeeper · Zipkin · MySQL · Redis  │
              └──────────────────────────────────────────────┘
```

---

## Services

| Service                  | Port | Responsibility                                                         |
| ------------------------ | ---- | ---------------------------------------------------------------------- |
| **api-gateway**          | 8080 | Single entry point — JWT validation, routing, circuit breaker          |
| **auth-service**         | 8081 | User registration, login, JWT + refresh token management               |
| **wallet-service**       | 8082 | Wallet creation, deposits, withdrawals, transfers, transaction history |
| **notification-service** | 8083 | Kafka consumer — sends email notifications on transfer events          |

---

## Tech Stack

| Category         | Technology                   | Why                                                                         |
| ---------------- | ---------------------------- | --------------------------------------------------------------------------- |
| Framework        | Spring Boot 3.2.5            | Industry standard for Java microservices                                    |
| API Gateway      | Spring Cloud Gateway         | Reactive gateway with built-in filter support                               |
| Security         | Spring Security + JWT        | Stateless auth — no server-side session needed                              |
| Database         | MySQL 8.0 + JPA/Hibernate    | Relational integrity for financial data                                     |
| Migrations       | Flyway                       | Versioned, reproducible schema changes across environments                  |
| Caching          | Redis                        | Sub-millisecond wallet balance reads                                        |
| Messaging        | Apache Kafka                 | Decoupled async notification — wallet doesn't depend on notification uptime |
| Resilience       | Resilience4j Circuit Breaker | Prevents cascade failures when downstream services go down                  |
| Tracing          | Micrometer + Zipkin          | Single traceId across all services including Kafka hops                     |
| Containerization | Docker + Docker Compose      | One command to run entire system                                            |
| Build            | Maven                        | Dependency management and build lifecycle                                   |

---

## Key Engineering Decisions

### 1. JWT validated at Gateway, not in each service

Rather than duplicating JWT validation logic across every service, the API Gateway validates the token once and forwards `X-User-Id` as a trusted header. Individual services trust the gateway — no JWT dependency needed downstream. Services are only reachable via the gateway's internal Docker network (not exposed externally), so this header cannot be faked by external callers.

### 2. Optimistic locking for deposits/withdrawals, pessimistic for transfers

Wallet balance uses `@Version` for optimistic concurrency — suitable for low-collision operations like single-wallet updates. Transfers lock two wallets simultaneously using `SELECT ... FOR UPDATE` with consistent lock ordering (always lock lower wallet ID first) to prevent deadlocks.

### 3. Idempotency at two layers

Client-generated `referenceId` is checked at application level before processing, and enforced by a composite unique constraint `(reference_id, type)` at database level. The DB constraint handles race conditions where two identical requests pass the application check simultaneously. Transfers specifically check `referenceId + DEBIT` only — allowing the paired CREDIT record to share the same `referenceId`.

### 4. Kafka event published after transaction commit

Using `@TransactionalEventListener(phase = AFTER_COMMIT)` ensures the Kafka event is only published if the database transaction fully commits. Publishing inside the transaction risks sending a notification for a transfer that later rolls back.

### 5. Circuit breaker only triggers on 5xx, not 4xx

Client errors (invalid amount, insufficient balance) are expected business responses — not service failures. `statusCodes: [500, 502, 503, 504]` ensures only real service failures count toward the circuit breaker threshold. A user sending invalid requests can never accidentally trip the circuit and cause an outage for other users.

### 6. Database-per-service

Each service owns its own MySQL database (`auth_db`, `wallet_db`, `notification_db`). Services never query each other's tables directly — all cross-service communication goes through APIs or Kafka events.

---

## Core Flows

### Transfer Flow

```
POST /api/wallet/transfer
          │
          ▼
    Gateway validates JWT
    Extracts userId → adds X-User-Id header
          │
          ▼
    Wallet Service
    1. Validate amount > 0
    2. Check sender != receiver
    3. Check referenceId not duplicate (DEBIT type)
    4. Find sender + receiver wallets
    5. Pessimistic lock both wallets (consistent order)
    6. Check sender balance >= amount
    7. Deduct sender, credit receiver
    8. Save both wallets
    9. Save DEBIT + CREDIT transaction records
    10. Publish TransferEvent to Kafka (after commit)
          │
          ▼
    Notification Service (async via Kafka)
    1. Check referenceId not already processed
    2. Send email to sender
    3. Log to notification_db
```

### Circuit Breaker States

```
CLOSED → normal traffic flows through
  ↓ (>50% of last 10 requests fail with 5xx)
OPEN → instant fallback, no requests reach wallet-service
  ↓ (after 10 seconds)
HALF_OPEN → 3 test requests allowed through
  ↓ success → CLOSED again
  ↓ failure → OPEN again
```

---

## Running Locally

### Prerequisites

- Docker Desktop
- Java 21
- Maven 3.9+

### Start everything

```bash
git clone https://github.com/yourusername/walletx.git
cd walletx
docker-compose up --build
```

That's it. All services, databases, Kafka, Redis, and Zipkin start automatically.

### Service URLs

```
API Gateway    → http://localhost:8080
Zipkin UI      → http://localhost:9411
```

---

## API Reference

### Auth

```bash
# Register
POST http://localhost:8080/api/auth/register
{
  "name": "Abhi",
  "email": "abhi@example.com",
  "password": "xxxxxxxxxxxx"
}

# Login — returns access + refresh token
POST http://localhost:8080/api/auth/login
{
  "email": "abhi@example.com",
  "password": "xxxxxxxxxxxx"
}

# Refresh token
POST http://localhost:8080/api/auth/refresh
Authorization: Bearer <refresh_token>

# Logout
POST http://localhost:8080/api/auth/logout
Authorization: Bearer <refresh_token>
```

### Wallet (all require Authorization header)

```bash
# Create wallet
POST http://localhost:8080/api/wallet/create
Authorization: Bearer <access_token>

# Get balance
GET http://localhost:8080/api/wallet/balance
Authorization: Bearer <access_token>

# Deposit
POST http://localhost:8080/api/wallet/deposit
Authorization: Bearer <access_token>
{
  "amount": 1000.00,
  "referenceId": "DEP-UUID-001",
  "description": "Initial deposit"
}

# Withdraw
POST http://localhost:8080/api/wallet/withdraw
Authorization: Bearer <access_token>
{
  "amount": 500.00,
  "referenceId": "WTH-UUID-001",
  "description": "ATM withdrawal"
}

# Transfer
POST http://localhost:8080/api/wallet/transfer
Authorization: Bearer <access_token>
{
  "receiverUserId": 2,
  "amount": 200.00,
  "referenceId": "TRF-UUID-001",
  "description": "Split bill"
}

# Transaction history (paginated)
GET http://localhost:8080/api/wallet/transactions?page=0&size=10&sort=createdAt,desc
Authorization: Bearer <access_token>
```

---

## Observability

### Distributed Tracing

Every request gets a `traceId` at the gateway. The same `traceId` flows through wallet-service and notification-service (including across Kafka), enabling end-to-end trace reconstruction.

```
View traces → http://localhost:9411
```

### Logs

Every log line includes `[serviceName, traceId, spanId]`:

```
INFO [wallet,6a3bfece8f996bc664ef,d40d59bade703f30] WalletService : Transfer processed
INFO [notification,6a3bfece8f996bc664ef,f84224b0b021] NotificationService : Email sent
```

Same `traceId` across both — one search finds the complete picture.

---

## Database Schema

### auth_db

```
abhi_user       → id, name, email, password, role, created_at
refresh_token   → id, user_id, refresh_token, expiry
```

### wallet_db

```
wallet          → id, user_id, balance, version, created_at
transaction     → id, wallet_id, type, amount, status,
                  sender_user_id, receiver_user_id,
                  reference_id, description, created_at
```

### notification_db

```
notification    → id, reference_id, type, status, processed_at
```

---

## What I Learned Building This

- **Idempotency** is a two-layer problem — application checks handle the common case, database constraints handle race conditions
- **Circuit breakers** need careful status code configuration — client errors (4xx) should never count as service failures
- **JWT at the gateway** eliminates security code duplication across services but requires all services to be unreachable except through the gateway
- **Distributed tracing across Kafka** requires `spring.kafka.template.observation-enabled=true` — REST propagation is automatic but Kafka is opt-in
- **Flyway** forces you to think about schema changes as versioned artifacts rather than silent Hibernate magic — critical for production systems

---

## What's Next

- [ ] Unit and integration tests with Testcontainers
- [ ] Rate limiting per user at gateway level
- [ ] Spring Cloud Config Server for centralized configuration
- [ ] Kubernetes deployment manifests

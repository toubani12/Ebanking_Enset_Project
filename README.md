# E-Banking Full-Stack Application

> Technical documentation & project report

| Field       | Value                                  |
| ----------- | -------------------------------------- |
| **Student** | TOUBANI BADR EDDINE                    |
| **Professor** | ELLYOUSSFI MOHAMMED                  |
| **School**  | ENSET                                  |
| **Module**  | Architecture JEE et Middleware         |
| **Filière** | SDIA                                   |

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Functional Scope](#2-functional-scope)
3. [Architecture](#3-architecture)
4. [Technology Stack](#4-technology-stack)
5. [Backend — Spring Boot](#5-backend--spring-boot)
   - [Project Structure](#51-project-structure)
   - [Domain Model (Entities)](#52-domain-model-entities)
   - [Data Transfer Objects (DTOs)](#53-data-transfer-objects-dtos)
   - [Mappers](#54-mappers)
   - [Repositories](#55-repositories)
   - [Service Layer](#56-service-layer)
   - [Exception Handling](#57-exception-handling)
   - [REST Controllers](#58-rest-controllers)
   - [Security (JWT + OAuth2 Resource Server)](#59-security-jwt--oauth2-resource-server)
   - [Database & Configuration](#510-database--configuration)
6. [Frontend — Angular 21](#6-frontend--angular-21)
   - [Project Structure](#61-project-structure)
   - [Routing & Lazy Loading](#62-routing--lazy-loading)
   - [Authentication Service](#63-authentication-service)
   - [HTTP Interceptor](#64-http-interceptor)
   - [Route Guards](#65-route-guards)
   - [Feature Components](#66-feature-components)
   - [Domain Services](#67-domain-services)
7. [Security Model & Roles](#7-security-model--roles)
8. [REST API Reference](#8-rest-api-reference)
9. [Installation & Run](#9-installation--run)
10. [Default Users & Test Data](#10-default-users--test-data)
11. [Authors](#11-authors)

---

## 1. Project Overview

This project is a **full-stack e-banking application** developed within the *Architecture JEE et Middleware* module. The application allows a banking institution to manage customers, current and saving accounts, and to perform financial operations (debit, credit, transfer) through a secure REST API consumed by a modern Angular SPA.

The project demonstrates a complete **layered architecture** based on industry-standard JEE patterns: presentation (Angular SPA) → REST controllers → service layer → repository layer (Spring Data JPA) → persistence (MySQL via JPA/Hibernate). Authentication and authorization rely on **stateless JWT tokens** issued by an OAuth2-style authorization endpoint and validated by a Spring Security resource server.

## 2. Functional Scope

The application covers the following business functionalities:

- **Customer management** — list, search by keyword, create, update, delete (admin only).
- **Account management** — automatic creation of current and saving accounts on customer creation.
- **Operations** — debit, credit and transfer between accounts.
- **Account history** — paginated listing of all operations performed on an account.
- **Authentication** — login via username / password, JWT issued with embedded scope (roles).
- **Authorization** — fine-grained role-based access using `@PreAuthorize` and Angular route guards.

## 3. Architecture

The system follows a clean **N-tier architecture** split between two independent deployable modules: a Spring Boot backend exposing REST endpoints, and an Angular SPA consuming them.

```
┌────────────────────────────────────────────────────────────────────┐
│                       Angular 21 SPA (port 4200)                   │
│  Components ─► Services ─► HttpClient ─► HttpInterceptor (JWT)     │
│        ▲                                                            │
│        │  Route Guards: Authentication + Authorisation              │
└────────┼────────────────────────────────────────────────────────────┘
         │  HTTPS / JSON + Bearer JWT
         ▼
┌────────────────────────────────────────────────────────────────────┐
│                  Spring Boot 3.4.5 Backend (port 8080)             │
│                                                                    │
│   Web Layer   ─► RestControllers (CustmerController,               │
│                  BankAccountController, Securitycontroller)        │
│                            │                                       │
│   Security    ─► SecurityFilterChain + JWT Decoder/Encoder         │
│                            │                                       │
│   Service     ─► BankAccountServiceImpl (@Transactional)           │
│                            │                                       │
│   Mapper      ─► BankAccountMapperImpl  (Entity <-> DTO)           │
│                            │                                       │
│   Repository  ─► Spring Data JPA (Customer, BankAccount,           │
│                                   AccountOperation)                │
│                            │                                       │
│   Persistence ─► Hibernate / JPA ─► MySQL (database E-BANK)        │
└────────────────────────────────────────────────────────────────────┘
```

## 4. Technology Stack

### Backend
- **Java 21**
- **Spring Boot 3.4.5** (Web, Data JPA, Security, OAuth2 Resource Server)
- **Hibernate / JPA** (single-table inheritance for accounts)
- **MySQL 8** (database `E-BANK`, MariaDB dialect)
- **H2** (runtime, available for in-memory testing)
- **Lombok** (boilerplate reduction)
- **springdoc-openapi** 2.8.9 (Swagger UI / OpenAPI 3)
- **Nimbus JOSE + JWT** (token signing — HS256)

### Frontend
- **Angular 21.2** (standalone components, signals, SSR)
- **TypeScript 5.9**
- **RxJS 7.8**
- **Bootstrap 5.3 + Bootstrap Icons**
- **jwt-decode 4** (client-side token decoding)
- **Angular SSR + Express** (server-side rendering capability)

### Build & Tooling
- **Maven Wrapper** (`mvnw`)
- **Angular CLI 21**
- **Vitest** (Angular tests)

---

## 5. Backend — Spring Boot

### 5.1 Project Structure

```
backend/src/main/java/org/sid/ebankingbackend/
│
├── EbankingBackenApplication.java     # main class + CommandLineRunner (seeding)
├── entities/                          # JPA entities
│   ├── Custmer.java
│   ├── BankAccount.java               # abstract (SINGLE_TABLE inheritance)
│   ├── CurrentAccount.java
│   ├── SavingAccount.java
│   └── AccountOperation.java
├── enums/
│   ├── AccountStatus.java             # CREATED, ACTIVATED, SUSPENDED
│   └── OperationType.java             # DEBIT, CREDIT
├── dtos/                              # data-transfer objects
│   ├── CustmerDto.java
│   ├── BankAccountDto.java            # abstract base
│   ├── CurrentAccountDto.java
│   ├── SavingAccountDto.java
│   ├── AccountOperationDto.java
│   └── AccountHistoryDto.java
├── repositories/                      # Spring Data JPA
│   ├── CustomerRepository.java
│   ├── BankAccountRepository.java
│   └── AccountOperationRepository.java
├── mappers/
│   └── BankAccountMapperImpl.java
├── services/
│   ├── BankAccountService.java        # interface
│   ├── BankAccountServiceImpl.java
│   └── BankService.java
├── exceptions/
│   ├── CustomerNotFoundException.java
│   ├── BankAccountNotFoundException.java
│   └── BalanceNotSufisantException.java
├── web/
│   ├── CustmerController.java
│   └── BankAccountController.java
└── security/
    ├── SecurityConfig.java
    └── Securitycontroller.java
```

### 5.2 Domain Model (Entities)

#### Customer (`Custmer`)
```java
@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Custmer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    @OneToMany(mappedBy = "custmer", fetch = FetchType.LAZY)
    private List<BankAccount> bankAccounts;
}
```

#### Bank Account hierarchy
`BankAccount` is an **abstract entity** mapped with `InheritanceType.SINGLE_TABLE` and a 4-character `type` discriminator column. Concrete sub-entities are `CurrentAccount` (`CA`) and `SavingAccount` (`SA`).

```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", length = 4)
public abstract class BankAccount {
    @Id private String id;             // UUID
    private double balance;
    private Date createdAt;
    @Enumerated(EnumType.STRING) private AccountStatus status;
    @ManyToOne private Custmer custmer;
    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    private List<AccountOperation> accountOperations;
}
```

- `CurrentAccount` adds `overDraft`.
- `SavingAccount` adds `intrestRAte`.

#### Account Operation
Represents any movement on an account (DEBIT or CREDIT).
```java
@Entity
public class AccountOperation {
    @Id @GeneratedValue Long id;
    private Date operationDate;
    private double amount;
    @Enumerated(EnumType.STRING) private OperationType type;
    @ManyToOne private BankAccount bankAccount;
    private String description;
}
```

### 5.3 Data Transfer Objects (DTOs)

To avoid lazy-loading issues and to decouple the API from the persistence model, the application exposes DTOs over the wire:

- `CustmerDto` — flat customer projection.
- `BankAccountDto` — abstract base with a `type` discriminator string used for JSON polymorphism.
- `CurrentAccountDto` / `SavingAccountDto` — extend `BankAccountDto`, embed `CustmerDto`.
- `AccountOperationDto` — single operation projection.
- `AccountHistoryDto` — paginated operations envelope (`accountId`, `balance`, `currentPage`, `totalPages`, `pageSize`, `accountOperationDtos`).

### 5.4 Mappers

`BankAccountMapperImpl` performs Entity ⇄ DTO conversion using `BeanUtils.copyProperties`. It also stamps the runtime class name into the DTO `type` field (`"CurrentAccount"` / `"SavingAccount"`), which the Angular frontend uses to render account-specific information.

### 5.5 Repositories

Spring Data JPA repositories are interface-only — implementations are generated at runtime.

| Repository                       | Key methods |
| -------------------------------- | ----------- |
| `CustomerRepository`             | `findAll`, `save`, `findById`, `deleteById`, `findByNameContains(String name)` |
| `BankAccountRepository`          | Standard CRUD by `String` id |
| `AccountOperationRepository`     | `findByBankAccountId(String)`, `findByBankAccountId(String, Pageable)` |

### 5.6 Service Layer

`BankAccountService` is the central façade for the business logic. Its implementation `BankAccountServiceImpl` is annotated `@Service`, `@Transactional`, `@AllArgsConstructor`, `@Slf4j`.

Main operations:

- `saveCustmer`, `listCustmers`, `getCustmer`, `updateCustmer`, `deleteCustmer`, `searchCustmers(keyword)`
- `saveCurrentBankAccount(initialBalance, overDraft, customerId)`
- `saveSavingBankAccount(initialBalance, interestRate, customerId)`
- `getBankAccount(accountId)` — polymorphic dispatch via `instanceof`.
- `debit(accountId, amount, description)` — verifies balance, persists an `AccountOperation` of type `DEBIT`, then updates the account balance.
- `credit(accountId, amount, description)` — symmetrical, type `CREDIT`.
- `transfer(source, destination, amount)` — composed of `debit` + `credit`.
- `accountHistory(accountId)` — all operations.
- `getAccountHistory(accountId, page, size)` — paginated history wrapped in `AccountHistoryDto`.

### 5.7 Exception Handling

Domain-level checked exceptions:

- `CustomerNotFoundException`
- `BankAccountNotFoundException`
- `BalanceNotSufisantException`

They are thrown by the service layer and propagated by the REST controllers. The HTTP status defaults to `500` (the project does not declare a global `@ControllerAdvice`).

### 5.8 REST Controllers

#### `CustmerController` (`/custmers`)
All endpoints require the `SCOPE_ADMIN` authority (configured with `@PreAuthorize`).

| Method   | URI                  | Description                          |
| -------- | -------------------- | ------------------------------------ |
| `GET`    | `/custmers`          | List all customers                   |
| `GET`    | `/custmers/search`   | Search by keyword (`?keyword=`)      |
| `GET`    | `/custmers/{id}`     | Get one customer                     |
| `POST`   | `/custmers`          | Create a customer (`CustmerDto` body)|
| `PUT`    | `/custmers/{id}`     | Update a customer                    |
| `DELETE` | `/custmers/{id}`     | Delete a customer                    |

#### `BankAccountController`
| Method | URI                                          | Description                       |
| ------ | -------------------------------------------- | --------------------------------- |
| `GET`  | `/accounts`                                  | List all accounts                 |
| `GET`  | `/accounts/{accountId}`                      | Get a single account              |
| `GET`  | `/accounts/{accountId}/operations`           | All operations for an account     |
| `GET`  | `/accounts/{accountId}/pageOperations`       | Paginated operations + balance    |
| `POST` | `/accounts/debit`                            | Debit an account                  |
| `POST` | `/accounts/credit`                           | Credit an account                 |
| `POST` | `/accounts/transfer`                         | Transfer between two accounts     |

#### `Securitycontroller` (`/auth`)
| Method | URI            | Description                                  |
| ------ | -------------- | -------------------------------------------- |
| `POST` | `/auth/login`  | Authenticate and obtain a JWT access token   |

Both `BankAccountController` and `CustmerController` are annotated with `@CrossOrigin("*")` so the Angular development server (`http://localhost:4200`) can call them directly.

### 5.9 Security (JWT + OAuth2 Resource Server)

`SecurityConfig` is annotated `@EnableWebSecurity` and `@EnableMethodSecurity(prePostEnabled = true)`. Highlights:

- **Stateless session** policy — no HTTP session is created.
- **CSRF disabled** (REST API with bearer tokens).
- **CORS** enabled with a permissive configuration source.
- `/auth/**` is publicly accessible; every other endpoint requires authentication.
- The chain registers an OAuth2 resource server using a JWT decoder.

Token cryptography:
- `JwtEncoder` — `NimbusJwtEncoder` over a symmetric secret (`jwt.secret` property).
- `JwtDecoder` — `NimbusJwtDecoder` validating HS256 signatures with the same secret.

User base — **in-memory** `InMemoryUserDetailsManager` with two users:

| Username | Password | Authorities    |
| -------- | -------- | -------------- |
| `user`   | `1234`   | `USER`         |
| `admin`  | `1234`   | `USER, ADMIN`  |

Passwords are BCrypt-encoded. The JWT subject is the username; the `scope` claim carries space-separated authorities. Authorities are mapped to Spring Security `SCOPE_ADMIN` / `SCOPE_USER` and consumed by `@PreAuthorize` annotations.

`Securitycontroller#login` flow:
1. Authenticate with `AuthenticationManager` (`UsernamePasswordAuthenticationToken`).
2. Build a `JwtClaimsSet` with `sub`, `iat`, `exp` (now + 10 minutes), and `scope`.
3. Encode it with HS256 and return `{"access-token": "<jwt>"}`.

### 5.10 Database & Configuration

`backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/E-BANK?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
jwt.secret=bXktc3VwZXItc2VjdXJlLWtleS1mb3Itand0LXNpZ25pbmctMjAyNg==
```

> The schema is **dropped and recreated** at each startup (`ddl-auto=create`). The application also seeds three demo customers (Hassan, Yassine, Aicha) and generates one current + one saving account per customer plus 20 random credit/debit operations per account through `EbankingBackenApplication#commandLineRunner`.

---

## 6. Frontend — Angular 21

### 6.1 Project Structure

```
frontend/src/app/
├── app.ts / app.html / app.css        # root component (RouterOutlet)
├── app.config.ts                       # standalone bootstrap config
├── app.routes.ts                       # lazy-loaded routes
│
├── login/                              # login page
├── admin-template/                     # layout shell with navbar
├── navbar/
├── customers/                          # search / list / delete customers
├── new-custmer/                        # create customer form (admin only)
├── accounts/                           # search account, debit/credit/transfer
├── not-authorised/                     # 403 page
│
├── services/
│   ├── Auth/auth-service.ts            # JWT login + state
│   ├── Customer/customers.ts           # CRUD customers
│   └── Accounts/accounts.ts            # operations + history
│
├── guards/
│   ├── athentication-guard.ts          # blocks unauthenticated
│   └── authorisation-guard.ts          # requires ADMIN role
│
├── interceptors/
│   └── app-http-interceptor.ts         # injects Bearer token
│
└── model/customer.model.ts
```

### 6.2 Routing & Lazy Loading

`app.routes.ts` declares the application routes with `loadComponent` (lazy import of standalone components):

```typescript
export const routes: Routes = [
  { path: 'login',  loadComponent: () => import('./login/login').then(m => m.Login) },
  { path: '',       redirectTo: '/login', pathMatch: 'full' },
  {
    path: 'admin',
    loadComponent: () => import('./admin-template/admin-template').then(m => m.AdminTemplate),
    canActivate: [athenticationGuard],
    children: [
      { path: 'customers',     loadComponent: () => import('./customers/customers').then(m => m.Customers) },
      { path: 'accounts',      loadComponent: () => import('./accounts/accounts').then(m => m.Accounts) },
      { path: 'new-customer',  loadComponent: () => import('./new-custmer/new-custmer').then(m => m.NewCustmer),
                               canActivate: [authorisationGuard] },
      { path: 'not-authorised',loadComponent: () => import('./not-authorised/not-authorised').then(m => m.NotAuthorised) }
    ]
  }
];
```

The `admin` route mounts a layout shell (navbar + `<router-outlet>`). Children are protected by `athenticationGuard`; the create-customer screen further requires `authorisationGuard` (ADMIN only).

`app.config.ts` provides the router, `HttpClient` (with the JWT interceptor) and client hydration for SSR:

```typescript
providers: [
  provideRouter(routes),
  provideClientHydration(withEventReplay()),
  provideHttpClient(withFetch(), withInterceptors([appHttpInterceptor]))
]
```

### 6.3 Authentication Service

`AuthService` is a root-provided service that:

- Calls `POST http://localhost:8080/auth/login` with `application/x-www-form-urlencoded` credentials.
- Decodes the returned JWT manually (base64 of the payload) to extract `scope` (roles) and `sub` (username).
- Persists the token in `localStorage` under the key `jwt-token` so the session survives page reloads.
- Exposes the flags `isAuthenticated`, `roles`, `uaername`, and the raw `accesTokken`.
- Provides `logout()` which clears the local state and storage.

The root `App` component calls `authservice.loadToken()` on `ngOnInit` to restore the session at bootstrap.

### 6.4 HTTP Interceptor

```typescript
export const appHttpInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  if (!req.url.includes('/auth/login') && authService.isAuthenticated) {
    const newRequest = req.clone({
      headers: req.headers.set('Authorization', 'Bearer ' + authService.accesTokken)
    });
    return next(newRequest);
  }
  return next(req);
};
```

The interceptor automatically attaches the JWT to every outgoing request **except** the login call.

### 6.5 Route Guards

- `athenticationGuard` — redirects to `/login` when the user is not authenticated. It is **SSR-safe**: on the server `PLATFORM_ID` is not the browser, so the guard short-circuits to `true` (`localStorage` is unavailable on the server).
- `authorisationGuard` — checks that `authService.roles` includes `ADMIN`; otherwise navigates to `/admin/not-authorised`.

### 6.6 Feature Components

- **`Login`** — `FormGroup` with `username` / `password` (both `Validators.required`). On submit, calls `AuthService.login`, then `loadProfile` and finally navigates to `/admin`.
- **`AdminTemplate`** — composed of `<app-navbar>` and a `<router-outlet>`.
- **`Navbar`** — Bootstrap navbar showing customer / accounts links, conditional `*ngIf` items for ADMIN (e.g. *new customer*), the current username and a *Logout* button.
- **`Customers`** — list customers as an `Observable<Array<Customer>>` driven by a `BehaviorSubject`. Provides keyword search, delete with `confirm()` and graceful error handling via `catchError` / `finalize`.
- **`NewCustmer`** — reactive form with `name` and `email` validators; calls `CustomerService.saveCustomer` and redirects to `/customers`.
- **`Accounts`** — search an account by id, paginated history, plus a second form to perform DEBIT / CREDIT / TRANSFER operations. On `401/403` responses the user is logged out and redirected to `/login`.
- **`NotAuthorised`** — static 403 information page.

### 6.7 Domain Services

- `CustomerService` (`services/Customer/customers.ts`) — wraps `/custmers`, `/custmers/search`, `/custmers/{id}` (CRUD).
- `AccountService` (`services/Accounts/accounts.ts`) — wraps the account endpoints (`getAccount`, `debit`, `credit`, `transfer`) and exposes the `AccountDetails` and `AccountOperation` TypeScript interfaces.

---

## 7. Security Model & Roles

| Role    | Granted by JWT scope | Can access                                                                 |
| ------- | -------------------- | -------------------------------------------------------------------------- |
| `USER`  | `SCOPE_USER`         | `/accounts/**` endpoints (read operations, debit, credit, transfer)        |
| `ADMIN` | `SCOPE_ADMIN`        | Everything `USER` can do **plus** the whole `/custmers/**` namespace and the *new customer* screen in the SPA |

Server-side enforcement: `@PreAuthorize("hasAuthority('SCOPE_ADMIN')")` on customer endpoints.
Client-side enforcement: `authorisationGuard` + `*ngIf="authService.roles.includes('ADMIN')"` on dynamic UI elements.

---

## 8. REST API Reference

### Authentication
```http
POST /auth/login
Content-Type: application/x-www-form-urlencoded

username=admin&password=1234
```
Response:
```json
{ "access-token": "eyJhbGciOiJIUzI1NiJ9..." }
```

Subsequent requests must carry:
```
Authorization: Bearer <access-token>
```

### Customers (ADMIN)
```http
GET    /custmers
GET    /custmers/search?keyword=has
GET    /custmers/{id}
POST   /custmers                       Body: CustmerDto JSON
PUT    /custmers/{id}                  Body: CustmerDto JSON
DELETE /custmers/{id}
```

### Accounts
```http
GET    /accounts
GET    /accounts/{accountId}
GET    /accounts/{accountId}/operations
GET    /accounts/{accountId}/pageOperations?page=0&size=5

POST   /accounts/debit?accountId=...&amount=...&description=...
POST   /accounts/credit?accountId=...&amount=...&description=...
POST   /accounts/transfer?accountSource=...&accountDestination=...&amount=...
```

OpenAPI / Swagger UI: `http://localhost:8080/swagger-ui.html` (provided by `springdoc-openapi`).

---

## 9. Installation & Run

### Prerequisites
- JDK 21
- Maven (or use the bundled `mvnw` wrapper)
- Node.js 20+ and npm 10+
- MySQL 8 running on `localhost:3306` (the database `E-BANK` is auto-created)

### Backend
```bash
cd backend
./mvnw spring-boot:run            # macOS / Linux
mvnw.cmd spring-boot:run          # Windows
```
The server listens on **http://localhost:8080**.

### Frontend
```bash
cd frontend
npm install
npm start                          # equivalent to: ng serve
```
The SPA is served on **http://localhost:4200** and proxies all API calls to `http://localhost:8080`.

### Build for production
```bash
# Backend
./mvnw clean package
java -jar target/Backend-0.0.1-SNAPSHOT.jar

# Frontend
ng build
```

---

## 10. Default Users & Test Data

On every startup the `CommandLineRunner` declared in `EbankingBackenApplication` resets the schema and inserts:

- **Customers**: `hassan`, `yassine`, `Aicha` — emails `<name>@gmail.com`.
- **Accounts**: one `CurrentAccount` (random balance ≤ 90 000, overdraft 9 000) and one `SavingAccount` (random balance ≤ 120 000, interest rate 5.5) per customer.
- **Operations**: 20 random credits and debits per account.

Available login credentials:

| Username | Password | Role         |
| -------- | -------- | ------------ |
| `user`   | `1234`   | USER         |
| `admin`  | `1234`   | USER + ADMIN |

---

## 11. Authors

- **Student** — TOUBANI BADR EDDINE
- **Supervisor** — Prof. ELLYOUSSFI MOHAMMED
- **Institution** — ENSET
- **Module** — Architecture JEE et Middleware
- **Filière** — SDIA

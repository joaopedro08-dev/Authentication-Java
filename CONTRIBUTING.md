# Contribution & Technical Guide

## Secure Java Auth API

This document defines the technical standards, security rules, and
contribution guidelines for the **Secure Java Auth API**.\
It is intended for developers who want to study the architecture, run
the project locally, or contribute safely without weakening the security
model.

------------------------------------------------------------------------

## 🏗 Project Architecture & Security Model

This API implements a **Hybrid Authentication Architecture** designed
for scalability, control, and immediate session revocation.

### Authentication Strategy

-   **Stateless Access Tokens (JWT)**
    -   Short-lived
    -   Optimized for horizontal scalability
-   **Stateful Refresh Tokens**
    -   Persisted in PostgreSQL
    -   Enable full session lifecycle control

### 🔄 Authentication Flow (High-Level)

1.  Client authenticates via `signIn`
2.  A short-lived JWT Access Token is issued
3.  A Refresh Token is persisted and linked to the user
4.  Token refresh rotates the Refresh Token UUID
5.  Logout immediately revokes the session via blacklist

------------------------------------------------------------------------

## 📦 Database Design (PostgreSQL 18.1)

The persistence layer is explicitly designed to support secure session
management.

### Core Tables

-   **`db_users`**
    -   Centralizes user identity and authorization roles
    -   Enforces unique email constraints
-   **`db_refresh_tokens`**
    -   Stores active refresh tokens
    -   Linked to users via foreign key
    -   Uses UUID-based identifiers to prevent token guessing
    -   Indexed for fast refresh operations
    -   Uses `ON DELETE CASCADE` for automatic session cleanup
-   **`db_blacklist_tokens`**
    -   Stores invalidated JWT identifiers
    -   Enables immediate logout and forced revocation

------------------------------------------------------------------------

## 🔐 Core Security Pillars

### Refresh Token Rotation

-   Every refresh request invalidates the previous token UUID
-   Prevents replay attacks and token reuse

### Cookie Hardening

Authentication cookies are issued with: - `HttpOnly` - `Secure` -
`SameSite=Strict`

These settings mitigate XSS and CSRF attacks.

### Timezone Consistency

-   All expiration logic operates strictly in **UTC**
-   Prevents token mismatches across environments

------------------------------------------------------------------------

## 🚫 Security Non-Negotiables

The following rules **must never be violated**:

-   Refresh Tokens must always be rotated
-   Access Tokens must remain short-lived
-   Authentication secrets must never be logged
-   Cookie security flags must not be relaxed in production
-   UTC must be used for all time-based security logic

------------------------------------------------------------------------

## 🛠 Development Environment

### 🔧 Requirements

-   **Java:** 21\
-   **Build Tool:** Maven\
-   **Database:** PostgreSQL 18.1\
-   **Containerization:** Docker 

### ▶️ Quick Start (Dev)

``` bash
docker build -d Authentication-Java .
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### ⚙️ Spring Profiles

-   **`dev`**
    -   Allows HTTP connections
    -   Intended strictly for local development
-   **`prod`**
    -   Enforces HTTPS
    -   Uses hardened cookies
    -   Intended only for production deployments

------------------------------------------------------------------------

## 💻 Contribution Workflow

### 1. Fork the Repository

Create your own fork before making changes.

### 2. Create a Branch

Use descriptive branch names:

``` bash
git checkout -b feature/authentication-improvement
```

**Branch naming conventions:** - `feature/*` - `fix/*` - `security/*`

### 3. Follow Architectural Constraints

-   Do not bypass Refresh Token rotation logic
-   Do not store raw secrets or credentials
-   Respect the session lifecycle design

### 4. Database Integrity

-   Preserve existing indexes such as:
    -   `idx_user_email`
    -   `idx_refresh_token_string`
-   Do not break foreign key constraints
-   Avoid schema changes without clear justification

### 5. GraphQL Schema Changes

Any change must be reflected in: - `.graphqls` schema files -
Corresponding resolvers - Relevant automated tests

### 6. Security Review (Mandatory)

Before submitting a PR, verify: - Refresh Token rotation integrity -
Blacklist behavior on logout - Cookie flags and expiration logic - No
sensitive data is exposed in logs or responses

------------------------------------------------------------------------

## 🚨 What Not To Do

-   Do not introduce long-lived Access Tokens
-   Do not expose tokens unnecessarily in GraphQL responses
-   Do not weaken cookie policies for convenience
-   Do not add authentication bypasses for testing
-   Do not disable security checks in production profiles

------------------------------------------------------------------------

## 📄 License

By contributing to this repository, you agree that your contributions
will be licensed under the **MIT License**, without additional
restrictions, as defined in the root `LICENSE` file.

------------------------------------------------------------------------

Built with a strong focus on security and correctness.

© 2026 João Pedro Dala Dea Mello

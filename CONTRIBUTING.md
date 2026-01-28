# Contribution & Technical Guide --- Secure Java Auth API

This project is a high-security authentication system designed with **Java 21**, **Spring Boot 3**, and **GraphQL**. Below are the technical guidelines for those who wish to study, test, or contribute to this implementation.

------------------------------------------------------------------------

## 🏗 Project Architecture & Security Model

This API follows a **Hybrid Authentication Architecture**:

-   **Stateless Access Tokens (JWT)** for scalability
-   **Stateful Refresh Tokens** for full session control and revocation

### 📦 Database Design (PostgreSQL 18.1)

The persistence layer is explicitly designed for session security and
integrity:

-   **`db_users`**
    -   Centralizes user identity and authorization roles.
-   **`db_refresh_tokens`**
    -   Stores active refresh tokens.
    -   Linked to users via foreign key.
    -   Uses `ON DELETE CASCADE` to guarantee session cleanup.
-   **`db_blacklist_tokens`**
    -   Stores invalidated JWT identifiers.
    -   Enables immediate logout and forced revocation.

### 🔐 Core Security Pillars

-   **Refresh Token Rotation**
    -   Every refresh request invalidates the previous token UUID.
    -   Prevents replay attacks and token reuse.
-   **Cookie Hardening**
    -   Authentication cookies are issued with:
        -   `HttpOnly`
        -   `Secure`
        -   `SameSite=Strict`
    -   Mitigates XSS and CSRF attacks.
-   **Timezone Consistency**
    -   All expiration logic operates in **UTC**.
    -   Prevents token mismatch across environments.

------------------------------------------------------------------------

## 🛠 Development Environment

To run or contribute locally, ensure the following requirements are met:

### 🔧 Requirements

-   **Java:** 21
-   **Build Tool:** Maven
-   **Database:** PostgreSQL 18.1
-   **Containerization:** Docker 

### ⚙️ Spring Profiles

The project uses Spring Profiles to control security behavior:

-   **`dev`**
    -   Allows HTTP connections for local development.

    -   Example:

        ``` bash
        mvn spring-boot:run -Dspring-boot.run.profiles=dev
        ```
-   **`prod`**
    -   Enforces HTTPS and secure cookies.
    -   Intended for production deployments only.

------------------------------------------------------------------------

## 💻 Contribution Workflow

1.  **Fork the repository**

2.  **Create a feature branch**

    ``` bash
    git checkout -b feature/Authentication-Java
    ```

3.  **Follow architectural constraints**

    -   Do not bypass token rotation logic.
    -   Do not store raw secrets.
    -   Respect session lifecycle rules.

4.  **Database Integrity**

    -   Preserve existing indexes such as:
        -   `idx_user_email`
        -   `idx_refresh_token_string`
    -   Avoid breaking foreign key constraints.

5.  **GraphQL Schema Changes**

    -   Any model change must be reflected in:
        -   `.graphqls` schema files
        -   Corresponding resolvers
        -   Relevant tests

6.  **Security Review**

    -   Always verify:
        -   Refresh Token Rotation integrity
        -   Blacklist behavior
        -   Cookie flags and expiration logic

------------------------------------------------------------------------

## 📄 License

By contributing to this repository, you agree that all submitted code
will be licensed under the **MIT License**, as defined in the root
`LICENSE` file.

------------------------------------------------------------------------

© 2026 João Pedro Dala Dea Mello
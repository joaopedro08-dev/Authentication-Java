# Secure Java Auth API 🛡️

A high-performance, enterprise-grade Authentication API built with
**Java 21**, **Spring Boot 4**, and **GraphQL**. This project implements
a *Security-First* architecture, focusing on resilient session
management and modern defense-in-depth practices.

![License](https://img.shields.io/badge/License-MIT-yellow.svg)
![Java](https://img.shields.io/badge/Java-21-orange)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18.1-blue)
![Spring](https://img.shields.io/badge/Spring_Boot-4-green)
![Docker](https://img.shields.io/badge/Docker-ready-blue)

## 🔐 Advanced Security Features

This API goes beyond basic JWT issuance by implementing a robust session
lifecycle:

-   **Hybrid Session Management:** Stateless JWT access tokens combined
    with persistent Refresh Tokens stored in PostgreSQL.
-   **Refresh Token Rotation:** Each refresh invalidates the previous
    token, preventing replay attacks.
-   **Hardened Cookies:** `HttpOnly`, `Secure`, and `SameSite=Strict`
    cookies mitigate XSS and CSRF risks.
-   **Token Blacklisting:** Immediate session revocation on logout.
-   **Rate Limiting:** Brute-force protection using the Token Bucket
    algorithm (Bucket4j).
-   **Database Integrity:** Strong relational constraints with
    `ON DELETE CASCADE`.

## 🛠️ Tech Stack

-   **Language:** Java 21
-   **Framework:** Spring Boot 4 (Security, Data JPA, Validation)
-   **API Layer:** GraphQL
-   **Database:** PostgreSQL 18.1
-   **Containerization:** Docker

## 🚀 Quick Start with Docker

The project is fully containerized and ready to run.

``` bash
git clone https://github.com/joaopedro08-dev/Authentication-Java
cd Authentication-Java
mvn spring-boot:run
```

The API will be available at:

    http://localhost:8080/graphql

## ⚙️ Environment Profiles

Spring Profiles are used to control security behavior:

-   **dev:** Allows insecure cookies for local testing.
-   **prod:** Enforces secure cookies and HTTPS-only operation.

## 🤝 Contributing

Contributions are welcome!\
Please follow clean architecture principles and provide tests for new
features.

## 📄 License

This project is licensed under the MIT License.

------------------------------------------------------------------------

© 2026 João Pedro Dala Dea Mello
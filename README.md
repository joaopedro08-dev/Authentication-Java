# Secure Java Auth API 🛡️

A high-performance, enterprise-grade Authentication API built with **Java 21**, **Spring Boot 4**, and **GraphQL**. This project implements a *Security-First* architecture, focusing on resilient session management and modern defense-in-depth practices.

![License](https://img.shields.io/badge/License-MIT-yellow.svg)
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18.1-blue?logo=postgresql)
![Spring](https://img.shields.io/badge/Spring_Boot-4-green?logo=springboot)
![Docker](https://img.shields.io/badge/Docker-ready-blue?logo=docker)

## 🔐 Advanced Security Features

- **Hybrid Session Management:** Stateless JWT access tokens combined with persistent Refresh Tokens stored in PostgreSQL.
- **Refresh Token Rotation:** Each refresh request invalidates the previous token UUID, preventing replay attacks.
- **Hardened Cookies:** Tokens are delivered via `HttpOnly`, `Secure`, and `SameSite=Strict` cookies to mitigate XSS and CSRF.
- **Token Blacklisting:** Immediate session revocation on logout via an indexed database blacklist.
- **Rate Limiting:** Protection against brute-force attacks using the Token Bucket algorithm (Bucket4j).
- **Database Integrity:** Strong relational constraints with `ON DELETE CASCADE` and b-tree indexing.

## 🌐 Live Demo

The API is deployed and can be tested at:
👉 **[https://authentication-java.onrender.com/graphql](https://authentication-java.onrender.com/graphql)**

> **⚠️ Note on Render Free Tier:** This service uses a free instance that spins down after 15 minutes of inactivity. The first request might take **30-50 seconds** to wake up the server. Subsequent requests will be fast.


## 🚀 API Testing Guide (Postman / Insomnia / Playground)

To test the API, send a **POST** request to the GraphQL endpoint with the following structures:

### Get User Info (Query)
```graphql
query GetUserInfo {
    getUserInfo {
        id
        name
        email
        role
        statusSystem
        createdAt
        lastLogin
    }
}
```
---
### Sign Up (Mutation)
```graphql
mutation SignUp {
    signUp(
        input: {
            name: "Usuário"
            email: "usuario@gmail.com"
            password: "Usu@rio123"
            confirmPassword: "Usu@rio123"
        }
    ) {
        message
        success
    }
}
```
---
### Sign In (Mutation)
```graphql
mutation SignIn {
    signIn(
        input: { email: "usuario@gmail.com", password: "Usu@rio123" }
    ) {
        message
        success
        token
    }
}
```
---
### Logout (Mutation)
```graphql
mutation Logout {
    logout {
        message
        success
        token
    }
}
```
---
### Refresh Token (Mutation)
```graphql
mutation RefreshToken {
    refreshToken(token: "") {
        message
        success
        token
    }
}
```
---
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
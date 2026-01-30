# 🛡️ Secure Java Auth API 

A high-performance, enterprise-grade Authentication API built with
**Java 21**, **Spring Boot 4**, and **GraphQL**.\
This project follows a **Security-First** architecture, focusing on
resilient session management and modern defense-in-depth practices.

![License](https://img.shields.io/badge/License-MIT-yellow.svg)
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18.1-blue?logo=postgresql)
![Spring](https://img.shields.io/badge/Spring_Boot-4-green?logo=springboot)
![Docker](https://img.shields.io/badge/Docker-ready-blue?logo=docker)

------------------------------------------------------------------------

## 🎯 Motivation

This project was created to demonstrate a modern, production-ready
authentication architecture using Java and GraphQL, designed to address
real-world security threats such as token replay, session hijacking,
CSRF, XSS, and brute-force attacks.

It aims to serve both as a reference implementation and a
portfolio-grade project.

------------------------------------------------------------------------

## 🧠 Architecture Overview

-   GraphQL API layer using Spring for GraphQL\
-   Clean, domain-driven authentication module\
-   Short-lived JWT Access Tokens\
-   Persistent Refresh Tokens stored in PostgreSQL\
-   Refresh Token rotation with UUID invalidation\
-   Token Blacklist for immediate session revocation\
-   Rate limiting via Bucket4j (Token Bucket algorithm)

------------------------------------------------------------------------

## 🔐 Advanced Security Features

-   **Hybrid Session Management:** Stateless JWT access tokens combined
    with persistent Refresh Tokens.
-   **Refresh Token Rotation:** Every refresh invalidates the previous
    token, preventing replay attacks.
-   **Hardened Cookies:** `HttpOnly`, `Secure`, and `SameSite=Strict`
    cookies to mitigate XSS and CSRF.
-   **Immediate Revocation:** Token blacklisting on logout.
-   **Rate Limiting:** Protection against brute-force attacks.
-   **Database Integrity:** Strong relational constraints, indexed
    UUIDs, and cascading deletes.

------------------------------------------------------------------------

## 🛡️ Security Threat Model

This API is designed to mitigate:

-   Cross-Site Scripting (XSS)
-   Cross-Site Request Forgery (CSRF)
-   Token replay attacks
-   Brute-force authentication attempts
-   Stolen token reuse

------------------------------------------------------------------------

## 🔄 Authentication Flow

1.  User signs in and receives a short-lived Access Token
2.  When the Access Token expires, the client requests a refresh
3.  The Refresh Token is rotated and the previous one is invalidated
4.  Logout immediately revokes the session via blacklist

------------------------------------------------------------------------

## 🌐 Live Demo

The API is deployed and available at:

👉 **https://authentication-java.onrender.com/graphql**

> ⚠️ **Render Free Tier Notice:**\
> The service may take **30--50 seconds** to wake up after inactivity.

------------------------------------------------------------------------

## 🚀 API Testing (Postman / Insomnia / Playground)

### Get User Info

``` graphql
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

### Sign Up

``` graphql
mutation SignUp {
  signUp(
    input: {
      name: "John Doe"
      email: "john.doe@example.com"
      password: "Str0ngP@ssword!"
      confirmPassword: "Str0ngP@ssword!"
    }
  ) {
    message
    success
  }
}
```

### Sign In

``` graphql
mutation SignIn {
  signIn(
    input: { email: "john.doe@example.com", password: "Str0ngP@ssword!" }
  ) {
    message
    success
    token
  }
}
```

### Logout

``` graphql
mutation Logout {
  logout {
    message
    success
    token
  }
}
```

### Refresh Token

``` graphql
mutation RefreshToken {
  refreshToken {
    message
    success
    token
  }
}
```

------------------------------------------------------------------------

## ⚙️ Environment Profiles

-   **dev:** Allows insecure cookies for local development
-   **prod:** Enforces HTTPS and secure cookies

------------------------------------------------------------------------

## 🛠️ Local Setup

### Requirements

-   Java 21
-   Docker
-   PostgreSQL

### Running Locally

``` bash
docker build -t authentication-java .
./mvnw spring-boot:run
```

------------------------------------------------------------------------

## 🤝 Contributing

Contributions are welcome!\
Please follow clean architecture principles and include tests for new
features.

------------------------------------------------------------------------

## 📄 License

This project is licensed under the MIT License.

------------------------------------------------------------------------

Built with a strong focus on security.

© 2026 João Pedro Dala Dea Mello

<div align="center">

# 🔗 URL Shortener

### Modern URL Shortening Service built with Java, Spring Boot, MySQL & Docker

<p>
A scalable RESTful URL Shortening application that converts long URLs into compact, shareable links using an efficient Base62 encoding algorithm. Built with Spring Boot following layered architecture and containerized using Docker for easy deployment.
</p>

<br>

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot)
![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker)
![REST API](https://img.shields.io/badge/Architecture-REST_API-blue?style=for-the-badge)

</div>

---

# 📖 Overview

URL Shortener is a backend application developed using **Java** and **Spring Boot** that transforms long URLs into short, easy-to-share links.

The application exposes REST APIs for shortening URLs and redirecting users to the original destination. URL information is stored in a MySQL database using Spring Data JPA, while Docker support simplifies deployment.

The project demonstrates practical backend engineering concepts including:

- REST API Development
- Spring Boot
- Spring Data JPA
- MySQL Integration
- Layered Architecture
- Docker Deployment
- URL Encoding using Base62
- Input Validation
- Exception Handling

---

# ✨ Features

## 🔗 URL Shortening

- Convert long URLs into short URLs
- Base62 encoding algorithm
- Unique URL generation
- Fast redirection

---

## 💾 Database Integration

- MySQL Database
- Spring Data JPA
- Repository Pattern
- Persistent URL storage

---

## ⚡ REST API

- Create Short URL
- Redirect to Original URL
- Health Check Endpoint
- JSON Responses

---

## 🛡 Validation

- HTTP/HTTPS URL validation
- Proper exception handling
- Meaningful error responses
- Request validation

---

## 🐳 Docker Support

- Dockerized Spring Boot application
- MySQL Docker container
- Docker Compose configuration

---

## 📊 Monitoring

- Spring Boot Actuator
- Health Endpoint
- Application Metrics

---

# 🛠 Tech Stack

| Layer | Technology |
|--------|------------|
| Language | Java |
| Framework | Spring Boot |
| Build Tool | Gradle |
| Database | MySQL |
| ORM | Spring Data JPA (Hibernate) |
| Validation | Commons Validator |
| Monitoring | Spring Boot Actuator |
| Containerization | Docker |
| API Style | REST |

---

# 🏗 Architecture

```
                Client

                  │

                  ▼

         Spring Boot REST API

                  │

                  ▼

          Controller Layer

                  │

                  ▼

            Service Layer

                  │

                  ▼

          Repository Layer

                  │

                  ▼

             MySQL Database
```

---

# 📂 Project Structure

```
url-shortener
│
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── common
│   │   │   ├── controller
│   │   │   ├── dto
│   │   │   ├── error
│   │   │   ├── model
│   │   │   ├── repository
│   │   │   ├── service
│   │   │   └── UrlShortenerApplication
│   │   │
│   │   └── resources
│   │
│   └── test
│
├── docker-compose.yml
├── schema.sql
├── build.gradle
└── README.md
```

---

# ✅ Prerequisites

Before running the project, ensure the following are installed:

- Java 21
- Gradle 8+
- MySQL 8+
- Docker Desktop (Optional)
- Git

---

# ⚙ Installation

## Clone Repository

```bash
git clone https://github.com/KaRtHiK-030/url-shortener.git

cd url-shortener
```

---

## Build Project

```bash
./gradlew clean build
```

---

## Run Application

```bash
./gradlew bootRun
```

Application runs at

```
http://localhost:8080
```

---

# 🐳 Docker Deployment

Build and start the application using Docker Compose

```bash
docker-compose up --build
```

The following containers will be created:

- Spring Boot API
- MySQL Database

---

# 🔌 REST API

## Shorten URL

```
POST /shorten
```

Request

```json
{
    "fullUrl":"https://example.com"
}
```

Response

```json
{
    "shortUrl":"http://localhost:8080/abc123"
}
```

---

## Redirect

```
GET /{shortCode}
```

Redirects the user to the original URL.

---

## Health Check

```
GET /actuator/health
```

Returns application health information.

---

# 🗄 Database

The application stores URL information inside MySQL.

Main entity fields include:

| Field | Description |
|--------|-------------|
| id | Unique identifier |
| fullUrl | Original URL |
| shortUrl | Generated short code |

---

# 🔐 URL Shortening Algorithm

This project uses the **Base62 Encoding Algorithm** to generate short URLs.

### How it works

1. Store the original URL in the database.
2. Obtain the generated numeric ID.
3. Convert the numeric ID into a Base62 string.
4. Return the Base62 string as the short URL.

Base62 uses:

```
0-9
a-z
A-Z
```

Advantages:

- Short URLs
- Human readable
- Collision free
- Fast lookup
- Scalable

---

# 🚀 Future Enhancements

- JWT Authentication
- Redis Caching
- Custom URL Alias
- QR Code Generation
- URL Expiration
- Click Analytics Dashboard
- Rate Limiting
- Swagger / OpenAPI Documentation
- Unit & Integration Testing Improvements
- CI/CD Pipeline
- Kubernetes Deployment

---

# 👨‍💻 Author

**Karthik Naik**

Backend Developer | Java Developer

GitHub

https://github.com/KaRtHiK-030

LinkedIn

https://www.linkedin.com/in/karthik-naik-/

---

# ⭐ Support

If you found this project useful, consider giving it a **Star ⭐** on GitHub.

---

## 📄 License

This project is intended for educational and learning purposes.

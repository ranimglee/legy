# Legy — Modular Backend for Large-Scale Delivery Platform

## 📌 Project Overview

**Legy** is a backend system designed using **Clean Architecture** and **Domain-Driven Design (DDD)** to support the core operations of a large-scale food delivery platform. Developed as the backend component of my engineering final-year project (PFE), this repository emphasizes architectural integrity, modularity, and real-world operational workflows.

This system manages financial workflows, user roles, logistics, analytics, recommendations, and support operations in a scalable manner.

---

## 🎯 Motivation

Modern delivery platforms must support complex business logic involving:
- Financial settlements and commission distribution
- Dynamic assignment of drivers and delivery routing
- Modular handling of complaints and operational exceptions
- Extendability for future modules (e.g., forecasting or inventory)

Legy was created to explore **software engineering principles** like Clean Architecture and DDD in a real operational context while keeping maintainability and extensibility central.

---

## 🧠 Architectural Highlights

### 🏗 Design Principles
- **Clean Architecture**: Logical separation of business rules, interface adapters, and frameworks.  
- **Domain-Driven Design (DDD)**: Each bounded context represents a business domain with clear responsibilities.

### 🧩 Tech Stack
- **Language:** Java
- **Framework:** Spring Boot
- **Messaging:** Apache Kafka
- **Cache:** Redis
- **Database:** MongoDB
- **CI/CD:** Jenkins + Docker
- **Deployment:** Docker containers

---

## 📊 Core Modules & Responsibilities

| Module | Responsibility |
|--------|----------------|
| **Ordering** | Handles user orders and lifecycle transitions |
| **Delivery** | Assigns drivers and manages delivery tracking |
| **Payment** | Secures transactions and manages commissions |
| **User** | Authentication & role management |
| **Support** | Complaint handling with basic severity logic |
| **Recommendation** | Personalized suggestions for users |
| **Analytics** | Reporting and KPIs for decision support |

---

## 🔍 Research Relevance

This project demonstrates several key **software engineering research topics**:
- Architectural patterns for scalable backend systems
- Application of DDD to complex business domains
- Event-driven communication via Kafka for decoupled modules
- CI/CD pipelines supporting maintainability and rapid deployment

These themes align with research areas explored in graduate-level studies and can be referenced directly in PhD applications.

---

## 🚀 How to Run

1. Clone the repository
2. Configure environment variables for your database and messaging
3. Build with Maven:  
   ```bash
   mvn clean install

4.Run locally with Docker and Docker-Compose

5.Explore APIs with Swagger or Postman

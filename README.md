# 🚀 Core Banking System

**Secure | Scalable | Enterprise-Grade Banking App**

A modern digital banking platform enabling secure onboarding, account management, transactions, claims processing, and real-time balance updates — built with clean architecture and enterprise-grade practices.

---

## 🎯 Vision

Build a banking UI that doesn’t look like it time-traveled from 2001 — backed by a powerful, modular backend that can handle real-world financial workloads.
Open-source friendly. Production-ready.

---

## 🖥️ Tech Stack

| Layer        | Technologies                                                               |
| ------------ | -------------------------------------------------------------------------- |
| **Frontend** | Next.js, Tailwind CSS, Axios, React Query / Context API, React Router v6   |
| **Backend**  | Spring Boot, Spring Security (JWT), Spring Data JPA, Lombok, DTO + Mappers |
| **Database** | PostgreSQL (ACID-compliant)                                                |
| **Storage**  | Supabase Storage (KYC & Claims Documents)                                  |
| **DevOps**   | Docker, Docker Compose, GitHub                                             |
| **Testing**  | JUnit 5, Mockito, MockMvc, TestContainers                                  |

✔ Real banking features
✔ Scalable + clean architecture
✔ Portfolio-ready for hiring managers

---

## 🧩 System Architecture

```
Frontend (Next.js)
      ↓ API Calls
Backend (Spring Boot)
      ↓ Transactions
Database (PostgreSQL)
      ↓ File Links
Supabase Storage
```

---

## 🛡 Security & Compliance

* JWT-based authentication & authorization
* Role-based access (`ADMIN`, `CUSTOMER`)
* BCrypt password hashing
* Audit logging for sensitive operations
* Planned: API rate-limiting

Banking without security = just vibes 🫠

---

## ✅ Core Features

### 🔐 Authentication

* Register & Login
* JWT token flow
* Forgot-password (coming soon)

### 🏦 Banking Operations

* Create/Close Accounts
* Deposit / Withdraw / Transfer
* View Transactions
* Validate balance before transfer
* Account limits & status

### 📄 Claims & Support

* Upload documents (Supabase Storage)
* Claim creation
* Status tracking for admins

### 🎛 Admin Portal

* Manage users & accounts
* Approvals workflow
* Analytics dashboard (coming soon)

---

## 🏗 Backend Project Structure

*(Cleaned & consistently formatted)*

```
cbs-backend/
└── src/
    ├── main/java/com/cbs/
    │   ├── CbsApplication.java
    │   ├── config/
    │   ├── controller/
    │   ├── service/
    │   │   ├── interface/
    │   │   └── impl/
    │   ├── repository/
    │   ├── model/
    │   │   ├── entity/
    │   │   ├── dto/
    │   │   │   ├── request/
    │   │   │   └── response/
    │   │   └── enums/
    │   ├── annotation/
    │   └── aspect/
    │
    └── main/resources/
        ├── application.yml
        └── db/migration (Flyway scripts)
```

---

## 🖥 Frontend Structure (Next.js — Feature-Based Architecture)

```
cbs-frontend/
└── src/
    ├── app/
    │   ├── (auth)/
    │   ├── (dashboard)/
    │   └── api/
    ├── components/
    │   ├── layout/
    │   ├── ui/
    │   ├── features/
    │   └── shared/
    ├── lib/
    │   ├── api/
    │   ├── hooks/
    │   ├── utils/
    │   └── context/
    ├── types/
    └── styles/
```

---

## 📊 Diagrams

### 🎨 UI / UX

Coming soon – Figma link will be added.

### 🧩 UML Class Diagram

Located at:
`docs/UML_Class_Diagram.png`

Images:

<details>
  <summary>Show UML Diagrams</summary>

  <img width="1031" src="https://github.com/user-attachments/assets/57da33b7-5b73-4c6e-ae5d-0e66c06cccf5" />
  <img width="547"  src="https://github.com/user-attachments/assets/e88d70c7-e544-42d4-9bda-111c209cfdcf" />
  <img width="463"  src="https://github.com/user-attachments/assets/3483c2eb-785e-4dc1-bae4-6f89e4a4545a" />
  <img width="419"  src="https://github.com/user-attachments/assets/801d39f3-8090-40df-8309-9cea73223b7d" />
  <img width="1081" src="https://github.com/user-attachments/assets/dd8878f5-c394-42dd-9d39-50989d61006a" />
</details>

---

### 🗂 ERD Diagram

`docs/ERD_Diagram.png`

Miro Board:
🔗 [https://miro.com/app/board/uXjVJ9AxbxQ=/?share_link_id=687314840776](https://miro.com/app/board/uXjVJ9AxbxQ=/?share_link_id=687314840776)

<img width="600" src="https://github.com/user-attachments/assets/21184ef5-8bbf-4520-b0e9-7d7207a08012" />

---

## 🔗 API Documentation

Swagger UI:
`http://localhost:8080/swagger-ui/index.html`

### Endpoints (more will be added)

| Feature                 | Method | Endpoint                    | Auth       |
| ----------------------- | ------ | --------------------------- | ---------- |
| Register                | POST   | `/api/auth/register`        | ❌          |
| Login                   | POST   | `/api/auth/login`           | ❌          |
| View Accounts           | GET    | `/api/accounts`             | ✅          |
| Transfer Money          | POST   | `/api/accounts/transfer`    | ✅          |
| View Audit Logs (Admin) | GET    | `/api/audit/logs`           | Admin      |
| View User Logs          | GET    | `/api/audit/logs/user/{id}` | Self/Admin |

---

## 🧪 Testing Suite

* Unit Tests → JUnit + Mockito
* Controller Tests → MockMvc
* Integration Tests → TestContainers
* @Sql for seed/test data

---

## 🐳 Docker Support

Run backend + PostgreSQL together:

```sh
docker compose up --build
```

Runs:
✔ Backend → :8080
✔ PostgreSQL → :5432

---

## 💻 Local Development

Manual run scripts included:

* Windows → `.\run-dev.ps1`
* Linux/Mac → `./run-dev.sh`

---

## 📌 Roadmap

* ✅ JWT + RBAC
* 🔜 Notifications System
* 🔜 Automated Claim Approval
* 🔜 Multi-Currency
* 🔜 CI/CD (GitHub Actions)
* 🔜 Analytics Dashboard

Shipping season never ends 📦

---

## 🧑‍💻 Author

Built by **Sudhan** 👑
Pull requests welcome — just don’t accidentally trigger a global recession.

---

## 📜 License

To be added post-MVP.

---

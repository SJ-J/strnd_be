# ✂️ Strnd — 고객 설문·시술 기록 관리 서비스(Backend)

> 고객이 시술 전 설문을 작성하고, 디자이너가 이를 확인하여 시술 내용을 기록하는 웹 서비스의 백엔드 API 서버입니다.

<br>

## 🌐 배포 URL

| 환경 | URL |
|---|---|
| Production | https://strnd-be.onrender.com |
| Health Check | https://strnd-be.onrender.com/health |

<br>

## 🛠 Tech Stack

| Category | Stack |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security + JWT |
| ORM | MyBatis |
| Database | MySQL 8 (TiDB Cloud) |
| Build | Gradle |
| Deploy | Render (Docker) |

<br>

## 📁 Project Structure

```
src/main/java/com/strnd/api
├── auth/                  # 인증 (JWT 발급·검증)
│   ├── dto/
│   └── jwt/
├── user/                  # 사용자
│   └── domain/
├── survey/                # 설문
└── config/                # 전역 설정 (Security, CORS 등)

src/main/resources
├── application.yaml
└── mapper/                # MyBatis SQL XML
```

<br>

## ⚙️ Getting Started

### Prerequisites

- Java 21

### Setup

**1. 저장소 클론**
```bash
git clone https://github.com/SJ-J/strnd_be.git
cd strnd_be
```

**2. 로컬 환경 설정**

`src/main/resources/application-local.yaml` 파일을 생성하고 아래 내용을 입력합니다.
> ⚠️ 이 파일은 `.gitignore`에 등록되어 있어 GitHub에 올라가지 않습니다.

```yaml
spring:
  datasource:
    password: 실제_비밀번호
```

**3. IntelliJ 실행 설정**

Run Configuration → Active profiles에 `local` 입력

**4. 실행**
```bash
./gradlew bootRun
```

서버가 `http://localhost:8080`에서 실행됩니다.

<br>

## 🔐 인증 방식

JWT Bearer Token 방식을 사용합니다.

```
Authorization: Bearer {token}
```

| 역할 | 설명 |
|---|---|
| `ROLE_USER` | 고객 — 설문 작성 |
| `ROLE_DESIGNER` | 디자이너 — 설문 확인 및 시술 기록 |

<br>

## 👥 Team

| 역할 | 담당 |
|---|---|
| Backend | [@SJ-J](https://github.com/SJ-J) |
| Frontend | [@mightyantgirl](https://github.com/mightyantgirl) |

<br>

---

> **개발 기간:** 2026.06.01 ~ 2026.06.26

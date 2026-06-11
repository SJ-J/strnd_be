# ✂️ Strnd — 고객 설문·시술 기록 관리 서비스(Backend)

> 고객이 시술 전 설문을 작성하고, 디자이너가 이를 확인하여 시술 내용을 기록하는 웹 서비스의 백엔드 API 서버입니다.

<br>

## 🌐 배포 URL

| 환경 | URL |
|---|---|
| Production (Backend) | https://strnd-be.onrender.com |
| Production (Frontend) | https://strnd.vercel.app |
| Health Check | https://strnd-be.onrender.com/health |

<br>

## ⚙️ Tech Stack

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
├── auth/         # 인증 (로그인, JWT 발급·검증)
├── designer/     # 디자이너 도메인
├── customer/     # 고객 도메인
├── home/         # 홈 화면
├── visit/        # 방문 기록 (설문 시작·시술 기록)
├── survey/       # 설문 (제출)
├── service/      # 시술 서비스 메뉴
├── styleimage/   # 스타일 이미지
├── common/       # 전역 예외 처리, 공통 DTO, TypeHandler
└── config/       # 전역 설정 (Security, CORS 등)
```

<br>

## 🚀 Getting Started

```bash
git clone https://github.com/SJ-J/strnd_be.git
cd strnd_be
```

`src/main/resources/application-local.yaml` 생성 후 DB 비밀번호 입력 (`.gitignore` 등록됨)

IntelliJ Run Configuration → Active profiles: `local`

```bash
./gradlew bootRun
```

<br>

## 🔐 인증 방식

JWT Bearer Token 방식을 사용합니다.

```
Authorization: Bearer {accessToken}
```

- `rememberMe: true` → 7일 / `rememberMe: false` → 8시간

<br>

## 📋 API

### 🔐 Auth

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| POST | `/api/auth/signup` | 불필요 | 관리자 회원가입 |
| POST | `/api/auth/login` | 불필요 | 관리자 로그인 |

<br>

### 💈 Customers

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| GET | `/api/customers` | 필요 | 고객 목록 조회 (비활성 포함, 활성 우선 정렬) |
| GET | `/api/customers?keyword={검색어}` | 필요 | 고객 검색 (이름) |
| GET | `/api/customers/{customerId}` | 필요 | 고객 상세 조회 |
| GET | `/api/customers/{customerId}/visits` | 필요 | 고객 방문 히스토리 조회 |
| POST | `/api/customers` | 필요 | 고객 등록 |
| PUT | `/api/customers/{customerId}` | 필요 | 고객 정보 수정 |

> 방문 히스토리는 `serviceCodes` / `startDate` / `endDate` 파라미터로 필터링 가능

<br>

### 📝 Visits

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| POST | `/api/visits` | 필요 | 방문 기록 생성 (설문 시작 또는 설문 없이 바로 기록) |
| GET | `/api/visits/{visitId}` | 필요 | 방문 기록 단건 조회 |
| PUT | `/api/visits/{visitId}/treatment` | 필요 | 시술 내용 기록 |

> `skipSurvey: true` 요청 시 설문 없이 STATUS='COMPLETED'로 생성

<br>

### 🧴 Services

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| GET | `/api/services` | 필요 | 서비스(시술 메뉴) 목록 조회 |

<br>

### 🖼️ Style Images

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| GET | `/api/style-images` | 불필요 | 스타일 이미지 목록 조회 |

> `gender` / `serviceCode` 파라미터로 필터링 가능 (설문 STEP3 이미지 선택용)

<br>

### 📝 Survey

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| POST | `/api/survey/{surveyToken}` | 불필요 | 설문 제출 |

> `consentRequiredYn`(필수 동의) 미체크 시 `400` / 만료 토큰 `410` / 중복 제출 `409`

<br>

### 🏠 Home

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| GET | `/api/home` | 필요 | 홈 화면 조회 (이번 달 방문 수, 최근 방문 고객 5명) |

<br>

## 👥 Team

| 역할 | 담당 |
|---|---|
| 🔧 DB · Backend | [@SJ-J](https://github.com/SJ-J) |
| 🎨 Design · Publishing · Frontend | [@mightyantgirl](https://github.com/mightyantgirl) |

<br>

---

> **개발 기간:** 2026.06.01 ~ 2026.06.26

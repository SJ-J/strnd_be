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
├── auth/                  # 인증 (로그인, JWT 발급·검증)
│   ├── dto/
│   └── jwt/
├── designer/              # 디자이너 도메인
│   └── domain/
├── customer/              # 고객 도메인
│   ├── dto/
│   └── domain/
├── home/                  # 홈 화면
│   └── dto/
├── visit/                 # 방문 기록 (설문 시작·시술 기록)
│   ├── dto/
│   └── domain/
├── survey/                # 설문 (제출)
│   └── dto/
├── service/               # 시술 서비스 메뉴
│   ├── dto/
│   └── domain/
├── styleimage/            # 스타일 이미지
│   ├── dto/
│   └── domain/
├── common/                # 전역 예외 처리, 공통 DTO, TypeHandler
│   ├── dto/
│   └── typehandler/
└── config/                # 전역 설정 (Security, CORS 등)

src/main/resources
├── application.yaml
└── mapper/                # MyBatis SQL XML
    ├── designer/
    ├── customer/
    ├── home/
    ├── visit/
    ├── survey/
    ├── service/
    └── styleimage/
```

<br>

## 🚀 Getting Started

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
> 🔗 서버가 `http://localhost:8080`에서 실행됩니다.

<br>

## 🔐 인증 방식

JWT Bearer Token 방식을 사용합니다.

```
Authorization: Bearer {accessToken}
```

- 로그인 성공 시 발급된 `accessToken`을 이후 모든 API 요청 헤더에 포함합니다.
- 토큰 유효 기간: `rememberMe: true` → 7일 / `rememberMe: false` → 8시간

<br>

## 📋 API

### 🔐 Auth

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| POST | `/api/auth/signup` | 불필요 | 관리자 회원가입 |
| POST | `/api/auth/login` | 불필요 | 관리자 로그인 |

**POST /api/auth/signup**

Request
```json
{
    "designerName": "임코비",
    "phone": "01020250205",
    "pinCode": "1234"
}
```

Response `201`
```json
{
    "accessToken": "eyJhbGci...",
    "designerId": 1,
    "designerName": "임코비"
}
```

Response `409` — 중복 연락처

<br>

**POST /api/auth/login**

Request
```json
{
    "phone": "01020250205",
    "pinCode": "5678",
    "rememberMe": true
}
```

> `rememberMe` — `true` 시 7일, `false` 시 8시간 토큰 발급 (기본값: `false`)

Response `200`
```json
{
    "accessToken": "eyJhbGci...",
    "designerId": 1,
    "designerName": "임코비"
}
```

Response `401` — 연락처 또는 PIN 불일치 / 비활성 계정

<br>

### 💈 Customers

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| GET | `/api/customers` | 필요 | 고객 목록 조회 |
| GET | `/api/customers?keyword={검색어}` | 필요 | 고객 검색 (이름) |
| GET | `/api/customers/{customerId}` | 필요 | 고객 상세 조회 |
| GET | `/api/customers/{customerId}/visits` | 필요 | 고객 방문 히스토리 조회 |
| POST | `/api/customers` | 필요 | 고객 등록 |
| PUT | `/api/customers/{customerId}` | 필요 | 고객 정보 수정 |

**GET /api/customers**

Response `200`
```json
[
  {
    "customerId": 1,
    "customerName": "주수진",
    "phone": "01019931219",
    "gender": "FEMALE",
    "memo": "탈색했지만 앞머리 펌 하고 싶어요",
    "isActive": true,
    "lastVisitDt": "2026-05-31T08:48:07",
    "regDt": "2026-05-31T08:48:07"
  }
]
```

> 비활성 고객(`isActive: false`) 포함. 활성 우선(`IS_ACTIVE DESC`) → 최근 방문일 내림차순 정렬

**GET /api/customers?keyword={검색어}**

> 고객명(이름)으로 검색. `keyword` 미입력 시 전체 목록 반환
>
> 비활성 고객 포함, 활성 우선 정렬

Response `200` — 전체 목록과 동일한 구조

**GET /api/customers/{customerId}**

Response `200`
```json
{
  "customerId": 1,
  "customerName": "주수진",
  "phone": "01019931219",
  "gender": "FEMALE",
  "memo": "탈색했지만 앞머리 펌 하고 싶어요",
  "isActive": true,
  "lastVisitDt": "2026-05-31T08:48:07",
  "regDt": "2026-05-31T08:48:07"
}
```

Response `404` — 미담당 고객 ID

**POST /api/customers**

Request
```json
{
  "customerName": "임희진",
  "phone": "01019970901",
  "gender": "FEMALE",
  "memo": "반려견과 같은 헤어스타일 희망해요"
}
```
> `gender` — `FEMALE` / `MALE` 중 하나, 생략 가능 (설문 제출 시 자동 갱신)
>
> `memo` 생략 가능

Response `201` — 등록된 고객 정보

Response `409` — 중복 연락처

**PUT /api/customers/{customerId}**

Request — POST와 동일한 구조

Response `200` — 수정된 고객 정보

Response `404` — 미담당 고객 ID

<br>

### 📝 Visits

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| POST | `/api/visits` | 필요 | 방문 기록 생성 (설문 시작 또는 설문 없이 바로 기록) |
| GET | `/api/visits/{visitId}` | 필요 | 방문 기록 단건 조회 |
| PUT | `/api/visits/{visitId}/treatment` | 필요 | 시술 내용 기록 |

**POST /api/visits**

> `skipSurvey` — `false`(기본값): 설문 시작, `true`: 설문 없이 STATUS='COMPLETED'로 바로 생성 후 `/api/visits/{visitId}/treatment` 재사용

Request — 설문 시작
```json
{
  "customerId": 60001
}
```

Response `201`
```json
{
  "visitId": 30001,
  "surveyToken": "1acc356c2e374877b7a4b44f53f2b915",
  "surveyUrl": "https://strnd.example.com/survey/1acc356c2e374877b7a4b44f53f2b915"
}
```

Request — 설문 없이 바로 기록
```json
{
  "customerId": 60001,
  "skipSurvey": true
}
```

Response `201`
```json
{
  "visitId": 90001,
  "surveyToken": null,
  "surveyUrl": null
}
```

Response `404` — 미담당 고객 ID

**GET /api/customers/{customerId}/visits**

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `serviceCodes` | String (다중) | 선택 | 서비스 코드 필터 (CUT / PERM / COLOR / CLINIC / ETC) |
| `startDate` | yyyy-MM-dd | 선택 | 조회 시작일 |
| `endDate` | yyyy-MM-dd | 선택 | 조회 종료일 |

```
GET /api/customers/1/visits?serviceCodes=CUT&serviceCodes=COLOR&startDate=2026-01-01&endDate=2026-06-07
```

Response `200`
```json
[
  {
    "visitId": 10,
    "status": "COMPLETED",
    "visitDt": "2026-06-07T17:11:33",
    "services": "COLOR",
    "treatmentMenu": [
      "코비네 특급 무손상 탈색제",
      "프리미엄 클리닉"
    ],
    "treatmentProduct": "유호표 7NB",
    "treatmentDetail": "7NB 0.6 톤다운",
    "treatmentNote": "극손상모"
  }
]
```

> 최신 방문순(VISIT_DT 내림차순) 정렬
>
> 파라미터 미입력 시 전체 조회
>
> 시술 기록 전이면 treatment 필드 전체 `null`

Response `404` — 존재하지 않거나 미담당 customerId

**GET /api/visits/{visitId}**

Response `200`
```json
{
  "visitId": 1,
  "status": "COMPLETED",
  "visitDt": "2026-06-07T10:10:42",
  "submitDt": "2026-06-07T01:14:16",
  "customerId": 1,
  "customerName": "주수진",
  "phone": "01019931219",
  "gender": "FEMALE",
  "visitRoute": "SNS",
  "refDesigner": "임희진",
  "services": "COLOR",
  "moods": ["깔끔하고 단정한", "세련되고 고급스러운"],
  "styleImageIds": [1, 2],
  "hairConcerns": ["모발 손상", "볼륨 부족"],
  "requestMemo": "숱 많이 쳐주세요",
  "treatmentMenu": ["시세이도 컬러", "기본 클리닉"],
  "treatmentProduct": "웰라 7NB",
  "treatmentDetail": "7NB 0.6 톤다운",
  "treatmentNote": "두피 민감"
}
```

> 설문 전(PENDING) 상태면 survey 필드 전체 `null`
>
> 시술 기록 전이면 treatment 필드 전체 `null`

Response `404` — 존재하지 않거나 미담당 visitId

**PUT /api/visits/{visitId}/treatment**

Request
```json
{
  "serviceCode": "COLOR",
  "treatmentMenu": ["시세이도 컬러", "기본 클리닉"],
  "treatmentProduct": "웰라 7NB",
  "treatmentDetail": "7NB 0.6 톤다운",
  "treatmentNote": "두피 민감"
}
```

> `serviceCode` — 필수. 고객 설문의 `services` 값을 기본으로 노출하고 디자이너가 변경 가능
>
> `treatmentMenu` — 필수. 시술 메뉴명 배열 (콤마 구분 자유 입력)
>
> `treatmentProduct`, `treatmentDetail`, `treatmentNote` — 선택

Response `200`
```json
{ "message": "시술 내용이 기록되었습니다." }
```

Response `404` — 존재하지 않거나 미담당 visitId

<br>

### 🧴 Services

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| GET | `/api/services` | 필요 | 서비스(시술 메뉴) 목록 조회 |

**GET /api/services**

Response `200`
```json
[
  { "serviceId": 1, "serviceCode": "CUT",    "serviceName": "컷",    "sortOrder": 1 },
  { "serviceId": 2, "serviceCode": "PERM",   "serviceName": "펌",    "sortOrder": 2 },
  { "serviceId": 3, "serviceCode": "COLOR",  "serviceName": "컬러",  "sortOrder": 3 },
  { "serviceId": 4, "serviceCode": "CLINIC", "serviceName": "클리닉","sortOrder": 4 },
  { "serviceId": 5, "serviceCode": "ETC",    "serviceName": "상담",  "sortOrder": 5 }
]
```

> `IS_ACTIVE = 1` 인 항목만, `SORT_ORDER` 오름차순 정렬

<br>

### 🖼️ Style Images

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| GET | `/api/style-images` | 불필요 | 스타일 이미지 목록 조회 |

**GET /api/style-images**

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `gender` | String | 선택 | 성별 필터 (FEMALE / MALE) |
| `serviceCode` | String | 선택 | 서비스 코드 필터 (CUT / PERM / COLOR / CLINIC / ETC) |

```
GET /api/style-images?gender=FEMALE&serviceCode=COLOR
```

Response `200`
```json
[
  {
    "imageId": 1,
    "serviceId": 3,
    "gender": "FEMALE",
    "imageUrl": "https://example.com/color-style.jpg",
    "imageAlt": "컬러 스타일 예시",
    "sortOrder": 1
  }
]
```

> `IS_ACTIVE = 1` 인 항목만, `SERVICE_ID → SORT_ORDER` 오름차순 정렬
>
> 파라미터 미입력 시 전체 조회
>
> 인증 불필요 (설문 STEP3 이미지 선택용 — 고객 성별·선택 서비스 기준 필터링)

<br>

### 📝 Survey

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| POST | `/api/survey/{surveyToken}` | 불필요 | 설문 제출 |

**POST /api/survey/{surveyToken}**

Request
```json
{
  "consentRequiredYn": true,
  "consentOptionalYn": false,
  "gender": "FEMALE",
  "visitRoute": "SNS",
  "refDesigner": "임희진",
  "serviceId": 3,
  "moods": ["깔끔하고 단정한", "세련되고 고급스러운"],
  "styleImageIds": [1, 2],
  "hairConcerns": ["모발 손상", "볼륨 부족"],
  "requestMemo": "숱 많이 쳐주세요"
}
```
> `consentRequiredYn` — 개인정보 수집·이용 필수 동의 (필수)
>
> `consentOptionalYn` — 민감정보 수집·이용 선택 동의 (생략 가능)
>
> `gender` — `FEMALE` / `MALE` 중 하나 (필수, STEP0)
>
> `serviceId` — mg_services의 SERVICE_ID (설문 STEP1 선택값)
>
> `gender`, `consentRequiredYn` 외 모든 필드 생략 가능

Response `200`
```json
{ "message": "설문이 제출되었습니다." }
```

Response `400` — 필수 동의 미체크

Response `404` — 유효하지 않은 토큰

Response `409` — 이미 제출된 설문

Response `410` — 만료된 토큰

<br>

### 🏠 Home

| Method | URL | 인증 | 설명 |
|---|---|---|---|
| GET | `/api/home` | 필요 | 홈 화면 조회 |

**GET /api/home**

Response `200`
```json
{
  "monthlyVisitCount": 3,
  "recentCustomers": [
    {
      "customerId": 1,
      "customerName": "주수진",
      "phone": "01019931219",
      "lastVisitDt": "2026-06-02T10:00:00"
    }
  ]
}
```

> `monthlyVisitCount` — 이번 달 방문 수 (tb_visit_record.VISIT_DT 기준)
>
> `recentCustomers` — 최근 방문 고객 최대 5명 (LAST_VISIT_DT 내림차순)

<br>

## 👥 Team

| 역할                              | 담당        |
|---------------------------------|-----------|
| 🔧 DB · Backend                 | [@SJ-J](https://github.com/SJ-J) |
| 🎨 Design · Publishing · Frontend | [@mightyantgirl](https://github.com/mightyantgirl) |

<br>

---

> **개발 기간:** 2026.06.01 ~ 2026.06.26

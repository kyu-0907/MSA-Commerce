# Auth Server

JWT 기반 인증 서버입니다. 아이디는 이메일을 사용하고, 비밀번호는 사용자가 직접 정합니다.

이번 단계의 범위는 **이메일 인증을 거치는 회원가입**입니다. 이메일 중복 여부를 먼저 확인하고,
`JavaMailSender`로 인증 코드를 발송해 본인 확인을 마친 뒤에야 비밀번호를 받아 가입을 완료합니다.
로그인/토큰 발급 등은 이후 단계에서 별도로 설계합니다.

## 현재 구성 (기존 세팅)

| 항목 | 내용 |
|---|---|
| 인증 방식 | JWT (`io.jsonwebtoken:jjwt` 0.12.6, 로그인 단계에서 사용 예정) |
| 비밀번호 저장 | BCrypt (`PasswordEncoder` 빈이 `SecurityConfig`에 이미 등록되어 있음) |
| DB | MySQL (`authdb`), Spring Data JPA |
| 세션 정책 | STATELESS (세션 미사용, 토큰 기반) |
| 이메일 발송 | *(추가 필요)* `spring-boot-starter-mail` + `JavaMailSender` |
| 인증 코드 저장 | *(추가 필요)* Redis (컨테이너는 이미 떠 있음, `application.yml` 설정만 없음) |

`SecurityConfig`는 현재 `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`만 permitAll이고 나머지는
인증을 요구합니다. 회원가입 관련 엔드포인트는 모두 `/api/auth/**` 하위에 만들면 되므로 별도 보안 설정
변경은 필요 없습니다.

## 이번 단계 목표: 이메일 인증 회원가입

전체 흐름은 3단계입니다.

```
1) 이메일 제출        →  중복 확인 + 인증 코드 발송 (JavaMailSender)
2) 인증 코드 확인      →  코드 일치 시 "인증 완료" 상태로 전환
3) 비밀번호 제출        →  인증 완료된 이메일에 한해 최종 회원가입 처리
```

- 아이디 = 이메일
- 비밀번호 = 사용자가 직접 입력 (평문으로 저장하지 않고 BCrypt로 해싱)
- 이메일 중복 가입 방지 (1단계에서 먼저 걸러내고, 3단계에서 한 번 더 확인)
- 이메일 인증을 통과하지 못하면 비밀번호를 받지 않음 (회원가입 자체가 불가)
- 성공 시 비밀번호를 절대 응답에 포함하지 않음

### 도메인 모델: `User`

| 필드 | 타입 | 설명 |
|---|---|---|
| `id` | `Long` (PK, auto increment) | 내부 식별자 |
| `email` | `String`, unique, not null | 로그인 아이디로 사용, 인증 완료된 이메일만 저장 |
| `password` | `String`, not null | BCrypt 해시값 |
| `createdAt` | `LocalDateTime` | 가입 시각 |

> 이메일 인증 여부 자체는 `User` 테이블에 컬럼으로 남기지 않습니다. 인증을 통과해야만 `User` row가
> 생성되므로, `User`가 존재한다는 것 자체가 "인증된 이메일"이라는 의미가 됩니다.
> 인증 진행 중 상태(코드 발송됨/인증됨)는 Redis에서만 임시로 관리합니다.

### 인증 코드 저장: Redis

DB에 별도 테이블을 두지 않고, TTL이 있는 Redis 키로 관리합니다. 인증이 끝나면 자연스럽게
사라지도록 하기 위함입니다.

| 키 | 값 | TTL | 용도 |
|---|---|---|---|
| `email-verification:{email}` | 6자리 인증 코드 | 5분 | 2단계에서 사용자가 입력한 코드와 비교 |
| `email-verified:{email}` | `true` | 30분 | 3단계 회원가입 시 "인증을 통과한 이메일"인지 확인 |

### API 1: `POST /api/auth/signup/email` — 인증 코드 발송

**Request
```json
{
  "email": "user@example.com"
}
```

**처리 흐름**
1. 이메일 형식 검증 (`@Email`)
2. `UserRepository.existsByEmail(email)`로 중복 확인 → 이미 가입된 이메일이면 즉시 거부
3. 6자리 랜덤 숫자 인증 코드 생성
4. Redis에 `email-verification:{email}` = 코드, TTL 5분으로 저장 (재요청 시 덮어씀)
5. `JavaMailSender`로 인증 코드가 담긴 메일 발송

**Response — 200 OK**
```json
{
  "email": "user@example.com",
  "message": "인증 코드를 이메일로 발송했습니다."
}
```

### API 2: `POST /api/auth/signup/email/verify` — 인증 코드 확인

**Request**
```json
{
  "email": "user@example.com",
  "code": "482913"
}
```

**처리 흐름**
1. Redis에서 `email-verification:{email}` 조회
2. 키가 없으면(만료) 또는 코드가 다르면 실패 처리
3. 일치하면 `email-verification:{email}` 삭제하고, `email-verified:{email}` = `true`, TTL 30분으로 저장

**Response — 200 OK**
```json
{
  "email": "user@example.com",
  "verified": true
}
```

### API 3: `POST /api/auth/signup` — 비밀번호 제출 및 가입 완료

**Request**
```json
{
  "email": "user@example.com",
  "password": "P@ssw0rd123"
}
```

| 필드 | 검증 규칙 |
|---|---|
| `email` | 비어있지 않음, 이메일 형식(`@Email`) |
| `password` | 비어있지 않음, 최소 8자 ~ 최대 64자 |

**처리 흐름**
1. `AuthController`가 `SignupRequest`를 받아 `@Valid`로 형식 검증
2. Redis에서 `email-verified:{email}` 존재 여부 확인 → 없으면(인증 안 함/만료) 거부
3. `UserRepository.existsByEmail(email)`로 중복 재확인 (2단계 이후 동시에 가입 시도하는 경우 대비)
4. `PasswordEncoder`로 비밀번호 해싱 후 `User` 엔티티 저장
5. `email-verified:{email}` Redis 키 삭제
6. 저장된 `User`를 `SignupResponse`(비밀번호 제외)로 변환해 응답

**Response — 201 Created**
```json
{
  "id": 1,
  "email": "user@example.com",
  "createdAt": "2026-09-17T10:00:00"
}
```

### 에러 응답

에러 응답 포맷은 프로젝트 전반에서 재사용할 수 있도록 아래처럼 통일합니다.

```json
{
  "code": "EMAIL_NOT_VERIFIED",
  "message": "이메일 인증을 먼저 완료해주세요."
}
```

| 상황 | 상태 코드 | 코드 |
|---|---|---|
| 요청값 검증 실패 (형식 오류, 빈 값 등) | `400 Bad Request` | `VALIDATION_ERROR` |
| 인증 코드 불일치 또는 만료 | `400 Bad Request` | `INVALID_OR_EXPIRED_CODE` |
| 인증을 완료하지 않고 회원가입 시도 | `400 Bad Request` | `EMAIL_NOT_VERIFIED` |
| 이미 가입된 이메일 | `409 Conflict` | `EMAIL_ALREADY_EXISTS` |
| 메일 발송 실패 | `500 Internal Server Error` | `MAIL_SEND_FAILED` |

## 예상 패키지 구조

```
com.example.auth
├── controller
│   └── AuthController.kt              # /api/auth/signup/email, /email/verify, /signup
├── service
│   ├── EmailVerificationService.kt    # 코드 생성, Redis 저장/조회, 메일 발송
│   └── AuthService.kt                 # 회원가입 완료 처리
├── domain
│   └── User.kt                        # JPA 엔티티
├── repository
│   └── UserRepository.kt              # existsByEmail, save 등
├── dto
│   ├── EmailSignupRequest.kt          # 1단계: email
│   ├── EmailVerifyRequest.kt          # 2단계: email, code
│   ├── SignupRequest.kt               # 3단계: email, password
│   └── SignupResponse.kt
├── exception
│   ├── EmailAlreadyExistsException.kt
│   ├── InvalidOrExpiredCodeException.kt
│   ├── EmailNotVerifiedException.kt
│   └── GlobalExceptionHandler.kt
├── config
│   ├── SecurityConfig.kt              # (기존)
│   └── MailConfig.kt                  # JavaMailSender 빈 (필요 시)
└── security
    └── JwtTokenProvider.kt            # (기존, 로그인 단계에서 본격 사용)
```

## 필요한 설정/의존성 추가

`build.gradle.kts`
```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}
```

`application.yml`
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
  data:
    redis:
      host: redis-auth
      port: 6379

mail:
  verification:
    code-ttl-seconds: 300      # 5분
    verified-ttl-seconds: 1800 # 30분
```

> 메일 계정 정보는 코드/설정 파일에 그대로 커밋하지 않고 환경변수(`MAIL_USERNAME`, `MAIL_PASSWORD`)로 주입합니다.

## 다음 단계 (지금은 만들지 않음)

- 로그인 (`POST /api/auth/login`) → 이메일/비밀번호 검증 후 JWT 발급
- Access/Refresh 토큰 분리, 로그아웃/블랙리스트
- 인증 필터(`JwtAuthenticationFilter`)로 `Authorization: Bearer <token>` 검증
- 인증 코드 재발송 시 요청 빈도 제한(rate limit) — 메일 스팸 방지

## 체크리스트

- [ ] `application.yml`에 `spring.mail.*`, `spring.data.redis.*` 설정 추가
- [ ] `build.gradle.kts`에 `spring-boot-starter-mail`, `spring-boot-starter-data-redis` 추가
- [ ] `User` 엔티티 + `UserRepository`
- [ ] `EmailSignupRequest` / `EmailVerifyRequest` / `SignupRequest` / `SignupResponse` DTO
- [ ] `EmailVerificationService` (코드 생성, Redis 저장/조회, 메일 발송)
- [ ] `AuthService.signup()` (인증 완료 여부 확인 후 `User` 저장)
- [ ] `AuthController` (`POST /api/auth/signup/email`, `/email/verify`, `/signup`)
- [ ] 이메일 중복/인증 실패/검증 실패에 대한 전역 예외 처리
- [ ] Swagger에서 3단계 회원가입 플로우 확인

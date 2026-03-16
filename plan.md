# Reminder Web App - 개발 계획

## Tech Stack 상세

### Backend
- **Spring Boot 4.0.3** / Java 25
- **Spring Data JPA** + **H2** (in-memory DB, `jdbc:h2:mem:reminder`)
- **Lombok** (boilerplate 제거)
- API: REST, JSON 응답
- 빌드: Gradle (Kotlin DSL)

### Frontend
- **Next.js** (latest, App Router)
- **TypeScript** (strict mode)
- **Tailwind CSS** (유틸리티 기반 스타일링)
- 개발 포트: 3000 → 백엔드 8080 프록시

### 개발 환경
- Backend: `./gradlew bootRun` (port 8080)
- Frontend: `cd frontend && npm run dev` (port 3000)
- Next.js `rewrites`로 `/api/**` 요청을 백엔드로 프록시

---

## Phase 1: 백엔드 - Reminder 기본 API

> 목표: Reminder CRUD + 필터/검색 API 완성

### 1-1. Spring Boot 프로젝트 구성
- `Reminder` 엔티티 생성 (id, title, description, remindAt, completed, createdAt)
- `ReminderRepository` 생성 (JpaRepository)
- `ReminderService` 생성 (CRUD 로직)
- `ReminderController` 생성 (REST API)
- CORS 설정 (`WebMvcConfigurer`, `http://localhost:3000` 허용)
- `application.yml` H2 설정

### 1-2. 필터 및 검색 API
- `GET /api/reminders?filter=today|scheduled|completed` - 필터 조회
- `GET /api/reminders?search=keyword` - 제목/메모 검색
- `GET /api/reminders/counts` - 필터별 카운트

**이 Phase 완료 시**: Reminder CRUD + 필터 + 검색 API 전체 동작

---

## Phase 2: 백엔드 - 리스트(그룹) 관리

> 목표: ReminderList CRUD API, 리마인더-리스트 연결

### 2-1. ReminderList 엔티티 및 API
- `ReminderList` 엔티티: id, name, color, createdAt
- `Reminder`에 `reminderList` (외래키, nullable) 추가
- `ReminderListRepository` / `ReminderListService` / `ReminderListController`
- API: `GET/POST/PUT/DELETE /api/lists`, `GET /api/lists/{id}/reminders`

**이 Phase 완료 시**: 리마인더를 리스트로 분류하는 백엔드 API 완성

---

## Phase 3: 백엔드 - 고도화 (우선순위, 하위 작업)

> 목표: 우선순위, Subtask 기능 백엔드 완성

### 3-1. 우선순위
- `Priority` enum (NONE, LOW, MEDIUM, HIGH)
- `Reminder` 엔티티에 `priority` 필드 추가

### 3-2. 하위 작업 (Subtask)
- `Subtask` 엔티티 (id, title, completed, reminder, sortOrder)
- `SubtaskRepository` / `SubtaskService` / `SubtaskController`
- API: `GET/POST/PUT/DELETE /api/reminders/{id}/subtasks`

**이 Phase 완료 시**: 백엔드 전체 기능 완성 (Reminder + List + Priority + Subtask)

---

## Phase 4: 프론트엔드 - 기본 UI + CRUD

> 목표: Next.js 세팅, 리마인더 목록/생성/수정/삭제/완료, Apple 스타일 레이아웃

### 4-1. Next.js 프로젝트 초기화
- `frontend/` 디렉토리에 Next.js 프로젝트 생성 (App Router, TypeScript, Tailwind CSS)
- `next.config.ts`에 API 프록시 설정
- TypeScript 타입 정의, API 클라이언트 모듈

### 4-2. 리마인더 목록 + 생성 UI
- 메인 페이지에 전체 리마인더 목록 표시
- 원형 체크박스 (Apple 스타일), 항목 구조, 인덴트된 구분선
- 목록 하단 "+ 새로운 미리 알림" 버튼, 인라인 입력 필드

### 4-3. CRUD 완성
- 완료 토글 (체크 애니메이션)
- 삭제 (hover 시 버튼, 즉시 삭제, 높이 축소 애니메이션)
- 인라인 편집 + 상세 편집 패널

### 4-4. Apple 스타일 레이아웃
- 사이드바 + 메인 영역 2단 레이아웃
- SF Pro 폰트, 둥근 모서리, 기본 반응형

**이 Phase 완료 시**: 리마인더 CRUD 전체 동작 + Apple Reminder 기본 레이아웃

---

## Phase 5: 프론트엔드 - 리스트 + 필터 + 고도화 기능

> 목표: 리스트 관리 UI, 필터 카드, 우선순위/하위 작업/검색 UI

### 5-1. 리스트 관리 UI
- 사이드바 리스트 목록 (컬러 아이콘 + 이름 + 항목 수)
- 리스트 생성/수정/삭제, 색상 팔레트

### 5-2. 필터 카드 UI
- 사이드바 상단 2x2 그리드 필터 카드 (오늘/예정/전체/완료됨)
- 카운트 배지 연동, 예정 뷰 날짜 그룹핑

### 5-3. 고도화 기능 UI
- 우선순위 선택/표시/정렬
- 하위 작업 추가/완료/삭제, 진행률 표시
- 검색 바, 실시간 검색, 결과 하이라이트

**이 Phase 완료 시**: 프론트엔드 전체 기능 구현 완료

---

## Phase 6: UI 폴리싱 + 다크 모드

> 목표: Apple Reminder 수준의 완성도

### 6-1. 다크 모드
- `prefers-color-scheme` 연동, Tailwind `dark:` 유틸리티

### 6-2. 애니메이션 강화
- 항목 추가/삭제 transition, 리스트 전환 fade/slide
- 체크박스 완료 애니메이션 정교화

### 6-3. 스와이프 삭제
- 좌측 스와이프 → 빨간 삭제 버튼

### 6-4. 반응형 정교화
- 모바일/태블릿/데스크톱 레이아웃 최적화

**이 Phase 완료 시**: Apple Reminder와 시각적으로 유사한 완성된 UI

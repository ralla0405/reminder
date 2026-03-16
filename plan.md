# Reminder Web App - 개발 계획

## Tech Stack 상세

### Backend (기존)
- **Spring Boot 4.0.3** / Java 25
- **Spring Data JPA** + **H2** (in-memory DB, `jdbc:h2:mem:reminderdb`)
- **Lombok** (boilerplate 제거)
- API: REST (`/api/reminders`), JSON 응답
- 빌드: Gradle (Kotlin DSL)

### Frontend (신규)
- **Next.js** (latest, App Router)
- **TypeScript** (strict mode)
- **Tailwind CSS** (유틸리티 기반 스타일링)
- 개발 포트: 3000 → 백엔드 8080 프록시

### 개발 환경
- Backend: `./gradlew bootRun` (port 8080)
- Frontend: `cd frontend && npm run dev` (port 3000)
- Next.js `rewrites`로 `/api/**` 요청을 백엔드로 프록시

---

## Phase 1: 프로젝트 세팅 + 기본 목록

> 목표: Next.js 프로젝트 생성, 백엔드 연동, 리마인더 목록 표시

### 1-1. Next.js 프로젝트 초기화
- `frontend/` 디렉토리에 Next.js 프로젝트 생성 (App Router, TypeScript, Tailwind CSS)
- `next.config.ts`에 API 프록시 설정 (`/api/**` → `http://localhost:8080`)
- TypeScript 타입 정의 (`types/reminder.ts`)
- API 클라이언트 모듈 (`lib/api.ts`) - fetch 기반

### 1-2. 백엔드 CORS 설정
- `WebMvcConfigurer` 구현으로 `http://localhost:3000` 허용

### 1-3. 리마인더 목록 UI
- 메인 페이지에 전체 리마인더 목록 표시
- 원형 체크박스 (Apple 스타일)
- 항목 구조: 체크박스 | 제목 + 메모(1줄) + 날짜
- 완료된 항목: 취소선 + 흐림 처리
- 인덴트된 구분선

### 1-4. 리마인더 생성
- 목록 하단 "+ 새로운 미리 알림" 버튼
- 클릭 시 인라인 입력 필드 (제목 필수)
- Enter로 저장, Escape로 취소
- 저장 후 즉시 목록에 반영

**이 Phase 완료 시**: 리마인더를 보고 추가할 수 있는 기본 화면 동작

---

## Phase 2: CRUD 완성 + 기본 스타일링

> 목표: 수정/삭제/완료 토글, Apple 스타일 기본 레이아웃

### 2-1. 완료 토글
- 체크박스 클릭으로 완료/미완료 전환
- `PATCH /api/reminders/{id}/complete` 호출
- 체크 애니메이션 (원형 채움 0.3s ease)

### 2-2. 삭제
- 항목에 삭제 버튼 (hover 시 노출)
- 클릭 시 즉시 삭제 (확인 없음, Apple 스타일)
- 삭제 시 높이 축소 애니메이션

### 2-3. 수정
- 항목 클릭 시 인라인 편집 모드 (제목/메모)
- (i) 버튼 → 상세 편집 패널 (슬라이드-인)
- 상세 패널에서 날짜/시간 설정 가능

### 2-4. Apple 스타일 레이아웃
- 사이드바(고정) + 메인 영역 2단 레이아웃
- 사이드바: 연한 회색 배경 (`#F2F2F7`), "전체" 항목만 표시 (Phase 1 수준)
- SF Pro 폰트, 둥근 모서리 (10-12px)
- 기본 반응형 (모바일에서 사이드바 숨김/토글)

**이 Phase 완료 시**: CRUD 전체 동작 + Apple Reminder와 유사한 기본 레이아웃

---

## Phase 3: 리스트(그룹) 관리

> 목표: 리마인더를 그룹으로 분류, 사이드바에서 리스트 전환

### 3-1. 백엔드 확장 - ReminderList 엔티티
- `ReminderList` 엔티티: id, name, color, createdAt
- `Reminder`에 `listId` (외래키, nullable) 추가
- `ReminderListRepository` / `ReminderListService` / `ReminderListController`
- API: `GET/POST/PUT/DELETE /api/lists`

### 3-2. 사이드바 - 리스트 목록
- 리스트별 원형 컬러 아이콘 + 이름 + 항목 수
- 리스트 생성/수정/삭제 UI
- 색상 선택 팔레트 (Apple 기본 12색)

### 3-3. 리스트별 리마인더 표시
- 사이드바에서 리스트 선택 → 해당 리스트의 리마인더만 표시
- 리마인더 생성/수정 시 리스트 지정 가능
- 메인 영역 상단에 리스트 이름 + 색상 헤더

**이 Phase 완료 시**: 리마인더를 그룹으로 분류하고 사이드바에서 전환

---

## Phase 4: 필터 뷰 + 스마트 목록

> 목표: 오늘/예정/전체/완료됨 필터, 사이드바 상단 카드

### 4-1. 백엔드 확장 - 필터 API
- `GET /api/reminders?filter=today` - 오늘 알림
- `GET /api/reminders?filter=scheduled` - 알림 날짜 있는 것 (날짜순)
- `GET /api/reminders?filter=completed` - 완료된 것
- (전체는 기존 `GET /api/reminders`)

### 4-2. 필터 카드 UI
- 사이드바 상단 2x2 그리드 카드
- 오늘 (파랑) / 예정 (빨강) / 전체 (검정) / 완료됨 (회색)
- 각 카드에 아이콘 + 숫자 배지
- 카드 클릭 시 메인 영역에 필터된 목록 표시

### 4-3. 예정 뷰 날짜 그룹핑
- 날짜별 섹션 헤더로 그룹핑 (오늘, 내일, 이번 주, 이후)

**이 Phase 완료 시**: Apple Reminder의 스마트 필터 기능 완성

---

## Phase 5: UI 폴리싱 + 다크 모드

> 목표: Apple Reminder 수준의 완성도

### 5-1. 다크 모드
- `prefers-color-scheme` 연동
- Tailwind `dark:` 유틸리티 활용
- 다크 모드 색상 체계 (배경 `#1C1C1E`, 카드 `#2C2C2E`)

### 5-2. 애니메이션 강화
- 항목 추가/삭제: 높이 확장/축소 transition
- 리스트 전환: fade/slide transition
- 사이드바 토글: slide-in/out (모바일)
- 체크박스 완료 애니메이션 정교화

### 5-3. 스와이프 삭제
- 모바일/데스크톱 터치/드래그로 좌측 스와이프 → 빨간 삭제 버튼

### 5-4. 반응형 정교화
- 모바일: 사이드바 전체 화면 → 리스트 선택 시 목록 전환 (뒤로가기)
- 태블릿: 좁은 사이드바 + 메인
- 데스크톱: 넓은 사이드바 + 메인

**이 Phase 완료 시**: Apple Reminder와 시각적으로 유사한 완성된 UI

---

## Phase 6: 고도화 기능

> 목표: 우선순위, 하위 작업, 검색

### 6-1. 백엔드 확장
- `Reminder`에 `priority` 필드 (NONE, LOW, MEDIUM, HIGH)
- `Subtask` 엔티티 (id, title, completed, reminderId, sortOrder)
- API: `GET/POST/PUT/DELETE /api/reminders/{id}/subtasks`
- 검색 API: `GET /api/reminders?search=keyword`

### 6-2. 우선순위
- 상세 패널에서 우선순위 설정
- 우선순위 표시: 느낌표 아이콘 (!, !!, !!!)
- 우선순위별 정렬 옵션

### 6-3. 하위 작업
- 리마인더 상세에서 하위 체크리스트 추가
- 하위 작업 완료/삭제
- 목록에서 "0/3" 형태로 진행률 표시

### 6-4. 검색
- 사이드바 상단 검색 바
- 제목/메모 기준 실시간 검색
- 검색 결과 하이라이트

**이 Phase 완료 시**: Apple Reminder의 주요 기능 대부분 구현 완료
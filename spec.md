# Reminder Web App - Spec

## Overview

Apple Reminder 앱의 핵심 기능을 웹으로 구현한다. 깔끔하고 직관적인 UI로 리마인더를 관리할 수 있는 풀스택 웹 애플리케이션.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 4.0.3, Java 25, JPA, H2 (in-memory) |
| Frontend | Next.js (latest, App Router), TypeScript, Tailwind CSS |
| API 통신 | REST API (JSON) |

## 도메인 모델

### Reminder
- id (Long, PK, auto-increment)
- title (String, 필수)
- description (String, 선택)
- remindAt (LocalDateTime, 선택)
- completed (Boolean, 기본 false)
- priority (enum: NONE, LOW, MEDIUM, HIGH)
- reminderList (ManyToOne → ReminderList, nullable)
- createdAt (LocalDateTime, 자동 생성)

### ReminderList
- id (Long, PK, auto-increment)
- name (String, 필수)
- color (String, 기본 "#007AFF")
- reminders (OneToMany → Reminder)
- createdAt (LocalDateTime, 자동 생성)

### Subtask
- id (Long, PK, auto-increment)
- title (String, 필수)
- completed (Boolean, 기본 false)
- reminder (ManyToOne → Reminder)
- sortOrder (Integer)

## API Endpoints

### Reminders
- `GET /api/reminders` - 전체 조회
- `GET /api/reminders?filter=today|scheduled|completed` - 필터 조회
- `GET /api/reminders?search=keyword` - 검색
- `GET /api/reminders/counts` - 필터별 카운트
- `GET /api/reminders/{id}` - 단건 조회
- `POST /api/reminders` - 생성
- `PUT /api/reminders/{id}` - 수정
- `PATCH /api/reminders/{id}/complete` - 완료 처리
- `DELETE /api/reminders/{id}` - 삭제

### ReminderLists
- `GET /api/lists` - 전체 리스트 조회
- `GET /api/lists/{id}/reminders` - 리스트별 리마인더 조회
- `POST /api/lists` - 리스트 생성
- `PUT /api/lists/{id}` - 리스트 수정
- `DELETE /api/lists/{id}` - 리스트 삭제

### Subtasks
- `GET /api/reminders/{id}/subtasks` - 하위 작업 조회
- `POST /api/reminders/{id}/subtasks` - 하위 작업 생성
- `PUT /api/reminders/{id}/subtasks/{subtaskId}` - 하위 작업 수정
- `DELETE /api/reminders/{id}/subtasks/{subtaskId}` - 하위 작업 삭제

## 기능 요구사항

### 리마인더 CRUD
- 리마인더 생성 (제목 필수, 메모/날짜/우선순위 선택)
- 리마인더 목록 표시 (완료/미완료 체크박스)
- 리마인더 인라인 편집 + 상세 패널 수정
- 리마인더 완료/미완료 토글
- 리마인더 삭제 (확인 없이 즉시, Apple 스타일)

### 리스트(그룹) 관리
- 리스트 생성/수정/삭제 (예: "업무", "개인", "쇼핑")
- 리스트별 색상 지정 (Apple 기본 12색 팔레트)
- 리마인더를 리스트에 할당
- 사이드바에서 리스트 간 전환

### 필터 뷰
- "오늘" - 오늘 알림이 있는 리마인더
- "예정" - 알림 날짜가 설정된 리마인더 (날짜순, 그룹핑)
- "전체" - 모든 리마인더
- "완료됨" - 완료된 리마인더

### 우선순위
- 없음 / 낮음 / 보통 / 높음 (4단계)
- 우선순위별 정렬

### 하위 작업 (Subtask)
- 리마인더 하위에 체크리스트 항목 추가/완료/삭제
- 진행률 표시 ("2/5" 형태)

### 검색
- 제목/메모 기준 실시간 검색
- 검색 결과 하이라이트

## UI/UX 요구사항 (Apple Reminder 충실 재현)

### 레이아웃
- **좌측 사이드바**: 리스트 목록 + 상단 필터 카드 (오늘, 예정, 전체, 완료됨)
- **우측 메인 영역**: 선택된 리스트/필터의 리마인더 목록
- 반응형: 모바일에서는 사이드바가 전체 화면, 리스트 선택 시 목록으로 전환

### 시각 디자인
- **배경**: 사이드바는 연한 회색 (`#F2F2F7`), 메인 영역은 흰색
- **필터 카드**: 2x2 그리드, 각 카드에 아이콘 + 숫자 배지 (오늘=파랑, 예정=빨강, 전체=검정, 완료=회색)
- **리스트 아이콘**: 원형 컬러 아이콘 (리스트 색상) + 리스트명 + 항목 수
- **폰트**: SF Pro 계열 (-apple-system, BlinkMacSystemFont) 사용
- **둥근 모서리**: 카드/입력 요소에 `border-radius: 10~12px`
- 다크 모드: 시스템 설정 연동 (`prefers-color-scheme`)

### 리마인더 항목 스타일
- **체크박스**: 빈 원형 (미완료) → 채워진 원형 + 체크마크 (완료), 리스트 색상 적용
- **완료 시**: 체크 애니메이션 후 텍스트 취소선 + 흐림 처리, 잠시 후 완료 섹션으로 이동
- **항목 구조**: 체크박스 | 제목 (볼드) + 메모 (회색, 1줄) + 날짜 (작은 텍스트) | 우측 상세 화살표
- **구분선**: 항목 간 얇은 separator (체크박스 이후부터 시작, Apple 스타일 인덴트)

### 인터랙션
- **새 리마인더 추가**: 목록 하단 "+ 새로운 미리 알림" 텍스트 버튼 클릭 → 인라인 입력 필드 활성화
- **인라인 편집**: 항목 클릭 시 바로 제목/메모 편집 가능
- **상세 패널**: 항목 우측 (i) 버튼 클릭 시 슬라이드-인 상세 편집 패널 (날짜, 메모, 우선순위 등)
- **삭제**: 항목 좌측 스와이프 → 빨간 삭제 버튼 노출
- **드래그 앤 드롭**: 항목 순서 변경

### 애니메이션
- 체크박스 완료: 원형 채움 + 체크 등장 (0.3s ease)
- 항목 추가/삭제: 높이 확장/축소 transition
- 리스트 전환: 부드러운 fade 또는 slide
- 사이드바 토글: slide-in/out (모바일)

## 프로젝트 구조

```
reminder/
├── src/                          # Spring Boot Backend
│   └── main/java/logan/ai/reminder/
│       ├── entity/               # Reminder, ReminderList, Subtask
│       ├── repository/
│       ├── service/
│       ├── controller/
│       └── config/               # CORS 등
├── frontend/                     # Next.js Frontend
│   ├── app/
│   │   ├── layout.tsx
│   │   ├── page.tsx
│   │   └── globals.css
│   ├── components/
│   │   ├── Sidebar.tsx
│   │   ├── ReminderList.tsx
│   │   ├── ReminderItem.tsx
│   │   └── ReminderForm.tsx
│   ├── lib/
│   │   └── api.ts               # API 클라이언트
│   ├── types/
│   │   └── reminder.ts          # TypeScript 타입
│   └── package.json
```

## 실행 방법

- Backend: `./gradlew bootRun` (port 8080)
- Frontend: `cd frontend && npm run dev` (port 3000)
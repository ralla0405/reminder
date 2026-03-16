# Reminder Web App - Tasks

## Phase 1: 백엔드 - Reminder 기본 API

### 1-1. Spring Boot 프로젝트 구성
- [x] `Reminder` 엔티티 생성 (id, title, description, remindAt, completed, createdAt)
- [x] `ReminderRepository` 생성 (JpaRepository)
- [x] `ReminderService` 생성 (findAll, findById, create, update, complete, delete)
- [x] `ReminderController` 생성 (GET/POST/PUT/PATCH/DELETE `/api/reminders`)
- [x] CORS 설정 (`WebConfig.java` - `WebMvcConfigurer`, `http://localhost:3000` 허용)
- [x] `application.yml` H2 데이터소스 + JPA 설정

### 1-2. 필터 및 검색 API
- [x] `GET /api/reminders?filter=today` - 오늘 날짜의 리마인더
- [x] `GET /api/reminders?filter=scheduled` - remindAt이 설정된 리마인더 (날짜순)
- [x] `GET /api/reminders?filter=completed` - completed=true 리마인더
- [x] `GET /api/reminders?search=keyword` - 제목/메모 검색
- [x] 각 필터 카운트 API (`GET /api/reminders/counts`)

---

## Phase 2: 백엔드 - 리스트(그룹) 관리

### 2-1. ReminderList 엔티티 및 API
- [x] `ReminderList` 엔티티 생성 (id, name, color, createdAt)
- [x] `Reminder` 엔티티에 `reminderList` 외래키 추가 (nullable, ManyToOne)
- [x] `ReminderListRepository` 생성
- [x] `ReminderListService` 생성 (CRUD)
- [x] `ReminderListController` 생성 (`GET/POST/PUT/DELETE /api/lists`)
- [x] 리스트별 리마인더 조회 API (`GET /api/lists/{id}/reminders`)

---

## Phase 3: 백엔드 - 고도화 (우선순위, 하위 작업)

### 3-1. 우선순위
- [x] `Priority` enum 생성 (NONE, LOW, MEDIUM, HIGH)
- [x] `Reminder` 엔티티에 `priority` 필드 추가

### 3-2. 하위 작업 (Subtask)
- [x] `Subtask` 엔티티 생성 (id, title, completed, reminder, sortOrder)
- [x] `SubtaskRepository` / `SubtaskService` / `SubtaskController` 생성
- [x] API: `GET/POST/PUT/DELETE /api/reminders/{id}/subtasks`

---

## Phase 4: 프론트엔드 - 기본 UI + CRUD

### 4-1. Next.js 프로젝트 초기화
- [x] `frontend/` 디렉토리에 Next.js 프로젝트 생성 (App Router, TypeScript, Tailwind CSS)
- [x] `next.config.ts`에 API 프록시 설정 (`/api/**` → `http://localhost:8080`)
- [x] TypeScript 타입 정의 (`types/reminder.ts` - Reminder 인터페이스)
- [x] API 클라이언트 모듈 작성 (`lib/api.ts` - fetchAll, fetchById, create, update, complete, delete)

### 4-2. 리마인더 목록 UI
- [x] 메인 페이지 (`app/page.tsx`) - 리마인더 목록 fetch & 표시
- [x] `ReminderItem` 컴포넌트 - 원형 체크박스 + 제목 + 메모(1줄) + 날짜
- [x] 완료된 항목 스타일링 (취소선 + opacity 처리)
- [x] 항목 간 인덴트된 구분선 (Apple 스타일 separator)

### 4-3. 리마인더 생성
- [x] "+ 새로운 미리 알림" 버튼 (목록 하단)
- [x] 인라인 입력 필드 (제목 필수, Enter 저장, Escape 취소)
- [x] `POST /api/reminders` 호출 후 목록 즉시 갱신

### 4-4. 완료 토글 + 삭제 + 수정
- [x] 체크박스 클릭 시 `PATCH /api/reminders/{id}/complete` 호출
- [x] 완료/미완료 상태 즉시 반영 (Optimistic UI)
- [x] 체크 애니메이션 (원형 채움 0.3s ease)
- [x] 항목 hover 시 삭제 버튼 노출
- [x] 클릭 시 `DELETE /api/reminders/{id}` 호출 (확인 없이 즉시)
- [ ] 삭제 시 높이 축소 애니메이션
- [x] 항목 클릭 시 인라인 편집 모드 (제목/메모 수정)
- [x] `ReminderDetail` 컴포넌트 - (i) 버튼 클릭 시 슬라이드-인 상세 패널
- [x] 상세 패널에서 제목, 메모, 날짜/시간 수정
- [x] `PUT /api/reminders/{id}` 호출로 저장

### 4-5. Apple 스타일 레이아웃
- [x] 2단 레이아웃 - `Sidebar` + 메인 영역
- [x] 사이드바: 연한 회색 배경 (`#F2F2F7`), "전체" 항목 표시
- [x] SF Pro 폰트 적용 (`-apple-system, BlinkMacSystemFont`)
- [x] 둥근 모서리 적용 (카드/입력 `border-radius: 10-12px`)
- [ ] 기본 반응형 (모바일에서 사이드바 숨김/토글 버튼)

---

## Phase 5: 프론트엔드 - 리스트 + 필터 + 고도화 기능

### 5-1. 사이드바 - 리스트 목록
- [ ] 사이드바에 리스트 목록 표시 (원형 컬러 아이콘 + 이름 + 항목 수)
- [ ] 리스트 생성 UI (+ 버튼 → 이름/색상 입력)
- [ ] 리스트 수정/삭제 UI (우클릭 또는 ... 메뉴)
- [ ] 색상 선택 팔레트 (Apple 기본 12색)

### 5-2. 리스트별 리마인더 표시
- [ ] 사이드바 리스트 클릭 → 해당 리스트의 리마인더만 메인 영역에 표시
- [ ] 리마인더 생성/수정 시 리스트 선택 드롭다운
- [ ] 메인 영역 상단 헤더에 리스트 이름 + 색상 표시

### 5-3. 필터 카드 UI
- [ ] 사이드바 상단 2x2 그리드 필터 카드 컴포넌트
- [ ] 오늘 (파랑 아이콘) / 예정 (빨강) / 전체 (검정) / 완료됨 (회색)
- [ ] 각 카드에 숫자 배지 (카운트 API 연동)
- [ ] 카드 클릭 시 필터된 리마인더 목록 표시

### 5-4. 예정 뷰 날짜 그룹핑
- [ ] 날짜별 섹션 헤더 ("오늘", "내일", "이번 주", "이후")
- [ ] 각 섹션 내 리마인더 시간순 정렬

### 5-5. 우선순위 UI
- [ ] 상세 패널에 우선순위 선택 UI (없음/낮음/보통/높음)
- [ ] 목록에서 우선순위 표시 (느낌표 아이콘: !, !!, !!!)
- [ ] 우선순위별 정렬 옵션 추가

### 5-6. 하위 작업 UI
- [ ] 상세 패널에 하위 작업 추가/삭제 UI
- [ ] 하위 작업 체크박스 완료 토글
- [ ] 목록 항목에 하위 작업 진행률 표시 ("2/5" 형태)

### 5-7. 검색
- [ ] 사이드바 상단 검색 입력 바
- [ ] 실시간 검색 (debounce 300ms)
- [ ] 검색 결과에서 매칭 텍스트 하이라이트

---

## Phase 6: UI 폴리싱 + 다크 모드

### 6-1. 다크 모드
- [ ] Tailwind `darkMode: 'media'` 설정 (`prefers-color-scheme` 연동)
- [ ] 다크 모드 배경색 (`#1C1C1E`), 카드색 (`#2C2C2E`) 적용
- [ ] 모든 컴포넌트에 `dark:` 유틸리티 추가
- [ ] 체크박스/아이콘/텍스트 색상 다크 모드 대응

### 6-2. 애니메이션 강화
- [ ] 항목 추가 시 높이 확장 transition
- [ ] 항목 삭제 시 높이 축소 + fade-out transition
- [ ] 리스트/필터 전환 시 fade 또는 slide transition
- [ ] 체크박스 완료 애니메이션 정교화 (원형 채움 + 체크 등장)

### 6-3. 스와이프 삭제
- [ ] 터치/마우스 드래그로 좌측 스와이프 감지
- [ ] 스와이프 시 빨간 삭제 버튼 노출
- [ ] 삭제 버튼 클릭 또는 끝까지 스와이프 시 삭제 실행

### 6-4. 반응형 정교화
- [ ] 모바일 (<768px): 사이드바 전체 화면, 리스트 선택 시 목록 전환 + 뒤로가기 버튼
- [ ] 태블릿 (768-1024px): 좁은 사이드바 + 메인 영역
- [ ] 데스크톱 (>1024px): 넓은 사이드바 + 메인 영역

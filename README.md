# 팀장봇 (Team jjang Bot)

팀 프로젝트의 작업, 담당자, 진행 상황을 한곳에서 관리하고 일정 위험을 분석하는 웹 서비스입니다.

AI가 프로젝트 설명을 바탕으로 작업 분해를 돕고, 향후 Monte Carlo 시뮬레이션으로 마감 내 완료 가능성과 주요 위험 작업을 확인할 수 있도록 개발하고 있습니다.

> 현재 개발 중입니다. Spring Boot와 React 기본 구조를 구성했으며, 프로젝트 관리 기능부터 단계적으로 구현하고 있습니다.

## 기술 스택

| 구분 | 기술 |
|---|---|
| Frontend | JavaScript, React, React Router |
| Frontend Build | Vite, pnpm |
| Backend | Java 17, Spring Boot, Spring Data JPA |
| Backend Build | Maven |
| Database | MySQL |
| Test | JUnit, H2, Vitest |
| Code Check | Oxlint |

## 기능 및 개발 현황

<details>
<summary><strong>주요 기능 · 개발 목표</strong></summary>

### 프로젝트·팀원 관리

- 프로젝트 생성 및 목록·상세 조회
- 프로젝트 이름·설명 및 마감일 관리
- 프로젝트별 팀원 등록 및 역할 관리
- 작업 담당자 지정

### 작업·진행 상황 관리

- 프로젝트별 작업 등록 및 조회
- 낙관·보통·비관의 3점 작업시간 추정
- 작업별 진행률과 메모 기록
- 진행률 변경 이력 조회
- 작업 간 의존관계 관리

### AI·일정 위험 분석

- AI 작업 분해(WBS) 초안 생성
- Monte Carlo 기반 마감 내 완료 가능성 추정
- 일정에 영향을 주는 주요 작업 분석
- 진행 상황 변경에 따른 일정 위험 재계산
- 계획 변경 전후의 What-if 비교

위 목록은 전체 개발 목표이며, 모두 구현된 상태는 아닙니다.

</details>

<details>
<summary><strong>백엔드 구현 상태</strong></summary>

- [x] Spring Boot 기본 구조 구성
- [x] Project, Member, Task, TaskProgress 도메인 구현
- [x] 프로젝트 생성 및 목록·상세 조회 API
- [x] 프로젝트별 팀원 등록·조회 API
- [x] 프로젝트별 작업 등록·조회 API
- [x] 진행률 기록 및 이력 조회 API
- [x] 담당자의 프로젝트 소속 검증
- [x] 3점 예상 시간 및 진행률 입력 검증
- [x] 공통 오류 응답 및 CORS 설정
- [x] H2 기반 HTTP 통합 테스트
- [ ] 프로젝트 이름·설명 수정 API의 main 병합
- [ ] 프로젝트 삭제 및 팀원·작업 수정·삭제
- [ ] 로그인 및 프로젝트 접근 권한 검사
- [ ] 작업 의존관계 관리
- [ ] AI 작업 분해 기능
- [ ] Monte Carlo 일정 시뮬레이션
- [ ] 운영용 데이터베이스 및 마이그레이션 구성

프로젝트 이름·설명 수정 API는 로컬 구현 및 자동 테스트를 완료했습니다.
관련 PR을 병합한 뒤 위 상태를 갱신합니다.

</details>

<details>
<summary><strong>프론트엔드 구현 상태</strong></summary>

- [x] React + JavaScript + Vite 기본 구조 구성
- [x] React Router 기반 화면 분리
- [x] 도메인별 API 호출 로직 분리
- [x] 프로젝트 목록·생성·상세 화면
- [x] 프로젝트 상세 화면의 팀원·작업 조회
- [x] 로딩·빈 목록·연결 오류 안내
- [x] 잘못된 주소 처리
- [x] API 통신 테스트
- [ ] 팀원 등록·관리 화면
- [ ] 작업 등록·관리 화면
- [ ] 진행률 입력 및 이력 화면
- [ ] 로그인 화면
- [ ] 일정 시뮬레이션 결과 화면

</details>

## 개발 가이드

<details>
<summary><strong>프로젝트 구조</strong></summary>

```text
team-jjang-bot/
├── backend/
│   ├── src/main/java/com/teamjjang/
│   │   ├── common/       # 공통 오류 처리
│   │   ├── config/       # CORS 설정
│   │   ├── project/      # 프로젝트
│   │   ├── member/       # 프로젝트 참여자
│   │   ├── task/         # 작업 및 시간 추정
│   │   ├── progress/     # 진행률 이력
│   │   └── simulation/   # 향후 시뮬레이션 구현 위치
│   ├── src/main/resources/
│   ├── src/test/
│   └── docs/
└── frontend/
    └── src/
        ├── app/          # 라우팅 및 데이터 로딩
        ├── pages/        # 페이지
        ├── components/   # 공통 컴포넌트
        ├── features/     # 도메인별 API
        ├── lib/          # 공통 HTTP 요청 처리
        └── styles/       # 공통 스타일
```

</details>

<details>
<summary><strong>백엔드 API 및 입력 기준</strong></summary>

| 기능 | 메서드 | 경로 |
|---|---|---|
| 프로젝트 목록 조회 | GET | `/api/projects` |
| 프로젝트 생성 | POST | `/api/projects` |
| 프로젝트 상세 조회 | GET | `/api/projects/{projectId}` |
| 팀원 목록 조회 | GET | `/api/projects/{projectId}/members` |
| 팀원 등록 | POST | `/api/projects/{projectId}/members` |
| 작업 목록 조회 | GET | `/api/projects/{projectId}/tasks` |
| 작업 등록 | POST | `/api/projects/{projectId}/tasks` |
| 진행률 이력 조회 | GET | `/api/projects/{projectId}/tasks/{taskId}/progress` |
| 진행률 기록 | POST | `/api/projects/{projectId}/tasks/{taskId}/progress` |

추가 구현한 API: `PATCH /api/projects/{projectId}`  
프로젝트 이름·설명을 함께 수정하며, 마감일은 변경하지 않습니다. main 병합 후 위 표에 반영합니다.

### 입력 기준

- 작업시간은 정수 시간 단위로 입력합니다.
- `1 ≤ 낙관 시간 ≤ 보통 시간 ≤ 비관 시간 ≤ 100000`을 만족해야 합니다.
- 진행률은 `0~100`의 정수이며 변경할 때마다 이력으로 저장합니다.
- 재작업을 표현하기 위해 진행률 감소도 허용합니다.

현재 Member는 프로젝트별 참여자이며 로그인 계정과 연결되어 있지 않습니다.
다른 프로젝트의 팀원을 작업 담당자로 지정할 수 없지만, 사용자 인증 및 접근 권한 검사는 구현 전입니다.

</details>

<details>
<summary><strong>백엔드 실행 및 환경변수</strong></summary>

### 준비 사항

- JDK 17
- 실행 중인 MySQL
- `team_jjang_bot` 데이터베이스와 접근 권한이 있는 계정

Maven은 Wrapper를 사용하므로 별도 설치하지 않아도 됩니다.

### 환경변수

`backend/.env.example`을 참고하여 IDE 실행 설정 또는 터미널에 등록합니다.

| 변수 | 기본값 | 설명 |
|---|---|---|
| `SERVER_ADDRESS` | `127.0.0.1` | 서버 접근 주소 |
| `SERVER_PORT` | `8080` | 서버 포트 |
| `DB_URL` | `jdbc:mysql://localhost:3306/team_jjang_bot` | MySQL 연결 주소 |
| `DB_USERNAME` | 없음 · 필수 | DB 계정 |
| `DB_PASSWORD` | 없음 · 필수 | DB 비밀번호 |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:3000` | 허용할 프론트엔드 주소 |

Spring Boot는 `.env` 파일을 자동으로 읽지 않습니다.
IntelliJ에서는 실제 실행할 `TeamJjangApplication` 구성의 **환경 변수**에 등록합니다.

기본 프로필은 `local`입니다. 로컬에서는 JPA의 `ddl-auto=update`를 사용하며, 운영 환경에서는 마이그레이션과 `validate` 사용을 계획하고 있습니다.

### 실행

환경변수 등록 후 실행합니다.

```bash
cd backend
./mvnw spring-boot:run
```

Windows:

```bat
cd backend
mvnw.cmd spring-boot:run
```

- 기본 주소: `http://localhost:8080`
- 상태 확인: `GET /actuator/health`
- 실행용 DB: MySQL
- 테스트용 DB: 별도의 H2 메모리 DB

</details>

<details>
<summary><strong>프론트엔드 실행 및 환경변수</strong></summary>

Node.js 24 LTS와 pnpm 11.19.0을 기준으로 합니다.

```bash
npm install -g pnpm@11.19.0
```

백엔드와 별도 터미널에서 실행합니다.

```bash
cd frontend
pnpm install --frozen-lockfile
cp .env.example .env.local
pnpm dev
```

Windows에서는 `.env.example`을 복사하여 `.env.local`로 저장합니다.

### 환경변수

`frontend/.env.local`에 설정합니다.

```dotenv
VITE_API_BASE_URL=http://localhost:8080
```

- 기본 주소: `http://localhost:5173`
- API 연동을 위해 백엔드도 함께 실행해야 합니다.
- 환경변수 변경 후 개발 서버를 재시작합니다.
- `VITE_` 환경변수는 브라우저에 공개되므로 비밀번호나 비밀키를 넣지 않습니다.

</details>

<details>
<summary><strong>테스트 및 빌드</strong></summary>

### 백엔드

```bash
cd backend
./mvnw clean verify
```

별도의 H2 메모리 DB를 사용하므로 MySQL 연결 없이 테스트할 수 있습니다.
API 정상 흐름, 잘못된 입력, 프로젝트 간 데이터 혼용 방지, CORS 등을 검증합니다.

### 프론트엔드

```bash
cd frontend
pnpm lint
pnpm test
pnpm build
```

API 통신 테스트는 JSON 요청, 서버 검증 오류, 네트워크 오류 및 요청 취소를 검증합니다.
빌드 결과는 `frontend/dist/`에 생성됩니다.

</details>

<details>
<summary><strong>일정 시뮬레이션 개발 방향</strong></summary>

AI는 작업과 의존관계 초안을 제안하고, 일정 위험도는 별도의 계산 로직으로 산출할 예정입니다.

1. 작업 의존관계와 순환 검증
2. 잔여 작업시간 및 팀원별 가용시간 정의
3. 3점 추정 기반 Monte Carlo 반복 계산
4. 마감 내 완료 비율과 예상 종료 시점 분석
5. 계획 변경 전후 결과 비교

현재는 3점 예상 시간 저장까지 구현했습니다.
시뮬레이션 계산은 구현 전이며, 결과는 입력한 가정에 따른 추정치로 제공할 예정입니다.

자세한 내용은 [시뮬레이션 확장 설계](backend/docs/simulation.md)를 참고하세요.

</details>

## 협업 규칙

**이슈 생성 → 브랜치 생성 → 구현·테스트 → PR → 리뷰·Merge**

<details>
<summary><strong>커밋 메시지 규칙</strong></summary>

`타입: 변경 내용` 형식으로 작성합니다.
필요하면 `feat(frontend): 프로젝트 생성 화면 추가`처럼 범위를 표시합니다.

| 타입 | 사용 목적 |
|---|---|
| `feat` | 새로운 기능 추가 |
| `fix` | 오류 수정 |
| `refactor` | 기능을 유지하면서 코드 구조 개선 |
| `design` | 화면 디자인·CSS·레이아웃 변경 |
| `test` | 테스트 추가·수정 |
| `docs` | 문서 작성·수정 |
| `style` | 들여쓰기·공백 등 코드 형식 정리 |
| `chore` | 빌드·의존성·개발 도구 설정 |
| `perf` | 성능 개선 |
| `ci` | 자동 빌드·테스트 설정 |
| `revert` | 이전 변경 되돌리기 |

```text
feat: 프로젝트 생성 화면 추가
fix: 다른 프로젝트의 팀원 지정 오류 수정
refactor: 공통 API 요청 로직 분리
design: 프로젝트 카드 모바일 배치 개선
test: 진행률 입력 검증 테스트 추가
docs: README에 협업 규칙 추가
```

- 제목은 한국어로 구체적으로 작성하고 끝에 마침표를 붙이지 않습니다.
- 하나의 커밋에는 하나의 목적에 해당하는 변경을 묶습니다.
- 자세한 설명이 필요하면 한 줄을 띄우고 본문을 작성합니다.
- PR 제목에도 같은 형식을 사용합니다.

</details>

<details>
<summary><strong>이슈 생성 규칙 및 양식</strong></summary>

- 작업 전에 이슈를 생성하고 담당자와 라벨을 지정합니다.
- 하나의 이슈에는 완료 여부를 확인할 수 있는 작업 단위를 담습니다.
- 목적, 작업 내용, 완료 기준을 작성합니다.
- 실제로 확인한 완료 기준만 체크합니다.
- 관련 PR이 병합되기 전까지 이슈를 열어둡니다.

### 제목 예시

- `[Backend] 프로젝트 이름·설명 수정 API 구현`
- `[Frontend] 프로젝트 수정 화면 구현`
- `[Bug] 프로젝트 수정 시 입력 검증 오류 해결`
- `[Docs] README에 협업 규칙 추가`

### 본문 양식

```markdown
## 목적
이 작업이 필요한 이유를 작성합니다.

## 작업 내용
- 구현하거나 변경할 내용을 작성합니다.

## 완료 기준
- [ ] 요구한 동작을 확인했다.
- [ ] 필요한 입력 검증과 오류 처리를 확인했다.
- [ ] 변경에 필요한 테스트 또는 검증을 완료했다.

## 참고
관련 이슈, API 명세, 디자인 링크 등을 작성합니다.
```

</details>

<details>
<summary><strong>브랜치 규칙</strong></summary>

최신 `main`에서 작업 브랜치를 생성합니다.

이름은 `타입/이슈번호-작업내용` 형식으로 작성합니다.
작업 내용은 영문 소문자와 하이픈을 사용합니다.

| 타입 | 예시 |
|---|---|
| `feat` | `feat/4-project-update` |
| `fix` | `fix/7-project-validation` |
| `refactor` | `refactor/8-project-service` |
| `test` | `test/9-project-api` |
| `docs` | `docs/10-workflow-rules` |
| `chore` | `chore/11-mysql-config` |

- 하나의 브랜치에는 해당 이슈와 관련된 변경만 담습니다.
- 비밀번호, 실제 환경변수 파일, 개인 IDE 설정은 커밋하지 않습니다.
- 예시 환경변수 파일에는 실제 비밀번호를 넣지 않습니다.

</details>

<details>
<summary><strong>Pull Request 규칙 및 양식</strong></summary>

작업 브랜치를 Push한 뒤 `main`을 대상으로 PR을 생성합니다.

- 제목은 `타입: 변경 내용` 형식으로 작성합니다.
- 본문에는 변경 내용, 테스트 결과, 관련 이슈를 작성합니다.
- 작업 중 공유가 필요하면 Draft PR을 사용합니다.
- 확인하지 않은 항목은 완료했다고 작성하지 않습니다.
- 화면 변경이 있으면 필요한 스크린샷을 첨부합니다.
- 리뷰어를 지정하고 피드백을 반영합니다.

### 제목 예시

`feat: 프로젝트 이름·설명 수정 API 구현`

### 본문 양식

```markdown
## 변경 내용
- 변경 사항과 필요한 경우 변경 이유를 작성합니다.

## 테스트
- 실행한 테스트와 결과를 작성합니다.
- 수동으로 확인한 동작을 작성합니다.
- 확인하지 못한 부분이 있다면 명시합니다.

## 관련 이슈
Closes #이슈번호

## 리뷰 참고
집중해서 확인할 부분이나 남은 제약사항을 작성합니다.
```

- 이슈 전체 범위를 완료하면 `Closes #이슈번호`를 사용합니다.
- 일부 작업만 포함하면 `Refs #이슈번호`를 사용하고 이슈를 유지합니다.

</details>

<details>
<summary><strong>리뷰 및 Merge 규칙</strong></summary>

### 병합 전 확인

- [ ] 변경 내용과 이슈의 완료 기준을 확인했다.
- [ ] 필요한 테스트·빌드·수동 검증을 통과했다.
- [ ] 비밀번호나 불필요한 파일이 포함되지 않았다.
- [ ] 리뷰어 1명 이상이 승인했다.
- [ ] 수정 요청을 반영하고 관련 논의를 해결했다.
- [ ] 병합 충돌이 없다.

### 병합 방식

기본적으로 **Squash and merge**를 사용합니다.
하나의 PR을 하나의 커밋으로 묶고, 최종 커밋 메시지는 `타입: 변경 내용`으로 작성합니다.

### 병합 후

1. 관련 이슈의 완료 및 종료 여부를 확인합니다.
2. 병합이 끝난 원격 작업 브랜치를 삭제합니다.
3. 로컬에서 `main`으로 이동해 최신 내용을 Pull합니다.
4. 다음 작업은 최신 `main`에서 새 브랜치를 만들어 시작합니다.

</details>

## 기여자

| <img src="https://github.com/yaeryung93.png" width="100" alt="김예령 프로필"> | <img src="https://github.com/ksm524923.png" width="100" alt="고성민 프로필"> | <img src="https://github.com/chlgeun112.png" width="100" alt="최강은 프로필"> |
| :---: | :---: | :---: |
| **[김예령](https://github.com/yaeryung93)** | **[고성민](https://github.com/ksm524923)** | **[최강은](https://github.com/chlgeun112)** |
| 백엔드 개발 | 프론트엔드 개발 | UI/UX 디자인<br>기획 |

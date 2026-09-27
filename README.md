# team-jjang-bot

팀장봇: 학생 팀 프로젝트의 작업과 진행 상황을 관리하고, 향후 일정 위험 시뮬레이션으로 확장할 서비스입니다.

현재는 **로컬 개발용 Spring Boot + React 초기 구조**입니다. 백엔드는 프로젝트·팀원·작업 등록/조회 및 진행률 이력을 구현했습니다. React는 프로젝트 목록/생성/상세와 팀원·작업 조회를 연결했습니다. 로그인, AI WBS, 작업 의존관계 및 Monte Carlo 계산은 아직 구현하지 않았습니다.

## 구조

```text
backend/
  pom.xml, mvnw, mvnw.cmd       Maven 빌드와 Wrapper
  .env.example                 로컬 환경변수 예시
  src/main/java/com/teamjjang/
    config/                    React 개발 서버용 CORS
    common/                    API 오류 처리
    project/                   프로젝트 + 마감일
    member/                    프로젝트별 참여자
    task/                      담당자 + 3점 작업시간 추정
    progress/                  진행률 변경 이력
    simulation/                향후 계산 모듈 경계 설명
  src/main/resources/          실행 설정
  src/test/                   HTTP API 통합 테스트
  docs/simulation.md          시뮬레이션 확장 설계
```

도메인별로 Controller → Service → Repository를 둡니다. JPA 엔티티를 직접 응답하지 않고 DTO를 사용하며, 쓰기는 서비스 트랜잭션에서 처리합니다. 인터페이스/구현 클래스 분리나 다중 모듈은 도입하지 않았습니다. React는 루트의 `frontend/`에 있습니다. 화면·도메인별 API·공통 컴포넌트로 나누며 [프론트엔드 실행 안내](frontend/README.md)를 참고하세요.

## React 프론트엔드 실행

Node.js 24 LTS와 pnpm 11.19.0 기준입니다. 백엔드와 별도 터미널에서 실행합니다.

```bash
cd frontend
pnpm install --frozen-lockfile
cp .env.example .env.local
pnpm dev
```

`http://localhost:5173`에서 프로젝트 목록/생성/상세 화면을 확인합니다.

## 백엔드 빠른 실행

JDK 17 이상이 필요합니다. Maven은 Wrapper가 내려받으므로 따로 설치하지 않아도 됩니다. 최초 실행에는 인터넷 연결이 필요합니다.

```bash
cd backend
./mvnw spring-boot:run
```

Windows에서는 `mvnw.cmd spring-boot:run`을 실행합니다. 기본 주소는 `http://localhost:8080`이며 `GET /actuator/health`가 `{"status":"UP"}`를 반환하면 정상입니다.

```bash
./mvnw clean verify
java -jar target/team-jjang-backend-0.0.1-SNAPSHOT.jar
```

실행 중인 서버를 종료한 뒤 jar를 실행하세요. 작업 디렉터리는 `backend/`로 유지해야 같은 H2 파일을 사용합니다. 기본 데이터는 `backend/data/`에 저장되며 재시작 후 유지됩니다. 테스트는 별도의 메모리 DB를 사용합니다.

Spring Boot 4.1.1, Spring MVC, Data JPA, Validation, Actuator, H2를 사용합니다. [Spring 공식 Java/빌드 요구사항](https://docs.spring.io/spring-boot/4.1/system-requirements.html)을 참고하세요.

## 환경변수

기본 설정만으로 바로 실행할 수 있습니다. 필요하면 `backend/.env.example`을 `.env`로 복사해 수정하세요. **Spring Boot는 `.env` 파일을 자동으로 읽지 않습니다.** IDE 실행 설정에 환경변수를 등록하거나 다음과 같이 로드합니다. 아래 명령은 직접 작성한 신뢰할 수 있는 `.env`에만 사용하세요.

```bash
cp .env.example .env
set -a
. ./.env
set +a
./mvnw spring-boot:run
```

| 변수 | 기본값 | 설명 |
|---|---|---|
| `SERVER_ADDRESS` | `127.0.0.1` | 로컬에서만 접근 |
| `SERVER_PORT` | `8080` | API 포트 |
| `DB_URL` | `jdbc:h2:file:./data/teamjjang` | H2 파일 DB |
| `DB_USERNAME` | `sa` | 로컬 DB 사용자 |
| `DB_PASSWORD` | 빈 문자열 | 로컬 DB 비밀번호 |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:3000` | 쉼표로 구분한 React 주소 |

현재 H2 드라이버만 포함되어 있습니다. MySQL/PostgreSQL 전환은 해당 드라이버와 스키마 마이그레이션을 추가해야 하며 URL 변경만으로 지원되지 않습니다. `ddl-auto=update`는 개발 편의용이므로 공유/운영 DB 도입 시 Flyway 등으로 바꾸세요. `.env` 및 DB 파일은 Git에서 제외합니다.

## API

모든 경로는 `/api`로 시작합니다. 생성은 `201`, 조회는 `200`을 반환합니다.

| 메서드 | 경로 | 기능 |
|---|---|---|
| GET / POST | `/api/projects` | 프로젝트 목록 / 생성 |
| GET | `/api/projects/{projectId}` | 프로젝트 상세 |
| GET / POST | `/api/projects/{projectId}/members` | 참여자 목록 / 등록 |
| GET / POST | `/api/projects/{projectId}/tasks` | 작업 목록 / 등록 |
| GET / POST | `/api/projects/{projectId}/tasks/{taskId}/progress` | 진행률 이력 / 기록 |

프로젝트 생성:

```bash
curl -i http://localhost:8080/api/projects \
  -H 'Content-Type: application/json' \
  -d '{"name":"팀장봇 MVP","description":"3인 팀 프로젝트","deadline":"2099-12-31"}'
```

실제 마감일로 바꾸세요. 생성 시 과거 날짜는 거부합니다. 이후 요청의 `1`은 생성 응답의 프로젝트·팀원·작업 ID로 각각 바꾸세요.

```bash
curl http://localhost:8080/api/projects/1/members \
  -H 'Content-Type: application/json' \
  -d '{"name":"예령","role":"백엔드"}'

curl http://localhost:8080/api/projects/1/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"프로젝트 API","assigneeId":1,"optimisticHours":4,"likelyHours":8,"pessimisticHours":16}'

curl http://localhost:8080/api/projects/1/tasks/1/progress \
  -H 'Content-Type: application/json' \
  -d '{"percent":40,"note":"기본 API 구현 완료"}'
```

- Member는 프로젝트별 참여자입니다. 로그인 계정이 아니며, 다른 프로젝트의 Member를 작업 담당자로 지정할 수 없습니다. 미배정 작업은 `assigneeId`를 생략하거나 `null`로 보냅니다.
- 추정값은 정수 작업시간(hours)이며 `1 ≤ optimisticHours ≤ likelyHours ≤ pessimisticHours ≤ 100000`입니다. 세 값이 같아도 허용합니다.
- 진행률은 필수 정수 `0~100`입니다. `note`와 프로젝트의 `description`은 빈 문자열을 허용하며 누락은 거부합니다.
- 진행률 기록은 덮어쓰지 않고 추가합니다. 이력은 ID 내림차순이며 첫 항목을 현재 상태로 해석합니다. 기록이 없으면 0%입니다. 재작업을 표현하기 위해 감소도 허용합니다. `recordedAt`은 서버가 기록한 UTC 시간입니다.
- 목록은 초기 소규모 팀을 위한 전체 조회입니다. 데이터가 늘어나면 페이지네이션을 추가하세요. 수정·삭제 API는 후속 구현 범위입니다.

입력 오류는 `400`, 없는 리소스는 `404`, DB 제약 충돌은 `409`입니다. 오류는 `application/problem+json` 형식입니다.

```json
{"type":"about:blank","title":"Bad Request","status":400,"detail":"입력값을 확인해 주세요.","errors":{"name":"must not be blank"}}
```

## React 연결 예시

Vite 프로젝트의 `.env.local`:

```dotenv
VITE_API_BASE_URL=http://localhost:8080
```

```javascript
const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/projects`);
const body = await response.json();
if (!response.ok) throw new Error(body.detail ?? "요청 실패");
console.log(body);
```

기본 CORS는 `localhost:5173`과 `localhost:3000`만 허용합니다. `127.0.0.1`을 프론트 주소로 사용하면 환경변수에 그 origin도 추가하세요. 현재 쿠키 인증은 사용하지 않습니다.

## 커밋 메시지 규칙

프론트엔드와 백엔드 모두 아래 형식을 사용합니다.

```text
타입(범위): 변경 내용
```

타입은 소문자 영어, 변경 내용은 한국어로 작성합니다. 범위는 선택 사항이며 `frontend`, `backend`, `docs` 등을 사용합니다. 공통 변경은 `docs: 실행 방법 정리`처럼 범위를 생략해도 됩니다.

| 타입 | 사용 목적 | 예시 |
|---|---|---|
| `feat` | 새로운 기능 추가 | `feat(frontend): 프로젝트 생성 화면 추가` |
| `fix` | 잘못된 동작 수정 | `fix(backend): 다른 프로젝트의 담당자 지정 차단` |
| `refactor` | 기능 변경 없이 코드 구조 개선 | `refactor(frontend): API 요청 로직 분리` |
| `design` | 화면 디자인, CSS, 레이아웃 변경 | `design(frontend): 프로젝트 카드 모바일 배치 개선` |
| `style` | 동작에 영향 없는 코드 서식 변경 | `style(backend): 들여쓰기 정리` |
| `docs` | README 등 문서 변경 | `docs: 커밋 메시지 규칙 추가` |
| `test` | 테스트 추가·수정 | `test(backend): 진행률 입력 검증 테스트 추가` |
| `chore` | 의존성, 빌드 설정 등 개발 환경 관리 | `chore(frontend): 개발 의존성 업데이트` |
| `perf` | 성능 개선 | `perf(backend): 작업 목록 조회 쿼리 개선` |
| `ci` | 자동 빌드·테스트 설정 변경 | `ci: PR 빌드 검사 추가` |
| `revert` | 이전 커밋 되돌리기 | `revert: 프로젝트 목록 정렬 변경 되돌리기` |

### 작성 원칙

- 제목은 변경 내용을 한눈에 알 수 있게 짧게 작성하고 끝에 마침표를 붙이지 않습니다.
- `수정`, `작업 완료`처럼 모호한 제목 대신 무엇을 바꿨는지 적습니다.
- 한 커밋에는 하나의 목적을 담습니다. 관련 없는 기능 추가와 문서 수정은 나눕니다.
- 설명이 더 필요하면 제목 다음에 빈 줄을 두고 변경 이유와 주요 내용을 본문에 적습니다.
- 관련 이슈가 있으면 본문 마지막에 `Refs: #이슈번호`를 적습니다. 이슈가 없으면 생략합니다.
- PR 제목도 같은 형식을 사용합니다. Squash merge 시 최종 커밋 제목도 이 규칙에 맞춥니다.

```text
fix(backend): 작업 담당자의 프로젝트 소속 검증

다른 프로젝트의 팀원을 담당자로 지정할 수 있던 문제를 수정합니다.
담당자가 해당 프로젝트에 속하지 않으면 400 응답을 반환합니다.
```

이 규칙은 팀 협업을 위한 작성 기준이며, 자동 검사 도구는 아직 설정하지 않았습니다.

## 구현 경계와 다음 단계

현재 인증/인가가 없으며 로컬 개발용입니다. 공개 배포 전 로그인과 프로젝트 접근 권한을 구현해야 합니다. CORS는 접근 권한 검증을 대체하지 않습니다.

1. 팀원·작업 등록 및 진행률 입력 화면, 필요한 수정/삭제 API 추가
2. 로그인 계정과 프로젝트 참여자 연결 및 권한 검증
3. 의존관계 DAG, 잔여시간 추정, 팀원 가용시간 정의
4. 순수 Java 시뮬레이션과 결과 저장/조회 추가

상세한 계산 경계는 [시뮬레이션 확장 설계](backend/docs/simulation.md)를 참고하세요. 테스트는 프로젝트 등록부터 진행률 이력까지의 HTTP 흐름, 프로젝트 간 데이터 혼용 방지, 잘못된 입력, CORS 및 헬스 체크를 검증합니다.

# 팀장봇 프론트엔드

React + TypeScript + Vite 기본 구조입니다. React Router로 화면을 나누고 브라우저 fetch로 기존 Spring Boot API를 호출합니다. 전역 상태 관리나 별도 UI 라이브러리는 도입하지 않았습니다.

## 실행

Node.js 24 LTS와 pnpm 11.19.0을 기준으로 합니다. `.nvmrc`를 제공하며, [Vite 공식 실행 조건](https://vite.dev/guide/)에 맞는 Node가 필요합니다.

```bash
# 저장소 루트에서
cd frontend
pnpm install --frozen-lockfile
cp .env.example .env.local
pnpm dev
```

pnpm이 없으면 Node 설치 후 `npm install -g pnpm@11.19.0`으로 설치합니다.

별도 터미널에서 백엔드를 실행하세요.

```bash
cd backend
./mvnw spring-boot:run
```

브라우저에서 `http://localhost:5173`을 엽니다. 포트가 사용 중이면 임의 포트로 바꾸지 않고 종료하므로 기존 프로세스를 종료하거나 CORS와 포트를 함께 수정하세요.

## 환경변수

```dotenv
VITE_API_BASE_URL=http://localhost:8080
```

- URL은 `/api`를 제외한 서버 주소입니다. 끝의 `/`는 자동 제거합니다.
- Vite는 `.env.local`을 읽습니다. 수정 후 개발 서버를 재시작하세요.
- `VITE_` 변수는 브라우저에 공개됩니다. 비밀번호나 API 비밀키를 넣지 마세요.
- 기본 백엔드 CORS는 `http://localhost:5173`을 허용합니다.

## 디렉터리

```text
src/
  app/router.tsx          경로, 데이터 loader, 생성 action
  pages/                  프로젝트 목록 / 생성 / 상세 화면
  components/             공통 레이아웃, 오류 화면
  features/
    projects/             프로젝트 타입, API
    members/              팀원 타입, API
    tasks/                작업 타입, API
    progress/             진행률 타입, API
  lib/http.ts             공통 fetch 및 ProblemDetail 오류 처리
  styles/global.css       기본 스타일과 모바일 대응
  main.tsx                시작점
```

## 현재 화면

| 경로 | 기능 |
|---|---|
| `/` | 프로젝트 목록으로 이동 |
| `/projects` | 프로젝트 목록, 빈 상태, 연결 오류 및 재시도 |
| `/projects/new` | 프로젝트 등록, 서버 검증 오류 안내 |
| `/projects/:projectId` | 프로젝트 정보, 팀원과 작업 조회 |

실제 백엔드 응답만 표시합니다. 목 데이터와 가짜 시뮬레이션 확률은 없습니다. 팀원/작업 등록, 진행률 입력 화면, 인증 및 시뮬레이션은 후속 범위입니다. 이들 도메인의 기존 백엔드 API 호출 함수와 타입은 준비되어 있습니다.

React Router의 loader에서 조회하고 action에서 등록합니다. 경로가 바뀌면 이전 조회 요청을 취소합니다. 폼 제출 중에는 중복 클릭을 막고, 실패 시 입력값을 유지합니다. 진행률 타입의 `recordedAt`은 UTC ISO 문자열이고 프로젝트 마감일은 `YYYY-MM-DD` 문자열입니다.

## 검증 및 빌드

```bash
pnpm lint
pnpm test
pnpm build
pnpm preview
```

`build`는 TypeScript 검사 후 `dist/`를 생성합니다. `preview`는 빌드 확인용이며 기본 4173 포트를 사용하므로 API 연결 확인 시 백엔드 `CORS_ALLOWED_ORIGINS`에 `http://localhost:4173`을 추가하세요. 테스트는 공통 API 통신의 JSON 전송, 서버 오류, 네트워크 오류와 요청 취소를 검증합니다.

배포 시 `VITE_API_BASE_URL`을 빌드 전에 설정하고, 호스팅 서버에서 존재하지 않는 파일 경로를 `index.html`로 연결하는 SPA fallback을 설정해야 상세 URL 새로고침이 동작합니다. 현재 백엔드는 로컬 개발용이며 공개 배포 전에 인증/인가와 운영 설정이 필요합니다.

# 로컬 MySQL 연결

서비스 로직은 그대로 두고 실행 DB를 MySQL, 자동 테스트 DB를 H2로 분리했습니다.

## 실행 준비

1. MySQL 서버를 실행합니다.
2. `team_jjang_bot` 데이터베이스와 이 DB에 접근할 계정을 준비합니다.
3. IDE의 실행 환경변수에 다음 값을 입력합니다. 비밀번호는 채팅이나 Git에 올리지 않습니다.

```dotenv
DB_URL=jdbc:mysql://localhost:3306/team_jjang_bot
DB_USERNAME=사용할_MySQL_계정
DB_PASSWORD=해당_계정의_비밀번호
```

DB 이름이나 포트가 다르면 `DB_URL`을 바꿉니다. 데이터베이스는 직접 생성해야 하며 애플리케이션이 자동 생성하지 않습니다.

`backend/`에서 `./mvnw spring-boot:run`을 실행합니다. 기본 local 프로필에서는 JPA가 필요한 테이블을 생성·갱신합니다. 비밀번호가 틀리면 Access denied, DB가 없으면 Unknown database, 서버가 꺼져 있으면 연결 실패 오류가 발생할 수 있습니다.

`.env.example`은 예시일 뿐이며 Spring은 `.env`를 자동 로딩하지 않습니다. IDE 환경변수 설정을 권장합니다. 기존 H2 데이터는 자동으로 이전되지 않습니다.

## 검증

`./mvnw clean verify`는 test 프로필과 메모리 H2를 사용하며 MySQL 계정 없이 실행할 수 있습니다. 이 테스트 통과만으로 실제 MySQL 연결 성공을 의미하지는 않습니다.

MySQL 접속 설정 후 서버 시작 및 `/actuator/health` 응답, 프로젝트 생성·조회로 연결을 별도 검증합니다.

## Aiven 배포 시

Aiven은 아직 연결하지 않았습니다. 배포 단계에서 제공받은 주소·포트·계정, TLS 인증서 검증 설정을 확인하여 구성합니다. 운영에서는 local 프로필을 사용하지 않고 Flyway 등으로 스키마 변경을 관리한 뒤 validate로 검증합니다. 기본 공통 설정은 validate이며 local에서만 update를 사용합니다.

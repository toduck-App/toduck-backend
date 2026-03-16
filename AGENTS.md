# toduck-backend

ADHD 자기관리 앱 toduck의 백엔드 서버.
Java 17 · Spring Boot 3.3.1 · Gradle · 단일 모듈

## Commands
- Build: `./gradlew build`
- Test: `./gradlew test`
- Code style check: `./gradlew checkstyleMain` (Naver Checkstyle)

## Architecture
도메인별 Clean Architecture: `domain/{name}/` → common, domain(service/usecase), persistence(entity/repository), presentation(controller/dto)

의존 방향: Controller → UseCase → Service → Repository (단방향)

## Code Style
Checkstyle(Naver rules)과 EditorConfig로 자동 강제됨.
에이전트가 코드 스타일을 수동으로 관리할 필요 없음.

## Core Rules
- 모든 메서드 파라미터에 `final` 키워드 필수 (Builder 생성자 파라미터는 Lombok이 생성하므로 예외)
- 엔티티는 `BaseEntity` 상속, `@Builder` + private 생성자
- Service는 자기 도메인 Repository만 의존. 교차 도메인은 UseCase에서 조합
- UseCase 클래스는 `@UseCase` 어노테이션 사용 (`@Service` 아님)
- API 응답: `ApiResponse.createSuccess(data)` 또는 `ApiResponse.createSuccessWithNoContent()`
  - `ApiResponse.onSuccess()`는 존재하지 않음 — 절대 사용 금지
- 예외: `CommonException.from(ExceptionCode.XXX)`, VO 검증은 `VoException`
- DTO는 Java record + Bean Validation
- 조회: `@Transactional(readOnly = true)`, 수정: `@Transactional`
- 이벤트 리스너: `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` (`@EventListener` 아님)

## Testing
- 베이스 클래스: `UseCaseTest`, `ServiceTest`, `RepositoryTest`
- 한글 `@DisplayName`, Given-When-Then, AssertJ
- 상세: `.agent-docs/test-conventions.md`

## Detailed Conventions
새 도메인 개발 시 반드시 참조: `.agent-docs/conventions.md`
도메인 모델: `docs/도메인모델.md` · 용어집: `docs/용어.md`

## Code Navigation
- 코드 탐색 시 LSP를 우선 사용할 것. grep/ripgrep보다 정확한 타입 정보, 참조 추적, 컴파일 에러 감지 가능.
- IDE 내장 LSP 서버가 가용하면 우선 활용.

## Git
- 커밋 메시지: commitlint 규칙 준수 (commitlint.config.js)
  - 타입: feat, fix, docs, style, refactor, test, chore
  - 스코프 사용 안 함, 헤더 100자 제한
- PR: 수동 리뷰 프로세스 (템플릿 없음)

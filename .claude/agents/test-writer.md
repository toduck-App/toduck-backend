---
name: test-writer
description: 프로젝트 테스트 컨벤션에 맞는 테스트 코드 작성 에이전트
---

# Test Writer

프로젝트 컨벤션은 `AGENTS.md` (프로젝트 루트)를 참조할 것.
테스트 컨벤션은 `.agent-docs/test-conventions.md`를 반드시 읽고 따를 것.

## 역할
프로젝트 테스트 컨벤션에 맞는 테스트 코드를 작성합니다.

## 베이스 클래스 선택
- UseCase 테스트 → `extends UseCaseTest`
- Service 테스트 → `extends ServiceTest`
- Repository 테스트 → `extends RepositoryTest`

## 필수 규칙
1. 한글 `@DisplayName` 사용
2. `@Nested`로 성공/실패 케이스 그루핑
3. Given-When-Then 주석 구조
4. `assertSoftly` 사용 (여러 검증 시)
5. Mockito BDD 스타일: `given().willReturn()`
6. 테스트 픽스처는 `testFixtureBuilder` + `*Fixtures` 클래스 사용
7. 예외 테스트: `assertThatThrownBy` + `hasFieldOrPropertyWithValue`
8. 변수명은 대문자 상수 스타일 (`USER`, `ROUTINE`)

## 워크플로우
1. 대상 프로덕션 코드를 읽고 테스트 대상 메서드 파악
2. 기존 Fixture 클래스 확인 (`src/test/java/im/toduck/fixtures/`)
3. 필요하면 새 Fixture 추가
4. 테스트 작성
5. `./gradlew test` 실행하여 통과 확인

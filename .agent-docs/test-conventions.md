# 테스트 컨벤션

## 베이스 클래스 선택 기준

| 베이스 클래스 | 용도 | 특징 |
|---|---|---|
| `UseCaseTest` | UseCase 계층 테스트 | `@SpringBootTest`, `@ActiveProfiles("test")`, 외부 서비스 `@MockBean` (OAuth, JWT, Firebase, RabbitMQ 등), `TestFixtureBuilder` 주입 |
| `ServiceTest` | Service 계층 및 동시성 테스트 | `@SpringBootTest`, `@ActiveProfiles("test")`, `EntityManager` 포함, `AccessTokenProvider`/`RefreshTokenProvider` 등 MockBean |
| `RepositoryTest` | Repository/QueryDSL 테스트 | `@DataJpaTest`, QueryDSL 설정 import, `@AutoConfigureDataRedis`, Quartz 비활성화, 경량 JPA 테스트 |

**주요 차이점**:
- `UseCaseTest`/`ServiceTest`: 전체 Spring Context를 로드하므로 통합 테스트에 적합
- `RepositoryTest`: `@DataJpaTest`로 JPA 관련 빈만 로드하여 빠른 테스트 실행
- 동시성 테스트(`ConcurrencyTest`)는 `ServiceTest`를 상속

## 테스트 구조

### 한글 `@DisplayName` + `@Nested` 그루핑

```java
class RoutineUseCaseTest extends UseCaseTest {

	@Nested
	@DisplayName("루틴 목록 조회시")
	class ReadMyRoutineListTest {

		@Nested
		@DisplayName("성공")
		class Success {
			@Test
			void 루틴_기록이_존재하는_경우에는_해당_기록을_그대로_사용한다() {
				// ...
			}
		}

		@Nested
		@DisplayName("실패")
		class Fail {
			@Test
			void 유효한_유저가_아닐경우_실패한다() {
				// ...
			}
		}
	}
}
```

### Given-When-Then 구조

```java
@Test
void 루틴_기록이_존재하는_경우에는_해당_기록을_그대로_사용한다() {
	// given
	Routine WEEKDAY_ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
		PUBLIC_WEEKDAY_MORNING_ROUTINE(USER)
			.createdAt("2024-11-29 01:00:00")
			.build()
	);

	// when
	MyRoutineRecordReadListResponse responses =
		routineUseCase.readMyRoutineRecordList(USER.getId(), queryDate);

	// then
	assertSoftly(softly -> {
		softly.assertThat(responses.queryDate()).isEqualTo(queryDate);
		softly.assertThat(responses.routines()).hasSize(1);
	});
}
```

### 예외 테스트에서는 when-then 결합 가능

```java
@Test
void 유효한_유저가_아닐경우_실패한다() {
	// given
	int NOISE_USER_ID = 9999;

	// when -> then
	assertSoftly(softly -> {
		softly.assertThatThrownBy(
			() -> routineUseCase.createRoutine(
				savedUser.getId() + NOISE_USER_ID, request))
			.isInstanceOf(CommonException.class)
			.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.NOT_FOUND_USER.getHttpStatus())
			.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.NOT_FOUND_USER.getErrorCode())
			.hasFieldOrPropertyWithValue("message", ExceptionCode.NOT_FOUND_USER.getMessage());
	});
}
```

## 테스트 픽스처

### TestFixtureBuilder

`TestFixtureBuilder`는 `@Component`로 모든 베이스 테스트 클래스에 `@Autowired`로 주입됩니다.
내부적으로 `BuilderSupporter`를 통해 각 도메인 Repository에 접근합니다:

```java
// 사용자 생성
USER = testFixtureBuilder.buildUser(GENERAL_USER());

// 루틴 생성 (audit 필드 포함)
Routine routine = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
	PUBLIC_WEEKDAY_MORNING_ROUTINE(USER)
		.createdAt("2024-11-29 01:00:00")
		.build()
);

// 루틴 기록 생성
testFixtureBuilder.buildRoutineRecord(
	COMPLETED_RECORD(routine).recordAt("2024-12-02 07:00:00").build()
);
```

### Fixture 클래스 (`src/test/java/im/toduck/fixtures/`)

정적 팩토리 메서드로 테스트 데이터 생성. 각 Fixture는 Builder를 반환하여 체이닝 가능:

```java
// 사용자 픽스처
GENERAL_USER()

// 루틴 픽스처 - 네이밍: [공개여부]_[요일패턴]_[시간대]_ROUTINE
PUBLIC_WEEKDAY_MORNING_ROUTINE(user)    // 평일, 오전 7시, 공개
PRIVATE_DAILY_EVENING_ROUTINE(user)     // 매일, 오후 7시, 비공개
PUBLIC_MONDAY_ALLDAY_ROUTINE(user)      // 월요일, 종일(time=null), 공개

// 루틴 기록 픽스처
COMPLETED_RECORD(routine)
INCOMPLETED_RECORD(routine)
```

### Audit 필드 설정 (RoutineWithAuditInfo)

`ReflectionTestUtils.setField()`를 사용하여 audit 필드를 설정하는 래퍼 클래스.
날짜 형식은 `"yyyy-MM-dd HH:mm:ss"`:

```java
PUBLIC_WEEKDAY_MORNING_ROUTINE(USER)
	.createdAt("2024-11-29 01:00:00")          // 생성 시점
	.scheduleModifiedAt("2024-12-01 09:00:00") // 스케줄 수정 시점
	.deletedAt("2024-12-05 00:00:00")          // 삭제 시점 (소프트 삭제 테스트)
	.build()
```

**스마트 기본값**: `createdAt`만 설정하면 `scheduleModifiedAt`이 자동으로 동일 값 설정됨.
조합이 유효하지 않으면 `IllegalStateException` 발생.

## Assertion 패턴

### AssertJ `assertSoftly` (여러 검증 한 번에)

```java
assertSoftly(softly -> {
	softly.assertThat(responses.queryDate()).isEqualTo(queryDate);
	softly.assertThat(responses.routines()).hasSize(1);
	softly.assertThat(response.routineId()).isEqualTo(routine.getId());
});
```

### 예외 테스트

`hasFieldOrPropertyWithValue`로 ExceptionCode의 각 필드를 검증:

```java
assertSoftly(softly -> {
	softly.assertThatThrownBy(
		() -> routineUseCase.createRoutine(invalidUserId, request))
		.isInstanceOf(CommonException.class)
		.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.NOT_FOUND_USER.getHttpStatus())
		.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.NOT_FOUND_USER.getErrorCode())
		.hasFieldOrPropertyWithValue("message", ExceptionCode.NOT_FOUND_USER.getMessage());
});
```

### 예외 미발생 검증

```java
assertThatCode(() ->
	routineRecordRepository.deleteIncompletedFuturesByRoutine(routine, now)
).doesNotThrowAnyException();
```

### 컬렉션 검증

```java
assertThat(unrecordedRoutines).contains(ROUTINE);
assertThat(routineRecords).hasSize(1);
assertThat(remainingIds).doesNotContain(futureIncomplete.getId());
```

### Stream 기반 복합 검증

```java
Map<LocalDate, List<MyRoutineReadResponse>> routinesByDate =
	response.dateRoutines().stream()
		.collect(Collectors.toMap(
			MyRoutineRecordReadListResponse::queryDate,
			MyRoutineRecordReadListResponse::routines
		));

Set<Long> actualIds = routines.stream()
	.map(MyRoutineReadResponse::routineId)
	.collect(Collectors.toSet());

unexpectedIds.forEach(unexpectedId ->
	softly.assertThat(actualIds)
		.as("%s - 루틴(ID: %d)는 존재하지 않아야 함", date, unexpectedId)
		.doesNotContain(unexpectedId)
);
```

## Mockito BDD 패턴

### `@MockBean` 선언 및 설정

베이스 클래스에 공통 MockBean이 선언되어 있고, 테스트에서 `@BeforeEach`/`@AfterEach`로 설정/해제:

```java
// Setup (@BeforeEach)
given(userService.getUserById(any(Long.class))).willReturn(Optional.ofNullable(USER));

// Teardown (@AfterEach) - 모의 상태 초기화
reset(userService);
```

### MockedStatic (정적 메서드 모킹)

`LocalDateTime.now()` 등 정적 메서드를 고정할 때 사용. **반드시 `@AfterEach`에서 `close()` 호출**:

```java
private MockedStatic<LocalDateTime> mockedStatic;

@BeforeEach
void setUp() {
	LocalDateTime fixedNow = LocalDateTime.parse("2024-12-15T10:00:00");
	mockedStatic = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS);
	mockedStatic.when(LocalDateTime::now).thenReturn(fixedNow);
}

@AfterEach
void tearDown() {
	mockedStatic.close();
}
```

## 트랜잭션 관리

### 기본 동작

`UseCaseTest`/`ServiceTest` 상속 시 테스트별로 트랜잭션이 롤백됩니다.

### `Propagation.NEVER` — 실제 DB 상태 검증

트랜잭션 롤백 없이 실제 커밋된 DB 상태를 확인해야 할 때 사용:

```java
@Test
@Transactional(propagation = Propagation.NEVER)
void 기존_기록이_존재하는_경우에_완료_상태_변경이_성공한다() {
	// 실제 DB 커밋 후 상태 검증
}
```

### `TransactionTemplate` — 테스트 데이터 사전 커밋

동시성 테스트 등에서 데이터를 먼저 커밋한 후 테스트 실행:

```java
TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
Long routineId = transactionTemplate.execute(status -> {
	User user = testFixtureBuilder.buildUser(GENERAL_USER());
	Routine routine = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
		PUBLIC_WEEKDAY_MORNING_ROUTINE(user).createdAt("2024-11-29 01:00:00").build()
	);
	entityManager.flush();
	entityManager.clear();
	return routine.getId();
});
```

## 동시성 테스트 패턴

`ServiceTest`를 상속하고 `CountDownLatch` + `ExecutorService`를 사용:

```java
class RoutineUseCaseConcurrencyTest extends ServiceTest {

	@Test
	void 동시에_여러_스레드가_같은_루틴의_완료_상태를_변경해도_정확히_하나의_기록만_생성된다() {
		// given
		int numberOfThreads = 6;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch endLatch = new CountDownLatch(numberOfThreads);
		AtomicInteger successCount = new AtomicInteger(0);
		List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

		// when
		for (int i = 0; i < numberOfThreads; i++) {
			executorService.submit(() -> {
				try {
					startLatch.await();
					Thread.sleep(random.nextInt(10));
					routineUseCase.updateRoutineCompletion(userId, routineId, request);
					successCount.incrementAndGet();
				} catch (Exception e) {
					exceptions.add(e);
				} finally {
					endLatch.countDown();
				}
			});
		}

		startLatch.countDown();  // 모든 스레드 동시 시작
		boolean completed = endLatch.await(5, TimeUnit.SECONDS);
		executorService.shutdown();

		// then
		assertSoftly(softly -> {
			softly.assertThat(completed).isTrue();
			softly.assertThat(routineRecords).hasSize(1);
			softly.assertThat(successCount.get()).isEqualTo(numberOfThreads);
			softly.assertThat(exceptions).isEmpty();
		});
	}
}
```

## 변수 네이밍 규칙

- **UPPER_CASE**: 픽스처로 생성된 엔티티 변수 (`USER`, `WEEKDAY_ROUTINE`, `RECORD`)
- **camelCase**: 로컬/일시적 변수 (`queryDate`, `isCompleted`, `numberOfThreads`)
- **날짜 상수**: `private final LocalDate QUERY_START_DATE = LocalDate.of(2025, 1, 10);`
- **요청 DTO**: camelCase (`successScheduleCreateRequest`)

## 주요 어노테이션 정리

| 어노테이션 | 용도 |
|---|---|
| `@Test` | 테스트 메서드 표시 |
| `@DisplayName("한글 설명")` | 사람이 읽을 수 있는 테스트 설명 |
| `@Nested` | 관련 테스트 그루핑 |
| `@BeforeEach` / `@AfterEach` | 테스트 전후 setup/teardown |
| `@Disabled("사유")` | 테스트 일시 비활성화 |
| `@Transactional` | 테스트 후 롤백 (클래스/메서드 레벨) |
| `@Transactional(propagation = Propagation.NEVER)` | 롤백 없이 실제 DB 상태 검증 |
| `@MockBean` | Spring Context에 Mock 빈 주입 |
| `@Autowired` | 테스트 대상 빈 주입 |

## 표준 Static Import

```java
import static im.toduck.fixtures.routine.RoutineFixtures.*;
import static im.toduck.fixtures.routine.RoutineRecordFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static im.toduck.global.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
```

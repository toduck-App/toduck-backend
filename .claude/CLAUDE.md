# 새로운 도메인 개발 가이드

이 문서는 toduck-backend의 루틴(routine) 도메인을 중심으로 분석하여 새로운 도메인 개발 시 동일한 코드 패턴과 품질을 유지하기 위한 상세한 가이드입니다.

## 프로젝트 구조 및 패키지 구성

새로운 도메인을 개발할 때는 다음과 같은 패키지 구조를 엄격히 따라야 합니다:

```
domain/{도메인명}/
├── common/              # 도메인 내 공통 유틸리티
│   ├── converter/       # JPA 컨버터 (enum, 사용자 정의 타입 등)
│   ├── dto/             # 도메인 내부 DTO (DailyRoutineData 등)
│   ├── helper/          # 도메인별 헬퍼 클래스
│   └── mapper/          # 엔티티-DTO 매핑 클래스
├── domain/              # 핵심 비즈니스 로직 (Clean Architecture)
│   ├── event/           # 도메인 이벤트 (RoutineCreatedEvent 등)
│   ├── service/         # 도메인 서비스 (데이터 접근 로직)
│   └── usecase/         # 애플리케이션 유스케이스 (비즈니스 로직 조합)
├── infrastructure/      # 외부 시스템 연동
│   └── scheduler/       # 스케줄링 관련 작업 (Quartz Job 등)
├── persistence/         # 데이터 계층
│   ├── entity/          # JPA 엔티티
│   ├── repository/      # 레포지토리 인터페이스 및 구현
│   │   └── querydsl/    # QueryDSL 구현체 (복잡한 쿼리)
│   └── vo/              # 값 객체 (Value Object)
└── presentation/        # 프레젠테이션 계층
    ├── api/             # API 문서화 인터페이스 (Swagger 통합)
    ├── controller/      # REST 컨트롤러
    └── dto/             # 요청/응답 DTO
        ├── request/     # 요청 DTO
        └── response/    # 응답 DTO
```

## 엔티티 작성 규칙

### 1. BaseEntity 상속 및 소프트 삭제 패턴

모든 엔티티는 `BaseEntity`를 상속하여 공통 필드(id, createdAt, updatedAt, deletedAt)를 사용합니다:

```java

@Entity
@Getter
@Table(name = "routine")
@NoArgsConstructor  // 반드시 기본 생성자 필요
public class Routine extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)  // IDENTITY 전략 사용
	private Long id;

	// ... 비즈니스 필드들
}
```

**소프트 삭제 메서드 -> 소프트 삭제는 상황에 따라 필요한 경우에만 선택적으로 수행**

```java
public void delete() {
	this.deletedAt = LocalDateTime.now();
}

public Boolean isInDeletedState() {
	return deletedAt != null;
}
```

### 2. Builder 패턴 사용 (생성자 패턴)

엔티티 생성 시 반드시 Builder 패턴을 사용하고, private 생성자로 외부 생성을 방지합니다:

```java

@Builder
private Routine(
	final PlanCategory category,         // final 키워드 필수
	final PlanCategoryColor color,
	final String title,
	final Boolean isPublic,
	final Integer reminderMinutes,
	final RoutineMemo memo,
	final LocalTime time,
	final DaysOfWeekBitmask daysOfWeekBitmask,
	final User user
) {
	this.category = category;
	this.color = color;
	this.title = title;
	this.isPublic = isPublic;
	this.reminderMinutes = reminderMinutes;
	this.memo = memo;
	this.time = time;
	this.daysOfWeekBitmask = daysOfWeekBitmask;
	this.user = user;
	this.scheduleModifiedAt = LocalDateTime.now(); // 생성 시점 자동 기록
}
```

### 3. 비즈니스 로직 메서드

엔티티에 도메인 로직을 포함하는 메서드를 작성합니다:

```java
public void updateFromRequest(final RoutineUpdateRequest request) {
	if (request.isTitleChanged()) {
		this.title = request.title();
	}

	// 스케줄 변경 시 자동으로 수정 시점 기록
	if (request.isTimeChanged() && !Objects.equals(this.time, request.time())) {
		this.time = request.time();
		this.scheduleModifiedAt = LocalDateTime.now();
	}

	if (request.isDaysOfWeekChanged()) {
		DaysOfWeekBitmask newDaysOfWeek = DaysOfWeekBitmask.createByDayOfWeek(request.daysOfWeek());
		if (!newDaysOfWeek.equals(this.daysOfWeekBitmask)) {
			this.daysOfWeekBitmask = newDaysOfWeek;
			this.scheduleModifiedAt = LocalDateTime.now();
		}
	}

	// ... 다른 필드 업데이트 로직
}

// 권한 체크 메서드
public boolean isOwner(final User requestingUser) {
	return this.user.getId().equals(requestingUser.getId());
}

// 비즈니스 상태 체크 메서드
public Boolean isAllDay() {
	return time == null;
}
```

### 4. 연관관계 설정 원칙

연관관계는 꼭 필요한 경우에만 사용하도록 하고, 남용하면 유지보수에 문제가 생기므로 잘 생각해서 사용하세요.

**지연 로딩을 기본으로 사용** (성능 최적화):

```java

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

**일대다 관계에서 cascade 및 orphanRemoval 설정**:

```java

@OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
private List<DiaryImage> diaryImages = new ArrayList<>();
```

**소프트 삭제를 위한 Hibernate 어노테이션 사용**:

```java
@SQLDelete(sql = "UPDATE diary SET deleted_at = NOW() where id=?")
@SQLRestriction(value = "deleted_at is NULL")
```

## 값 객체(Value Object) 작성 규칙

### 1. @Embeddable 사용 및 검증 패턴

값 객체의 경우, 자주 사용되거나 맥락상 사용하는 경우가 사용하지 않을때 보다 더 적절한 경우 사용하세요.
값 객체는 `@Embeddable`을 사용하여 작성하고, 필요한 경우 검증 로직을 포함합니다:

```java

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA 요구사항
@EqualsAndHashCode  // 값 객체 동등성 비교
@Getter
public class PlanCategoryColor {
	private static final Pattern HEX_COLOR_CODE_PATTERN = Pattern.compile(HEX_COLOR_CODE_REGEX);

	@Column(name = "color")
	private String value;

	private PlanCategoryColor(final String value) {
		validate(value);
		this.value = value;
	}

	// 팩토리 메서드 패턴
	public static PlanCategoryColor from(final String color) {
		if (color == null) {
			return null;
		}
		return new PlanCategoryColor(color);
	}

	// 필수 검증 로직
	private void validate(final String value) {
		if (value != null) {
			if (!HEX_COLOR_CODE_PATTERN.matcher(value).matches()) {
				throw new VoException("색상은 '#RRGGBB' 형식이어야 합니다.");
			}
		}
	}
}
```

### 2. 길이 제한이 있는 값 객체

```java

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class RoutineMemo {
	private static final int MAX_LENGTH = 40;

	@Column(name = "memo", columnDefinition = "TEXT")
	private String value;

	private RoutineMemo(final String value) {
		validate(value);
		this.value = value;
	}

	public static RoutineMemo from(final String memo) {
		if (memo == null) {
			return null;
		}
		return new RoutineMemo(memo);
	}

	private void validate(final String value) {
		if (value != null && value.length() > MAX_LENGTH) {
			throw new VoException("메모는 " + MAX_LENGTH + "자를 초과할 수 없습니다.");
		}
	}
}
```

## 레포지토리 작성 규칙

### 1. 표준 레포지토리 인터페이스

기본 CRUD 및 표준 쿼리를 위한 메인 레포지토리 패턴:

```java

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long>, RoutineRepositoryCustom {

	// 표준 쿼리 메서드 (Spring Data JPA)
	Optional<Routine> findByIdAndUser(final Long id, final User user);

	List<Routine> findAllByUserAndIsPublicTrueAndDeletedAtIsNullOrderByUpdatedAtDesc(final User user);

	// 원자적 연산을 위한 @Modifying 쿼리 (동시성 제어)
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Routine r SET r.sharedCount = r.sharedCount + 1 WHERE r.id = :id")
	void incrementSharedCountAtomically(@Param("id") final Long id);

	// 집계 쿼리 (COALESCE로 null 처리)
	@Query("SELECT COALESCE(SUM(r.sharedCount), 0) FROM Routine r WHERE r.user = :user AND r.isPublic = true AND r.deletedAt IS NULL")
	int sumRoutineSharedCountByUser(@Param("user") final User user);
}
```

### 2. QueryDSL을 활용한 복잡한 쿼리 구조

복잡한 쿼리는 QueryDSL을 사용하여 별도 인터페이스로 분리합니다:

**인터페이스 정의:**

```java
public interface RoutineRepositoryCustom {
	List<Routine> findUnrecordedRoutinesByDateMatchingDayOfWeek(
		final User user,
		final LocalDate date,
		final List<RoutineRecord> routineRecords
	);

	List<Routine> findRoutinesByDateBetween(
		final User user,
		final LocalDate startDate,
		final LocalDate endDate
	);

	boolean isActiveForDate(final Routine routine, final LocalDate date);

	void deleteAllUnsharedRoutinesByUser(final User user);
}
```

**QueryDSL 구현체:**

```java

@Repository
@RequiredArgsConstructor
public class RoutineRepositoryCustomImpl implements RoutineRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QRoutine qRoutine = QRoutine.routine;

	@Override
	public List<Routine> findUnrecordedRoutinesByDateMatchingDayOfWeek(
		final User user,
		final LocalDate date,
		final List<RoutineRecord> routineRecords
	) {
		return queryFactory
			.selectFrom(qRoutine)
			.where(
				qRoutine.user.eq(user),
				scheduleModifiedOnOrBeforeDate(date),
				routineNotRecorded(routineRecords),
				routineMatchesDate(date),
				routineNotDeleted()
			)
			.fetch();
	}

	// 재사용 가능한 조건 메서드들
	private BooleanExpression routineNotDeleted() {
		return qRoutine.deletedAt.isNull();
	}

	private BooleanExpression scheduleModifiedOnOrBeforeDate(final LocalDate date) {
		LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
		return qRoutine.scheduleModifiedAt.loe(endOfDay);
	}

	// 비트 연산을 활용한 요일 매칭 (성능 최적화)
	private BooleanExpression routineMatchesDate(final LocalDate date) {
		byte dayBitmask = DaysOfWeekBitmask.getDayBitmask(date.getDayOfWeek());

		return Expressions.numberTemplate(
			Byte.class, "function('bitand', {0}, CAST({1} as byte))",
			qRoutine.daysOfWeekBitmask, dayBitmask
		).gt((byte)0);
	}
}
```

### 3. QueryDSL 사용 시 주의사항

- **성능 최적화**: 복잡한 조건은 메서드로 분리하여 재사용
- **타입 안전성**: Q클래스를 사용하여 컴파일 타임 안전성 확보
- **동적 쿼리**: BooleanExpression을 활용한 조건부 쿼리
- **페이징**: limit, offset을 활용한 커서 기반 페이징 구현

## 서비스 레이어 작성 규칙

### 1. 도메인 서비스 (데이터 접근 계층)

**중요한 아키텍처 원칙**: 
- **서비스는 자신의 도메인 레포지토리만 사용**해야 합니다
- **다른 도메인의 레포지토리를 직접 의존하면 안됩니다**
- **다른 도메인의 데이터가 필요한 경우, 유스케이스 레이어에서 여러 서비스를 조합**해야 합니다

도메인 서비스는 주로 **자신의 도메인 데이터 접근 로직과 단순한 비즈니스 로직**을 담당합니다:

```java

@Slf4j
@Service
@RequiredArgsConstructor
public class RoutineService {
	private final RoutineRepository routineRepository;

	@Transactional
	public RoutineCreateResponse create(final User user, final RoutineCreateRequest request) {
		Routine routine = RoutineMapper.toRoutine(user, request);
		Routine savedRoutine = routineRepository.save(routine);
		return RoutineMapper.toRoutineCreateResponse(savedRoutine);
	}

	@Transactional(readOnly = true)
	public List<Routine> getUnrecordedRoutinesForDate(
		final User user,
		final LocalDate date,
		final List<RoutineRecord> routineRecords
	) {
		return routineRepository.findUnrecordedRoutinesByDateMatchingDayOfWeek(user, date, routineRecords);
	}

	// Optional 사용으로 null 안전성 확보
	@Transactional(readOnly = true)
	public Optional<Routine> getUserRoutine(final User user, final Long id) {
		return routineRepository.findByIdAndUser(id, user);
	}

	// 비즈니스 로직 메서드
	public boolean canCreateRecordForDate(final Routine routine, final LocalDate date) {
		return routineRepository.isActiveForDate(routine, date);
	}
}
```

### 2. 트랜잭션 경계 설정 원칙

- **조회 전용 메서드**: `@Transactional(readOnly = true)` (성능 최적화)
- **수정 메서드**: `@Transactional` (기본값)
- **배치 작업**: `@Transactional` + 적절한 flush 및 clear

## 유스케이스 작성 규칙

### 1. 비즈니스 로직 조합 및 트랜잭션 관리

유스케이스는 **여러 서비스를 조합하여 복잡한 비즈니스 로직을 구현**합니다:

```java

@Slf4j
@UseCase  // 커스텀 어노테이션 (비즈니스 계층 표시)
@RequiredArgsConstructor
public class RoutineUseCase {
	private static final int MAX_ROUTINE_DATE_RANGE_DAYS = 13;  // 비즈니스 상수

	private final UserService userService;
	private final RoutineService routineService;
	private final RoutineRecordService routineRecordService;
	private final DistributedLock distributedLock;              // 동시성 제어
	private final ApplicationEventPublisher eventPublisher;    // 도메인 이벤트

	@Transactional
	public RoutineCreateResponse createRoutine(final Long userId, final RoutineCreateRequest request) {
		// 1. 사용자 검증
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		// 2. 비즈니스 로직 실행
		RoutineCreateResponse response = routineService.create(user, request);

		// 3. 로깅 (비즈니스 이벤트 추적)
		log.info("루틴 생성 - UserId: {}, RoutineId:{}", userId, response.routineId());

		// 4. 도메인 이벤트 발행 (비동기 처리)
		eventPublisher.publishEvent(new RoutineCreatedEvent(response.routineId(), userId));

		return response;
	}
}
```

### 2. 비즈니스 규칙 검증 패턴

유스케이스에서 **비즈니스 규칙을 엄격히 검증**합니다:

```java

@Transactional(readOnly = true)
public MyRoutineRecordReadMultipleDatesResponse readMyRoutineRecordListMultipleDates(
	final Long userId,
	final LocalDate startDate,
	final LocalDate endDate
) {
	// 비즈니스 규칙 검증 (사전 조건)
	if (startDate.isAfter(endDate) ||
		ChronoUnit.DAYS.between(startDate, endDate) > MAX_ROUTINE_DATE_RANGE_DAYS) {
		throw CommonException.from(ExceptionCode.EXCEED_ROUTINE_DATE_RANGE);
	}

	User user = userService.getUserById(userId)
		.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

	// 복잡한 비즈니스 로직 실행
	List<RoutineRecord> allRoutineRecords =
		routineRecordService.getRecordsBetweenDates(user, startDate, endDate);

	List<DailyRoutineData> dailyRoutineDatas =
		routineService.getRoutineDataByDateRange(user, startDate, endDate, allRoutineRecords);

	log.info("본인 루틴 기록 기간 조회 - UserId: {}, 조회 기간: {} ~ {}", userId, startDate, endDate);

	return RoutineMapper.toMyRoutineRecordReadMultipleDatesResponse(
		startDate, endDate, dailyRoutineDatas
	);
}
```

### 3. 분산 락을 활용한 동시성 제어

동시성 제어가 필요한 경우 분산 락을 사용합니다:

```java

@Transactional
public void updateRoutineCompletion(
	final Long userId,
	final Long routineId,
	final RoutinePutCompletionRequest request
) {
	User user = userService.getUserById(userId)
		.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
	Routine routine = routineService.getUserRoutineIncludingDeleted(user, routineId)
		.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_ROUTINE));

	LocalDate date = request.routineDate();
	boolean isCompleted = request.isCompleted();

	// 분산 락 키 생성 (리소스별 세밀한 제어)
	String lockKey = "routine:" + routineId + ":date:" + date;

	distributedLock.executeWithLock(lockKey, () -> {
		// 원자적으로 실행되어야 하는 로직
		if (routineRecordService.updateIfPresent(routine, date, isCompleted)) {
			log.info("루틴 상태 변경 성공(기록 수정) - 사용자 Id: {}, 루틴 Id: {}, 루틴 날짜: {}, 완료상태: {}",
				userId, routineId, date, isCompleted);
			return;
		}

		if (!routineService.canCreateRecordForDate(routine, date)) {
			log.info("루틴 상태 변경 실패 - 사용자 Id: {}, 루틴 Id: {}, 루틴 날짜: {}", userId, routineId, date);
			throw CommonException.from(ExceptionCode.ROUTINE_INVALID_DATE);
		}

		routineRecordService.create(routine, date, isCompleted);
		log.info("루틴 상태 변경 성공(기록 생성) - 사용자 Id: {}, 루틴 Id: {}, 루틴 날짜: {}, 완료상태: {}",
			userId, routineId, date, isCompleted);
	});
}
```

## 프레젠테이션 레이어 작성 규칙

### 1. API 문서화를 위한 인터페이스 분리

API 문서화와 구현을 분리하여 관리합니다:

```java

@Tag(name = "Routine")  // Swagger 태그
public interface RoutineApi {
	@Operation(
		summary = "루틴 생성",
		description = "내 루틴을 생성합니다. Request body 에서 필수 값 여부를 확인할 수 있습니다. 예를들어, time 필드가 null 인 경우에는 종일 루틴으로 간주합니다."
	)
	@ApiResponseExplanations(  // 커스텀 어노테이션
		success = @ApiSuccessResponseExplanation(
			responseClass = RoutineCreateResponse.class,
			description = "루틴 생성 성공, 생성된 루틴의 Id를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_USER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_INPUT)
		}
	)
	ResponseEntity<ApiResponse<RoutineCreateResponse>> postRoutine(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestBody @Valid final RoutineCreateRequest request
	);
}
```

### 2. 컨트롤러 구현 패턴

컨트롤러는 API 인터페이스를 구현하고 **유스케이스에만 위임**합니다:

```java

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/routines")
public class RoutineController implements RoutineApi {
	private final RoutineUseCase routineUseCase;

	@Override
	@PostMapping
	@PreAuthorize("isAuthenticated()")  // 인증 체크
	public ResponseEntity<ApiResponse<RoutineCreateResponse>> postRoutine(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestBody @Valid final RoutineCreateRequest request
	) {
		RoutineCreateResponse response = routineUseCase.createRoutine(
			userDetails.getUserId(),
			request
		);

		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}
}
```

### 3. ApiResponse 사용 규칙

**중요한 API 응답 규칙**:
- **성공 응답**: `ApiResponse.createSuccess(content)` 사용
- **내용 없는 성공 응답**: `ApiResponse.createSuccessWithNoContent()` 사용  
- **절대 사용 금지**: `ApiResponse.onSuccess()` (존재하지 않는 메서드)

```java
// ✅ 올바른 패턴 - 데이터가 있는 성공 응답
return ResponseEntity.ok(ApiResponse.createSuccess(response));

// ✅ 올바른 패턴 - 데이터가 없는 성공 응답 (삭제, 수정 등)
return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());

// ❌ 잘못된 패턴 - 존재하지 않는 메서드
return ResponseEntity.ok(ApiResponse.onSuccess(response));

// ❌ 잘못된 패턴 - 응답 래핑 누락
return ResponseEntity.ok(response);
```

**ApiResponse의 주요 정적 메서드들**:
- `createSuccess(T content)`: 성공 응답 (데이터 포함)
- `createSuccessWithNoContent()`: 성공 응답 (빈 Map 반환)
- `createError(ExceptionCode ec)`: 에러 응답 (ExceptionCode 기반)
- `createValidationError(Map<String, String> errors)`: 유효성 검사 오류 응답
- `createServerError()`: 서버 오류 응답

## DTO 작성 규칙

### 1. Record 사용 및 검증 어노테이션

요청/응답 DTO는 **불변 객체인 record**를 사용합니다:

```java

@Schema(description = "루틴 생성 요청 DTO")
public record RoutineCreateRequest(
	@NotBlank(message = "제목은 비어있을 수 없습니다.")
	@Size(max = 20, message = "제목은 20자를 초과할 수 없습니다.")
	@Schema(description = "루틴 제목", example = "아침 운동")
	String title,

	@NotNull(message = "카테고리는 비어있을 수 없습니다.")
	@Schema(description = "루틴 카테고리", example = "COMPUTER")
	PlanCategory category,

	@Schema(description = "루틴 색상 (색상 없으면 null)", example = "#FF5733")
	@Pattern(regexp = HEX_COLOR_CODE_REGEX, message = "색상은 유효한 Hex code 여야 합니다.")
	String color,

	@JsonDeserialize(using = LocalTimeDeserializer.class)  // 커스텀 직렬화
	@JsonFormat(pattern = "HH:mm")
	@Schema(description = "루틴 시간 (종일 루틴이면 null)", example = "07:00")
	LocalTime time,

	@NotNull(message = "공개 여부는 null 일 수 없습니다.")
	@Schema(description = "공개 여부", example = "true")
	Boolean isPublic,

	@JsonDeserialize(using = DayOfWeekListDeserializer.class)  // 커스텀 리스트 직렬화
	@NotNull(message = "반복 요일은 null 일 수 없습니다.")
	@NotEmpty(message = "반복 요일은 최소 하나 이상 선택되어야 합니다.")
	@Schema(description = "반복 요일", example = "[\"MONDAY\",\"TUESDAY\"]")
	List<DayOfWeek> daysOfWeek,

	@PositiveOrZero(message = "분은 양수여야 합니다.")
	@Schema(description = "알림 시간 (분 단위, null 이면 알림을 보내지 않음)", example = "30")
	Integer reminderMinutes,

	@Schema(description = "메모", example = "30분 동안 조깅하기")
	@Size(max = 40, message = "메모는 40자를 넘을 수 없습니다.")
	String memo
) {
}
```

### 2. 응답 DTO에서 Builder 패턴

복잡한 응답 DTO에서는 Builder 패턴을 사용합니다:

```java

@Schema(description = "루틴 상세조회 응답 DTO")
@Builder
public record RoutineDetailResponse(
	@Schema(description = "루틴 Id", example = "1")
	Long routineId,

	@Schema(description = "루틴 카테고리", example = "PENCIL")
	PlanCategory category,

	@JsonSerialize(using = LocalTimeSerializer.class)  // 커스텀 직렬화
	@JsonFormat(pattern = "HH:mm")
	@Schema(description = "루틴 시간(null 이면 종일 루틴)", example = "14:30")
	LocalTime time,

	@Schema(description = "반복 요일 목록")
	List<DayOfWeek> daysOfWeek
) {
}
```

## 매퍼 작성 규칙

### 1. 정적 유틸리티 클래스 패턴

매퍼는 **정적 유틸리티 클래스**로 작성하고 외부 인스턴스화를 방지합니다:

```java

@NoArgsConstructor(access = AccessLevel.PRIVATE)  // 인스턴스화 방지
public final class RoutineMapper {
	private static final boolean INCOMPLETE_STATUS = false;  // 상수 정의

	public static Routine toRoutine(final User user, final RoutineCreateRequest request) {
		// 값 객체 생성
		DaysOfWeekBitmask daysOfWeekBitmask = DaysOfWeekBitmask.createByDayOfWeek(request.daysOfWeek());
		PlanCategoryColor planCategoryColor = PlanCategoryColor.from(request.color());
		RoutineMemo routineMemo = RoutineMemo.from(request.memo());

		return Routine.builder()
			.user(user)
			.category(request.category())
			.color(planCategoryColor)
			.title(request.title())
			.memo(routineMemo)
			.isPublic(request.isPublic())
			.reminderMinutes(request.reminderMinutes())
			.time(request.time())
			.daysOfWeekBitmask(daysOfWeekBitmask)
			.build();
	}

	public static RoutineCreateResponse toRoutineCreateResponse(final Routine routine) {
		return RoutineCreateResponse.builder()
			.routineId(routine.getId())
			.build();
	}
}
```

### 2. Stream API 활용 패턴

복잡한 데이터 변환 시 **Stream API**를 적극 활용합니다:

```java
public static MyRoutineRecordReadListResponse toMyRoutineRecordReadListResponse(
	final DailyRoutineData dailyRoutineData
) {
	// 미완료 루틴 응답 생성
	List<MyRoutineReadResponse> routineResponses = dailyRoutineData.routines()
		.stream()
		.map(routine -> toMyRoutineRecordReadResponse(routine, INCOMPLETE_STATUS))
		.toList();

	// 기록된 루틴 응답 생성
	List<MyRoutineReadResponse> recordResponses = dailyRoutineData.routineRecords()
		.stream()
		.map(record -> toMyRoutineRecordReadResponse(record.getRoutine(), record.getIsCompleted()))
		.toList();

	// 두 스트림 합치기
	List<MyRoutineReadResponse> combinedResponses =
		Stream.concat(routineResponses.stream(), recordResponses.stream())
			.toList();

	return MyRoutineRecordReadListResponse.builder()
		.queryDate(dailyRoutineData.date())
		.routines(combinedResponses)
		.build();
}
```

## 커스텀 어노테이션 사용법

### 1. UseCase 어노테이션

비즈니스 계층을 명확히 표시하는 커스텀 어노테이션:

```java

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component  // Spring Bean 등록
public @interface UseCase {
}
```

사용법:

```java

@UseCase  // 유스케이스 계층임을 명시
@RequiredArgsConstructor
public class RoutineUseCase {
	// ...
}
```

### 2. API 문서화 어노테이션

API 응답을 체계적으로 문서화하는 커스텀 어노테이션:

```java

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiResponseExplanations {
	ApiSuccessResponseExplanation success() default @ApiSuccessResponseExplanation();

	ApiErrorResponseExplanation[] errors() default {};
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSuccessResponseExplanation {
	HttpStatus status() default HttpStatus.OK;

	Class<?> responseClass() default EmptyClass.class;

	String description() default "";
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorResponseExplanation {
	ExceptionCode exceptionCode();
}
```

## 메서드 파라미터 및 코딩 컨벤션

### 1. 메서드 파라미터 규칙

- **모든 메서드 파라미터는 final 키워드 사용**
- **명확한 의미를 가진 파라미터명 사용**
- **null 체크가 필요한 경우 Optional 사용**

```java
// ✅ 올바른 패턴
public void updateRoutine(final Long userId, final Long routineId, final RoutineUpdateRequest request) {
	// ...
}

public Optional<Routine> getUserRoutine(final User user, final Long id) {
	// ...
}

// ❌ 잘못된 패턴
public void updateRoutine(Long userId, Long routineId, RoutineUpdateRequest request) {
	// ...
}
```

### 2. 메서드 명명 규칙

- **동사 + 명사** 조합으로 명확한 의도 표현
- **비즈니스 용어** 사용
- **boolean 반환 메서드는 is/can/has 접두사** 사용

```java
// ✅ 올바른 메서드명
public RoutineCreateResponse createRoutine(...)

public boolean canCreateRecordForDate(...)

public boolean isActiveForDate(...)

public boolean isOwner(...)

public void deleteIncompletedFuturesByRoutine(...)

// ❌ 잘못된 메서드명
public RoutineCreateResponse create(...)  // 너무 일반적

public boolean checkDate(...)             // 불명확

public void remove(...)                   // 비즈니스 의미 부족
```

### 3. 클래스 및 패키지 명명 규칙

- **도메인 용어를 중심**으로 명명
- **계층별 일관된 접미사** 사용
- **패키지는 기능별** 구성

```java
// ✅ 올바른 명명
RoutineUseCase          // 유스케이스 계층
	RoutineService          // 서비스 계층  
RoutineRepository       // 레포지토리 계층
	RoutineCreateRequest    // 요청 DTO
RoutineDetailResponse   // 응답 DTO
	RoutineMapper          // 매퍼 클래스
PlanCategoryColor      // 값 객체
```

## 예외 처리 규칙

### 1. 비즈니스 예외는 ExceptionCode 사용

시스템 전반에서 **일관된 예외 처리**를 위해 ExceptionCode를 사용합니다:

```java
// 표준 예외 처리 패턴
User user = userService.getUserById(userId)
		.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

Routine routine = routineService.getUserRoutine(user, routineId)
	.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_ROUTINE));

// 비즈니스 규칙 위반 시
if(startDate.

isAfter(endDate)){
	throw CommonException.

from(ExceptionCode.EXCEED_ROUTINE_DATE_RANGE);
}
```

### 2. 값 객체 검증 예외

값 객체의 검증 예외는 `VoException`을 사용합니다:

```java
private void validate(final String value) {
	if (value != null && value.length() > MAX_LENGTH) {
		throw new VoException("메모는 " + MAX_LENGTH + "자를 초과할 수 없습니다.");
	}
}
```

## 도메인 이벤트 작성 규칙

### 1. 이벤트 클래스 (불변 객체)

이벤트를 사용하는것이 유지보수나 가독성 측면에서 효과가 있다고 판단되는 경우에만 선택적으로 사용하세요
도메인 이벤트는 **불변 객체**로 작성하고 **final 필드**를 사용합니다:

```java

@Getter
public class RoutineCreatedEvent {
	private final Long routineId;
	private final Long userId;
	private final LocalDateTime occurredAt;

	public RoutineCreatedEvent(final Long routineId, final Long userId) {
		this.routineId = routineId;
		this.userId = userId;
		this.occurredAt = LocalDateTime.now();  // 이벤트 발생 시점 기록
	}
}

// 더 복잡한 이벤트 예제
@Getter
public class RoutineUpdatedEvent {
	private final Long routineId;
	private final Long userId;
	private final boolean isTimeChanged;
	private final boolean isDaysOfWeekChanged;
	private final boolean isReminderMinutesChanged;
	private final boolean isTitleChanged;
	private final LocalDateTime occurredAt;

	public RoutineUpdatedEvent(final Long routineId, final Long userId,
		final boolean isTimeChanged, final boolean isDaysOfWeekChanged,
		final boolean isReminderMinutesChanged, final boolean isTitleChanged) {
		this.routineId = routineId;
		this.userId = userId;
		this.isTimeChanged = isTimeChanged;
		this.isDaysOfWeekChanged = isDaysOfWeekChanged;
		this.isReminderMinutesChanged = isReminderMinutesChanged;
		this.isTitleChanged = isTitleChanged;
		this.occurredAt = LocalDateTime.now();
	}
}
```

### 2. 이벤트 발행 패턴

유스케이스에서 `ApplicationEventPublisher`를 사용하여 이벤트를 발행합니다:

```java

@Transactional
public void updateRoutine(final Long userId, final Long routineId, final RoutineUpdateRequest request) {
	// ... 비즈니스 로직 실행

	routineService.updateFields(routine, request);

	log.info("루틴 수정 성공 - 사용자 Id: {}, 루틴 Id: {}", userId, routineId);

	// 도메인 이벤트 발행 (트랜잭션 커밋 후 비동기 처리)
	eventPublisher.publishEvent(new RoutineUpdatedEvent(
		routineId,
		userId,
		request.isTimeChanged(),
		request.isDaysOfWeekChanged(),
		request.isReminderMinutesChanged(),
		request.isTitleChanged()
	));
}
```

### 3. 이벤트 리스너 패턴

이벤트 리스너는 `@EventListener`와 `@Async`를 사용합니다:

```java

@Component
@RequiredArgsConstructor
@Slf4j
public class RoutineReminderEventListener {

	private final RoutineReminderSchedulerService schedulerService;

	@EventListener
	@Async  // 비동기 처리
	@Transactional  // 별도 트랜잭션
	public void handleRoutineCreated(final RoutineCreatedEvent event) {
		try {
			schedulerService.scheduleReminder(event.getRoutineId());
			log.info("루틴 알림 스케줄링 성공 - RoutineId: {}", event.getRoutineId());
		} catch (Exception e) {
			log.error("루틴 알림 스케줄링 실패 - RoutineId: {}", event.getRoutineId(), e);
		}
	}

	@EventListener
	@Async
	@Transactional
	public void handleRoutineUpdated(final RoutineUpdatedEvent event) {
		// 알림 설정이 변경된 경우에만 처리
		if (event.isTimeChanged() || event.isDaysOfWeekChanged() || event.isReminderMinutesChanged()) {
			try {
				schedulerService.rescheduleReminder(event.getRoutineId());
				log.info("루틴 알림 재스케줄링 성공 - RoutineId: {}", event.getRoutineId());
			} catch (Exception e) {
				log.error("루틴 알림 재스케줄링 실패 - RoutineId: {}", event.getRoutineId(), e);
			}
		}
	}
}
```

## 로깅 규칙

### 1. 구조화된 로깅 패턴

주요 비즈니스 이벤트는 **구조화된 형태**로 로깅합니다:

```java
// ✅ 올바른 로깅 패턴 (구조화된 정보)
log.info("루틴 생성 - UserId: {}, RoutineId: {}",userId, routineCreateResponse.routineId());
	log.

info("루틴 상태 변경 성공(기록 수정) - 사용자 Id: {}, 루틴 Id: {}, 루틴 날짜: {}, 완료상태: {}",
	userId, routineId, date, isCompleted);
log.

info("본인 루틴 기록 기간 조회 - UserId: {}, 조회 기간: {} ~ {}",userId, startDate, endDate);

// ❌ 잘못된 로깅 패턴
log.

info("루틴을 생성했습니다.");  // 정보 부족
log.

info("루틴 생성 완료: "+userId +", "+routineId);  // 문자열 연결
```

### 2. 로그 레벨별 사용 기준

- **INFO**: 주요 비즈니스 이벤트, 성공 케이스
- **WARN**: 권한 부족, 비정상적인 접근 시도
- **ERROR**: 예외 발생, 시스템 오류

```java
// INFO - 정상적인 비즈니스 플로우
log.info("루틴 생성 - UserId: {}, RoutineId: {}",userId, routine.getId());

	// WARN - 보안 관련, 권한 위반
	log.

warn("권한이 없는 유저가 일기 수정 시도 - UserId: {}, DiaryId: {}",user.getId(),diary.

getId());

	// ERROR - 예외 상황, 시스템 오류
	log.

error("루틴 알림 스케줄링 실패 - RoutineId: {}",routine.getId(),e);
```

## 성능 최적화 규칙

### 1. 연관관계 최적화

**지연 로딩을 기본으로 사용**하고, 필요한 경우에만 fetch join 사용:

```java
// 기본 설정 - 지연 로딩
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;

// QueryDSL에서 필요한 경우 fetch join
public List<RoutineRecord> findAllByUserAndRecordAtDate(final User user, final LocalDate date) {
	return queryFactory
		.selectFrom(qRecord)
		.join(qRecord.routine, qRoutine).fetchJoin()  // 명시적 fetch join
		.where(
			qRoutine.user.eq(user),
			recordAtBetween(date)
		)
		.fetch();
}
```

### 2. 배치 연산 활용

여러 데이터를 처리할 때는 **배치 연산**을 사용합니다:

```java
// 일괄 저장
public void saveAll(final List<RoutineRecord> newRecords) {
	if (!newRecords.isEmpty()) {
		routineRecordRepository.saveAll(newRecords);
	}
}

// 일괄 삭제 (QueryDSL)
@Override
public void deleteIncompletedFuturesByRoutine(final Routine routine, final LocalDateTime targetDateTime) {
	queryFactory
		.delete(qRecord)
		.where(
			qRecord.routine.eq(routine),
			qRecord.recordAt.after(targetDateTime),
			qRecord.isCompleted.isFalse(),
			qRecord.deletedAt.isNull()
		)
		.execute();
}
```

### 3. 원자적 연산 (동시성 제어)

카운터 등의 업데이트는 **원자적 연산**을 사용합니다:

```java

@Modifying(clearAutomatically = true)  // 영속성 컨텍스트 자동 클리어
@Query("UPDATE Routine r SET r.sharedCount = r.sharedCount + 1 WHERE r.id = :id")
void incrementSharedCountAtomically(@Param("id") final Long id);

// 사용 예제
public void shareRoutine(final Long routineId) {
	routineRepository.incrementSharedCountAtomically(routineId);
}
```

### 4. 커서 기반 페이징

대용량 데이터 조회 시 **커서 기반 페이징**을 사용합니다:

```java
private BooleanExpression cursorCondition(final Long cursor) {
	if (cursor == null) {
		return null;
	}
	return qSocial.id.lt(cursor);  // ID 기준 커서
}

private JPAQuery<Social> applyPagination(final JPAQuery<Social> query, final Pageable pageable) {
	return query
		.orderBy(qSocial.id.desc())
		.offset(pageable.getOffset())
		.limit(pageable.getPageSize());
}
```

이 가이드를 따라 새로운 도메인을 개발하면 **코드 품질과 패턴 일관성**을 유지할 수 있습니다. 특히 QueryDSL을 활용한 복잡한 쿼리 처리, 커스텀 어노테이션을 통한 API
문서화, final 키워드를 통한 불변성 보장, 그리고 철저한 비즈니스 로직 검증을 통해 견고하고 확장 가능한 시스템을 구축할 수 있습니다.

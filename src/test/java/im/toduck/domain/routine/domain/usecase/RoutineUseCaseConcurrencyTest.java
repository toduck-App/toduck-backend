package im.toduck.domain.routine.domain.usecase;

import static im.toduck.fixtures.routine.RoutineFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import im.toduck.ServiceTest;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.routine.persistence.repository.RoutineRecordRepository;
import im.toduck.domain.routine.persistence.repository.RoutineRepository;
import im.toduck.domain.routine.presentation.dto.request.RoutinePutCompletionRequest;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import jakarta.persistence.EntityManager;

class RoutineUseCaseConcurrencyTest extends ServiceTest {

	@Autowired
	private RoutineUseCase routineUseCase;

	@Autowired
	private RoutineRepository routineRepository;

	@Autowired
	private RoutineRecordRepository routineRecordRepository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@MockBean
	private UserService userService;

	private User USER;

	@BeforeEach
	void setUp() {
		// given
		USER = testFixtureBuilder.buildUser(GENERAL_USER());
		given(userService.getUserById(any(Long.class))).willReturn(Optional.ofNullable(USER));
	}

	@Nested
	@DisplayName("루틴 완료 상태 변경의 동시성 처리시")
	class UpdateRoutineCompletionConcurrencyTest {
		private Long routineId;

		@BeforeEach
		void setUp() {
			TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

			routineId = transactionTemplate.execute(status -> {
				Routine routine = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
					PUBLIC_MONDAY_MORNING_ROUTINE(USER)
						.createdAt("2024-11-29 01:00:00")
						.build()
				);
				entityManager.flush();
				entityManager.clear();
				return routine.getId();
			});
		}

		@AfterEach
		void tearDown() {
			// 테스트 후 생성된 데이터 정리
			TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
			transactionTemplate.execute(status -> {
				routineRecordRepository.deleteAll();
				if (routineId != null) {
					routineRepository.deleteById(routineId);
				}
				entityManager.flush();
				return null;
			});
		}

		@Test
		void 거의_동시에_여러_요청이_들어올때_루틴_기록이_중복_생성되지_않아야_한다() throws InterruptedException {
			// given
			int numberOfThreads = 5;
			ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
			CountDownLatch startLatch = new CountDownLatch(1);
			CountDownLatch endLatch = new CountDownLatch(numberOfThreads);
			AtomicInteger successCount = new AtomicInteger(0);
			List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
			Random random = new Random();

			LocalDate TEST_MONDAY_DATE = LocalDate.parse("2025-04-14"); // 월요일
			RoutinePutCompletionRequest request = new RoutinePutCompletionRequest(TEST_MONDAY_DATE, true);

			// when - 여러 스레드에서 거의 동시에 같은 루틴에 대한 기록 생성 요청
			for (int i = 0; i < numberOfThreads; i++) {
				executorService.submit(() -> {
					try {
						startLatch.await(); // 모든 스레드가 준비될 때까지 대기

						// 랜덤 지연 시간 (0-50ms)
						Thread.sleep(random.nextInt(50));

						routineUseCase.updateRoutineCompletion(USER.getId(), routineId, request);
						successCount.incrementAndGet();
					} catch (Exception e) {
						exceptions.add(e);
					} finally {
						endLatch.countDown();
					}
				});
			}

			// 모든 스레드 동시 시작
			startLatch.countDown();

			// 모든 작업이 완료될 때까지 대기 (최대 5초)
			boolean completed = endLatch.await(5, TimeUnit.SECONDS);
			executorService.shutdown();

			// then
			assertThat(completed).isTrue();

			TransactionTemplate readTemplate = new TransactionTemplate(transactionManager);
			List<RoutineRecord> routineRecords = readTemplate.execute(status -> {
				entityManager.clear(); // 영속성 컨텍스트 초기화
				return routineRecordRepository.findAll();
			});

			assertSoftly(softly -> {
				softly.assertThat(routineRecords).hasSize(1);
				softly.assertThat(routineRecords.get(0).getRoutine().getId()).isEqualTo(routineId);
				softly.assertThat(routineRecords.get(0).getRecordAt().toLocalDate()).isEqualTo(TEST_MONDAY_DATE);
				softly.assertThat(routineRecords.get(0).getIsCompleted()).isTrue();

				softly.assertThat(successCount.get()).isEqualTo(numberOfThreads);
				softly.assertThat(exceptions).isEmpty();
			});
		}
	}

	@Nested
	@DisplayName("개별 루틴 삭제의 동시성 처리시")
	class DeleteIndividualRoutineConcurrencyTest {
		private Long routineId;

		@BeforeEach
		void setUp() {
			TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

			routineId = transactionTemplate.execute(status -> {
				Routine routine = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
					PUBLIC_MONDAY_MORNING_ROUTINE(USER)
						.createdAt("2024-11-29 01:00:00")
						.build()
				);
				entityManager.flush();
				entityManager.clear();
				return routine.getId();
			});
		}

		@AfterEach
		void tearDown() {
			// 테스트 후 생성된 데이터 정리
			TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
			transactionTemplate.execute(status -> {
				routineRecordRepository.deleteAll();
				if (routineId != null) {
					routineRepository.deleteById(routineId);
				}
				entityManager.flush();
				return null;
			});
		}

		@Test
		void 거의_동시에_여러_요청이_들어올때_개별_루틴_삭제_기록이_중복_생성되지_않아야_한다() throws InterruptedException {
			// given
			int numberOfThreads = 5;
			ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
			CountDownLatch startLatch = new CountDownLatch(1);
			CountDownLatch endLatch = new CountDownLatch(numberOfThreads);
			AtomicInteger successCount = new AtomicInteger(0);
			List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
			Random random = new Random();

			LocalDate TEST_MONDAY_DATE = LocalDate.parse("2025-04-14"); // 월요일

			// when - 여러 스레드에서 거의 동시에 같은 날짜의 개별 루틴 삭제 요청
			for (int i = 0; i < numberOfThreads; i++) {
				executorService.submit(() -> {
					try {
						startLatch.await(); // 모든 스레드가 준비될 때까지 대기

						// 랜덤 지연 시간 (0-50ms)
						Thread.sleep(random.nextInt(50));

						routineUseCase.deleteIndividualRoutine(USER.getId(), routineId, TEST_MONDAY_DATE);
						successCount.incrementAndGet();
					} catch (Exception e) {
						exceptions.add(e);
					} finally {
						endLatch.countDown();
					}
				});
			}

			// 모든 스레드 동시 시작
			startLatch.countDown();

			// 모든 작업이 완료될 때까지 대기 (최대 5초)
			boolean completed = endLatch.await(5, TimeUnit.SECONDS);
			executorService.shutdown();

			// then
			assertThat(completed).isTrue();

			TransactionTemplate readTemplate = new TransactionTemplate(transactionManager);
			List<RoutineRecord> routineRecords = readTemplate.execute(status -> {
				entityManager.clear(); // 영속성 컨텍스트 초기화
				return routineRecordRepository.findAll();
			});

			assertSoftly(softly -> {
				softly.assertThat(routineRecords).hasSize(1);
				softly.assertThat(routineRecords.get(0).getRoutine().getId()).isEqualTo(routineId);
				softly.assertThat(routineRecords.get(0).getRecordAt().toLocalDate()).isEqualTo(TEST_MONDAY_DATE);
				softly.assertThat(routineRecords.get(0).isInDeletedState()).isTrue(); // 삭제 상태인지 확인

				softly.assertThat(successCount.get()).isEqualTo(numberOfThreads);
				softly.assertThat(exceptions).isEmpty();
			});
		}
	}
}

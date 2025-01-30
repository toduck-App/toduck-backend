package im.toduck.domain.routine.domain.usecase;

import static im.toduck.fixtures.routine.RoutineFixtures.*;
import static im.toduck.fixtures.routine.RoutineRecordFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static im.toduck.global.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.routine.persistence.repository.RoutineRecordRepository;
import im.toduck.domain.routine.persistence.repository.RoutineRepository;
import im.toduck.domain.routine.presentation.dto.request.RoutinePutCompletionRequest;
import im.toduck.domain.routine.presentation.dto.response.MyRoutineRecordReadListResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;

;

@Transactional
class RoutineUseCaseTest extends ServiceTest {

	private User USER;

	@Autowired
	private RoutineUseCase routineUseCase;

	@Autowired
	private RoutineRepository routineRepository;

	@Autowired
	private RoutineRecordRepository routineRecordRepository;

	@MockBean
	private UserService userService;

	@BeforeEach
	void setUp() {
		// given
		USER = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Nested
	@DisplayName("루틴 목록 조회시")
	class ReadMyRoutineListTest {
		@BeforeEach
		void setUp() {
			// given
			given(userService.getUserById(any(Long.class))).willReturn(Optional.ofNullable(USER));
		}

		@Test
		void 루틴_기록이_존재하는_경우에는_해당_기록을_그대로_사용한다() {
			// given
			Routine WEEKDAY_MORNING_ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_WEEKDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);
			RoutineRecord ROUTINE_RECORD = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(WEEKDAY_MORNING_ROUTINE).recordAt("2024-12-02 07:00:00").build() // 월요일
			);
			LocalDate queryDate = ROUTINE_RECORD.getRecordAt().toLocalDate();

			// when
			MyRoutineRecordReadListResponse responses = routineUseCase.readMyRoutineRecordList(USER.getId(), queryDate);

			// then
			assertSoftly(softly -> {
				assertThat(responses.queryDate()).isEqualTo(queryDate);
				assertThat(responses.routines()).hasSize(1);

				MyRoutineRecordReadListResponse.MyRoutineReadResponse response = responses.routines().get(0);
				assertThat(response.routineId()).isEqualTo(WEEKDAY_MORNING_ROUTINE.getId());
				assertThat(response.isCompleted()).isEqualTo(ROUTINE_RECORD.getIsCompleted());
				assertThat(response.time()).isEqualTo(ROUTINE_RECORD.getRecordAt().toLocalTime());
			});
		}

		@Test
		void 루틴_기록이_존재하지_않는_경우에도_모_루틴을_통해_해당_기록을_조회할_수_있다() {
			// given
			Routine WEEKDAY_ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_WEEKDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);
			LocalDate queryDate = LocalDate.parse("2024-12-02");

			// when
			MyRoutineRecordReadListResponse responses = routineUseCase.readMyRoutineRecordList(USER.getId(), queryDate);

			// then
			assertSoftly(softly -> {
				assertThat(responses.queryDate()).isEqualTo(queryDate);
				assertThat(responses.routines()).hasSize(1);

				MyRoutineRecordReadListResponse.MyRoutineReadResponse response = responses.routines().get(0);
				assertThat(response.routineId()).isEqualTo(WEEKDAY_ROUTINE.getId());
				assertThat(response.isCompleted()).isFalse();
				assertThat(response.time()).isEqualTo(WEEKDAY_ROUTINE.getTime());
			});
		}

		@Test
		void 루틴_수정으로_인해_동기화되지_않은_루틴_기록을_정상적으로_조회할_수_있다() {
			// given
			Routine WEEKDAY_ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ONLY_MORNING_ROUTINE(USER) // 월요일
					.createdAt("2024-11-29 01:00:00")
					.scheduleModifiedAt("2024-12-05 00:01:00")
					.build()
			);
			RoutineRecord ROUTINE_RECORD = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(WEEKDAY_ROUTINE).recordAt("2024-12-14 07:00:00").build() // 주말
			);

			LocalDate queryDate = LocalDate.parse("2024-12-14");

			// when
			MyRoutineRecordReadListResponse responses = routineUseCase.readMyRoutineRecordList(USER.getId(), queryDate);

			// then
			assertSoftly(softly -> {
				assertThat(responses.queryDate()).isEqualTo(queryDate);
				assertThat(responses.routines()).hasSize(1);

				MyRoutineRecordReadListResponse.MyRoutineReadResponse response = responses.routines().get(0);
				assertThat(response.routineId()).isEqualTo(WEEKDAY_ROUTINE.getId());
				assertThat(response.isCompleted()).isTrue();
				assertThat(response.time()).isEqualTo(ROUTINE_RECORD.getRecordAt().toLocalTime());
			});
		}

		@Test
		void 모_루틴이_Soft_DELETE_된_경우에도_루틴_기록이_존재한다면_정상적으로_조회할_수_있다() {
			// given
			Routine WEEKDAY_ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ONLY_MORNING_ROUTINE(USER) // 월요일
					.createdAt("2024-11-29 01:00:00")
					.scheduleModifiedAt("2024-12-05 00:01:00")
					.deletedAt("2024-12-06 00:01:00")
					.build()
			);
			RoutineRecord ROUTINE_RECORD = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(WEEKDAY_ROUTINE).recordAt("2024-12-14 07:00:00").build()
			);
			LocalDate queryDate = ROUTINE_RECORD.getRecordAt().toLocalDate();

			// when
			MyRoutineRecordReadListResponse responses = routineUseCase.readMyRoutineRecordList(USER.getId(), queryDate);

			// then
			assertSoftly(softly -> {
				assertThat(responses.queryDate()).isEqualTo(queryDate);
				assertThat(responses.routines()).hasSize(1);

				MyRoutineRecordReadListResponse.MyRoutineReadResponse response = responses.routines().get(0);
				assertThat(response.routineId()).isEqualTo(WEEKDAY_ROUTINE.getId());
				assertThat(response.isCompleted()).isEqualTo(ROUTINE_RECORD.getIsCompleted());
				assertThat(response.time()).isEqualTo(ROUTINE_RECORD.getRecordAt().toLocalTime());
			});
		}
	}

	@Nested
	@DisplayName("루틴 완료 상태 변경시")
	class UpdateRoutineCompletionTest {
		@BeforeEach
		void setUp() {
			// given
			given(userService.getUserById(any(Long.class))).willReturn(Optional.ofNullable(USER));
		}

		@Test
		void 기존_기록이_존재하는_경우에_완료_상태_변경이_성공한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));
			RoutineRecord RECORD = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_SYNCED_RECORD(ROUTINE)
			);
			RoutinePutCompletionRequest request =
				new RoutinePutCompletionRequest(RECORD.getRecordAt().toLocalDate(), !RECORD.getIsCompleted());

			// when
			routineUseCase.updateRoutineCompletion(USER.getId(), ROUTINE.getId(), request);

			// then
			List<RoutineRecord> routineRecords = routineRecordRepository.findAll();
			assertSoftly(softly -> {
				softly.assertThat(routineRecords).hasSize(1);
				softly.assertThat(routineRecords.get(0).getIsCompleted()).isFalse();
			});
		}

		@Test
		void 기존_기록이_존재하는_경우에_모_루틴의_반복요일에_변경이_있더라도_변경이_성공한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));
			RoutineRecord RECORD = testFixtureBuilder.buildRoutineRecord(
				INCOMPLETED_MODIFIED_RECORD(ROUTINE)
			);

			RoutinePutCompletionRequest request =
				new RoutinePutCompletionRequest(RECORD.getRecordAt().toLocalDate(), !RECORD.getIsCompleted());

			// when
			routineUseCase.updateRoutineCompletion(USER.getId(), ROUTINE.getId(), request);

			// then
			List<RoutineRecord> routineRecords = routineRecordRepository.findAll();
			assertSoftly(softly -> {
				softly.assertThat(routineRecords).hasSize(1);
				softly.assertThat(routineRecords.get(0).getIsCompleted()).isTrue();
			});
		}

		@Test
		void 기존_기록이_존재하지_않는_경우에_완료_상태_변경이_성공한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));

			RoutinePutCompletionRequest request =
				new RoutinePutCompletionRequest(getNextDayOfWeek(DayOfWeek.MONDAY), true);

			// when
			routineUseCase.updateRoutineCompletion(USER.getId(), ROUTINE.getId(), request);

			// then
			List<RoutineRecord> routineRecords = routineRecordRepository.findAll();
			assertSoftly(softly -> {
				softly.assertThat(routineRecords).hasSize(1);
				softly.assertThat(routineRecords.get(0).getIsCompleted()).isTrue();
			});
		}

		@Test
		void 루틴기록의_일자가_모_루틴의_반복_규칙과_상이한_경우에_예외를_반환한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));

			RoutinePutCompletionRequest request =
				new RoutinePutCompletionRequest(getNextDayOfWeek(DayOfWeek.SATURDAY), true);

			// when & then
			assertThatThrownBy(() -> routineUseCase.updateRoutineCompletion(USER.getId(), ROUTINE.getId(), request))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(ROUTINE_INVALID_DATE.getMessage());
		}

		@Test
		void 존재하지_않거나_권한이_없는_루틴_조회를_시도하는_경우에_예외를_반환한다() {
			// given
			RoutinePutCompletionRequest request =
				new RoutinePutCompletionRequest(getNextDayOfWeek(DayOfWeek.SATURDAY), true);

			// when & then
			assertThatThrownBy(() -> routineUseCase.updateRoutineCompletion(USER.getId(), 1L, request))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(NOT_FOUND_ROUTINE.getMessage());
		}

		@Disabled("추후 테스트 필요")
		@Test
		void 모_루틴이_Soft_DELETE_된_경우에도_상태_변경이_가능하다() {

		}
	}

	@Nested
	@DisplayName("루틴 삭제시")
	class DeleteRoutineTest {
		private MockedStatic<LocalDateTime> mockedStatic;

		@BeforeEach
		void setUp() {
			LocalDateTime fixedNow = LocalDateTime.parse("2024-12-15T10:00:00");
			mockedStatic = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS);
			mockedStatic.when(LocalDateTime::now).thenReturn(fixedNow);

			given(userService.getUserById(any(Long.class))).willReturn(Optional.ofNullable(USER));
		}

		@AfterEach
		void tearDown() {
			mockedStatic.close();
		}

		@Test
		void 모든_기록을_포함하여_삭제할_수_있다() {
			// given
			Routine routine = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_WEEKDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-01 01:00:00")
					.scheduleModifiedAt("2024-12-02 01:00:00")
					.build()
			);

			testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(routine).recordAt("2024-12-02 07:00:00").build()
			);
			testFixtureBuilder.buildRoutineRecord(
				INCOMPLETED_RECORD(routine).recordAt("2024-12-03 07:00:00").build()
			);
			testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(routine).recordAt("2024-12-09 07:00:00").build()
			);
			testFixtureBuilder.buildRoutineRecord(
				INCOMPLETED_RECORD(routine).recordAt("2024-12-10 07:00:00").build()
			);

			// when
			routineUseCase.deleteRoutine(USER.getId(), routine.getId(), false);

			// then
			List<RoutineRecord> remainingRecords = routineRecordRepository.findAll();
			Routine removedRoutine = routineRepository.findById(routine.getId()).get();
			assertSoftly(softly -> {
				softly.assertThat(remainingRecords).isEmpty();
				softly.assertThat(removedRoutine.isInDeletedState()).isTrue();
			});
		}

		@Test
		void 미래의_미완료_기록만_삭제하고_스케쥴변경일시_이후의_미완료기록이_일괄_저장된다() {
			// given
			Routine routine = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_WEEKDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.scheduleModifiedAt("2024-12-02 01:00:00")
					.build()
			);

			// 현재 시간: 2024-12-15T10:00:00

			// 과거 완료 기록 (유지되어야 함)
			RoutineRecord pastComplete = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(routine).recordAt("2024-12-12 07:00:00").build()
			);
			// 과거 미완료 기록 (유지되어야 함)
			RoutineRecord pastIncomplete = testFixtureBuilder.buildRoutineRecord(
				INCOMPLETED_RECORD(routine).recordAt("2024-12-13 07:00:00").build()
			);

			// 미래 미완료 (삭제 되어야 함)
			RoutineRecord futureIncomplete = testFixtureBuilder.buildRoutineRecord(
				INCOMPLETED_RECORD(routine).recordAt("2024-12-30 07:00:00").build()
			);
			// 미래 완료 (유지되어야 함)
			RoutineRecord futureComplete = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(routine).recordAt("2024-12-31 07:00:00").build()
			);

			// when
			routineUseCase.deleteRoutine(USER.getId(), routine.getId(), true);

			// then
			List<RoutineRecord> remainingRecords = routineRecordRepository.findAll();
			Routine removedRoutine = routineRepository.findById(routine.getId()).get();

			// 12월 2일 ~ 12월 13일 (10개), 12월 31일 (1개)
			assertSoftly(softly -> {
				softly.assertThat(remainingRecords).hasSize(11);

				List<Long> remainingIds = remainingRecords.stream()
					.map(RoutineRecord::getId)
					.toList();

				softly.assertThat(remainingIds)
					.contains(pastComplete.getId(), pastIncomplete.getId(), futureComplete.getId());
				softly.assertThat(remainingIds)
					.doesNotContain(futureIncomplete.getId());

				softly.assertThat(removedRoutine.isInDeletedState()).isTrue();
			});
		}
	}

	private LocalDate getNextDayOfWeek(DayOfWeek dayOfWeek) {
		return LocalDate.now().with(TemporalAdjusters.next(dayOfWeek));
	}
}

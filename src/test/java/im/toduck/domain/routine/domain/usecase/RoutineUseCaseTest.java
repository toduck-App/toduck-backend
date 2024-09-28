package im.toduck.domain.routine.domain.usecase;

import static im.toduck.fixtures.RoutineFixtures.*;
import static im.toduck.fixtures.RoutineRecordFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static im.toduck.global.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.routine.persistence.repository.RoutineRecordRepository;
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
			Routine ROUTINE = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));
			RoutineRecord RECORD = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_SYNCED_RECORD(ROUTINE)
			);
			LocalDate queryDate = RECORD.getRecordAt().toLocalDate();

			// when
			MyRoutineRecordReadListResponse responses = routineUseCase.readMyRoutineRecordList(USER.getId(), queryDate);

			// then
			assertSoftly(softly -> {
				assertThat(responses.queryDate()).isEqualTo(queryDate);
				assertThat(responses.routines()).hasSize(1);

				MyRoutineRecordReadListResponse.MyRoutineReadResponse response = responses.routines().get(0);
				assertThat(response.routineId()).isEqualTo(ROUTINE.getId());
				assertThat(response.isCompleted()).isEqualTo(RECORD.getIsCompleted());
				assertThat(response.time()).isEqualTo(RECORD.getRecordAt().toLocalTime());
			});
		}

		@Test
		void 루틴_기록이_존재하지_않는_경우에도_모_루틴을_통해_해당_기록을_조회할_수_있다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));
			LocalDate queryDate = getNextDayOfWeek(DayOfWeek.MONDAY);

			// when
			MyRoutineRecordReadListResponse responses = routineUseCase.readMyRoutineRecordList(USER.getId(), queryDate);

			// then
			assertSoftly(softly -> {
				assertThat(responses.queryDate()).isEqualTo(queryDate);
				assertThat(responses.routines()).hasSize(1);

				MyRoutineRecordReadListResponse.MyRoutineReadResponse response = responses.routines().get(0);
				assertThat(response.routineId()).isEqualTo(ROUTINE.getId());
				assertThat(response.isCompleted()).isFalse();
				assertThat(response.time()).isEqualTo(ROUTINE.getTime());
			});
		}

		@Disabled("추후 테스트 필요")
		@Test
		void 루틴_수정으로_인해_동기화되지_않은_루틴_기록을_정상적으로_조회할_수_있다() {

		}

		@Disabled("추후 테스트 필요")
		@Test
		void 모_루틴이_Soft_DELETE_된_경우에도_루틴_기록이_존재한다면_정상적으로_조회할_수_있다() {

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

	private LocalDate getNextDayOfWeek(DayOfWeek dayOfWeek) {
		return LocalDate.now().with(TemporalAdjusters.next(dayOfWeek));
	}
}

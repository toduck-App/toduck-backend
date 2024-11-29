package im.toduck.domain.routine.persistence.repository;

import static im.toduck.fixtures.RoutineFixtures.*;
import static im.toduck.fixtures.RoutineRecordFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.RepositoryTest;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.user.persistence.entity.User;

class RoutineRepositoryTest extends RepositoryTest {

	@Autowired
	private RoutineRepository routineRepository;

	private User USER;

	@BeforeEach
	void setUp() {
		USER = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Nested
	@DisplayName("기록에 없는 루틴 조회시")
	class FindUnrecordedRoutinesForDateTest {
		@Test
		void 주어진_날짜와_조건에_따라_기록되지_않은_루틴을_올바르게_조회한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(MONDAY_ONLY_MORNING_ROUTINE(USER));
			LocalDate monday = getNextDayOfWeek(DayOfWeek.MONDAY);

			// when
			List<Routine> unrecordedRoutines = routineRepository.findUnrecordedRoutinesForDate(USER, monday, List.of());

			// then
			assertThat(unrecordedRoutines).contains(ROUTINE);
		}

		@Test
		void 루틴_생성_날짜가_조회_날짜보다_이후인_경우_조회되지_않음을_확인한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(MONDAY_ONLY_MORNING_ROUTINE(USER));
			LocalDate monday = getPreviousDayOfWeek(DayOfWeek.MONDAY);

			// when
			List<Routine> unrecordedRoutines = routineRepository.findUnrecordedRoutinesForDate(USER, monday, List.of());

			// then
			assertThat(unrecordedRoutines).doesNotContain(ROUTINE);
		}

		@Test
		void 주어진_날짜와_조건에_따라_이미_루틴기록이_있는_루틴을_조회하지_않는다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(MONDAY_ONLY_MORNING_ROUTINE(USER));
			RoutineRecord ROUTINE_RECORD1 = testFixtureBuilder.buildRoutineRecord(COMPLETED_SYNCED_RECORD(ROUTINE));
			RoutineRecord ROUTINE_RECORD2 = testFixtureBuilder.buildRoutineRecord(COMPLETED_MODIFIED_RECORD(ROUTINE));
			LocalDate monday = getNextDayOfWeek(DayOfWeek.MONDAY);

			// when
			List<Routine> unrecordedRoutines = routineRepository.findUnrecordedRoutinesForDate(USER, monday,
				List.of(ROUTINE_RECORD1, ROUTINE_RECORD2));

			// then
			assertThat(unrecordedRoutines).doesNotContain(ROUTINE);
		}

		@Test
		void 매일_반복되는_루틴이_존재할_때_모든_요일에_조회된다() {
			// given
			Routine DAILYROUTINE = testFixtureBuilder.buildRoutine(DAILY_EVENING_ROUTINE(USER));

			// when & then
			for (int i = 1; i <= 7; i++) {
				LocalDate date = getNextDayOfWeek(DayOfWeek.of(i));
				List<Routine> unrecordedRoutines = routineRepository.findUnrecordedRoutinesForDate(USER, date,
					List.of());
				assertThat(unrecordedRoutines).contains(DAILYROUTINE);
			}
		}

		@Test
		void 일요일_자정_직전과_월요일_자정_직후_루틴이_올바르게_조회된다() {
			// given
			Routine SUNDAY_NIGHT_ROUTINE = testFixtureBuilder.buildRoutine(LAST_DAY_OF_WEEK_NIGHT_ROUTINE(USER));
			Routine MONDAY_MORNING_ROUTINE = testFixtureBuilder.buildRoutine(FIRST_DAY_OF_WEEK_MORNING_ROUTINE(USER));
			LocalDateTime sundayNight = getNextDayOfWeek(DayOfWeek.SUNDAY).atTime(23, 59, 59);
			LocalDateTime mondayMorning = getNextDayOfWeek(DayOfWeek.MONDAY).atTime(0, 0, 1);

			// when
			List<Routine> sundayRoutines = routineRepository.findUnrecordedRoutinesForDate(USER,
				sundayNight.toLocalDate(), List.of());
			List<Routine> mondayRoutines = routineRepository.findUnrecordedRoutinesForDate(USER,
				mondayMorning.toLocalDate(), List.of());

			// then

			// then
			assertSoftly(softly -> {
				softly.assertThat(sundayRoutines).contains(SUNDAY_NIGHT_ROUTINE).doesNotContain(MONDAY_MORNING_ROUTINE);
				softly.assertThat(mondayRoutines).contains(MONDAY_MORNING_ROUTINE).doesNotContain(SUNDAY_NIGHT_ROUTINE);
			});
		}

		@Test
		void 동일한_요일_패턴을_가진_여러_루틴이_있을_때_모두_조회된다() {
			// given
			Routine WEEKDAY_MORNING_ROUTINE1 = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));
			Routine WEEKDAY_MORNING_ROUTINE2 = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));
			LocalDate weekday = getNextDayOfWeek(DayOfWeek.MONDAY);

			// when
			List<Routine> unrecordedRoutines = routineRepository.findUnrecordedRoutinesForDate(USER, weekday,
				List.of());

			// then
			assertThat(unrecordedRoutines).contains(WEEKDAY_MORNING_ROUTINE1, WEEKDAY_MORNING_ROUTINE2);
		}

		@Test
		void 루틴이_삭제되었더라면_삭제시점_이후_날짜로_조회시_조회되지_않는다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(
				DELETED_MONDAY_ONLY_MORNING_ROUTINE(USER,
					getNextDayOfWeek(DayOfWeek.MONDAY).minusDays(1).atTime(23, 59, 59))
			);
			LocalDate monday = getNextDayOfWeek(DayOfWeek.MONDAY);

			// when
			List<Routine> unrecordedRoutines = routineRepository.findUnrecordedRoutinesForDate(USER, monday, List.of());

			// then
			assertThat(unrecordedRoutines).doesNotContain(ROUTINE);
		}

		@Test
		void 특정_기간_동안만_유효한_루틴이_해당_기간_내에서_올바르게_조회된다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(
				DELETED_MONDAY_ONLY_MORNING_ROUTINE(USER,
					getNextDayOfWeek(DayOfWeek.MONDAY).plusDays(7).atTime(7, 59, 59))
			);
			LocalDate monday = getNextDayOfWeek(DayOfWeek.MONDAY);

			// when
			List<Routine> unrecordedRoutines = routineRepository.findUnrecordedRoutinesForDate(USER, monday, List.of());

			// then
			assertThat(unrecordedRoutines).contains(ROUTINE);
		}
	}

	@Nested
	@DisplayName("루틴 날짜 유효성 검증시")
	class IsActiveForDateTest {
		@Test
		void 날짜가_유효한_경우를_정상적으로_확인한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(MONDAY_ONLY_MORNING_ROUTINE(USER));
			LocalDate monday = getNextDayOfWeek(DayOfWeek.MONDAY);

			// when
			boolean isActive = routineRepository.isActiveForDate(ROUTINE, monday);

			// then
			assertThat(isActive).isTrue();
		}

		@Test
		void 루틴_생성_날짜가_검증_날짜보다_이후인_경우를_정상적으로_확인한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(MONDAY_ONLY_MORNING_ROUTINE(USER));
			LocalDate previousMonday = getPreviousDayOfWeek(DayOfWeek.MONDAY);

			// when
			boolean isActive = routineRepository.isActiveForDate(ROUTINE, previousMonday);

			// then
			assertThat(isActive).isFalse();
		}

		@Test
		void 루틴의_반복_요일이_날짜의_요일과_일치하지_않는_경우를_정상적으로_확인한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(MONDAY_ONLY_MORNING_ROUTINE(USER));
			LocalDate tuesday = getNextDayOfWeek(DayOfWeek.TUESDAY);

			// when
			boolean isActive = routineRepository.isActiveForDate(ROUTINE, tuesday);

			// then
			assertThat(isActive).isFalse();
		}

		@Test
		void 루틴이_삭제되었더라면_삭제시점_이후_날짜에_대해_정상적으로_확인된다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(
				DELETED_MONDAY_ONLY_MORNING_ROUTINE(USER,
					getNextDayOfWeek(DayOfWeek.MONDAY).minusDays(1).atTime(23, 59, 59))
			);
			LocalDate monday = getNextDayOfWeek(DayOfWeek.MONDAY);

			// when
			boolean isActive = routineRepository.isActiveForDate(ROUTINE, monday);

			// then
			assertThat(isActive).isFalse();
		}

		@Test
		void 특정_기간_동안만_유효한_루틴이_해당_기간_내에서_정상적으로_확인된다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(
				DELETED_MONDAY_ONLY_MORNING_ROUTINE(USER,
					getNextDayOfWeek(DayOfWeek.MONDAY).plusDays(7).atTime(7, 59, 59))
			);
			LocalDate monday = getNextDayOfWeek(DayOfWeek.MONDAY);

			// when
			boolean isActive = routineRepository.isActiveForDate(ROUTINE, monday);

			// then
			assertThat(isActive).isTrue();
		}
	}

	private LocalDate getNextDayOfWeek(DayOfWeek dayOfWeek) {
		return LocalDate.now().with(TemporalAdjusters.next(dayOfWeek));
	}

	private LocalDate getPreviousDayOfWeek(DayOfWeek dayOfWeek) {
		return LocalDate.now().with(TemporalAdjusters.previous(dayOfWeek));
	}
}

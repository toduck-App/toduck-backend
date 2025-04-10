package im.toduck.domain.routine.persistence.repository;

import static im.toduck.fixtures.routine.RoutineFixtures.*;
import static im.toduck.fixtures.routine.RoutineRecordFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDate;
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
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ONLY_MORNING_ROUTINE(USER)
					.createdAt("2024-12-01 01:00:00")
					.build()
			);
			LocalDate monday = LocalDate.parse("2024-12-02");

			// when
			List<Routine> unrecordedRoutines = routineRepository.findUnrecordedRoutinesForDate(USER, monday, List.of());

			// then
			assertThat(unrecordedRoutines).contains(ROUTINE);
		}

		@Test
		void 종일_루틴이_아닌_경우_루틴_반복요일및시간_변경일시가_조회_날짜보다_이후인_경우_조회되지_않음을_확인한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ONLY_MORNING_ROUTINE(USER)
					.createdAt("2024-12-01 01:00:00")
					.scheduleModifiedAt("2024-12-06 01:00:00")
					.build()
			);
			LocalDate monday = LocalDate.parse("2024-12-02");

			// when
			List<Routine> unrecordedRoutines = routineRepository.findUnrecordedRoutinesForDate(USER, monday, List.of());

			// then
			assertThat(unrecordedRoutines).doesNotContain(ROUTINE);
		}

		@Test
		void 주어진_날짜와_조건에_따라_이미_루틴기록이_있는_루틴을_조회하지_않는다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ONLY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.scheduleModifiedAt("2024-12-01 01:00:00")
					.build()
			);
			RoutineRecord ROUTINE_RECORD1 = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(ROUTINE).recordAt("2024-12-02 01:00:00").build() // 월요일
			);
			RoutineRecord ROUTINE_RECORD2 = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(ROUTINE).recordAt("2024-12-03 01:00:00").build() // 화요일, 수정 있음
			);
			LocalDate monday = LocalDate.parse("2024-12-02");

			// when
			List<Routine> unrecordedRoutines = routineRepository.findUnrecordedRoutinesForDate(
				USER, monday, List.of(ROUTINE_RECORD1, ROUTINE_RECORD2)
			);

			// then
			assertThat(unrecordedRoutines).doesNotContain(ROUTINE);
		}

		@Test
		void 매일_반복되는_루틴이_존재할_때_모든_요일에_조회된다() {
			// given
			Routine dailyRoutine = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_DAILY_EVENING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);

			List<String> dates = List.of(
				"2024-11-29", // 금
				"2024-11-30", // 토
				"2024-12-01", // 일
				"2024-12-02", // 월
				"2024-12-03", // 화
				"2024-12-04", // 수
				"2024-12-05", // 목

				"2024-12-06", // 금
				"2024-12-07" // 토
			);

			// when & then
			assertSoftly(softly -> {
				dates.forEach(date -> {
					List<Routine> unrecordedRoutines = routineRepository.findUnrecordedRoutinesForDate(
						USER, LocalDate.parse(date), List.of()
					);

					softly.assertThat(unrecordedRoutines)
						.as("날짜 %s에 대한 루틴 조회 실패", date).contains(dailyRoutine);
				});
			});
		}

		@Test
		void 동일한_요일_패턴을_가진_여러_루틴이_있을_때_모두_조회된다() {
			// given
			Routine WEEKDAY_MORNING_ROUTINE1 = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_WEEKDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);
			Routine WEEKDAY_MORNING_ROUTINE2 = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_WEEKDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);

			LocalDate weekday = LocalDate.parse("2024-12-02");

			// when
			List<Routine> unrecordedRoutines = routineRepository.findUnrecordedRoutinesForDate(USER, weekday,
				List.of());

			// then
			assertThat(unrecordedRoutines).contains(WEEKDAY_MORNING_ROUTINE1, WEEKDAY_MORNING_ROUTINE2);
		}

		@Test
		void 루틴이_삭제되었더라면_조회되지_않는다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ONLY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.scheduleModifiedAt("2024-12-01 01:00:00")
					.deletedAt("2024-12-02 02:00:00")
					.build()
			);
			LocalDate monday = LocalDate.parse("2024-12-02");

			// when
			List<Routine> unrecordedRoutines = routineRepository.findUnrecordedRoutinesForDate(USER, monday, List.of());

			// then
			assertThat(unrecordedRoutines).doesNotContain(ROUTINE);
		}

		@Test
		void 종일_루틴_여부와_관계없이_조회_날짜가_루틴_반복요일및시간_변경일시의_날짜와_같다면_조회된다() {
			// given
			Routine ALLDAY_ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ONLY_ALLDAY_ROUTINE(USER)
					.createdAt("2024-12-01 01:00:00")
					.scheduleModifiedAt("2024-12-09 01:00:00")
					.build()
			);
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ONLY_MORNING_ROUTINE(USER)
					.createdAt("2024-12-01 01:00:00")
					.scheduleModifiedAt("2024-12-09 01:00:00")
					.build()
			);
			LocalDate monday = LocalDate.parse("2024-12-09");

			// when
			List<Routine> unrecordedRoutines = routineRepository.findUnrecordedRoutinesForDate(USER, monday, List.of());

			// then
			assertThat(unrecordedRoutines).contains(ALLDAY_ROUTINE, ROUTINE);
		}
	}

	@Nested
	@DisplayName("루틴 날짜 유효성 검증시")
	class IsActiveForDateTest {
		@Test
		void 날짜가_유효한_경우를_정상적으로_확인한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ONLY_MORNING_ROUTINE(USER)
					.createdAt("2024-12-01 01:00:00")
					.build()
			);
			LocalDate monday = LocalDate.parse("2024-12-02");

			// when
			boolean isActive = routineRepository.isActiveForDate(ROUTINE, monday);

			// then
			assertThat(isActive).isTrue();
		}

		@Test
		void 날짜가_루틴_생성_시점_이전인_경우를_정상적으로_확인한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ONLY_MORNING_ROUTINE(USER)
					.createdAt("2024-12-03 01:00:00")
					.build()
			);
			LocalDate monday = LocalDate.parse("2024-12-02");

			// when
			boolean isActive = routineRepository.isActiveForDate(ROUTINE, monday);

			// then
			assertThat(isActive).isFalse();
		}

		@Test
		void 반복요일및시간_변경일시가_조회_날짜보다_이후인_경우를_정상적으로_확인한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ONLY_MORNING_ROUTINE(USER)
					.createdAt("2024-12-01 01:00:00")
					.scheduleModifiedAt("2024-12-06 01:00:00")
					.build()
			);
			LocalDate previousMonday = LocalDate.parse("2024-12-02");

			// when
			boolean isActive = routineRepository.isActiveForDate(ROUTINE, previousMonday);

			// then
			assertThat(isActive).isFalse();
		}

		@Test
		void 루틴의_반복_요일이_날짜의_요일과_일치하지_않는_경우를_정상적으로_확인한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ONLY_MORNING_ROUTINE(USER)
					.createdAt("2024-12-01 01:00:00")
					.build()
			);
			LocalDate tuesday = LocalDate.parse("2024-12-03");

			// when
			boolean isActive = routineRepository.isActiveForDate(ROUTINE, tuesday);

			// then
			assertThat(isActive).isFalse();
		}

		@Test
		void 루틴이_삭제된_경우를_정상적으로_확인한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ONLY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.scheduleModifiedAt("2024-12-01 01:00:00")
					.deletedAt("2024-12-02 02:00:00")
					.build()
			);
			LocalDate monday = LocalDate.parse("2024-12-02");

			// when
			boolean isActive = routineRepository.isActiveForDate(ROUTINE, monday);

			// then
			assertThat(isActive).isFalse();
		}
	}
}

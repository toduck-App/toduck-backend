package im.toduck.domain.routine.persistence.repository;

import static im.toduck.fixtures.RoutineFixtures.*;
import static im.toduck.fixtures.RoutineRecordFixtures.*;
import static im.toduck.fixtures.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.RepositoryTest;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.user.persistence.entity.User;

class RoutineRecordRepositoryTest extends RepositoryTest {

	@Autowired
	private RoutineRecordRepository routineRecordRepository;

	private User USER;

	@BeforeEach
	void setUp() {
		USER = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Nested
	@DisplayName("루틴 기록 조회시")
	class FindRoutineRecordsForUserAndDateTest {

		@Test
		void 유효한_루틴_기록을_조회할_수_있다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(MONDAY_ONLY_MORNING_ROUTINE(USER));
			RoutineRecord RECORD = testFixtureBuilder.buildRoutineRecord(COMPLETED_SYNCED_RECORD(ROUTINE));

			// when
			List<RoutineRecord> records = routineRecordRepository.findRoutineRecordsForUserAndDate(
				USER,
				LocalDate.from(RECORD.getRecordAt())
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(records).hasSize(1);
				softly.assertThat(records).contains(RECORD);
			});

		}

		@Test
		void 여러_루틴_기록을_한번에_조회할_수_있다() {
			// given
			Routine ROUTINE_WEEKLY1 = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));
			Routine ROUTINE_WEEKLY2 = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));

			RoutineRecord RECORD_WEEKLY1_1 = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_SYNCED_RECORD(ROUTINE_WEEKLY1)
			);
			RoutineRecord RECORD_WEEKLY1_2 = testFixtureBuilder.buildRoutineRecord(
				INCOMPLETED_SYNCED_RECORD(ROUTINE_WEEKLY1)
			);
			RoutineRecord RECORD_WEEKLY2_1 = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_SYNCED_RECORD(ROUTINE_WEEKLY2)
			);
			RoutineRecord RECORD_WEEKLY2_2 = testFixtureBuilder.buildRoutineRecord(
				INCOMPLETED_SYNCED_RECORD(ROUTINE_WEEKLY2)
			);

			// when
			List<RoutineRecord> records = routineRecordRepository.findRoutineRecordsForUserAndDate(
				USER,
				LocalDate.from(RECORD_WEEKLY1_1.getRecordAt())
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(records).hasSize(4);
				softly.assertThat(records).containsExactlyInAnyOrder(
					RECORD_WEEKLY1_1,
					RECORD_WEEKLY1_2,
					RECORD_WEEKLY2_1,
					RECORD_WEEKLY2_2
				);
			});
		}
	}

	@Nested
	@DisplayName("루틴과 날짜로 기록 조회시")
	class FindByRoutineAndRecordDateTest {

		@Test
		void 유효한_루틴_기록을_조회할_수_있다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(MONDAY_ONLY_MORNING_ROUTINE(USER));
			RoutineRecord RECORD = testFixtureBuilder.buildRoutineRecord(COMPLETED_SYNCED_RECORD(ROUTINE));

			// when
			Optional<RoutineRecord> foundRecord = routineRecordRepository.findByRoutineAndRecordDate(
				ROUTINE,
				LocalDate.from(RECORD.getRecordAt())
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(foundRecord).isPresent();
				softly.assertThat(foundRecord.get()).isEqualTo(RECORD);
			});
		}

		@Test
		void 존재하지_않는_루틴_기록은_빈_Optional을_반환한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(MONDAY_ONLY_MORNING_ROUTINE(USER));
			LocalDate NON_EXISTENT_DATE = LocalDate.now().plusDays(1);

			// when
			Optional<RoutineRecord> foundRecord = routineRecordRepository.findByRoutineAndRecordDate(
				ROUTINE,
				NON_EXISTENT_DATE
			);

			// then
			assertThat(foundRecord).isEmpty();
		}

		@Test
		void 종일_루틴의_기록이_정상적으로_조회된디() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(MONDAY_ONLY_MORNING_ROUTINE_ALL_DAY(USER));
			RoutineRecord RECORD = testFixtureBuilder.buildRoutineRecord(COMPLETED_SYNCED_RECORD(ROUTINE));

			// when
			Optional<RoutineRecord> foundRecord = routineRecordRepository.findByRoutineAndRecordDate(
				ROUTINE,
				LocalDate.from(RECORD.getRecordAt())
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(foundRecord).isPresent();
				softly.assertThat(foundRecord.get()).isEqualTo(RECORD);
			});
		}
	}
}

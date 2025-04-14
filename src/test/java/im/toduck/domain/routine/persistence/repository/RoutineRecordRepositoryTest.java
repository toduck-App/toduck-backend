package im.toduck.domain.routine.persistence.repository;

import static im.toduck.fixtures.routine.RoutineFixtures.*;
import static im.toduck.fixtures.routine.RoutineRecordFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);

			RoutineRecord RECORD = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(ROUTINE).recordAt("2024-12-02 01:00:00").build() // 월요일
			);

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
			Routine ROUTINE_WEEKLY1 = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_WEEKDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);
			// given
			Routine ROUTINE_WEEKLY2 = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_WEEKDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);

			RoutineRecord RECORD_WEEKLY1_1 = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(ROUTINE_WEEKLY1).recordAt("2024-12-02 01:00:00").build() // 월요일
			);
			RoutineRecord RECORD_WEEKLY1_2 = testFixtureBuilder.buildRoutineRecord(
				INCOMPLETED_RECORD(ROUTINE_WEEKLY1).recordAt("2024-12-02 01:00:00").build() // 화요일
			);
			RoutineRecord RECORD_WEEKLY2_1 = testFixtureBuilder.buildRoutineRecord(
				INCOMPLETED_RECORD(ROUTINE_WEEKLY2).recordAt("2024-12-02 01:00:00").build() // 월요일
			);
			RoutineRecord RECORD_WEEKLY2_2 = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(ROUTINE_WEEKLY2).recordAt("2024-12-02 01:00:00").build() // 화요일
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
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);
			RoutineRecord RECORD = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(ROUTINE).recordAt("2024-12-02 07:00:00").build() // 월요일
			);

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
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);
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
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ALLDAY_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);
			RoutineRecord RECORD = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(ROUTINE).recordAt("2024-12-02 00:00:00").allDay(true).build() // 월요일
			);

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

	@Nested
	@DisplayName("미래의 미완료 루틴 기록 삭제시")
	class DeleteIncompletedFuturesByRoutineTest {

		@Test
		void 미래의_미완료_루틴_기록만_삭제된다() {
			// given
			Routine routine = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);

			// 기준 시간 설정 (테스트 내에서 "현재"로 간주)
			LocalDateTime now = LocalDateTime.of(2024, 12, 15, 0, 0);

			// 미래의 미완료 기록 (삭제 대상)
			RoutineRecord futureIncomplete1 = testFixtureBuilder.buildRoutineRecord(
				INCOMPLETED_RECORD(routine)
					.recordAt("2024-12-30 07:00:00") // 미래 날짜
					.allDay(routine.getTime() == null)
					.build()
			);
			RoutineRecord futureIncomplete2 = testFixtureBuilder.buildRoutineRecord(
				INCOMPLETED_RECORD(routine)
					.recordAt("2025-01-06 07:00:00") // 미래 날짜
					.allDay(routine.getTime() == null)
					.build()
			);

			// 미래의 완료 기록 (유지되어야 함)
			RoutineRecord futureComplete = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(routine)
					.recordAt("2024-12-23 07:00:00") // 미래 날짜
					.allDay(routine.getTime() == null)
					.build()
			);

			// 과거의 미완료 기록 (유지되어야 함)
			RoutineRecord pastIncomplete = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_RECORD(routine)
					.recordAt("2024-11-25 07:00:00") // 과거 날짜
					.allDay(routine.getTime() == null)
					.build()
			);

			// when
			routineRecordRepository.deleteIncompletedFuturesByRoutine(routine, now);

			// then
			List<RoutineRecord> remainingRecords = routineRecordRepository.findAll();

			assertSoftly(softly -> {
				softly.assertThat(remainingRecords).hasSize(2);
				softly.assertThat(remainingRecords).contains(futureComplete);
				softly.assertThat(remainingRecords).contains(pastIncomplete);
				softly.assertThat(remainingRecords).doesNotContain(futureIncomplete1);
				softly.assertThat(remainingRecords).doesNotContain(futureIncomplete2);
			});
		}

		@Test
		void 다른_루틴의_기록은_영향받지_않는다() {
			// given
			Routine routine1 = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);
			Routine routine2 = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_WEEKDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);

			// 기준 시간 설정 (테스트 내에서 "현재"로 간주)
			LocalDateTime now = LocalDateTime.of(2024, 12, 15, 0, 0);

			// routine1의 미래 미완료 기록 (삭제 대상)
			RoutineRecord routine1Future = testFixtureBuilder.buildRoutineRecord(
				INCOMPLETED_RECORD(routine1)
					.recordAt("2024-12-16 07:00:00") // 미래 날짜
					.allDay(routine1.getTime() == null)
					.build()
			);

			// routine2의 미래 미완료 기록 (유지되어야 함)
			RoutineRecord routine2Future = testFixtureBuilder.buildRoutineRecord(
				INCOMPLETED_RECORD(routine2)
					.recordAt("2024-12-16 07:00:00") // 미래 날짜
					.allDay(routine2.getTime() == null)
					.build()
			);

			// when
			routineRecordRepository.deleteIncompletedFuturesByRoutine(routine1, now);

			// then
			List<RoutineRecord> remainingRecords = routineRecordRepository.findAll();

			assertSoftly(softly -> {
				softly.assertThat(remainingRecords).hasSize(1);
				softly.assertThat(remainingRecords).contains(routine2Future);
				softly.assertThat(remainingRecords).doesNotContain(routine1Future);
			});
		}

		@Test
		void 루틴_기록이_없는_경우_정상_동작한다() {
			// given
			Routine routine = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_MORNING_ROUTINE(USER)
					.createdAt("2024-11-29 01:00:00")
					.build()
			);

			// 기준 시간 설정
			LocalDateTime now = LocalDateTime.of(2024, 12, 15, 0, 0);

			// when & then
			assertThatCode(() ->
				routineRecordRepository.deleteIncompletedFuturesByRoutine(routine, now)
			).doesNotThrowAnyException();
		}
	}
}

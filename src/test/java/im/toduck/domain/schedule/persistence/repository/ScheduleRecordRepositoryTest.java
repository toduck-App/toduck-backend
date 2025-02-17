package im.toduck.domain.schedule.persistence.repository;

import static im.toduck.fixtures.schedule.ScheduleFixtures.*;
import static im.toduck.fixtures.schedule.ScheduleRecordFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.RepositoryTest;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import im.toduck.domain.user.persistence.entity.User;

@Transactional
class ScheduleRecordRepositoryTest extends RepositoryTest {

	@Autowired
	private ScheduleRecordRepository scheduleRecordRepository;

	@Nested
	@DisplayName("특정 기간에 포함된 일정 기록 조회시")
	class findByScheduleAndBetweenStartDateAndEndDateTest {
		private final LocalDate QUERY_START_DATE = LocalDate.of(2025, 1, 10);
		private final LocalDate QUERY_END_DATE = LocalDate.of(2025, 1, 25);

		private final LocalDate LESS_THAN_QUERY_DATE = LocalDate.of(2025, 1, 1);
		private final LocalDate GREATER_THAN_QUERY_DATE = LocalDate.of(2025, 1, 30);

		private final LocalDate BETWEEN_QUERY_DATE_15 = LocalDate.of(2025, 1, 15);
		private final LocalDate BETWEEN_QUERY_DATE_20 = LocalDate.of(2025, 1, 20);

		private User savedUser;
		private Schedule savedSchedule;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
			savedSchedule = testFixtureBuilder.buildSchedule(
				DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, BETWEEN_QUERY_DATE_15, BETWEEN_QUERY_DATE_20));
		}

		@Test
		void 성공_조회_기간에_포함된_일정_기록을_조회한다() {
			// given
			testFixtureBuilder.buildScheduleRecord(
				IS_COMPLETE_SCHEDULE_RECORD(BETWEEN_QUERY_DATE_15, savedSchedule));

			// when
			List<ScheduleRecord> scheduleRecords = scheduleRecordRepository.findByScheduleAndBetweenStartDateAndEndDate(
				savedSchedule.getId(),
				QUERY_START_DATE,
				QUERY_END_DATE);
			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecords).hasSize(1);
				softly.assertThat(scheduleRecords.get(0).getSchedule()).isEqualTo(savedSchedule);
			});
		}

		@Test
		void 실패_기간에_해당하지_않는_일정_기록은_조회되지_않는다() {
			// given
			testFixtureBuilder.buildScheduleRecord(
				IS_COMPLETE_SCHEDULE_RECORD(LESS_THAN_QUERY_DATE, savedSchedule));
			testFixtureBuilder.buildScheduleRecord(
				IS_COMPLETE_SCHEDULE_RECORD(GREATER_THAN_QUERY_DATE, savedSchedule));

			// when
			List<ScheduleRecord> scheduleRecords = scheduleRecordRepository.findByScheduleAndBetweenStartDateAndEndDate(
				savedSchedule.getId(),
				QUERY_START_DATE,
				QUERY_END_DATE);
			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecords).isEmpty();
			});
		}
	}

	@Nested
	@DisplayName("일정 기록 일정 fetch join 조회시")
	class findScheduleRecordFetchJoinScheduleTest {
		private User savedUser;
		private Schedule savedSchedule;
		private ScheduleRecord savedScheduleRecord;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
			savedSchedule = testFixtureBuilder.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser,
				LocalDate.now(), LocalDate.now()));
			savedScheduleRecord = testFixtureBuilder.buildScheduleRecord(
				IS_COMPLETE_SCHEDULE_RECORD(LocalDate.now(), savedSchedule));
		}

		@Test
		void 성공_일정_기록_일정_fetch_join_조회한다() {
			// when
			ScheduleRecord scheduleRecord = scheduleRecordRepository.findScheduleRecordFetchJoinSchedule(
				savedScheduleRecord.getId()).get();
			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecord).isNotNull();
				softly.assertThat(scheduleRecord.getSchedule()).isNotNull();
			});
		}

		@Test
		void 실패_존재하지_않는_일정_기록_일정은_조회되지_않는다() {
			// when
			Optional<ScheduleRecord> scheduleRecord = scheduleRecordRepository.findScheduleRecordFetchJoinSchedule(0L);
			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecord.orElse(null)).isNull();
			});
		}
	}

}

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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Transactional
class ScheduleRecordRepositoryTest extends RepositoryTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ScheduleRecordRepository scheduleRecordRepository;

	private final LocalDate QUERY_START_DATE = LocalDate.of(2025, 1, 10);
	private final LocalDate QUERY_END_DATE = LocalDate.of(2025, 1, 25);

	private final LocalDate LESS_THAN_QUERY_DATE = LocalDate.of(2025, 1, 1);
	private final LocalDate GREATER_THAN_QUERY_DATE = LocalDate.of(2025, 1, 30);

	private final LocalDate BETWEEN_QUERY_DATE_15 = LocalDate.of(2025, 1, 15);
	private final LocalDate BETWEEN_QUERY_DATE_20 = LocalDate.of(2025, 1, 20);

	@Nested
	@DisplayName("특정 기간에 포함된 일정 기록 조회시")
	class findByScheduleAndBetweenStartDateAndEndDateTest {

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

	@Nested
	@DisplayName("특정 사용자, 날짜, 일정에 해당하는 일정 기록 조회시")
	class findScheduleRecordByUserIdAndRecordDateAndScheduleIdTest {

		private User savedUser;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
		}

		@Test
		void 성공_일정기록을_성공적으로_조회한다() {
			// given
			Schedule savedSchedule = testFixtureBuilder.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser,
				LESS_THAN_QUERY_DATE, GREATER_THAN_QUERY_DATE));
			ScheduleRecord savedScheduleRecord = testFixtureBuilder.buildScheduleRecord(
				IS_COMPLETE_SCHEDULE_RECORD(QUERY_END_DATE, savedSchedule));
			// when
			ScheduleRecord scheduleRecord = scheduleRecordRepository.findScheduleRecordByRecordDateAndScheduleId(
				QUERY_END_DATE,
				savedSchedule.getId()).get();

			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecord).isNotNull();
				softly.assertThat(scheduleRecord).isEqualTo(savedScheduleRecord);
			});
		}

		@Test
		void 성공_일정기록이_없다면_null을_반환한다() {
			// given
			Schedule savedSchedule = testFixtureBuilder.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser,
				LESS_THAN_QUERY_DATE, GREATER_THAN_QUERY_DATE));
			// when
			Optional<ScheduleRecord> scheduleRecord = scheduleRecordRepository.findScheduleRecordByRecordDateAndScheduleId(
				QUERY_END_DATE,
				savedSchedule.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecord.orElse(null)).isNull();
			});
		}
	}

	@Nested
	@DisplayName("특정 일정, 날짜에 해당하는 일정 기록 삭제시")
	class deleteByScheduleIdAndRecordDateTest {

		private User savedUser;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
		}

		@Test
		void 성공_일정_날짜에_해당하는_일정_기록을_삭제한다() {
			// given
			Schedule savedSchedule = testFixtureBuilder.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser,
				QUERY_END_DATE, QUERY_END_DATE));
			ScheduleRecord savedScheduleRecord = testFixtureBuilder.buildScheduleRecord(
				IS_COMPLETE_SCHEDULE_RECORD(QUERY_END_DATE, savedSchedule));
			// when
			scheduleRecordRepository.deleteByScheduleIdAndRecordDate(savedSchedule.getId(), QUERY_END_DATE);
			entityManager.flush();
			entityManager.clear();

			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecordRepository.findById(savedScheduleRecord.getId()).orElse(null)).isNull();
			});
		}

		@Test
		void 실패_일정_날짜에_해당하지_않는_일정_기록은_삭제되지_않는다() {
			// given
			Schedule savedSchedule = testFixtureBuilder.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser,
				QUERY_END_DATE, QUERY_END_DATE));
			ScheduleRecord savedScheduleRecord = testFixtureBuilder.buildScheduleRecord(
				IS_COMPLETE_SCHEDULE_RECORD(QUERY_END_DATE, savedSchedule));
			// when
			scheduleRecordRepository.deleteByScheduleIdAndRecordDate(savedSchedule.getId(), GREATER_THAN_QUERY_DATE);
			entityManager.flush();
			entityManager.clear();

			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecordRepository.findById(savedScheduleRecord.getId()).orElse(null))
					.isNotNull();
			});
		}
	}

	@Nested
	@DisplayName("특정 일정, 날짜에 해당하는 일정 기록 소프트 삭제시")
	class softDeleteByScheduleIdAndRecordDateTest {

		private User savedUser;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
		}

		@Test
		void 성공_일정_날짜에_해당하는_일정_기록을_소프트_삭제한다() {
			// given
			Schedule savedSchedule = testFixtureBuilder.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser,
				QUERY_END_DATE, QUERY_END_DATE));
			ScheduleRecord savedScheduleRecord = testFixtureBuilder.buildScheduleRecord(
				IS_COMPLETE_SCHEDULE_RECORD(QUERY_END_DATE, savedSchedule));
			// when
			scheduleRecordRepository.softDeleteByScheduleIdAndRecordDate(savedSchedule.getId(), QUERY_END_DATE);
			entityManager.flush();
			entityManager.clear();

			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecordRepository.findById(savedScheduleRecord.getId()).orElse(null))
					.isNotNull();
				softly.assertThat(scheduleRecordRepository.findById(savedScheduleRecord.getId()).get().getDeletedAt())
					.isNotNull();
			});
		}

		@Test
		void 실패_일정_날짜에_해당하지_않는_일정_기록은_소프트_삭제되지_않는다() {
			// given
			Schedule savedSchedule = testFixtureBuilder.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser,
				QUERY_END_DATE, QUERY_END_DATE));
			ScheduleRecord savedScheduleRecord = testFixtureBuilder.buildScheduleRecord(
				IS_COMPLETE_SCHEDULE_RECORD(QUERY_END_DATE, savedSchedule));
			// when
			scheduleRecordRepository.softDeleteByScheduleIdAndRecordDate(savedSchedule.getId(),
				GREATER_THAN_QUERY_DATE);
			entityManager.flush();
			entityManager.clear();

			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecordRepository.findById(savedScheduleRecord.getId()).orElse(null))
					.isNotNull();
				softly.assertThat(scheduleRecordRepository.findById(savedScheduleRecord.getId()).get().getDeletedAt())
					.isNull();
			});
		}
	}

	@Nested
	@DisplayName("특정 일정, 날짜에 해당하는 완료된 일정 기록 조회시")
	class findByCompletedScheduleAndBetweenStartDateAndEndDateTest {

		private User savedUser;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
		}

		@Test
		void 성공_완료된_일정_기록을_성공적으로_조회한다() {
			// given
			Schedule savedSchedule = testFixtureBuilder.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser,
				QUERY_START_DATE, QUERY_END_DATE));
			ScheduleRecord savedScheduleRecord = testFixtureBuilder.buildScheduleRecord(
				IS_COMPLETE_SCHEDULE_RECORD(QUERY_END_DATE, savedSchedule));
			testFixtureBuilder.buildScheduleRecord(
				IS_NOT_COMPLETE_SCHEDULE_RECORD(QUERY_END_DATE, savedSchedule));
			// when
			List<ScheduleRecord> scheduleRecords = scheduleRecordRepository.findByCompletedScheduleAndAfterStartDate(
				savedSchedule.getId(),
				QUERY_START_DATE);
			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecords).hasSize(1);
				softly.assertThat(scheduleRecords.get(0).getId()).isEqualTo(savedScheduleRecord.getId());
			});
		}

		@Test
		void 실패_완료된_일정_기록이_없다면_빈_리스트를_반환한다() {
			// given
			Schedule savedSchedule = testFixtureBuilder.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser,
				QUERY_START_DATE, QUERY_END_DATE));
			testFixtureBuilder.buildScheduleRecord(
				IS_NOT_COMPLETE_SCHEDULE_RECORD(QUERY_END_DATE, savedSchedule));
			testFixtureBuilder.buildScheduleRecord(
				IS_COMPLETE_SCHEDULE_RECORD(LESS_THAN_QUERY_DATE, savedSchedule));
			// when
			List<ScheduleRecord> scheduleRecords = scheduleRecordRepository.findByCompletedScheduleAndAfterStartDate(
				savedSchedule.getId(),
				QUERY_START_DATE);
			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecords).isEmpty();
			});
		}
	}

	@Nested
	@DisplayName("특정 일정, 날짜에 해당하는 미완료된 일정 기록 삭제시")
	class deleteByNonCompletedScheduleAndBetweenStartDateAndEndDateTest {

		private User savedUser;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
		}

		@Test
		void 성공_미완료된_일정_기록을_성공적으로_삭제한다() {
			// given
			Schedule savedSchedule = testFixtureBuilder.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser,
				QUERY_START_DATE, QUERY_END_DATE));
			ScheduleRecord savedScheduleRecord = testFixtureBuilder.buildScheduleRecord(
				IS_NOT_COMPLETE_SCHEDULE_RECORD(QUERY_END_DATE, savedSchedule));
			// when
			scheduleRecordRepository.deleteByNonCompletedScheduleAndAfterStartDate(savedSchedule.getId(),
				QUERY_START_DATE, QUERY_END_DATE);
			entityManager.flush();
			entityManager.clear();

			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecordRepository.findById(savedScheduleRecord.getId()).orElse(null)).isNull();
			});
		}

		@Test
		void 실패_미완료된_일정_기록이_없다면_삭제되지_않는다() {
			// given
			Schedule savedSchedule = testFixtureBuilder.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser,
				QUERY_START_DATE, QUERY_END_DATE));
			ScheduleRecord savedScheduleRecord = testFixtureBuilder.buildScheduleRecord(
				IS_COMPLETE_SCHEDULE_RECORD(QUERY_END_DATE, savedSchedule));

			// when
			scheduleRecordRepository.deleteByNonCompletedScheduleAndAfterStartDate(savedSchedule.getId(),
				QUERY_START_DATE, QUERY_END_DATE);
			entityManager.flush();
			entityManager.clear();

			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecordRepository.findById(savedScheduleRecord.getId()).orElse(null))
					.isNotNull();
			});
		}
	}
}

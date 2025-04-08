package im.toduck.domain.schedule.persistence.repository;

import static im.toduck.fixtures.schedule.ScheduleFixtures.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.RepositoryTest;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.fixtures.user.UserFixtures;

@Transactional
class ScheduleRepositoryTest extends RepositoryTest {

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Nested
	@DisplayName("특정 기간에 포함된 일정 조회시")
	class findSchedulesTest {
		private final LocalDate QUERY_START_DATE = LocalDate.of(2025, 1, 10);
		private final LocalDate QUERY_END_DATE = LocalDate.of(2025, 1, 25);

		private final LocalDate LESS_THAN_QUERY_START_DATE = LocalDate.of(2025, 1, 1);
		private final LocalDate GREATER_THAN_QUERY_END_DATE = LocalDate.of(2025, 1, 30);

		private final LocalDate GREATER_THAN_QUERY_START_DATE = LocalDate.of(2025, 1, 15);
		private final LocalDate LESS_THAN_QUERY_END_DATE = LocalDate.of(2025, 1, 20);
		private User savedUser;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
		}

		@Nested
		@DisplayName("기간X, 반복X인 단일 날짜 일정을 조회할 수 있다")
		class NonRepeatingSingleDayTest {
			@Test
			void 성공_조회_기간에_포함된_일정을_조회한다() {
				// given
				Schedule savedSchedule = testFixtureBuilder
					.buildSchedule(
						DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, GREATER_THAN_QUERY_START_DATE,
							GREATER_THAN_QUERY_START_DATE));
				// when
				List<Schedule> schedules = scheduleRepository.findSchedules(savedUser.getId(),
					GREATER_THAN_QUERY_START_DATE,
					GREATER_THAN_QUERY_START_DATE);
				// then
				assertSoftly(softly -> {
					softly.assertThat(schedules.size()).isEqualTo(1);
				});
			}

			@Test
			void 실패_기간에_해당하지_않는_일정은_조회되지_않는다() {
				// given
				testFixtureBuilder
					.buildSchedule(
						DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, LESS_THAN_QUERY_START_DATE,
							LESS_THAN_QUERY_START_DATE));
				testFixtureBuilder
					.buildSchedule(
						DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, GREATER_THAN_QUERY_END_DATE,
							GREATER_THAN_QUERY_END_DATE));
				// when
				List<Schedule> schedules = scheduleRepository.findSchedules(savedUser.getId(),
					QUERY_START_DATE,
					QUERY_END_DATE);
				// then
				assertSoftly(softly -> {
					softly.assertThat(schedules.size()).isEqualTo(0);
				});
			}
		}

		@Nested
		@DisplayName("기간X, 반복O인 단일 날짜 일정을 조회할 수 있다")
		class NonRepeatingMultiDayTest {
			@Test
			@DisplayName("성공 - 조회기간 사이에 있거나 조회기간 전에 있는 일정은 조회된다")
			void success() {
				// given
				testFixtureBuilder
					.buildSchedule(
						DEFAULT_REPEATABLE_SCHEDULE(savedUser, GREATER_THAN_QUERY_START_DATE,
							GREATER_THAN_QUERY_START_DATE));
				testFixtureBuilder
					.buildSchedule(
						DEFAULT_REPEATABLE_SCHEDULE(savedUser, LESS_THAN_QUERY_START_DATE,
							LESS_THAN_QUERY_START_DATE));
				// when
				List<Schedule> schedules = scheduleRepository.findSchedules(savedUser.getId(),
					GREATER_THAN_QUERY_START_DATE,
					GREATER_THAN_QUERY_START_DATE);
				// then
				assertSoftly(softly -> {
					softly.assertThat(schedules.size()).isEqualTo(2);
				});
			}

			@Test
			void 실패_기간_이후에_일정은_조회되지_않는다() {
				// given
				testFixtureBuilder
					.buildSchedule(
						DEFAULT_REPEATABLE_SCHEDULE(savedUser, GREATER_THAN_QUERY_END_DATE,
							GREATER_THAN_QUERY_END_DATE));

				// when
				List<Schedule> schedules = scheduleRepository.findSchedules(savedUser.getId(),
					QUERY_START_DATE,
					QUERY_END_DATE);
				// then
				assertSoftly(softly -> {
					softly.assertThat(schedules.size()).isEqualTo(0);
				});
			}
		}

		@Nested
		@DisplayName("기간O_반복여부는 상관없는_일정을_조회할_수_있다")
		class MultiDayNonRepeatingTest {
			@Test
			void 성공_기간_일정은_쿼리_기간과_부분_일치하는_모든_일정을_조회한다() {
				// given
				// 쿼리 날짜 <= 일정 시작 , 일정 종료 <= 쿼리 종료
				testFixtureBuilder
					.buildSchedule(
						DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, GREATER_THAN_QUERY_START_DATE,
							LESS_THAN_QUERY_END_DATE));
				// 일정 시작 < 쿼리 시작 , 일정 종료 < 쿼리 종료
				testFixtureBuilder
					.buildSchedule(
						DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, LESS_THAN_QUERY_START_DATE,
							LESS_THAN_QUERY_END_DATE));
				// 쿼리 시작 < 일정 시작 , 쿼리 종료 < 일정 종료
				testFixtureBuilder
					.buildSchedule(
						DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, GREATER_THAN_QUERY_START_DATE,
							GREATER_THAN_QUERY_END_DATE));
				// when
				List<Schedule> schedules = scheduleRepository.findSchedules(savedUser.getId(),
					GREATER_THAN_QUERY_START_DATE,
					LESS_THAN_QUERY_END_DATE);
				// then
				assertSoftly(softly -> {
					softly.assertThat(schedules.size()).isEqualTo(3);
				});
			}

			@Test
			void 실패_기간에_해당하지_않는_일정은_조회되지_않는다() {
				// given
				testFixtureBuilder
					.buildSchedule(
						DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, LESS_THAN_QUERY_START_DATE,
							LESS_THAN_QUERY_START_DATE));
				testFixtureBuilder
					.buildSchedule(
						DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, GREATER_THAN_QUERY_END_DATE,
							GREATER_THAN_QUERY_END_DATE));
				// when
				List<Schedule> schedules = scheduleRepository.findSchedules(savedUser.getId(),
					QUERY_START_DATE,
					QUERY_END_DATE);
				// then
				assertSoftly(softly -> {
					softly.assertThat(schedules.size()).isEqualTo(0);
				});
			}
		}

	}

}

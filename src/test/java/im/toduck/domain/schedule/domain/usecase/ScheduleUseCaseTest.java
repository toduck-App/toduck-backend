package im.toduck.domain.schedule.domain.usecase;

import static im.toduck.fixtures.schedule.ScheduleCreateRequestFixtures.*;
import static im.toduck.fixtures.schedule.ScheduleFixtures.*;
import static im.toduck.fixtures.schedule.ScheduleRecordFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.ServiceTest;
import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import im.toduck.domain.schedule.persistence.repository.ScheduleRecordRepository;
import im.toduck.domain.schedule.persistence.repository.ScheduleRepository;
import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCompleteRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleCreateResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleInfoResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.exception.VoException;

class ScheduleUseCaseTest extends ServiceTest {

	@Autowired
	private ScheduleUseCase scheduleUsecase;

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Autowired
	private ScheduleRecordRepository scheduleRecordRepository;

	@Nested
	@DisplayName("일정 생성시")
	class postSchedule {
		private User savedUser;

		private final ScheduleCreateRequest successScheduleCreateRequest = ScheduleCreateRequest.builder()
			.title("일정 제목")
			.category(PlanCategory.COMPUTER)
			.startDate(LocalDate.of(2025, 1, 1)) // 필수 값
			.endDate(LocalDate.of(2025, 1, 1))
			.isAllDay(false)
			.color("#FFFFFF")
			.time(LocalTime.of(10, 30))
			.daysOfWeek(List.of(DayOfWeek.MONDAY))
			.alarm(ScheduleAlram.TEN_MINUTE)
			.location("일정 장소")
			.memo("일정 메모")
			.build();

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
		}

		@Test
		void 성공적으로_생성한다() {
			// given ->when
			ScheduleCreateResponse result = scheduleUsecase.createSchedule(savedUser.getId(),
				successScheduleCreateRequest);

			// then
			assertSoftly(softly -> {
				softly.assertThat(result.scheduleId()).isNotNull();
			});
		}

		@Test
		void 반복_요일이_NULL이어도_성공적으로_생성한다() {
			//given
			ScheduleCreateRequest request = DAYS_OF_WEEK_NULL_REQUEST();

			//when
			ScheduleCreateResponse response = scheduleUsecase.createSchedule(savedUser.getId(), request);

			//then
			assertSoftly(softly -> {
				softly.assertThat(response.scheduleId()).isNotNull();
			});
		}

		@Nested
		@DisplayName("실패")
		class failPostSchedule {
			@Test
			void 유효한_유저가_아닐경우_실패한다() {
				// given
				int NOISE_USER_ID = 9999;

				// when -> then

				assertSoftly(softly -> {
					softly.assertThatThrownBy(
							() -> scheduleUsecase.createSchedule(savedUser.getId() + NOISE_USER_ID,
								successScheduleCreateRequest))
						.isInstanceOf(CommonException.class)
						.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.NOT_FOUND_USER.getHttpStatus())
						.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.NOT_FOUND_USER.getErrorCode())
						.hasFieldOrPropertyWithValue("message", ExceptionCode.NOT_FOUND_USER.getMessage());
				});
			}

			@Test
			void 종일_여부가_true인데_시간은_null이_아니면_실패한다() {
				// given
				ScheduleCreateRequest isAllDayTrueTimeNonNULLRequest = ERROR_TRUE_IS_ALL_DAY_TIME_NON_NULL_REQUEST();

				//when -> then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(
							() -> scheduleUsecase.createSchedule(savedUser.getId(), isAllDayTrueTimeNonNULLRequest))
						.isInstanceOf(VoException.class);
				});
			}

			@Test
			void 종일_여부가_false인데_시간이_null이면_실패한다() {
				// given
				ScheduleCreateRequest isAllDayFalseTimeNULLRequest = ERROR_FALSE_IS_ALL_DAY_TIME_NULL_REQUEST();

				//when -> then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(
							() -> scheduleUsecase.createSchedule(savedUser.getId(), isAllDayFalseTimeNULLRequest))
						.isInstanceOf(VoException.class);
				});
			}

			@Test
			void 종일_여부가_true인데_알람이_null이_아니면_실패한다() {
				// given
				ScheduleCreateRequest isAllDayTrueAlarmNonNULLRequest = ERROR_TRUE_IS_ALL_DAY_ALARM_NON_NULL_REQUEST();

				//when -> then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(
							() -> scheduleUsecase.createSchedule(savedUser.getId(), isAllDayTrueAlarmNonNULLRequest))
						.isInstanceOf(VoException.class);
				});
			}

			@Test
			void 시작_날짜가_종료_날짜보다_크다면_실패한다() {
				// given
				ScheduleCreateRequest startDateGreaterThanEndDateRequest = ERROR_START_DATE_GREATER_THAN_END_DATE_REQUEST();

				//when -> then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(
							() -> scheduleUsecase.createSchedule(savedUser.getId(), startDateGreaterThanEndDateRequest))
						.isInstanceOf(VoException.class);
				});
			}
		}
	}

	@Nested
	@DisplayName("일정 기간 조회시")
	class getSchedule {
		private final LocalDate QUERY_START_DATE = LocalDate.of(2025, 1, 10);
		private final LocalDate QUERY_END_DATE = LocalDate.of(2025, 1, 25);

		// 기간 밖
		private final LocalDate LESS_THAN_QUERY_START_DATE = LocalDate.of(2025, 1, 1);
		private final LocalDate GREATER_THAN_QUERY_END_DATE = LocalDate.of(2025, 1, 30);

		//기간 사이
		private final LocalDate GREATER_THAN_QUERY_START_DATE = LocalDate.of(2025, 1, 15);
		private final LocalDate LESS_THAN_QUERY_END_DATE = LocalDate.of(2025, 1, 20);
		private User savedUser;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
		}

		@Test
		void 성공적으로_일정을_조회한다() {
			// given
			testFixtureBuilder.buildSchedule(
				(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, GREATER_THAN_QUERY_START_DATE,
					GREATER_THAN_QUERY_START_DATE))); // 반복 없는 하루 일정
			testFixtureBuilder.buildSchedule(
				testFixtureBuilder.buildSchedule((DEFAULT_REPEATABLE_SCHEDULE(savedUser, LESS_THAN_QUERY_START_DATE,
					LESS_THAN_QUERY_START_DATE)))); // 반복 있는 하루 일정
			testFixtureBuilder.buildSchedule(
				testFixtureBuilder.buildSchedule(
					(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, GREATER_THAN_QUERY_START_DATE,
						LESS_THAN_QUERY_END_DATE)))); // 반복 없는 기간 일정
			testFixtureBuilder.buildSchedule(
				testFixtureBuilder.buildSchedule(
					(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, GREATER_THAN_QUERY_START_DATE,
						LESS_THAN_QUERY_END_DATE)))); // 반복 있는 기간 일정

			// when
			ScheduleHeadResponse scheduleHeadResponse = scheduleUsecase.getRangeSchedule(savedUser.getId(),
				QUERY_START_DATE, QUERY_END_DATE);

			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleHeadResponse.queryStartDate()).isEqualTo(QUERY_START_DATE);
				softly.assertThat(scheduleHeadResponse.queryEndDate()).isEqualTo(QUERY_END_DATE);
				softly.assertThat(scheduleHeadResponse.scheduleHeadDtos()).hasSize(4);
			});
		}

		@Test
		void 성공_조회_기간에_해당하는_일정_기록을_조회한다() {
			// given
			Schedule savedSchedule = testFixtureBuilder
				.buildSchedule(
					DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, GREATER_THAN_QUERY_START_DATE,
						GREATER_THAN_QUERY_START_DATE));

			testFixtureBuilder
				.buildScheduleRecord(IS_COMPLETE_SCHEDULE_RECORD(GREATER_THAN_QUERY_START_DATE, savedSchedule));
			testFixtureBuilder
				.buildScheduleRecord(IS_NOT_COMPLETE_SCHEDULE_RECORD(LESS_THAN_QUERY_END_DATE, savedSchedule));

			// when
			ScheduleHeadResponse scheduleHeadResponse = scheduleUsecase.getRangeSchedule(savedUser.getId(),
				QUERY_START_DATE, QUERY_END_DATE);
			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleHeadResponse.scheduleHeadDtos()).hasSize(1);
				softly.assertThat(scheduleHeadResponse.scheduleHeadDtos().get(0).scheduleId())
					.isEqualTo(savedSchedule.getId());
				softly.assertThat(scheduleHeadResponse.scheduleHeadDtos().get(0).scheduleRecordDto()).hasSize(2);
				softly.assertThat(
						scheduleHeadResponse.scheduleHeadDtos().get(0).scheduleRecordDto().get(0).isComplete())
					.isEqualTo(true);
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
			ScheduleHeadResponse scheduleHeadResponse = scheduleUsecase.getRangeSchedule(savedUser.getId(),
				QUERY_START_DATE, QUERY_END_DATE);
			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleHeadResponse.scheduleHeadDtos()).hasSize(0);
			});
		}

		@Test
		void 실패_기간에_해당하지_않는_일정_기록은_조회되지_않는다() {
			// given
			Schedule savedSchedule = testFixtureBuilder
				.buildSchedule(
					DEFAULT_REPEATABLE_SCHEDULE(savedUser, LESS_THAN_QUERY_START_DATE,
						LESS_THAN_QUERY_START_DATE));

			testFixtureBuilder
				.buildScheduleRecord(IS_COMPLETE_SCHEDULE_RECORD(LESS_THAN_QUERY_START_DATE, savedSchedule));
			testFixtureBuilder
				.buildScheduleRecord(IS_NOT_COMPLETE_SCHEDULE_RECORD(GREATER_THAN_QUERY_END_DATE, savedSchedule));

			// when
			ScheduleHeadResponse scheduleHeadResponse = scheduleUsecase.getRangeSchedule(savedUser.getId(),
				QUERY_START_DATE, QUERY_END_DATE);
			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleHeadResponse.scheduleHeadDtos()).hasSize(1);
				softly.assertThat(scheduleHeadResponse.scheduleHeadDtos().get(0).scheduleId())
					.isEqualTo(savedSchedule.getId());
				softly.assertThat(scheduleHeadResponse.scheduleHeadDtos().get(0).scheduleRecordDto()).hasSize(0);
			});
		}
	}

	@Nested
	@DisplayName("일정 상세 조회시")
	class findScheduleTest {
		private User savedUser;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
		}

		@Test
		void 성공적으로_일정을_조회한다() {
			// given
			Schedule savedSchedule = testFixtureBuilder
				.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, LocalDate.of(2025, 1, 1),
					LocalDate.of(2025, 1, 1)));
			ScheduleRecord savedScheduleRecord = testFixtureBuilder
				.buildScheduleRecord(IS_COMPLETE_SCHEDULE_RECORD(LocalDate.of(2025, 1, 1), savedSchedule));

			// when
			ScheduleInfoResponse scheduleInfoResponse = scheduleUsecase.getSchedule(savedUser.getId(),
				savedScheduleRecord.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(scheduleInfoResponse.scheduleId()).isEqualTo(savedSchedule.getId());
				softly.assertThat(scheduleInfoResponse.scheduleRecordId()).isEqualTo(savedScheduleRecord.getId());
				softly.assertThat(scheduleInfoResponse.daysOfWeek()).isEqualTo(savedSchedule.getDaysOfWeekBitmask());
			});
		}

		@Test
		void 실패_일정_기록이_없을_경우_실패한다() {
			// given
			Schedule savedSchedule = testFixtureBuilder
				.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(savedUser, LocalDate.of(2025, 1, 1),
					LocalDate.of(2025, 1, 1)));

			// when -> then
			assertSoftly(softly -> {
				softly.assertThatThrownBy(() -> scheduleUsecase.getSchedule(savedUser.getId(), 9999L))
					.isInstanceOf(CommonException.class)
					.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.NOT_FOUND_SCHEDULE_RECORD.getHttpStatus())
					.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.NOT_FOUND_SCHEDULE_RECORD.getErrorCode())
					.hasFieldOrPropertyWithValue("message", ExceptionCode.NOT_FOUND_SCHEDULE_RECORD.getMessage());
			});
		}
	}

	@Nested
	@DisplayName("일정 완료 변경 요청시")
	class completeSchedule {
		private final LocalDate MOCK_DATE = LocalDate.of(2025, 1, 15);

		private Schedule savedSchedule;
		private User savedUser;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
			savedSchedule = testFixtureBuilder
				.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(
					savedUser,
					MOCK_DATE,
					MOCK_DATE));
		}

		@Test
		void 성공_해당날짜_일정기록이_있으면_기록이_추가생성되지_않고_완료여부가_변경된다() {
			// given
			testFixtureBuilder
				.buildScheduleRecord(IS_NOT_COMPLETE_SCHEDULE_RECORD(
					MOCK_DATE,
					savedSchedule));
			ScheduleCompleteRequest request = ScheduleCompleteRequest.builder()
				.scheduleId(savedSchedule.getId())
				.isComplete(true)
				.queryDate(MOCK_DATE)
				.build();

			// when
			scheduleUsecase.completeSchedule(savedUser.getId(), request);
			// then
			Optional<ScheduleRecord> scheduleRecord = scheduleRecordRepository.findScheduleRecordByUserIdAndRecordDateAndScheduleId(
				savedUser.getId(),
				MOCK_DATE,
				savedSchedule.getId());
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecord.orElse(null)).isNotNull();
				softly.assertThat(scheduleRecord.get().getIsCompleted()).isEqualTo(request.isComplete());
				softly.assertThat(scheduleRecord.get().getSchedule().getId()).isEqualTo(savedSchedule.getId());
			});
		}

		@Test
		void 성공_해당날짜_일정기록이_없으면_일정_기록이_생성된다() {
			//given

			ScheduleCompleteRequest request = ScheduleCompleteRequest.builder()
				.scheduleId(savedSchedule.getId())
				.isComplete(true)
				.queryDate(MOCK_DATE)
				.build();

			// when
			scheduleUsecase.completeSchedule(savedUser.getId(), request);

			// then
			Optional<ScheduleRecord> scheduleRecord = scheduleRecordRepository.findScheduleRecordByUserIdAndRecordDateAndScheduleId(
				savedUser.getId(),
				MOCK_DATE,
				savedSchedule.getId());

			assertSoftly(softly -> {
				softly.assertThat(scheduleRecord.orElse(null)).isNotNull();
				softly.assertThat(scheduleRecord.get().getSchedule().getId()).isEqualTo(savedSchedule.getId());
			});
		}

		@Test
		void 실패_유효한_유저가_아닐경우_실패한다() {
			// given
			ScheduleCompleteRequest request = ScheduleCompleteRequest.builder()
				.scheduleId(savedSchedule.getId())
				.isComplete(true)
				.queryDate(MOCK_DATE)
				.build();

			// when -> then
			assertSoftly(softly -> {
				softly.assertThatThrownBy(() -> scheduleUsecase.completeSchedule(savedUser.getId() + 1, request))
					.isInstanceOf(CommonException.class)
					.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.NOT_FOUND_USER.getHttpStatus())
					.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.NOT_FOUND_USER.getErrorCode())
					.hasFieldOrPropertyWithValue("message", ExceptionCode.NOT_FOUND_USER.getMessage());
			});

		}

		@Test
		void 실패_유효한_일정_아이디가_아닐경우_실패한다() {
			// given
			ScheduleCompleteRequest request = ScheduleCompleteRequest.builder()
				.scheduleId(9999L)
				.isComplete(true)
				.queryDate(MOCK_DATE)
				.build();

			// when -> then
			assertSoftly(softly -> {
				softly.assertThatThrownBy(() -> scheduleUsecase.completeSchedule(savedUser.getId(), request))
					.isInstanceOf(CommonException.class)
					.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.NOT_FOUND_SCHEDULE.getHttpStatus())
					.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.NOT_FOUND_SCHEDULE.getErrorCode())
					.hasFieldOrPropertyWithValue("message", ExceptionCode.NOT_FOUND_SCHEDULE.getMessage());
			});

		}
	}

}

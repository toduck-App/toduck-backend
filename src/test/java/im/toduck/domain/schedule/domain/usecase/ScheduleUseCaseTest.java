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
import im.toduck.domain.schedule.presentation.dto.request.ScheduleDeleteRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleModifyRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleIdResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleInfoResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.exception.VoException;
import jakarta.persistence.EntityManager;

class ScheduleUseCaseTest extends ServiceTest {

	@Autowired
	private ScheduleUseCase scheduleUsecase;

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Autowired
	private EntityManager em;

	@Autowired
	private ScheduleRecordRepository scheduleRecordRepository;

	private final LocalDate QUERY_START_DATE = LocalDate.of(2025, 1, 10);
	private final LocalDate QUERY_END_DATE = LocalDate.of(2025, 1, 25);

	// 기간 밖
	private final LocalDate LESS_THAN_QUERY_START_DATE = LocalDate.of(2025, 1, 1);
	private final LocalDate GREATER_THAN_QUERY_END_DATE = LocalDate.of(2025, 1, 30);

	//기간 사이
	private final LocalDate GREATER_THAN_QUERY_START_DATE = LocalDate.of(2025, 1, 15);
	private final LocalDate LESS_THAN_QUERY_END_DATE = LocalDate.of(2025, 1, 20);

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

		private final ScheduleCreateRequest successAllDayAlarmOneDayRequest = ScheduleCreateRequest.builder()
			.title("종일 일정")
			.category(PlanCategory.COMPUTER)
			.startDate(LocalDate.of(2025, 1, 1)) // 필수 값
			.endDate(LocalDate.of(2025, 1, 1))
			.isAllDay(true)
			.color("#FFFFFF")
			.time(null)
			.daysOfWeek(List.of(DayOfWeek.MONDAY))
			.alarm(ScheduleAlram.ONE_DAY)
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
			ScheduleIdResponse result = scheduleUsecase.createSchedule(savedUser.getId(),
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
			ScheduleIdResponse response = scheduleUsecase.createSchedule(savedUser.getId(), request);

			//then
			assertSoftly(softly -> {
				softly.assertThat(response.scheduleId()).isNotNull();
			});
		}

		@Test
		void 종일_일정에서_알람은_null이거나_1일전이어야_성공한다() {
			// given -> when
			ScheduleIdResponse result = scheduleUsecase.createSchedule(savedUser.getId(),
				successAllDayAlarmOneDayRequest);

			// then
			assertSoftly(softly -> {
				softly.assertThat(result.scheduleId()).isNotNull();
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
			void 종일_여부가_true인데_알람이_null이거나_1일전이_아니면_실패한다() {
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
			Optional<ScheduleRecord> scheduleRecord = scheduleRecordRepository.findScheduleRecordByRecordDateAndScheduleId(
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
			Optional<ScheduleRecord> scheduleRecord = scheduleRecordRepository.findScheduleRecordByRecordDateAndScheduleId(
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

	@Nested
	@DisplayName("일정 삭제 요청시")
	class deleteSchedule {
		@BeforeEach
		void setUp() {
			testFixtureBuilder.buildUser(GENERAL_USER());
		}

		@Test
		void 성공_반복_없는_하루_일정은_일정과_일정기록_모두_삭제된다() {
			// given
			Schedule savedSchedule = testFixtureBuilder
				.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
					LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)));
			ScheduleRecord savedScheduleRecord = testFixtureBuilder
				.buildScheduleRecord(IS_COMPLETE_SCHEDULE_RECORD(LocalDate.of(2025, 1, 1), savedSchedule));

			ScheduleDeleteRequest request = ScheduleDeleteRequest.builder()
				.scheduleId(savedSchedule.getId())
				.isOneDayDeleted(true)
				.queryDate(LocalDate.of(2025, 1, 1))
				.build();

			// when
			scheduleUsecase.deleteSchedule(testFixtureBuilder.buildUser(GENERAL_USER()).getId(), request);

			// then
			Optional<Schedule> schedule = scheduleRepository.findById(savedSchedule.getId());
			Optional<ScheduleRecord> scheduleRecord = scheduleRecordRepository.findById(savedScheduleRecord.getId());
			assertSoftly(softly -> {
				softly.assertThat(schedule).isEmpty();
				softly.assertThat(scheduleRecord).isEmpty();
			});
		}

		@Test
		void 성공_반복_없는_하루_일정은_일정기록이_없더라도_일정은_삭제된다() {
			// given
			Schedule savedSchedule = testFixtureBuilder
				.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
					LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)));

			ScheduleDeleteRequest request = ScheduleDeleteRequest.builder()
				.scheduleId(savedSchedule.getId())
				.isOneDayDeleted(true)
				.queryDate(LocalDate.of(2025, 1, 1))
				.build();

			// when
			scheduleUsecase.deleteSchedule(testFixtureBuilder.buildUser(GENERAL_USER()).getId(), request);

			// then
			Optional<Schedule> schedule = scheduleRepository.findById(savedSchedule.getId());
			assertSoftly(softly -> {
				softly.assertThat(schedule).isEmpty();
			});
		}

		@Test
		void 성공_반복_있고_하루짜리_일정_하루_삭제는_해당_기록이_SoftDelete_된다() {
			// given
			Schedule savedSchedule = testFixtureBuilder
				.buildSchedule(DEFAULT_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
					LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)));
			ScheduleRecord savedScheduleRecord = testFixtureBuilder
				.buildScheduleRecord(IS_COMPLETE_SCHEDULE_RECORD(LocalDate.of(2025, 1, 10), savedSchedule));

			ScheduleDeleteRequest request = ScheduleDeleteRequest.builder()
				.scheduleId(savedSchedule.getId())
				.isOneDayDeleted(true)
				.queryDate(LocalDate.of(2025, 1, 10))
				.build();

			// when
			scheduleUsecase.deleteSchedule(testFixtureBuilder.buildUser(GENERAL_USER()).getId(), request);

			// then
			Optional<ScheduleRecord> scheduleRecord = scheduleRecordRepository.findById(savedScheduleRecord.getId());
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecord).isPresent();
				softly.assertThat(scheduleRecord.get().getDeletedAt()).isNotNull();
			});
		}

		@Test
		void 성공_반복_있고_하루짜리_일정_하루_삭제는_해당_기록이_없더라도_SoftDelete_된다() {
			// given
			Schedule savedSchedule = testFixtureBuilder
				.buildSchedule(DEFAULT_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
					LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)));

			ScheduleDeleteRequest request = ScheduleDeleteRequest.builder()
				.scheduleId(savedSchedule.getId())
				.isOneDayDeleted(true)
				.queryDate(LocalDate.of(2025, 1, 10))
				.build();

			// when
			scheduleUsecase.deleteSchedule(testFixtureBuilder.buildUser(GENERAL_USER()).getId(), request);

			// then
			Optional<ScheduleRecord> scheduleRecord = scheduleRecordRepository.findScheduleRecordByRecordDateAndScheduleId(
				LocalDate.of(2025, 1, 10),
				savedSchedule.getId());
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecord).isPresent();
				softly.assertThat(scheduleRecord.get().getDeletedAt()).isNotNull();
			});
		}

		@Test
		void 성공_반복_있고_하루짜리_일정_이후_삭제는_이후_완료_기록들은_다른_일정으로_변경되고_미완료_일정들은_삭제된다() {
			// given
			Schedule savedSchedule = testFixtureBuilder
				.buildSchedule(DEFAULT_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
					LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)));

			ScheduleRecord savedScheduleRecord = testFixtureBuilder
				.buildScheduleRecord(IS_COMPLETE_SCHEDULE_RECORD(QUERY_END_DATE.plusDays(1), savedSchedule));

			ScheduleRecord savedScheduleRecord2 = testFixtureBuilder
				.buildScheduleRecord(IS_NOT_COMPLETE_SCHEDULE_RECORD(QUERY_END_DATE.plusDays(2), savedSchedule));

			ScheduleDeleteRequest request = ScheduleDeleteRequest.builder()
				.scheduleId(savedSchedule.getId())
				.isOneDayDeleted(false)
				.queryDate(QUERY_END_DATE)
				.build();

			// when
			scheduleUsecase.deleteSchedule(testFixtureBuilder.buildUser(GENERAL_USER()).getId(), request);

			// then
			Optional<ScheduleRecord> scheduleRecord = scheduleRecordRepository.findScheduleRecordFetchJoinSchedule(
				savedScheduleRecord.getId());
			Optional<ScheduleRecord> scheduleRecord2 = scheduleRecordRepository.findScheduleRecordFetchJoinSchedule(
				savedScheduleRecord2.getId());
			Optional<Schedule> schedule = scheduleRepository.findById(savedSchedule.getId());

			assertSoftly(softly -> {
				System.out.println("하루 짜리 반복 일정이 특정 날짜 이후 삭제시 특정 날짜 이전으로 end_date 변경");
				softly.assertThat(schedule).isPresent();
				softly.assertThat(schedule.get().getScheduleDate().getEndDate()).isEqualTo(QUERY_END_DATE.minusDays(1));
				softly.assertThat(scheduleRecord).isPresent();

				System.out.println("하루 짜리 반복 일정이 특정 날짜 이후 삭제시 성공 일정 기록은 원래 일정이 아닌 다른 일정으로 변경된다.");
				Schedule schedule1 = scheduleRecord.get().getSchedule();
				softly.assertThat(schedule1.getScheduleDate().getEndDate())
					.isEqualTo(schedule1.getScheduleDate().getStartDate());
				softly.assertThat(schedule1.getId()).isNotEqualTo(schedule.get().getId());

				System.out.println("하루 짜리 반복 일정이 특정 날짜 이후 삭제시 실패 일정 기록은 삭제된다.");
				softly.assertThat(scheduleRecord2).isEmpty();
			});
		}

		@Test
		void 성공_반복_상관없이_기간_일정_하루_삭제는_해당_기록이_SoftDelete_된다() {
			// given
			Schedule savedSchedule = testFixtureBuilder
				.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
					LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 30)));
			ScheduleRecord savedScheduleRecord = testFixtureBuilder
				.buildScheduleRecord(IS_COMPLETE_SCHEDULE_RECORD(LocalDate.of(2025, 1, 1), savedSchedule));

			ScheduleDeleteRequest request = ScheduleDeleteRequest.builder()
				.scheduleId(savedSchedule.getId())
				.isOneDayDeleted(true)
				.queryDate(LocalDate.of(2025, 1, 1))
				.build();

			// when
			scheduleUsecase.deleteSchedule(testFixtureBuilder.buildUser(GENERAL_USER()).getId(), request);

			// then
			Optional<ScheduleRecord> scheduleRecord = scheduleRecordRepository.findById(savedScheduleRecord.getId());
			assertSoftly(softly -> {
				softly.assertThat(scheduleRecord).isPresent();
				softly.assertThat(scheduleRecord.get().getDeletedAt()).isNotNull();
			});
		}

		@Test
		void 성공_반복_상관없이_기간_일정_이후_삭제는_이후_완료_기록들은_다른_일정으로_변경되고_미완료_일정들은_삭제된다() {
			// given
			Schedule savedSchedule = testFixtureBuilder
				.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
					LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 30)));

			ScheduleRecord savedScheduleRecord = testFixtureBuilder
				.buildScheduleRecord(IS_COMPLETE_SCHEDULE_RECORD(QUERY_END_DATE.plusDays(1), savedSchedule));

			ScheduleRecord savedScheduleRecord2 = testFixtureBuilder
				.buildScheduleRecord(IS_NOT_COMPLETE_SCHEDULE_RECORD(QUERY_END_DATE.plusDays(2), savedSchedule));

			ScheduleDeleteRequest request = ScheduleDeleteRequest.builder()
				.scheduleId(savedSchedule.getId())
				.isOneDayDeleted(false)
				.queryDate(QUERY_END_DATE)
				.build();

			// when
			scheduleUsecase.deleteSchedule(testFixtureBuilder.buildUser(GENERAL_USER()).getId(), request);

			// then
			Optional<ScheduleRecord> scheduleRecord = scheduleRecordRepository.findScheduleRecordFetchJoinSchedule(
				savedScheduleRecord.getId());
			Optional<ScheduleRecord> scheduleRecord2 = scheduleRecordRepository.findScheduleRecordFetchJoinSchedule(
				savedScheduleRecord2.getId());
			Optional<Schedule> schedule = scheduleRepository.findById(savedSchedule.getId());

			assertSoftly(softly -> {
				System.out.println("기간 일정이 특정 날짜 이후 삭제시 특정 날짜 이전으로 end_date 변경");
				softly.assertThat(schedule).isPresent();
				softly.assertThat(schedule.get().getScheduleDate().getEndDate()).isEqualTo(QUERY_END_DATE.minusDays(1));
				softly.assertThat(scheduleRecord).isPresent();

				System.out.println("기간 일정이 특정 날짜 이후 삭제시 성공 일정 기록은 원래 일정이 아닌 다른 일정으로 변경된다.");
				Schedule schedule1 = scheduleRecord.get().getSchedule();
				softly.assertThat(schedule1.getScheduleDate().getEndDate())
					.isEqualTo(schedule1.getScheduleDate().getStartDate());
				softly.assertThat(schedule1.getId()).isNotEqualTo(schedule.get().getId());

				System.out.println("기간 일정이 특정 날짜 이후 삭제시 실패 일정 기록은 삭제된다.");
				softly.assertThat(scheduleRecord2).isEmpty();
			});
		}

		@Test
		void 성공_특정날짜_이후_삭제시_특정날짜와_일정의_시작날짜가_같을시_일정까지_삭제된다() {
			// given
			Schedule savedSchedule = testFixtureBuilder
				.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
					QUERY_END_DATE, LocalDate.of(2025, 1, 30)));
			Schedule savedSchedule2 = testFixtureBuilder
				.buildSchedule(DEFAULT_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
					QUERY_END_DATE, LocalDate.of(2025, 1, 30)));
			Schedule savedSchedule3 = testFixtureBuilder
				.buildSchedule(DEFAULT_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
					QUERY_END_DATE, QUERY_END_DATE));

			ScheduleDeleteRequest request = ScheduleDeleteRequest.builder()
				.scheduleId(savedSchedule.getId())
				.isOneDayDeleted(false)
				.queryDate(QUERY_END_DATE)
				.build();

			ScheduleDeleteRequest request2 = ScheduleDeleteRequest.builder()
				.scheduleId(savedSchedule2.getId())
				.isOneDayDeleted(false)
				.queryDate(QUERY_END_DATE)
				.build();

			ScheduleDeleteRequest request3 = ScheduleDeleteRequest.builder()
				.scheduleId(savedSchedule3.getId())
				.isOneDayDeleted(false)
				.queryDate(QUERY_END_DATE)
				.build();
			// when
			scheduleUsecase.deleteSchedule(testFixtureBuilder.buildUser(GENERAL_USER()).getId(), request);
			scheduleUsecase.deleteSchedule(testFixtureBuilder.buildUser(GENERAL_USER()).getId(), request2);
			scheduleUsecase.deleteSchedule(testFixtureBuilder.buildUser(GENERAL_USER()).getId(), request3);

			// then
			Optional<Schedule> schedule = scheduleRepository.findById(savedSchedule.getId());
			Optional<Schedule> schedule2 = scheduleRepository.findById(savedSchedule2.getId());
			Optional<Schedule> schedule3 = scheduleRepository.findById(savedSchedule3.getId());

			assertSoftly(softly -> {
				softly.assertThat(schedule).isEmpty();
				softly.assertThat(schedule2).isEmpty();
				softly.assertThat(schedule3).isEmpty();
			});
		}

		@Test
		void 실패_유효하지_않는_유저ID_요청시_실패한다() {
			// given
			ScheduleDeleteRequest request = ScheduleDeleteRequest.builder()
				.scheduleId(9999L)
				.isOneDayDeleted(true)
				.queryDate(LocalDate.of(2025, 1, 1))
				.build();

			// when -> then
			assertSoftly(softly -> {
				softly.assertThatThrownBy(() -> scheduleUsecase.deleteSchedule(9999L, request))
					.isInstanceOf(CommonException.class)
					.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.NOT_FOUND_USER.getHttpStatus())
					.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.NOT_FOUND_USER.getErrorCode())
					.hasFieldOrPropertyWithValue("message", ExceptionCode.NOT_FOUND_USER.getMessage());
			});
		}

		@Test
		void 실패_유효하지_않는_일정ID_요청시_실패한다() {
			// given
			ScheduleDeleteRequest request = ScheduleDeleteRequest.builder()
				.scheduleId(9999L)
				.isOneDayDeleted(true)
				.queryDate(LocalDate.of(2025, 1, 1))
				.build();

			// when -> then
			assertSoftly(softly -> {
				softly.assertThatThrownBy(
						() -> scheduleUsecase.deleteSchedule(testFixtureBuilder.buildUser(GENERAL_USER()).getId(), request))
					.isInstanceOf(CommonException.class)
					.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.NOT_FOUND_SCHEDULE.getHttpStatus())
					.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.NOT_FOUND_SCHEDULE.getErrorCode())
					.hasFieldOrPropertyWithValue("message", ExceptionCode.NOT_FOUND_SCHEDULE.getMessage());
			});
		}

		@Test
		void 실패_반복X_하루짜리_일정_삭제요청시_이후_삭제_요청은_실패한다() {
			// given
			Schedule savedSchedule = testFixtureBuilder
				.buildSchedule(DEFAULT_NON_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
					LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)));
			ScheduleDeleteRequest request = ScheduleDeleteRequest.builder()
				.scheduleId(savedSchedule.getId())
				.isOneDayDeleted(false)
				.queryDate(LocalDate.of(2025, 1, 1))
				.build();

			// when -> then
			assertSoftly(softly -> {

				softly.assertThatThrownBy(
						() -> scheduleUsecase.deleteSchedule(testFixtureBuilder.buildUser(GENERAL_USER()).getId(),
							request))
					.isInstanceOf(CommonException.class)
					.hasFieldOrPropertyWithValue("httpStatus",
						ExceptionCode.NON_REPESTITIVE_ONE_SCHEDULE_NOT_PERIOD_DELETE.getHttpStatus())
					.hasFieldOrPropertyWithValue("errorCode",
						ExceptionCode.NON_REPESTITIVE_ONE_SCHEDULE_NOT_PERIOD_DELETE.getErrorCode())
					.hasFieldOrPropertyWithValue("message",
						ExceptionCode.NON_REPESTITIVE_ONE_SCHEDULE_NOT_PERIOD_DELETE.getMessage());
			});
		}

	}

	@Nested
	@DisplayName("<일정 수정 요청시>")
	class updateSchedule {
		private User savedUser;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
		}

		@Nested
		@DisplayName("하루 반복 x 일정 수정시")
		class singleDateNonRepeatableModify {
			@Test
			void 성공_일정을_성공적으로_수정한다() {
				//given
				Schedule savedSchedule = testFixtureBuilder.buildSchedule(
					DEFAULT_NON_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
						LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)));

				ScheduleCreateRequest updateScheduleData = ScheduleCreateRequest.builder()
					.title("일정 제목")
					.category(PlanCategory.COMPUTER)
					.startDate(LocalDate.of(2025, 1, 1)) // 필수 값
					.endDate(LocalDate.of(2025, 1, 1))
					.isAllDay(false)
					.color("#FFFFFF")
					.time(LocalTime.of(10, 30))
					.daysOfWeek(null)
					.alarm(ScheduleAlram.TEN_MINUTE)
					.location("일정 장소")
					.memo("일정 메모")
					.build();

				ScheduleModifyRequest request = ScheduleModifyRequest.builder()
					.scheduleId(savedSchedule.getId())
					.queryDate(LocalDate.of(2025, 1, 1))
					.scheduleData(updateScheduleData)
					.isOneDayDeleted(true)
					.build();
				// when
				scheduleUsecase.updateSchedule(savedUser.getId(), request);

				// then
				Schedule schedule = scheduleRepository.findById(savedSchedule.getId()).get();
				assertSoftly(softly -> {
					softly.assertThat(schedule.getTitle()).isEqualTo(updateScheduleData.title());
					softly.assertThat(schedule.getCategory()).isEqualTo(updateScheduleData.category());
					softly.assertThat(schedule.getScheduleDate().getStartDate())
						.isEqualTo(updateScheduleData.startDate());
					softly.assertThat(schedule.getScheduleDate().getEndDate()).isEqualTo(updateScheduleData.endDate());
					softly.assertThat(schedule.getScheduleTime().getIsAllDay())
						.isEqualTo(updateScheduleData.isAllDay());
					softly.assertThat(schedule.getColor().getValue()).isEqualTo(updateScheduleData.color());
					softly.assertThat(schedule.getScheduleTime().getTime()).isEqualTo(updateScheduleData.time());
					softly.assertThat(schedule.getScheduleTime().getAlarm()).isEqualTo(updateScheduleData.alarm());
					softly.assertThat(schedule.getLocation()).isEqualTo(updateScheduleData.location());
					softly.assertThat(schedule.getMemo()).isEqualTo(updateScheduleData.memo());
					softly.assertThat(schedule.getDaysOfWeekBitmask()).isNull();
				});
			}

			@Test
			void 실패_하루_반복X_일정은_이후_일정_일괄_수정_실패한다() {
				//given
				Schedule savedSchedule = testFixtureBuilder.buildSchedule(
					DEFAULT_NON_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
						LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)));

				ScheduleCreateRequest updateScheduleData = ScheduleCreateRequest.builder()
					.title("일정 제목")
					.category(PlanCategory.COMPUTER)
					.startDate(LocalDate.of(2025, 1, 1)) // 필수 값
					.endDate(LocalDate.of(2025, 1, 1))
					.isAllDay(false)
					.color("#FFFFFF")
					.time(LocalTime.of(10, 30))
					.daysOfWeek(null)
					.alarm(ScheduleAlram.TEN_MINUTE)
					.location("일정 장소")
					.memo("일정 메모")
					.build();

				ScheduleModifyRequest request = ScheduleModifyRequest.builder()
					.scheduleId(savedSchedule.getId())
					.queryDate(LocalDate.of(2025, 1, 1))
					.scheduleData(updateScheduleData)
					.isOneDayDeleted(false)
					.build();

				// when -> then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(() -> scheduleUsecase.updateSchedule(savedUser.getId(), request))
						.isInstanceOf(CommonException.class)
						.hasFieldOrPropertyWithValue("errorCode",
							ExceptionCode.ONE_DAY__NONREPEATABLE_SCHEDULE_CANNOT_AFTER_DATE_UPDATE.getErrorCode());
				});
			}
		}

		@Nested
		@DisplayName("반복 일정 하루 수정시")
		class repeatableScheduleModify {
			@Test
			void 성공_일정을_성공적으로_수정한다() {
				//given
				Schedule savedSchedule = testFixtureBuilder.buildSchedule(
					DEFAULT_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
						LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)));

				ScheduleRecord scheduleRecord1 = testFixtureBuilder.buildScheduleRecord(
					IS_COMPLETE_SCHEDULE_RECORD(LocalDate.of(2025, 1, 10), savedSchedule));
				ScheduleRecord scheduleRecord2 = testFixtureBuilder.buildScheduleRecord(
					IS_NOT_COMPLETE_SCHEDULE_RECORD(LocalDate.of(2025, 1, 20), savedSchedule));

				ScheduleCreateRequest updateScheduleData = ScheduleCreateRequest.builder()
					.title("일정 제목")
					.category(PlanCategory.COMPUTER)
					.startDate(LocalDate.of(2025, 1, 1)) // 필수 값
					.endDate(LocalDate.of(2025, 1, 1))
					.isAllDay(false)
					.color("#FFFFFF")
					.time(LocalTime.of(10, 30))
					.daysOfWeek(null)
					.alarm(ScheduleAlram.TEN_MINUTE)
					.location("일정 장소")
					.memo("일정 메모")
					.build();

				ScheduleModifyRequest request = ScheduleModifyRequest.builder()
					.scheduleId(savedSchedule.getId())
					.queryDate(LocalDate.of(2025, 1, 5))
					.scheduleData(updateScheduleData)
					.isOneDayDeleted(true)
					.build();
				// when
				ScheduleIdResponse scheduleIdResponse = scheduleUsecase.updateSchedule(savedUser.getId(), request);

				// then
				Schedule preSchedule = scheduleRepository.findById(savedSchedule.getId()).get();
				Schedule updatedSchedule = scheduleRepository.findById(scheduleIdResponse.scheduleId()).get();
				Optional<ScheduleRecord> completedScheduleRecord = scheduleRecordRepository.findById(
					scheduleRecord1.getId());
				Optional<ScheduleRecord> notCompletedScheduleRecord = scheduleRecordRepository
					.findById(scheduleRecord2.getId());
				Optional<ScheduleRecord> softDeletedScheduleRecord = scheduleRecordRepository.findScheduleRecordByRecordDateAndScheduleId(
					LocalDate.of(2025, 1, 5),
					savedSchedule.getId());

				assertSoftly(softly -> {
					// 수정된 일정이 잘 만들어 지는가
					softly.assertThat(updatedSchedule.getTitle()).isEqualTo(updateScheduleData.title());
					softly.assertThat(updatedSchedule.getCategory()).isEqualTo(updateScheduleData.category());
					softly.assertThat(updatedSchedule.getScheduleDate().getStartDate())
						.isEqualTo(updateScheduleData.startDate());
					softly.assertThat(updatedSchedule.getScheduleDate().getEndDate())
						.isEqualTo(updateScheduleData.endDate());
					softly.assertThat(updatedSchedule.getScheduleTime().getIsAllDay())
						.isEqualTo(updateScheduleData.isAllDay());
					softly.assertThat(updatedSchedule.getColor().getValue()).isEqualTo(updateScheduleData.color());
					softly.assertThat(updatedSchedule.getScheduleTime().getTime()).isEqualTo(updateScheduleData.time());
					softly.assertThat(updatedSchedule.getScheduleTime().getAlarm())
						.isEqualTo(updateScheduleData.alarm());
					softly.assertThat(updatedSchedule.getLocation()).isEqualTo(updateScheduleData.location());
					softly.assertThat(updatedSchedule.getMemo()).isEqualTo(updateScheduleData.memo());
					softly.assertThat(updatedSchedule.getDaysOfWeekBitmask()).isNull();

					// 기존 일정 기록은 삭제되면 안된다.
					softly.assertThat(completedScheduleRecord).isPresent();
					softly.assertThat(notCompletedScheduleRecord).isPresent();

					// 기존 일정 기록의 쿼리 날짜는 soft delete 처리 되어야 한다.
					softly.assertThat(softDeletedScheduleRecord).isPresent();
					softly.assertThat(softDeletedScheduleRecord.get().getDeletedAt()).isNotNull();

				});

			}
		}

		@Nested
		@DisplayName("반복 일정 특정 날짜 이후 일괄 수정시")
		class repeatableScheduleAfterModify {
			@Test
			void 성공_일정을_성공적으로_수정한다() {
				// given
				Schedule savedSchedule = testFixtureBuilder.buildSchedule(
					DEFAULT_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
						LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)));
				ScheduleRecord scheduleRecord1 = testFixtureBuilder.buildScheduleRecord(
					IS_COMPLETE_SCHEDULE_RECORD(LocalDate.of(2025, 1, 10), savedSchedule));
				ScheduleRecord scheduleRecord2 = testFixtureBuilder.buildScheduleRecord(
					IS_NOT_COMPLETE_SCHEDULE_RECORD(LocalDate.of(2025, 1, 20), savedSchedule));

				ScheduleCreateRequest updateScheduleData = ScheduleCreateRequest.builder()
					.title("일정 제목")
					.category(PlanCategory.COMPUTER)
					.startDate(LocalDate.of(2025, 1, 1)) // 필수 값
					.endDate(LocalDate.of(2025, 1, 1))
					.isAllDay(false)
					.color("#FFFFFF")
					.time(LocalTime.of(10, 30))
					.daysOfWeek(null)
					.alarm(ScheduleAlram.TEN_MINUTE)
					.location("일정 장소")
					.memo("일정 메모")
					.build();

				ScheduleModifyRequest request = ScheduleModifyRequest.builder()
					.scheduleId(savedSchedule.getId())
					.queryDate(LocalDate.of(2025, 1, 5))
					.scheduleData(updateScheduleData)
					.isOneDayDeleted(false)
					.build();
				// when
				ScheduleIdResponse scheduleIdResponse = scheduleUsecase.updateSchedule(savedUser.getId(), request);
				// then
				Schedule preSchedule = scheduleRepository.findById(savedSchedule.getId()).get();
				Schedule updatedSchedule = scheduleRepository.findById(scheduleIdResponse.scheduleId()).get();
				Optional<ScheduleRecord> completedScheduleRecord = scheduleRecordRepository.findById(
					scheduleRecord1.getId());
				Optional<ScheduleRecord> notCompletedScheduleRecord = scheduleRecordRepository
					.findById(scheduleRecord2.getId());
				Optional<ScheduleRecord> softDeletedScheduleRecord = scheduleRecordRepository.findScheduleRecordByRecordDateAndScheduleId(
					LocalDate.of(2025, 1, 5),
					savedSchedule.getId());
				assertSoftly(softly -> {
					// 수정된 일정이 잘 만들어 지는가
					softly.assertThat(updatedSchedule.getTitle()).isEqualTo(updateScheduleData.title());
					softly.assertThat(updatedSchedule.getCategory()).isEqualTo(updateScheduleData.category());
					softly.assertThat(updatedSchedule.getScheduleDate().getStartDate())
						.isEqualTo(updateScheduleData.startDate());
					softly.assertThat(updatedSchedule.getScheduleDate().getEndDate())
						.isEqualTo(updateScheduleData.endDate());
					softly.assertThat(updatedSchedule.getScheduleTime().getIsAllDay())
						.isEqualTo(updateScheduleData.isAllDay());
					softly.assertThat(updatedSchedule.getColor().getValue()).isEqualTo(updateScheduleData.color());
					softly.assertThat(updatedSchedule.getScheduleTime().getTime()).isEqualTo(updateScheduleData.time());
					softly.assertThat(updatedSchedule.getScheduleTime().getAlarm())
						.isEqualTo(updateScheduleData.alarm());
					softly.assertThat(updatedSchedule.getLocation()).isEqualTo(updateScheduleData.location());
					softly.assertThat(updatedSchedule.getMemo()).isEqualTo(updateScheduleData.memo());
					softly.assertThat(updatedSchedule.getDaysOfWeekBitmask()).isNull();

					// 기존 일정 기록은 완료 되었다면 삭제되면 안된다. 미완료 기록만 삭제된다
					softly.assertThat(completedScheduleRecord).isPresent();
					softly.assertThat(notCompletedScheduleRecord).isEmpty();

					// 기존 일정의 마감 일자가 쿼리 일자 하루 전으로 변경된다.
					softly.assertThat(preSchedule.getScheduleDate().getEndDate())
						.isEqualTo(request.queryDate().minusDays(1));

				});

			}

			@Test
			void 성공_querydate가_일정의_startDate와_같아도_에러없이_수정된다() {
				// given
				Schedule savedSchedule = testFixtureBuilder.buildSchedule(
					DEFAULT_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
						LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)));
				ScheduleRecord scheduleRecord1 = testFixtureBuilder.buildScheduleRecord(
					IS_COMPLETE_SCHEDULE_RECORD(LocalDate.of(2025, 1, 10), savedSchedule));
				ScheduleRecord scheduleRecord2 = testFixtureBuilder.buildScheduleRecord(
					IS_NOT_COMPLETE_SCHEDULE_RECORD(LocalDate.of(2025, 1, 20), savedSchedule));

				ScheduleCreateRequest updateScheduleData = ScheduleCreateRequest.builder()
					.title("일정 제목")
					.category(PlanCategory.COMPUTER)
					.startDate(LocalDate.of(2025, 1, 1)) // 필수 값
					.endDate(LocalDate.of(2025, 1, 1))
					.isAllDay(false)
					.color("#FFFFFF")
					.time(LocalTime.of(10, 30))
					.daysOfWeek(null)
					.alarm(ScheduleAlram.TEN_MINUTE)
					.location("일정 장소")
					.memo("일정 메모")
					.build();

				ScheduleModifyRequest request = ScheduleModifyRequest.builder()
					.scheduleId(savedSchedule.getId())
					.queryDate(LocalDate.of(2025, 1, 1)) // startDate와 동일한 날짜
					.scheduleData(updateScheduleData)
					.isOneDayDeleted(false)
					.build();
				// when
				ScheduleIdResponse scheduleIdResponse = scheduleUsecase.updateSchedule(savedUser.getId(), request);
				// then
				Optional<Schedule> preSchedule = scheduleRepository.findById(savedSchedule.getId());
				Schedule updatedSchedule = scheduleRepository.findById(scheduleIdResponse.scheduleId()).get();
				Optional<ScheduleRecord> completedScheduleRecord = scheduleRecordRepository.findById(
					scheduleRecord1.getId());
				Optional<ScheduleRecord> notCompletedScheduleRecord = scheduleRecordRepository
					.findById(scheduleRecord2.getId());
				Optional<ScheduleRecord> softDeletedScheduleRecord = scheduleRecordRepository.findScheduleRecordByRecordDateAndScheduleId(
					LocalDate.of(2025, 1, 1),
					savedSchedule.getId());
				assertSoftly(softly -> {
					// 수정된 일정이 잘 만들어 지는가
					softly.assertThat(updatedSchedule.getTitle()).isEqualTo(updateScheduleData.title());
					softly.assertThat(updatedSchedule.getCategory()).isEqualTo(updateScheduleData.category());
					softly.assertThat(updatedSchedule.getScheduleDate().getStartDate())
						.isEqualTo(updateScheduleData.startDate());
					softly.assertThat(updatedSchedule.getScheduleDate().getEndDate())
						.isEqualTo(updateScheduleData.endDate());
					softly.assertThat(updatedSchedule.getScheduleTime().getIsAllDay())
						.isEqualTo(updateScheduleData.isAllDay());
					softly.assertThat(updatedSchedule.getColor().getValue()).isEqualTo(updateScheduleData.color());
					softly.assertThat(updatedSchedule.getScheduleTime().getTime()).isEqualTo(updateScheduleData.time());
					softly.assertThat(updatedSchedule.getScheduleTime().getAlarm())
						.isEqualTo(updateScheduleData.alarm());
					softly.assertThat(updatedSchedule.getLocation()).isEqualTo(updateScheduleData.location());
					softly.assertThat(updatedSchedule.getMemo()).isEqualTo(updateScheduleData.memo());
					softly.assertThat(updatedSchedule.getDaysOfWeekBitmask()).isNull();

					// 기존 일정 기록은 완료 되었다면 삭제되면 안된다. 미완료 기록만 삭제된다
					softly.assertThat(completedScheduleRecord).isPresent();
					softly.assertThat(notCompletedScheduleRecord).isEmpty();

					// 기존 일정의 마감 일자가 쿼리 일자 하루 전으로 변경된다.
					softly.assertThat(preSchedule)
						.isEmpty();
				});
			}

			@Test
			void 실패_특정_날짜_일괄_수정시_기간_날짜_변경은_실패한다() {
				// given
				Schedule savedSchedule = testFixtureBuilder.buildSchedule(
					DEFAULT_REPEATABLE_SCHEDULE(testFixtureBuilder.buildUser(GENERAL_USER()),
						LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)));
				ScheduleCreateRequest updateScheduleData = ScheduleCreateRequest.builder()
					.title("일정 제목")
					.category(PlanCategory.COMPUTER)
					.startDate(LocalDate.of(2025, 1, 1)) // 필수 값
					.endDate(LocalDate.of(2025, 1, 10))
					.isAllDay(false)
					.color("#FFFFFF")
					.time(LocalTime.of(10, 30))
					.daysOfWeek(null)
					.alarm(ScheduleAlram.TEN_MINUTE)
					.location("일정 장소")
					.memo("일정 메모")
					.build();

				ScheduleModifyRequest request = ScheduleModifyRequest.builder()
					.scheduleId(savedSchedule.getId())
					.queryDate(LocalDate.of(2025, 1, 5))
					.scheduleData(updateScheduleData)
					.isOneDayDeleted(false)
					.build();

				// when -> then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(() -> scheduleUsecase.updateSchedule(savedUser.getId(), request))
						.isInstanceOf(CommonException.class)
						.hasFieldOrPropertyWithValue("errorCode",
							ExceptionCode.PERIOD_SCHEDULE_CANNOT_AFTER_DATE_UPDATE.getErrorCode());
				});
			}

		}

		@Test
		void 실패_유효하지_않은_유저ID_요청시_실패한다() {
			// given
			ScheduleCreateRequest updateScheduleData = ScheduleCreateRequest.builder()
				.title("일정 제목")
				.category(PlanCategory.COMPUTER)
				.startDate(LocalDate.of(2025, 1, 1)) // 필수 값
				.endDate(LocalDate.of(2025, 1, 1))
				.isAllDay(false)
				.color("#FFFFFF")
				.time(LocalTime.of(10, 30))
				.daysOfWeek(null)
				.alarm(ScheduleAlram.TEN_MINUTE)
				.location("일정 장소")
				.memo("일정 메모")
				.build();

			ScheduleModifyRequest request = ScheduleModifyRequest.builder()
				.scheduleId(9999L)
				.queryDate(LocalDate.of(2025, 1, 1))
				.scheduleData(updateScheduleData)
				.isOneDayDeleted(true)
				.build();

			// when -> then
			assertSoftly(softly -> {
				softly.assertThatThrownBy(() -> scheduleUsecase.updateSchedule(9999L, request))
					.isInstanceOf(CommonException.class)
					.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.NOT_FOUND_USER.getHttpStatus())
					.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.NOT_FOUND_USER.getErrorCode())
					.hasFieldOrPropertyWithValue("message", ExceptionCode.NOT_FOUND_USER.getMessage());
			});

		}

		@Test
		void 실패_유효하지_않은_일정ID_요청시_실패한다() {
			// given
			ScheduleCreateRequest updateScheduleData = ScheduleCreateRequest.builder()
				.title("일정 제목")
				.category(PlanCategory.COMPUTER)
				.startDate(LocalDate.of(2025, 1, 1)) // 필수 값
				.endDate(LocalDate.of(2025, 1, 1))
				.isAllDay(false)
				.color("#FFFFFF")
				.time(LocalTime.of(10, 30))
				.daysOfWeek(null)
				.alarm(ScheduleAlram.TEN_MINUTE)
				.location("일정 장소")
				.memo("일정 메모")
				.build();

			ScheduleModifyRequest request = ScheduleModifyRequest.builder()
				.scheduleId(9999L)
				.queryDate(LocalDate.of(2025, 1, 1))
				.scheduleData(updateScheduleData)
				.isOneDayDeleted(true)
				.build();

			// when -> then
			assertSoftly(softly -> {
				softly.assertThatThrownBy(() -> scheduleUsecase.updateSchedule(savedUser.getId(), request))
					.isInstanceOf(CommonException.class)
					.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.NOT_FOUND_SCHEDULE.getHttpStatus())
					.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.NOT_FOUND_SCHEDULE.getErrorCode())
					.hasFieldOrPropertyWithValue("message", ExceptionCode.NOT_FOUND_SCHEDULE.getMessage());
			});
		}

	}
}

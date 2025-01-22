package im.toduck.domain.schedule.domain.usecase;

import static im.toduck.fixtures.schedule.ScheduleCreateRequestFixtures.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.ServiceTest;
import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleCreateResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.fixtures.user.UserFixtures;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.exception.VoException;

class ScheduleUseCaseTest extends ServiceTest {

	@Autowired
	private ScheduleUseCase scheduleUsecase;

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
			savedUser = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
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
		private User savedUser;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
		}

		@Test
		@Disabled
		void 성공적으로_조회한다() {
			// given
			LocalDate startDate = LocalDate.of(2025, 1, 1);
			LocalDate endDate = LocalDate.of(2025, 1, 1);

			// when
			ScheduleHeadResponse scheduleHeadResponse = scheduleUsecase.getRangeSchedule(savedUser.getId(),
				startDate, endDate);

			// then

		}
	}

}

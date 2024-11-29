package im.toduck.domain.schedule.domain.usecase;

import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.UseCaseTest;
import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;
import im.toduck.domain.schedule.presentation.dto.ScheduleCreateRequest;
import im.toduck.domain.user.persistence.entity.ScheduleInfoResponse;
import im.toduck.fixtures.user.UserFixtures;
import im.toduck.global.security.authentication.CustomUserDetails;

class ScheduleUsecaseTest extends UseCaseTest {

	@Autowired
	private ScheduleUsecase scheduleUsecase;

	@Nested
	@DisplayName("<일정생성>")
	class postSchedule {

		@Test
		void 일정을_성공적으로_생성한다() {
			// given
			ScheduleCreateRequest request = ScheduleCreateRequest.builder()
				.title("일정 제목")
				.category(PlanCategory.STUDY)
				.categoryColor("#FFFFFF")
				.time(LocalTime.of(10, 30)) // TODO : 시간 null 일 수 있나? (디자인에는 종일 일정이 존재) 만약 종일 일정이 있다면 알람은 선택 못하게 해야하나?
				.alarm(ScheduleAlram.TEN_MINUTE)
				.startDate(LocalDate.of(2021, 1, 1)) // 필수 값
				.endDate(null)
				.repeatDayOfWeek(null)
				.location("일정 장소")
				.memo("일정 메모")
				.build();

			ScheduleInfoResponse response = ScheduleInfoResponse.builder()
				.scheduleId(1L)
				.build();

			CustomUserDetails customUserDetails = CustomUserDetails.from(UserFixtures.GENERAL_USER());

			//when
			ScheduleInfoResponse result = scheduleUsecase.postSchedule(customUserDetails, request);

			// then
			assertSoftly(softly -> {
				softly.assertThat(result).isEqualTo(response);
			});
		}

		@Nested
		@DisplayName("[일정 생성 실패 경우]")
		class failPostSchedule {
			@Test
			@Disabled
			void 유효한_유저가_아닐경우_일정생성에_실패한다() {
			}
		}
	}

}

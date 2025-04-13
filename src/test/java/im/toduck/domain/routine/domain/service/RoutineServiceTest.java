package im.toduck.domain.routine.domain.service;

import static im.toduck.fixtures.routine.RoutineFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.repository.RoutineRepository;
import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.user.persistence.entity.User;

@Transactional
class RoutineServiceTest extends ServiceTest {

	@Autowired
	private RoutineService routineService;

	@Autowired
	private RoutineRepository routineRepository;

	private User USER;

	@BeforeEach
	void setUp() {
		// given
		USER = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Nested
	@DisplayName("루틴 생성시")
	class CreateTest {
		private RoutineCreateRequest request;

		@BeforeEach
		void setUp() {
			request = new RoutineCreateRequest(
				"Morning Exercise",
				PlanCategory.COMPUTER,
				"#FF5733",
				LocalTime.of(7, 0),
				true,
				List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
				30,
				"30 minutes jogging"
			);
		}

		@Test
		void 루틴을_생성할_수_있다() {
			// when
			RoutineCreateResponse result = routineService.create(USER, request);

			// then
			assertSoftly(softly -> {
				softly.assertThat(result).isNotNull();
				softly.assertThat(result.routineId()).isNotNull();

				Routine savedRoutine = routineRepository.findById(result.routineId()).orElse(null);
				softly.assertThat(savedRoutine).isNotNull();
				softly.assertThat(savedRoutine.getTitle()).isEqualTo(request.title());
				softly.assertThat(savedRoutine.getUser()).isEqualTo(USER);
			});
		}
	}

	@Nested
	@DisplayName("기록되지 않은 루틴 목록 조회 시")
	class GetUnrecordedRoutinesForDateTest {
		@Test
		void 종일_루틴의_경우_수정_시각과_관계없이_당일_수정된_루틴은_조회되지_않는다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ALLDAY_ROUTINE(USER)
					.createdAt("2024-12-01 01:00:00")
					.scheduleModifiedAt("2024-12-16 00:01:00")
					.build()
			);
			LocalDate monday = LocalDate.parse("2024-12-16");

			// when
			List<Routine> unrecordedRoutines = routineService.getUnrecordedRoutinesForDate(USER, monday, List.of());

			// then
			assertThat(unrecordedRoutines).doesNotContain(ROUTINE);
		}

		@Test
		void 특정_시간_루틴의_경우_해당_시간_이후_수정된_루틴은_조회되지_않는다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutineAndUpdateAuditFields(
				PUBLIC_MONDAY_ALLDAY_ROUTINE(USER) //아침 7시 루틴
					.createdAt("2024-12-01 01:00:00")
					.scheduleModifiedAt("2024-12-16 06:00:00") // 아침 6시 수정
					.build()
			);
			LocalDate monday = LocalDate.parse("2024-12-16");

			// when
			List<Routine> unrecordedRoutines = routineService.getUnrecordedRoutinesForDate(USER, monday, List.of());

			// then
			assertThat(unrecordedRoutines).doesNotContain(ROUTINE);
		}
	}
}

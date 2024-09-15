package im.toduck.domain.routine.domain.usecase;

import static im.toduck.fixtures.RoutineFixtures.*;
import static im.toduck.fixtures.RoutineRecordFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.routine.presentation.dto.response.MyRoutineReadListResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;

@Transactional
class RoutineUseCaseTest extends ServiceTest {

	private User USER;

	@Autowired
	private RoutineUseCase routineUseCase;

	@MockBean
	private UserService userService;

	@BeforeEach
	void setUp() {
		// given
		USER = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Nested
	@DisplayName("루틴 목록 조회시")
	class ReadMyRoutineListTest {
		@BeforeEach
		void setUp() {
			// given
			given(userService.getUserById(any(Long.class))).willReturn(Optional.ofNullable(USER));
		}

		@Test
		void 루틴_기록이_존재하는_경우에는_해당_기록을_그대로_사용한다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));
			RoutineRecord RECORD = testFixtureBuilder.buildRoutineRecord(
				COMPLETED_SYNCED_RECORD(ROUTINE)
			);
			LocalDate queryDate = RECORD.getRecordAt().toLocalDate();

			// when
			MyRoutineReadListResponse responses = routineUseCase.readMyRoutineList(USER.getId(), queryDate);

			// then
			assertSoftly(softly -> {
				assertThat(responses.queryDate()).isEqualTo(queryDate);
				assertThat(responses.routines()).hasSize(1);

				MyRoutineReadListResponse.MyRoutineReadResponse response = responses.routines().get(0);
				assertThat(response.routineId()).isEqualTo(ROUTINE.getId());
				assertThat(response.isCompleted()).isEqualTo(RECORD.getIsCompleted());
				assertThat(response.time()).isEqualTo(RECORD.getRecordAt().toLocalTime());
			});
		}

		@Test
		void 루틴_기록이_존재하지_않는_경우에도_모_루틴을_통해_해당_기록을_조회할_수_있다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));
			LocalDate queryDate = LocalDate.now()
				.with(TemporalAdjusters.next(ROUTINE.getCreatedAt().getDayOfWeek()));

			// when
			MyRoutineReadListResponse responses = routineUseCase.readMyRoutineList(USER.getId(), queryDate);

			// then
			assertSoftly(softly -> {
				assertThat(responses.queryDate()).isEqualTo(queryDate);
				assertThat(responses.routines()).hasSize(1);

				MyRoutineReadListResponse.MyRoutineReadResponse response = responses.routines().get(0);
				assertThat(response.routineId()).isEqualTo(ROUTINE.getId());
				assertThat(response.isCompleted()).isFalse();
				assertThat(response.time()).isEqualTo(ROUTINE.getTime());
			});
		}

		@Disabled("추후 테스트 필요")
		@Test
		void 루틴_수정으로_인해_동기화되지_않은_루틴_기록을_정상적으로_조회할_수_있다() {

		}

		@Disabled("추후 테스트 필요")
		@Test
		void 모_루틴이_Soft_DELETE_된_경우에도_루틴_기록이_존재한다면_정상적으로_조회할_수_있다() {

		}
	}
}

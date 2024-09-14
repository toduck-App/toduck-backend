package im.toduck.domain.routine.domain.service;

import static im.toduck.fixtures.RoutineFixtures.*;
import static im.toduck.fixtures.RoutineRecordFixtures.*;
import static im.toduck.fixtures.UserFixtures.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.routine.persistence.repository.RoutineRecordRepository;
import im.toduck.domain.user.persistence.entity.User;

@Transactional
class RoutineRecordServiceTest extends ServiceTest {

	@Autowired
	private RoutineRecordService routineRecordService;

	@Autowired
	private RoutineRecordRepository routineRecordRepository;

	private User USER;

	@BeforeEach
	void setUp() {
		// given
		USER = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Nested
	@DisplayName("루틴 기록 생성시")
	class CreateTest {
		Routine ROUTINE;

		@BeforeEach
		void setUp() {
			ROUTINE = testFixtureBuilder.buildRoutine(MONDAY_ONLY_MORNING_ROUTINE(USER));
		}

		@Test
		void 정상적으로_생성된다() {
			// given
			final LocalDate date = LocalDate.now();
			final boolean isCompleted = true;

			// when
			routineRecordService.create(ROUTINE, date, isCompleted);

			// then
			List<RoutineRecord> routineRecords = routineRecordRepository.findAll();
			assertSoftly(softly -> {
				softly.assertThat(routineRecords).hasSize(1);
				softly.assertThat(routineRecords.get(0).getRoutine()).isEqualTo(ROUTINE);
				softly.assertThat(routineRecords.get(0).getIsCompleted()).isEqualTo(isCompleted);
			});
		}
	}

	@Nested
	@DisplayName("루틴 기록 완료 상태 변경시")
	class UpdateIfPresentTest {
		Routine ROUTINE;

		@BeforeEach
		void setUp() {
			ROUTINE = testFixtureBuilder.buildRoutine(MONDAY_ONLY_MORNING_ROUTINE(USER));
		}

		@Test
		void 기록이_존재하는_경우에_정상적으로_변경된다() {
			// given
			RoutineRecord RECORD = testFixtureBuilder.buildRoutineRecord(COMPLETED_SYNCED_RECORD(ROUTINE));
			final LocalDate date = LocalDate.from(RECORD.getRecordAt());

			// when
			final boolean isCompletedForChange = !RECORD.getIsCompleted();
			final boolean isUpdated = routineRecordService.updateIfPresent(ROUTINE, date, isCompletedForChange);

			// then
			List<RoutineRecord> routineRecords = routineRecordRepository.findAll();
			assertSoftly(softly -> {
				softly.assertThat(routineRecords).hasSize(1);
				softly.assertThat(routineRecords.get(0).getRoutine()).isEqualTo(ROUTINE);
				softly.assertThat(routineRecords.get(0).getIsCompleted()).isEqualTo(isCompletedForChange);

				softly.assertThat(isUpdated).isTrue();
			});
		}

		@Test
		void 기록이_존재하지_않는_경우에_변경이_이루어지지_않는다() {
			// when
			final boolean isUpdated = routineRecordService.updateIfPresent(ROUTINE, LocalDate.now(), true);

			// then
			List<RoutineRecord> routineRecords = routineRecordRepository.findAll();
			assertSoftly(softly -> {
				softly.assertThat(routineRecords).hasSize(0);

				softly.assertThat(isUpdated).isFalse();
			});
		}
	}
}

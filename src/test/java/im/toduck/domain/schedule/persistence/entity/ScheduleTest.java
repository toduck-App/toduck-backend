package im.toduck.domain.schedule.persistence.entity;

import static im.toduck.fixtures.schedule.ScheduleCompleteFixtures.*;
import static im.toduck.fixtures.schedule.ScheduleCreateRequestFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import im.toduck.domain.user.persistence.entity.User;

class ScheduleTest {

	User user;

	@BeforeEach
	void setUp() {
		user = GENERAL_USER();
	}

	@Test
	void createTest() {
		Schedule schedule = Schedule.create(user, DAYS_OF_WEEK_NULL_REQUEST());

		assertThat(schedule).isNotNull();
	}

	@Test
	void complete() {
		Schedule schedule = Schedule.create(user, DAYS_OF_WEEK_NULL_REQUEST());

		schedule.completeSchedule(COMPLETE_REQUEST(schedule.getId()));

		assertThat(schedule.getScheduleRecords()).hasSize(1);
		assertThat(schedule.getScheduleRecords().get(0).getIsCompleted()).isTrue();

		LocalDate localDate = LocalDate.of(2020, 1, 1);
		schedule.getScheduleRecords()
			.add(ScheduleRecord.create(schedule, localDate));

		schedule.completeSchedule(COMPLETE_REQUEST(schedule.getId(), localDate));

		assertThat(schedule.getScheduleRecords()).hasSize(2);
		assertThat(schedule.getScheduleRecords().get(1).getIsCompleted()).isTrue();
	}

}

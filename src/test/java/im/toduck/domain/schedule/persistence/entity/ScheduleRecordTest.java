package im.toduck.domain.schedule.persistence.entity;

import static im.toduck.fixtures.schedule.ScheduleCreateRequestFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import im.toduck.domain.user.persistence.entity.User;

class ScheduleRecordTest {

	User user;
	Schedule schedule;

	@BeforeEach
	void setUp() {
		user = GENERAL_USER();
		schedule = Schedule.create(user, DAYS_OF_WEEK_NULL_REQUEST());
	}

	@Test
	void create() {
		ScheduleRecord scheduleRecord = ScheduleRecord.create(schedule, LocalDate.now());

		assertThat(scheduleRecord).isNotNull();
		assertThat(scheduleRecord.getIsCompleted()).isFalse();
	}

}

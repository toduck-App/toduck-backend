package im.toduck.domain.schedule.persistence.vo;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import im.toduck.global.exception.VoException;

class ScheduleTimeTest {

	@Test
	void createScheduleTimeTest() {
		// 종일 여부가 true 이면 알람은 1일전이거나 null 이어야 합니다.
		ScheduleTime.of(true, null, ScheduleAlram.ONE_DAY);
		assertThatThrownBy(() ->
			ScheduleTime.of(true, null, ScheduleAlram.TEN_MINUTE)
		).isInstanceOf(VoException.class);

		// 종일 여부가 true 이면 시간은 null 이어야 합니다.
		ScheduleTime.of(true, null, null);
		assertThatThrownBy(() ->
			ScheduleTime.of(true, LocalTime.of(1, 1), null)
		).isInstanceOf(VoException.class);

		// 종일 여부가 false 이면 시간은 필수입니다.
		ScheduleTime.of(false, LocalTime.of(10, 0), null);
		assertThatThrownBy(() ->
			ScheduleTime.of(false, null, null)
		).isInstanceOf(VoException.class);
	}

}

package im.toduck.domain.schedule.persistence.vo;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import im.toduck.global.exception.VoException;

class ScheduleDateTest {

	@Test
	void createScheduleDateTest() {
		ScheduleDate.of(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 2));

		assertThat(ScheduleDate.of(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)))
			.isEqualTo(ScheduleDate.of(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)));

		assertThatThrownBy(() ->
			ScheduleDate.of(LocalDate.of(2025, 1, 1), null)
		).isInstanceOf(VoException.class);

		assertThatThrownBy(() ->
			ScheduleDate.of(LocalDate.of(2025, 1, 2), LocalDate.of(2025, 1, 1))
		).isInstanceOf(VoException.class);
	}

}

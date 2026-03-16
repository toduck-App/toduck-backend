package im.toduck.domain.schedule.persistence.vo;

import lombok.Getter;

@Getter
public enum ScheduleAlram {
	TEN_MINUTE(10), // 10분전
	THIRTY_MINUTE(30), // 30분전
	ONE_DAY(1440); // 1일전 (24 * 60)

	private final int minutes;

	ScheduleAlram(final int minutes) {
		this.minutes = minutes;
	}
}

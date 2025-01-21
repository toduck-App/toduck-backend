package im.toduck.domain.schedule.persistence.vo;

import java.time.LocalTime;

import im.toduck.global.exception.VoException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ScheduleTime {
	@Column(nullable = false, name = "is_all_day")
	private Boolean isAllDay;

	@Column(nullable = true, name = "time")
	private LocalTime time;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true, name = "alarm")
	private ScheduleAlram alarm;

	private ScheduleTime(Boolean isAllDay, LocalTime time, ScheduleAlram alarm) {
		validate(isAllDay, time, alarm);
		this.isAllDay = isAllDay;
		this.time = time;
		this.alarm = alarm;
	}

	public static ScheduleTime from(Boolean isAllDay, LocalTime time, ScheduleAlram alarm) {
		return new ScheduleTime(isAllDay, time, alarm);
	}

	private void validate(Boolean isAllDay, LocalTime time, ScheduleAlram alarm) {
		if (isAllDay) {
			if (time != null) {
				throw new VoException("종일 여부가 true 이면 시간은 null 이어야 합니다.");
			}
			if (alarm != null) {
				throw new VoException("종일 여부가 true 이면 알람은 null 이어야 합니다.");
			}
		} else if (time == null) {
			throw new VoException("종일 여부가 false 이면 시간은 필수입니다.");
		}
	}

}

package im.toduck.domain.schedule.persistence.vo;

import java.time.LocalTime;

import im.toduck.global.exception.VoException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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

	private ScheduleTime(Boolean isAllDay, LocalTime time) {
		validate(isAllDay, time);
		this.isAllDay = isAllDay;
		this.time = time;
	}

	public static ScheduleTime from(Boolean isAllDay, LocalTime time) {
		return new ScheduleTime(isAllDay, time);
	}

	private void validate(Boolean isAllDay, LocalTime time) {
		if (isAllDay && time != null) {
			throw new VoException("종일 여부가 true 이면 시간은 null 이어야 합니다.");
		}
		if (!isAllDay && time == null) {
			throw new VoException("종일 여부가 false 이면 시간은 필수입니다.");
		}
	}

}

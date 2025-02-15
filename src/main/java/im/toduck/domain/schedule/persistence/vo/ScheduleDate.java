package im.toduck.domain.schedule.persistence.vo;

import java.time.LocalDate;

import im.toduck.global.exception.VoException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ScheduleDate {
	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;

	private ScheduleDate(LocalDate startDate, LocalDate endDate) {
		validate(startDate, endDate);
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public static ScheduleDate from(LocalDate startDate, LocalDate endDate) {
		return new ScheduleDate(startDate, endDate);
	}

	private void validate(LocalDate startDate, LocalDate endDate) {
		if (startDate.isAfter(endDate)) {
			throw new VoException("시작일은 종료일보다 이전이어야 합니다.");
		}
	}

	public void changeEndDate(LocalDate localDate) {
		validate(this.startDate, localDate);
		this.endDate = localDate;
	}
}

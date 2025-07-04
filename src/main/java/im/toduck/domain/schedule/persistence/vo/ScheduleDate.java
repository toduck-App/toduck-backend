package im.toduck.domain.schedule.persistence.vo;

import java.time.LocalDate;

import im.toduck.global.exception.VoException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	public static ScheduleDate of(LocalDate startDate, LocalDate endDate) {
		return new ScheduleDate(startDate, endDate);
	}

	private void validate(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null) {
			throw new VoException("일정의 시작일과 종료일은 null이 될 수 없습니다.");
		}
		if (startDate.isAfter(endDate)) {
			throw new VoException("시작일은 종료일보다 이전이어야 합니다.");
		}
	}
}

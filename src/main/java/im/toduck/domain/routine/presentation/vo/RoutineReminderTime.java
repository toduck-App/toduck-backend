package im.toduck.domain.routine.presentation.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 루틴 리마인더 시간 Enum
 * <p>
 * 앱 호환성을 위해 예외적으로 API 응답 시에만 사용되는 Enum입니다.
 * DB에는 Integer 분 단위로 저장되지만, 클라이언트에게는 Enum 문자열로 반환합니다.
 * </p>
 */
@Getter
@RequiredArgsConstructor
public enum RoutineReminderTime {
	TEN_MINUTE(10),
	THIRTY_MINUTE(30),
	ONE_HOUR(60),
	ONE_DAY(1440);

	private final int minutes;

	/**
	 * DB에 저장된 분 단위 값을 Enum으로 변환
	 *
	 * @param minutes DB에 저장된 알림 시간 (분 단위)
	 * @return 해당하는 Enum 값, 0이거나 null이면 null 반환
	 */
	public static RoutineReminderTime fromMinutes(Integer minutes) {
		if (minutes == null || minutes == 0) {
			return null;
		}

		for (RoutineReminderTime reminderTime : values()) {
			if (reminderTime.minutes == minutes) {
				return reminderTime;
			}
		}

		// 정의되지 않은 값의 경우 null 반환
		return null;
	}
}

package im.toduck.domain.notification.domain.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 루틴 공유 마일스톤에 사용하는 데이터
 */
@Getter
@NoArgsConstructor
public class RoutineShareMilestoneData extends AbstractNotificationData {
	private String routineTitle;
	private Integer shareCount;

	private RoutineShareMilestoneData(String routineTitle, Integer shareCount) {
		this.routineTitle = routineTitle;
		this.shareCount = shareCount;
	}

	public static RoutineShareMilestoneData of(String routineTitle, Integer shareCount) {
		return new RoutineShareMilestoneData(routineTitle, shareCount);
	}
}

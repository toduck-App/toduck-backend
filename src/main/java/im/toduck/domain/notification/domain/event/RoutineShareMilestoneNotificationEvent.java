package im.toduck.domain.notification.domain.event;

import im.toduck.domain.notification.domain.data.RoutineShareMilestoneData;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoutineShareMilestoneNotificationEvent extends NotificationEvent<RoutineShareMilestoneData> {

	private RoutineShareMilestoneNotificationEvent(Long userId, RoutineShareMilestoneData data) {
		super(userId, NotificationType.ROUTINE_SHARE_MILESTONE, data);
	}

	public static RoutineShareMilestoneNotificationEvent of(
		Long userId,
		String routineTitle,
		Integer shareCount
	) {
		return new RoutineShareMilestoneNotificationEvent(
			userId,
			RoutineShareMilestoneData.of(routineTitle, shareCount)
		);
	}

	@Override
	public String getInAppTitle() {
		return "루틴 총 공유수가 " + getData().getShareCount() + "회를 돌파했어요! 🎉";
	}

	@Override
	public String getInAppBody() {
		return "나의 인기 루틴을 확인해보세요.";
	}

	@Override
	public String getPushTitle() {
		return getInAppTitle();
	}

	@Override
	public String getPushBody() {
		return getInAppBody();
	}

	@Override
	public String getActionUrl() {
		return "toduck://profile?userId=me";
	}
}

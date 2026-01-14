package im.toduck.domain.notification.domain.data;

import im.toduck.domain.badge.persistence.entity.Badge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BadgeAcquiredNotificationData extends AbstractNotificationData {
	private String badgeName;

	public static BadgeAcquiredNotificationData from(final Badge badge) {
		return new BadgeAcquiredNotificationData(badge.getName());
	}
}

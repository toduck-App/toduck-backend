package im.toduck.domain.notification.domain.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BadgeAcquiredNotificationData extends AbstractNotificationData {
	private String badgeName;

	public static BadgeAcquiredNotificationData from(final String badgeName) {
		return new BadgeAcquiredNotificationData(badgeName);
	}
}

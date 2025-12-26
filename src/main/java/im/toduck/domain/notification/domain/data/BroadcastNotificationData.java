package im.toduck.domain.notification.domain.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastNotificationData extends AbstractNotificationData {
	private String title;
	private String message;
	private String actionUrl;

	public static BroadcastNotificationData of(
		final String title,
		final String message,
		final String actionUrl
	) {
		return new BroadcastNotificationData(title, message, actionUrl);
	}
}

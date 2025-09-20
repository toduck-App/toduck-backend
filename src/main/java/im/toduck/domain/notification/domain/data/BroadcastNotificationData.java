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

	public static BroadcastNotificationData of(final String title, final String message) {
		return new BroadcastNotificationData(title, message);
	}
}

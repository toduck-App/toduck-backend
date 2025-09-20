package im.toduck.domain.backoffice.domain.event;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class BroadcastNotificationExecutionEvent {
	private final Long broadcastId;
	private final String title;
	private final String message;
	private final LocalDateTime occurredAt;

	public BroadcastNotificationExecutionEvent(final Long broadcastId, final String title, final String message) {
		this.broadcastId = broadcastId;
		this.title = title;
		this.message = message;
		this.occurredAt = LocalDateTime.now();
	}
}

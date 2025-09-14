package im.toduck.domain.backoffice.persistence.entity;

import java.time.LocalDateTime;

import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "broadcast_notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BroadcastNotification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 500)
	private String message;

	@Column(name = "scheduled_at")
	private LocalDateTime scheduledAt;

	@Column(name = "sent_at")
	private LocalDateTime sentAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BroadcastNotificationStatus status;

	@Column(name = "target_user_count")
	private Integer targetUserCount;

	@Column(name = "sent_user_count")
	private Integer sentUserCount;

	@Column(name = "job_key")
	private String jobKey;

	@Column(name = "failure_reason", length = 500)
	private String failureReason;

	@Builder
	private BroadcastNotification(
		final String title,
		final String message,
		final LocalDateTime scheduledAt,
		final BroadcastNotificationStatus status,
		final Integer targetUserCount,
		final String jobKey
	) {
		this.title = title;
		this.message = message;
		this.scheduledAt = scheduledAt;
		this.status = status;
		this.targetUserCount = targetUserCount;
		this.jobKey = jobKey;
	}

	public void markAsSending() {
		this.status = BroadcastNotificationStatus.SENDING;
	}

	public void markAsCompleted(final Integer sentUserCount) {
		this.status = BroadcastNotificationStatus.COMPLETED;
		this.sentUserCount = sentUserCount;
		this.sentAt = LocalDateTime.now();
	}

	public void markAsCancelled() {
		if (!status.canCancel()) {
			throw new IllegalStateException("취소할 수 없는 상태입니다: " + status);
		}
		this.status = BroadcastNotificationStatus.CANCELLED;
	}

	public void markAsFailed(final String failureReason) {
		this.status = BroadcastNotificationStatus.FAILED;
		this.failureReason = failureReason;
	}

	public boolean canCancel() {
		return status.canCancel();
	}
}

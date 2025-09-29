package im.toduck.domain.backoffice.persistence.entity;

public enum BroadcastNotificationStatus {
	SCHEDULED("예약 대기중"),
	SENDING("발송 중"),
	COMPLETED("발송 완료"),
	CANCELLED("예약 취소"),
	FAILED("발송 실패");

	private final String description;

	BroadcastNotificationStatus(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public boolean canCancel() {
		return this == SCHEDULED;
	}

	public boolean isCompleted() {
		return this == COMPLETED || this == CANCELLED || this == FAILED;
	}
}

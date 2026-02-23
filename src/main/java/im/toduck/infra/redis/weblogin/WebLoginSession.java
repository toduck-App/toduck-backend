package im.toduck.infra.redis.weblogin;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@RedisHash("webLoginSession")
@Getter
@ToString(of = {"sessionToken", "status", "approvedUserId"})
@EqualsAndHashCode(of = {"sessionToken"})
public class WebLoginSession {
	private static final long DEFAULT_TTL_SECONDS = 300;

	@Id
	private String sessionToken;
	private WebLoginSessionStatus status;
	private Long approvedUserId;
	private String approvedUserRole;

	@TimeToLive
	private long ttl = DEFAULT_TTL_SECONDS;

	protected WebLoginSession() {
	}

	@Builder
	private WebLoginSession(
		final String sessionToken,
		final WebLoginSessionStatus status,
		final Long approvedUserId,
		final String approvedUserRole,
		final Long ttl
	) {
		this.sessionToken = sessionToken;
		this.status = status;
		this.approvedUserId = approvedUserId;
		this.approvedUserRole = approvedUserRole;
		this.ttl = (ttl != null) ? ttl : DEFAULT_TTL_SECONDS;
	}

	public static WebLoginSession createPending(final String sessionToken) {
		return WebLoginSession.builder()
			.sessionToken(sessionToken)
			.status(WebLoginSessionStatus.PENDING)
			.build();
	}

	public void approve(final Long userId, final String role) {
		this.status = WebLoginSessionStatus.APPROVED;
		this.approvedUserId = userId;
		this.approvedUserRole = role;
	}

	public boolean isPending() {
		return this.status == WebLoginSessionStatus.PENDING;
	}

	public boolean isApproved() {
		return this.status == WebLoginSessionStatus.APPROVED;
	}
}

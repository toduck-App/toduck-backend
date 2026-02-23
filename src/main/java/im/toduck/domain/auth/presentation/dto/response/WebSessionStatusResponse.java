package im.toduck.domain.auth.presentation.dto.response;

import im.toduck.infra.redis.weblogin.WebLoginSessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "웹 로그인 세션 상태 확인 응답 DTO")
@Builder
public record WebSessionStatusResponse(
	@Schema(description = "세션 상태 (PENDING: 대기중, APPROVED: 승인됨, EXPIRED: 만료됨)", example = "APPROVED")
	WebLoginSessionStatus status,

	@Schema(description = "액세스 토큰 (승인된 경우에만 포함)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	String accessToken,

	@Schema(description = "사용자 ID (승인된 경우에만 포함)", example = "1")
	Long userId
) {
	public static WebSessionStatusResponse pending() {
		return WebSessionStatusResponse.builder()
			.status(WebLoginSessionStatus.PENDING)
			.build();
	}

	public static WebSessionStatusResponse approved(final String accessToken, final Long userId) {
		return WebSessionStatusResponse.builder()
			.status(WebLoginSessionStatus.APPROVED)
			.accessToken(accessToken)
			.userId(userId)
			.build();
	}

	public static WebSessionStatusResponse expired() {
		return WebSessionStatusResponse.builder()
			.status(WebLoginSessionStatus.EXPIRED)
			.build();
	}
}

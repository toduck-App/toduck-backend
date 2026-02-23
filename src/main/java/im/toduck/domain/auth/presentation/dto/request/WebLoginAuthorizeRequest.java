package im.toduck.domain.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "웹 로그인 승인 요청 DTO")
public record WebLoginAuthorizeRequest(
	@NotBlank(message = "세션 토큰은 필수입니다.")
	@Schema(description = "QR 코드에서 추출한 세션 토큰", example = "abc123xyz...")
	String sessionToken
) {
}

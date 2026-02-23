package im.toduck.domain.auth.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "웹 로그인 세션 생성 응답 DTO")
@Builder
public record WebSessionCreateResponse(
	@Schema(description = "세션 토큰 (폴링 시 사용)", example = "abc123xyz...")
	String sessionToken,

	@Schema(description = "QR 코드 PNG 이미지 (Base64 인코딩)", example = "data:image/png;base64,iVBORw0KGgo...")
	String qrImageBase64
) {
}

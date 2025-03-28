package im.toduck.domain.user.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "로그인 아이디 응답 DTO")
@Builder
public record LoginIdResponse(
	@Schema(description = "로그인 아이디", example = "toduck")
	String loginId
) {
}

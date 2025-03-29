package im.toduck.domain.mypage.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record NickNameResponse(
	@Schema(description = "자신의 닉네임", example = "뽀덕이")
	String nickname
) {
}

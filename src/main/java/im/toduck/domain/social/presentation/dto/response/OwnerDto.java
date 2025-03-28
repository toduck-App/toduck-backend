package im.toduck.domain.social.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record OwnerDto(
	@Schema(description = "작성자 ID", example = "1")
	Long ownerId,

	@Schema(description = "작성자 닉네임", example = "오리발")
	String nickname,

	@Schema(description = "작성자 프로필 이미지 url", example = "https://cdn.toduck.app/profile.jpg")
	String profileImageUrl
) {
}

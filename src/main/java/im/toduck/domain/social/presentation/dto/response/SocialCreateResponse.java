package im.toduck.domain.social.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SocialCreateResponse(
	@Schema(description = "생성된 소셜 게시글 ID", example = "1")
	Long socialId
) {
}

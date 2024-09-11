package im.toduck.domain.social.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record OwnerDto(
	@Schema(description = "작성자 ID", example = "1")
	Long id,
	@Schema(description = "작성자 닉네임", example = "오리발")
	String nickname
) {
}

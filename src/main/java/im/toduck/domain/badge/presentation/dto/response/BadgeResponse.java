package im.toduck.domain.badge.presentation.dto.response;

import im.toduck.domain.badge.persistence.entity.Badge;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "뱃지 정보 응답")
public record BadgeResponse(
	@Schema(description = "뱃지 ID")
	Long id,

	@Schema(description = "뱃지 코드")
	String code,

	@Schema(description = "뱃지 이름")
	String name,

	@Schema(description = "뱃지 설명")
	String description,

	@Schema(description = "뱃지 이미지 URL")
	String imageUrl
) {
	public static BadgeResponse from(final Badge badge) {
		return BadgeResponse.builder()
			.id(badge.getId())
			.code(badge.getCode().name())
			.name(badge.getName())
			.description(badge.getDescription())
			.imageUrl(badge.getImageUrl())
			.build();
	}
}

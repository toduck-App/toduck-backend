package im.toduck.domain.badge.presentation.dto.response;

import im.toduck.domain.badge.persistence.entity.Badge;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "뱃지 정보 응답")
public class BadgeResponse {
	@Schema(description = "뱃지 ID")
	private Long id;

	@Schema(description = "뱃지 코드")
	private String code;

	@Schema(description = "뱃지 이름")
	private String name;

	@Schema(description = "뱃지 설명")
	private String description;

	@Schema(description = "뱃지 이미지 URL")
	private String imageUrl;

	public static BadgeResponse from(Badge badge) {
		return BadgeResponse.builder()
			.id(badge.getId())
			.code(badge.getCode().name())
			.name(badge.getName())
			.description(badge.getDescription())
			.imageUrl(badge.getImageUrl())
			.build();
	}
}

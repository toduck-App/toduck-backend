package im.toduck.domain.badge.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "내 뱃지 목록 조회 응답")
public record BadgeListResponse(
	@Schema(description = "전체 뱃지 개수")
	long totalCount,

	@Schema(description = "대표 뱃지 ID (없으면 null)")
	Long representativeBadgeId,

	@Schema(description = "보유한 뱃지 리스트")
	List<BadgeResponse> ownedBadges
) {
	public static BadgeListResponse of(
		final long totalCount,
		final Long representativeBadgeId,
		final List<BadgeResponse> ownedBadges
	) {
		return BadgeListResponse.builder()
			.totalCount(totalCount)
			.representativeBadgeId(representativeBadgeId)
			.ownedBadges(ownedBadges)
			.build();
	}
}

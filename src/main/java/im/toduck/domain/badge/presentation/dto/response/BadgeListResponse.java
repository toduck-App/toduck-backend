package im.toduck.domain.badge.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "내 뱃지 목록 조회 응답")
public class BadgeListResponse {
	@Schema(description = "전체 뱃지 개수")
	private long totalCount;

	@Schema(description = "대표 뱃지 ID (없으면 null)")
	private Long representativeBadgeId;

	@Schema(description = "보유한 뱃지 리스트")
	private List<BadgeResponse> ownedBadges;

	public static BadgeListResponse of(long totalCount, Long representativeBadgeId, List<BadgeResponse> ownedBadges) {
		return BadgeListResponse.builder()
			.totalCount(totalCount)
			.representativeBadgeId(representativeBadgeId)
			.ownedBadges(ownedBadges)
			.build();
	}
}

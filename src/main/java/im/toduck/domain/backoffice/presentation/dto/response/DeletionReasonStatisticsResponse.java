package im.toduck.domain.backoffice.presentation.dto.response;

import java.util.Map;

import im.toduck.domain.mypage.persistence.entity.AccountDeletionReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "탈퇴 사유별 통계 응답 DTO")
@Builder
public record DeletionReasonStatisticsResponse(
	@Schema(description = "탈퇴 사유별 건수 통계")
	Map<AccountDeletionReason, Long> reasonCounts,

	@Schema(description = "총 탈퇴 회원 수", example = "150")
	long totalCount
) {
}

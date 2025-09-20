package im.toduck.domain.backoffice.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "회원 탈퇴 사유 목록 응답 DTO")
@Builder
public record AccountDeletionLogListResponse(
	@Schema(description = "탈퇴 사유 목록")
	List<AccountDeletionLogResponse> deletionLogs,

	@Schema(description = "총 탈퇴 회원 수", example = "150")
	long totalCount
) {
}

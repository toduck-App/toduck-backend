package im.toduck.domain.backoffice.common.mapper;

import java.util.List;
import java.util.Map;

import im.toduck.domain.backoffice.presentation.dto.response.AccountDeletionLogListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.AccountDeletionLogResponse;
import im.toduck.domain.backoffice.presentation.dto.response.DeletionReasonStatisticsResponse;
import im.toduck.domain.mypage.persistence.entity.AccountDeletionLog;
import im.toduck.domain.mypage.persistence.entity.AccountDeletionReason;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountDeletionLogMapper {

	public static AccountDeletionLogResponse toAccountDeletionLogResponse(final AccountDeletionLog log) {
		return AccountDeletionLogResponse.builder()
			.id(log.getId())
			.userId(log.getUser().getId())
			.reasonCode(log.getReasonCode())
			.reasonDescription(log.getReasonCode().description())
			.reasonText(log.getReasonText())
			.deletedAt(log.getCreatedAt())
			.build();
	}

	public static AccountDeletionLogListResponse toAccountDeletionLogListResponse(
		final List<AccountDeletionLog> logs
	) {
		List<AccountDeletionLogResponse> deletionLogs = logs.stream()
			.map(AccountDeletionLogMapper::toAccountDeletionLogResponse)
			.toList();

		return AccountDeletionLogListResponse.builder()
			.deletionLogs(deletionLogs)
			.totalCount(logs.size())
			.build();
	}

	public static DeletionReasonStatisticsResponse toDeletionReasonStatisticsResponse(
		final Map<AccountDeletionReason, Long> reasonCounts,
		final long totalCount
	) {
		return DeletionReasonStatisticsResponse.builder()
			.reasonCounts(reasonCounts)
			.totalCount(totalCount)
			.build();
	}
}

package im.toduck.domain.mypage.common.mapper;

import im.toduck.domain.mypage.persistence.entity.AccountDeletionLog;
import im.toduck.domain.mypage.presentation.dto.request.UserDeleteRequest;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountDeletionLogMapper {
	public static AccountDeletionLog toAccountDeletionLog(User user, UserDeleteRequest request) {
		return AccountDeletionLog.builder()
			.user(user)
			.reasonCode(request.reasonCode())
			.reasonText(request.reasonText())
			.build();
	}
}

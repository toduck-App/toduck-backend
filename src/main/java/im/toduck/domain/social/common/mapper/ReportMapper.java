package im.toduck.domain.social.common.mapper;

import im.toduck.domain.social.persistence.entity.Report;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportMapper {
	public static Report toReport(User user, Social social, String reason) {
		return Report.builder()
			.user(user)
			.social(social)
			.reason(reason)
			.build();
	}
}

package im.toduck.domain.badge.common.mapper;

import im.toduck.domain.badge.persistence.entity.Badge;
import im.toduck.domain.badge.persistence.entity.UserBadge;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserBadgeMapper {

	public static UserBadge toUserBadge(final User user, final Badge badge) {
		return UserBadge.builder()
			.user(user)
			.badge(badge)
			.build();
	}
}

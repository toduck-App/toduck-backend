package im.toduck.domain.badge.common.mapper;

import java.util.List;

import im.toduck.domain.badge.persistence.entity.Badge;
import im.toduck.domain.badge.persistence.entity.UserBadge;
import im.toduck.domain.badge.presentation.dto.response.BadgeListResponse;
import im.toduck.domain.badge.presentation.dto.response.BadgeResponse;
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

	public static BadgeListResponse toBadgeListResponse(final long totalCount, final List<UserBadge> userBadges) {
		Long representativeBadgeId = userBadges.stream()
			.filter(UserBadge::isRepresentative)
			.findFirst()
			.map(userBadge -> userBadge.getBadge().getId())
			.orElse(null);

		List<BadgeResponse> ownedBadges = userBadges.stream()
			.map(UserBadge::getBadge)
			.map(BadgeResponse::from)
			.toList();

		return BadgeListResponse.of(totalCount, representativeBadgeId, ownedBadges);
	}
}

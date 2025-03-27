package im.toduck.domain.user.common.mapper;

import im.toduck.domain.user.persistence.entity.Follow;
import im.toduck.domain.user.persistence.entity.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class FollowMapper {
	public static Follow toFollow(final User follower, final User followed) {
		return Follow.builder()
			.follower(follower)
			.followed(followed)
			.build();
	}
}

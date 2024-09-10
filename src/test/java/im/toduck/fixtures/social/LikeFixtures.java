package im.toduck.fixtures.social;

import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.user.persistence.entity.User;

public class LikeFixtures {

	/**
	 * Like 엔티티를 생성합니다.
	 *
	 * @param user       좋아요를 누른 사용자
	 * @param socialBoard 좋아요가 달린 게시글
	 * @return 생성된 Like 엔티티
	 */
	public static Like CREATE_LIKE(User user, Social socialBoard) {
		return Like.builder()
			.user(user)
			.social(socialBoard)
			.build();
	}
}

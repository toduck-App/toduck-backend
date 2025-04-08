package im.toduck.domain.user.persistence.repository.querydsl;

import im.toduck.domain.user.persistence.entity.User;

public interface UserRepositoryCustom {
	void updateNickname(User user, String nickname);

	void updateProfileImageUrl(User user, String imageUrl);
}

package im.toduck.domain.user.persistence.repository.querydsl;

import java.util.List;

import im.toduck.domain.user.persistence.entity.User;

public interface UserRepositoryCustom {
	void updateNickname(User user, String nickname);

	void updateProfileImageUrl(User user, String imageUrl);

	void softDelete(User user);

	List<User> findBlockedUsersByUser(User user);
}

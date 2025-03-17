package im.toduck.domain.user.persistence.repository.querydsl;

import im.toduck.domain.user.persistence.entity.User;

public interface UserRepositoryCustom {
	long updateUniqueNickname(User user, String nickname);
}

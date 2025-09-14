package im.toduck.domain.user.persistence.repository.querydsl;

import java.time.LocalDateTime;
import java.util.List;

import im.toduck.domain.user.persistence.entity.User;

public interface UserRepositoryCustom {
	List<Long> findAllActiveUserIds();

	void updateNickname(User user, String nickname);

	void updateProfileImageUrl(User user, String imageUrl);

	void softDelete(User user);

	List<User> findBlockedUsersByUser(User user);

	long countByCreatedAtBetween(final LocalDateTime startDateTime, final LocalDateTime endDateTime);

	long countByDeletedAtBetween(final LocalDateTime startDateTime, final LocalDateTime endDateTime);
}

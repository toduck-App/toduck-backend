package im.toduck.domain.user.persistence.repository.querydsl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.entity.UserRole;
import im.toduck.global.persistence.projection.DailyCount;

public interface UserRepositoryCustom {
	List<Long> findAllActiveUserIds();

	List<User> findAllActiveUsers();

	void updateNickname(User user, String nickname);

	void updateProfileImageUrl(User user, String imageUrl);

	void softDelete(User user);

	List<User> findBlockedUsersByUser(User user);

	long countByCreatedAtBetween(final LocalDateTime startDateTime, final LocalDateTime endDateTime);

	long countByDeletedAtBetween(final LocalDateTime startDateTime, final LocalDateTime endDateTime);

	long countByDeletedAtIsNotNull();

	Page<User> findUsersWithFilters(
		final String keyword,
		final String searchType,
		final String status,
		final UserRole role,
		final String provider,
		final String sortBy,
		final String sortDirection,
		final Pageable pageable
	);

	long countByProvider(final String provider);

	List<DailyCount> countNewUsersByDateBetweenGroupByDate(
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	);

	List<DailyCount> countDeletedUsersByDateBetweenGroupByDate(
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	);
}

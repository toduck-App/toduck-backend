package im.toduck.domain.user.persistence.repository.querydsl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.domain.user.persistence.entity.QBlock;
import im.toduck.domain.user.persistence.entity.QUser;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.entity.UserRole;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QUser qUser = QUser.user;
	private final QBlock qBlock = QBlock.block;

	@Override
	public List<Long> findAllActiveUserIds() {
		return queryFactory.select(qUser.id)
			.from(qUser)
			.where(qUser.deletedAt.isNull())
			.fetch();
	}

	@Override
	public List<User> findAllActiveUsers() {
		return queryFactory.selectFrom(qUser)
			.where(qUser.deletedAt.isNull())
			.fetch();
	}

	@Override
	public void updateNickname(User user, String nickname) {
		queryFactory.update(qUser)
			.set(qUser.nickname, nickname)
			.where(qUser.id.eq(user.getId()))
			.execute();
	}

	@Override
	public void updateProfileImageUrl(User user, String imageUrl) {
		queryFactory.update(qUser)
			.set(qUser.imageUrl, imageUrl)
			.where(qUser.id.eq(user.getId()))
			.execute();
	}

	@Override
	public void softDelete(User user) {
		queryFactory.update(qUser)
			.set(qUser.nickname, (String)null)
			.set(qUser.imageUrl, (String)null)
			.set(qUser.phoneNumber, (String)null)
			.set(qUser.loginId, (String)null)
			.set(qUser.password, (String)null)
			.set(qUser.email, (String)null)
			.set(qUser.deletedAt, LocalDateTime.now())
			.where(qUser.id.eq(user.getId()))
			.execute();
	}

	@Override
	public List<User> findBlockedUsersByUser(User user) {
		return queryFactory.select(qBlock.blocked)
			.from(qBlock)
			.where(
				qBlock.blocker.eq(user),
				qBlock.blocked.deletedAt.isNull()
			)
			.fetch();
	}

	@Override
	public long countByCreatedAtBetween(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
		Long count = queryFactory.select(qUser.count())
			.from(qUser)
			.where(qUser.createdAt.between(startDateTime, endDateTime))
			.fetchOne();

		return count != null ? count : 0L;
	}

	@Override
	public long countByDeletedAtBetween(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
		Long count = queryFactory.select(qUser.count())
			.from(qUser)
			.where(qUser.deletedAt.between(startDateTime, endDateTime))
			.fetchOne();

		return count != null ? count : 0L;
	}

	@Override
	public long countByDeletedAtIsNotNull() {
		Long count = queryFactory.select(qUser.count())
			.from(qUser)
			.where(qUser.deletedAt.isNotNull())
			.fetchOne();

		return count != null ? count : 0L;
	}

	@Override
	public Page<User> findUsersWithFilters(
		final String keyword,
		final String searchType,
		final String status,
		final UserRole role,
		final String provider,
		final String sortBy,
		final String sortDirection,
		final Pageable pageable
	) {
		JPAQuery<User> query = queryFactory
			.selectFrom(qUser)
			.where(
				userNotDeleted(),
				keywordCondition(keyword, searchType),
				statusCondition(status),
				roleCondition(role),
				providerCondition(provider)
			);

		applySorting(query, sortBy, sortDirection);

		List<User> users = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long totalCount = getTotalCount(keyword, searchType, status, role, provider);

		return new PageImpl<>(users, pageable, totalCount);
	}

	private BooleanExpression userNotDeleted() {
		return qUser.deletedAt.isNull();
	}

	private BooleanExpression keywordCondition(final String keyword, final String searchType) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return null;
		}

		if (searchType == null || searchType.trim().isEmpty()) {
			return qUser.nickname.containsIgnoreCase(keyword)
				.or(qUser.email.containsIgnoreCase(keyword))
				.or(qUser.phoneNumber.containsIgnoreCase(keyword))
				.or(qUser.loginId.containsIgnoreCase(keyword));
		}

		return switch (searchType.toLowerCase()) {
			case "nickname" -> qUser.nickname.containsIgnoreCase(keyword);
			case "email" -> qUser.email.containsIgnoreCase(keyword);
			case "phone" -> qUser.phoneNumber.containsIgnoreCase(keyword);
			case "loginid" -> qUser.loginId.containsIgnoreCase(keyword);
			default -> qUser.nickname.containsIgnoreCase(keyword)
				.or(qUser.email.containsIgnoreCase(keyword))
				.or(qUser.phoneNumber.containsIgnoreCase(keyword))
				.or(qUser.loginId.containsIgnoreCase(keyword));
		};
	}

	private BooleanExpression statusCondition(final String status) {
		if (status == null || status.trim().isEmpty() || "all".equalsIgnoreCase(status)) {
			return null;
		}

		LocalDateTime now = LocalDateTime.now();

		return switch (status.toLowerCase()) {
			case "suspended" -> qUser.suspendedUntil.isNotNull()
				.and(qUser.suspendedUntil.after(now));
			case "active" -> qUser.suspendedUntil.isNull()
				.or(qUser.suspendedUntil.before(now));
			default -> null;
		};
	}

	private BooleanExpression roleCondition(final UserRole role) {
		if (role == null) {
			return null;
		}
		return qUser.role.eq(role);
	}

	private BooleanExpression providerCondition(final String providerString) {
		if (providerString == null || providerString.trim().isEmpty()) {
			return null;
		}

		if ("GENERAL".equalsIgnoreCase(providerString)) {
			return qUser.provider.isNull();
		}

		try {
			OAuthProvider provider = OAuthProvider.valueOf(providerString.toUpperCase());
			return qUser.provider.eq(provider);
		} catch (IllegalArgumentException e) {
			return null; // 잘못된 provider 값인 경우 무시
		}
	}

	private void applySorting(final JPAQuery<User> query, final String sortBy, final String sortDirection) {
		Order order = "desc".equalsIgnoreCase(sortDirection) ? Order.DESC : Order.ASC;

		OrderSpecifier<?> orderSpecifier = switch (sortBy != null ? sortBy.toLowerCase() : "createdat") {
			case "nickname" -> new OrderSpecifier<>(order, qUser.nickname);
			case "email" -> new OrderSpecifier<>(order, qUser.email);
			case "role" -> new OrderSpecifier<>(order, qUser.role);
			case "suspendedat" -> new OrderSpecifier<>(order, qUser.suspendedUntil);
			case "updatedat" -> new OrderSpecifier<>(order, qUser.updatedAt);
			default -> new OrderSpecifier<>(order, qUser.createdAt);
		};

		query.orderBy(orderSpecifier);
	}

	private long getTotalCount(
		final String keyword,
		final String searchType,
		final String status,
		final UserRole role,
		final String provider
	) {
		Long count = queryFactory
			.select(qUser.count())
			.from(qUser)
			.where(
				userNotDeleted(),
				keywordCondition(keyword, searchType),
				statusCondition(status),
				roleCondition(role),
				providerCondition(provider)
			)
			.fetchOne();

		return count != null ? count : 0L;
	}

	@Override
	public long countByProvider(final String provider) {
		Long count = queryFactory
			.select(qUser.count())
			.from(qUser)
			.where(
				userNotDeleted(),
				providerCondition(provider)
			)
			.fetchOne();

		return count != null ? count : 0L;
	}
}

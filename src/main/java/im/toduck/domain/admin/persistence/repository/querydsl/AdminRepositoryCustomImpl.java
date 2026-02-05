package im.toduck.domain.admin.persistence.repository.querydsl;

import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.admin.persistence.entity.Admin;
import im.toduck.domain.admin.persistence.entity.QAdmin;
import im.toduck.domain.user.persistence.entity.QUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminRepositoryCustomImpl implements AdminRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QAdmin admin = QAdmin.admin;
	private final QUser user = QUser.user;

	@Override
	public Optional<Admin> findActiveAdminByUserId(final Long userId) {
		Admin result = queryFactory
			.selectFrom(admin)
			.join(admin.user, user).fetchJoin()
			.where(
				user.id.eq(userId),
				admin.deletedAt.isNull()
			)
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public List<Admin> findAllActiveAdmins() {
		return queryFactory
			.selectFrom(admin)
			.where(admin.deletedAt.isNull())
			.fetch();
	}

	@Override
	public Optional<Admin> findByUserIdIncludeDeleted(final Long userId) {
		Admin result = queryFactory
			.selectFrom(admin)
			.where(
				admin.user.id.eq(userId)
			)
			.fetchOne();

		return Optional.ofNullable(result);
	}
}

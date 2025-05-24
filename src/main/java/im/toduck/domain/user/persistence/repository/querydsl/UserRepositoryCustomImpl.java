package im.toduck.domain.user.persistence.repository.querydsl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.user.persistence.entity.QBlock;
import im.toduck.domain.user.persistence.entity.QUser;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QUser qUser = QUser.user;
	private final QBlock qBlock = QBlock.block;

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
}

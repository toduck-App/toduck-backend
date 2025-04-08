package im.toduck.domain.user.persistence.repository.querydsl;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.user.persistence.entity.QUser;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QUser qUser = QUser.user;

	@Override
	public void updateNickname(User user, String nickname) {
		queryFactory.update(qUser)
			.set(qUser.nickname, nickname)
			.where(qUser.id.eq(user.getId()))
			.execute();
	}
}

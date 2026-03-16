package im.toduck.domain.badge.domain.service;

import static im.toduck.fixtures.badge.BadgeFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static im.toduck.global.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.badge.persistence.entity.Badge;
import im.toduck.domain.badge.persistence.entity.UserBadge;
import im.toduck.domain.user.persistence.entity.User;

@Transactional
class BadgeServiceTest extends ServiceTest {

	@Autowired
	BadgeService badgeService;

	private User user;
	private Badge badge;

	@BeforeEach
	void setUp() {
		user = testFixtureBuilder.buildUser(GENERAL_USER());
		badge = testFixtureBuilder.buildBadge(BABY_DUCK_BADGE());
	}

	@Test
	@DisplayName("배지를 처음 획득하면 UserBadge를 반환한다")
	void grantBadge_new() {
		// when
		UserBadge result = badgeService.grantBadge(user, badge.getCode());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getBadge()).isEqualTo(badge);
		assertThat(result.getUser()).isEqualTo(user);
	}

	@Test
	@DisplayName("이미 보유한 배지를 획득하려 하면 ALREADY_ACQUIRED_BADGE 예외가 발생한다")
	void grantBadge_duplicate() {
		// given
		badgeService.grantBadge(user, badge.getCode());

		// when & then
		assertThatThrownBy(() -> badgeService.grantBadge(user, badge.getCode()))
			.isInstanceOf(im.toduck.global.exception.CommonException.class)
			.hasMessageContaining(ALREADY_ACQUIRED_BADGE.getMessage());
	}
}

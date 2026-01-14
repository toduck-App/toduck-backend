package im.toduck.domain.badge.domain.service;

import static im.toduck.fixtures.badge.BadgeFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

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
		Optional<UserBadge> result = badgeService.grantBadge(user, badge.getCode());

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getBadge()).isEqualTo(badge);
		assertThat(result.get().getUser()).isEqualTo(user);
	}

	@Test
	@DisplayName("이미 보유한 배지를 획득하려 하면 Empty를 반환한다")
	void grantBadge_duplicate() {
		// given
		badgeService.grantBadge(user, badge.getCode());

		// when
		Optional<UserBadge> result = badgeService.grantBadge(user, badge.getCode());

		// then
		assertThat(result).isEmpty();
	}
}

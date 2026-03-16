package im.toduck.domain.badge.domain.usecase;

import static im.toduck.fixtures.badge.BadgeFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.badge.domain.service.BadgeService;
import im.toduck.domain.badge.persistence.entity.Badge;
import im.toduck.domain.badge.persistence.entity.UserBadge;
import im.toduck.domain.badge.presentation.dto.response.BadgeResponse;
import im.toduck.domain.user.persistence.entity.User;

@Transactional
class BadgeUseCaseTest extends ServiceTest {

	@Autowired
	BadgeUseCase badgeUseCase;

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
	@DisplayName("사용자가 확인하지 않은 배지 목록을 조회하고 읽음 처리한다")
	void getNewBadges_success() {
		// given
		badgeService.grantBadge(user, badge.getCode());

		// when
		List<BadgeResponse> newBadges = badgeUseCase.getNewBadges(user.getId());

		// then
		assertThat(newBadges).hasSize(1);
		assertThat(newBadges.get(0).code()).isEqualTo(badge.getCode().name());

		List<UserBadge> unseenBadges = badgeService.getUnseenBadges(user);
		assertThat(unseenBadges).isEmpty();
	}

	@Test
	@DisplayName("확인하지 않은 배지가 없으면 빈 목록을 반환한다")
	void getNewBadges_empty() {
		// given
		badgeService.grantBadge(user, badge.getCode());
		badgeUseCase.getNewBadges(user.getId());

		// when
		List<BadgeResponse> newBadges = badgeUseCase.getNewBadges(user.getId());

		// then
		assertThat(newBadges).isEmpty();
	}
}

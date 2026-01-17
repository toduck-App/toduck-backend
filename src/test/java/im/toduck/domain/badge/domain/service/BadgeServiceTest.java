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
import im.toduck.global.exception.CommonException;

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
			.isInstanceOf(CommonException.class)
			.hasMessageContaining(ALREADY_ACQUIRED_BADGE.getMessage());
	}

	@Test
	@DisplayName("보유한 배지로 대표 뱃지를 설정할 수 있다.")
	void setRepresentativeBadge_success() {
		// given
		UserBadge userBadge = badgeService.grantBadge(user, badge.getCode());

		// when
		badgeService.setRepresentativeBadge(user, badge.getId());

		// then
		assertThat(userBadge.isRepresentative()).isTrue();
	}

	@Test
	@DisplayName("기존 대표 배지가 있을 경우, 새로운 배지로 대표가 변경되고 기존 배지는 해제된다")
	void setRepresentativeBadge_change() {
		// given
		// 1. 첫 번째 뱃지 획득 및 대표 설정
		UserBadge oldBadge = UserBadge.builder().user(user).badge(badge).build();
		testFixtureBuilder.buildUserBadge(oldBadge, true);

		// 2. 두 번째 뱃지 생성 및 획득
		Badge newBadge = testFixtureBuilder.buildBadge(PERFECTIONIST_BADGE());
		UserBadge newUserBadge = badgeService.grantBadge(user, newBadge.getCode());

		// when
		badgeService.setRepresentativeBadge(user, newBadge.getId());

		// then
		assertThat(oldBadge.isRepresentative()).isFalse();
		assertThat(newUserBadge.isRepresentative()).isTrue();
	}

	@Test
	@DisplayName("보유하지 않은 배지를 대표로 설정하려 하면 NOT_OWNED_BADGE 예외가 발생한다")
	void setRepresentativeBadge_notOwned() {
		// given
		Badge notOwnedBadge = testFixtureBuilder.buildBadge(PERFECTIONIST_BADGE());

		// when & then
		assertThatThrownBy(() -> badgeService.setRepresentativeBadge(user, notOwnedBadge.getId()))
			.isInstanceOf(CommonException.class)
			.hasMessageContaining(NOT_OWNED_BADGE.getMessage());
	}
}

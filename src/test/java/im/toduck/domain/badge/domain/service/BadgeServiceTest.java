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
import im.toduck.domain.badge.presentation.dto.response.BadgeListResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;

@Transactional
class BadgeServiceTest extends ServiceTest {

	@Autowired
	BadgeService badgeService;

	private User USER;

	@BeforeEach
	void setUp() {
		USER = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Test
	@DisplayName("배지를 처음 획득하면 UserBadge를 반환한다")
	void grantBadge_new() {
		// given
		Badge BABY_DUCK_BADGE = testFixtureBuilder.buildBadge(BABY_DUCK_BADGE());

		// when
		UserBadge result = badgeService.grantBadge(USER, BABY_DUCK_BADGE.getCode());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getBadge()).isEqualTo(BABY_DUCK_BADGE);
		assertThat(result.getUser()).isEqualTo(USER);
	}

	@Test
	@DisplayName("이미 보유한 배지를 획득하려 하면 ALREADY_ACQUIRED_BADGE 예외가 발생한다")
	void grantBadge_duplicate() {
		// given
		Badge BABY_DUCK_BADGE = testFixtureBuilder.buildBadge(BABY_DUCK_BADGE());
		badgeService.grantBadge(USER, BABY_DUCK_BADGE.getCode());

		// when & then
		assertThatThrownBy(() -> badgeService.grantBadge(USER, BABY_DUCK_BADGE.getCode()))
			.isInstanceOf(CommonException.class)
			.hasMessageContaining(ALREADY_ACQUIRED_BADGE.getMessage());
	}

	@Test
	@DisplayName("보유한 배지로 대표 뱃지를 설정할 수 있다.")
	void setRepresentativeBadge_success() {
		// given
		Badge BABY_DUCK_BADGE = testFixtureBuilder.buildBadge(BABY_DUCK_BADGE());
		UserBadge userBadge = badgeService.grantBadge(USER, BABY_DUCK_BADGE.getCode());

		// when
		badgeService.setRepresentativeBadge(USER, BABY_DUCK_BADGE.getId());

		// then
		assertThat(userBadge.isRepresentative()).isTrue();
	}

	@Test
	@DisplayName("기존 대표 배지가 있을 경우, 새로운 배지로 대표가 변경되고 기존 배지는 해제된다")
	void setRepresentativeBadge_change() {
		// given
		Badge oldBadge = testFixtureBuilder.buildBadge(BABY_DUCK_BADGE());
		// 1. 첫 번째 뱃지 획득 및 대표 설정
		UserBadge oldUserBadge = testFixtureBuilder.buildUserBadge(USER, oldBadge, true);

		// 2. 두 번째 뱃지 생성 및 획득
		Badge newBadge = testFixtureBuilder.buildBadge(PERFECTIONIST_BADGE());
		UserBadge newUserBadge = testFixtureBuilder.buildUserBadge(USER, newBadge, false);

		// when
		badgeService.setRepresentativeBadge(USER, newBadge.getId());

		// then
		assertThat(oldUserBadge.isRepresentative()).isFalse();
		assertThat(newUserBadge.isRepresentative()).isTrue();
	}

	@Test
	@DisplayName("보유하지 않은 배지를 대표로 설정하려 하면 NOT_OWNED_BADGE 예외가 발생한다")
	void setRepresentativeBadge_notOwned() {
		// given
		Badge notOwnedBadge = testFixtureBuilder.buildBadge(PERFECTIONIST_BADGE());

		// when & then
		assertThatThrownBy(() -> badgeService.setRepresentativeBadge(USER, notOwnedBadge.getId()))
			.isInstanceOf(CommonException.class)
			.hasMessageContaining(NOT_OWNED_BADGE.getMessage());
	}

	@Test
	@DisplayName("내 배지 목록을 조회하면 전체 개수, 대표 뱃지 ID, 보유 뱃지 리스트를 반환한다")
	void getMyBadgeList() {
		// given
		// 1. 뱃지 2개 생성 (전체 뱃지 개수: 2)
		Badge firstBadge = testFixtureBuilder.buildBadge(BABY_DUCK_BADGE());
		Badge secondBadge = testFixtureBuilder.buildBadge(PERFECTIONIST_BADGE());

		// 2. 사용자에게 첫 번째 뱃지 지급 및 대표 설정
		UserBadge userBadge1 = testFixtureBuilder.buildUserBadge(USER, firstBadge, true);
		// 3. 사용자에게 두 번째 뱃지 지급 (대표 아님)
		UserBadge userBadge2 = testFixtureBuilder.buildUserBadge(USER, secondBadge, false);

		// when
		BadgeListResponse response = badgeService.getMyBadgeList(USER);

		// then
		assertThat(response.totalCount()).isEqualTo(2); // 전체 뱃지 종류 수
		assertThat(response.representativeBadgeId()).isEqualTo(firstBadge.getId());
		assertThat(response.ownedBadges()).hasSize(2)
			.extracting("id")
			.containsExactlyInAnyOrder(firstBadge.getId(), secondBadge.getId());
	}
}

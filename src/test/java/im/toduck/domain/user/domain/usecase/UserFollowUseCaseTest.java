package im.toduck.domain.user.domain.usecase;

import static im.toduck.global.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.ServiceTest;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.repository.FollowRepository;
import im.toduck.fixtures.user.UserFixtures;
import im.toduck.global.exception.CommonException;

class UserFollowUseCaseTest extends ServiceTest {

	@Autowired
	private UserFollowUseCase userFollowUseCase;

	@Autowired
	private FollowRepository followRepository;

	private User follower;
	private User followedUser;

	@BeforeEach
	void setUp() {
		follower = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
		followedUser = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
	}

	@Nested
	class FollowUserTest {

		@Test
		void 유저_팔로우를_할_수_있다() {
			// when
			userFollowUseCase.followUser(follower.getId(), followedUser.getId());

			// then
			assertThat(followRepository.existsByFollowerAndFollowed(follower, followedUser)).isTrue();
		}

		@Test
		void 이미_팔로우된_유저를_다시_팔로우_시도하면_실패한다() {
			// given
			userFollowUseCase.followUser(follower.getId(), followedUser.getId());

			// when & then
			assertThatThrownBy(() -> userFollowUseCase.followUser(follower.getId(), followedUser.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(ALREADY_FOLLOWING.getMessage());
		}

		@Test
		void 자기_자신을_팔로우_시도하면_실패한다() {
			// when & then
			assertThatThrownBy(() -> userFollowUseCase.followUser(follower.getId(), follower.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(CANNOT_FOLLOW_SELF.getMessage());
		}
	}

	@Nested
	class UnfollowUserTest {
		User anotherUser = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());

		@BeforeEach
		void setUp() {
			userFollowUseCase.followUser(follower.getId(), followedUser.getId());
		}

		@Test
		void 유저_언팔로우를_할_수_있다() {
			// when
			userFollowUseCase.unfollowUser(follower.getId(), followedUser.getId());

			// then
			assertThat(followRepository.existsByFollowerAndFollowed(follower, followedUser)).isFalse();
		}

		@Test
		void 팔로우_관계가_존재하지_않으면_언팔로우에_실패한다() {
			// when & then
			assertThatThrownBy(() -> userFollowUseCase.unfollowUser(follower.getId(), anotherUser.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(NOT_FOUND_FOLLOW.getMessage());
		}
	}
}

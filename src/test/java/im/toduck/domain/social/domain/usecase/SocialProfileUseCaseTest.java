package im.toduck.domain.social.domain.usecase;

import static im.toduck.fixtures.user.UserFixtures.*;
import static im.toduck.global.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.UseCaseTest;
import im.toduck.domain.social.presentation.dto.response.SocialProfileResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;

public class SocialProfileUseCaseTest extends UseCaseTest {

	@Autowired
	private SocialProfileUseCase socialProfileUseCase;

	private User profileUser;
	private User authUser;

	@BeforeEach
	void setUp() {
		profileUser = testFixtureBuilder.buildUser(GENERAL_USER());
		authUser = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Nested
	@DisplayName("SocialProfile 조회시")
	class GetUserProfileTests {

		@Test
		void 프로필_조회를_할_수_있다() {
			// given
			int followingCount = 3;
			int followerCount = 2;
			int postCount = 4;

			// profileUser가 팔로우한 수 생성 (followingCount)
			for (int i = 0; i < followingCount; i++) {
				User followed = testFixtureBuilder.buildUser(GENERAL_USER());
				testFixtureBuilder.buildFollow(profileUser, followed);
			}

			// profileUser를 팔로우하는 수 생성 (followerCount)
			for (int i = 0; i < followerCount; i++) {
				User follower = testFixtureBuilder.buildUser(GENERAL_USER());
				testFixtureBuilder.buildFollow(follower, profileUser);
			}

			// profileUser가 작성한 게시글 생성 (postCount)
			for (int i = 0; i < postCount; i++) {
				testFixtureBuilder.buildSocial(
					im.toduck.fixtures.social.SocialFixtures.SINGLE_SOCIAL(profileUser, false));
			}

			// when
			SocialProfileResponse response = socialProfileUseCase.getUserProfile(profileUser.getId(), authUser.getId());

			// then
			assertThat(response).isNotNull();
			assertThat(response.nickname()).isEqualTo(profileUser.getNickname());
			assertThat(response.followingCount()).isEqualTo(followingCount);
			assertThat(response.followerCount()).isEqualTo(followerCount);
			assertThat(response.postCount()).isEqualTo(postCount);
			assertThat(response.isMe()).isFalse();
		}

		@Test
		void 존재하지_않는_사용자_프로필_조회에_실패한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(() -> socialProfileUseCase.getUserProfile(nonExistentUserId, authUser.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(NOT_FOUND_USER.getMessage());
		}
	}
}

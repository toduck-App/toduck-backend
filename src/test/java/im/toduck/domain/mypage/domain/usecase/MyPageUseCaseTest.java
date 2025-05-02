package im.toduck.domain.mypage.domain.usecase;

import static im.toduck.fixtures.user.BlockFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.ServiceTest;
import im.toduck.domain.mypage.persistence.entity.AccountDeletionLog;
import im.toduck.domain.mypage.persistence.entity.AccountDeletionReason;
import im.toduck.domain.mypage.persistence.repository.AccountDeletionLogRepository;
import im.toduck.domain.mypage.presentation.dto.request.NickNameUpdateRequest;
import im.toduck.domain.mypage.presentation.dto.request.ProfileImageUpdateRequest;
import im.toduck.domain.mypage.presentation.dto.request.UserDeleteRequest;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.repository.SocialRepository;
import im.toduck.domain.user.persistence.entity.Block;
import im.toduck.domain.user.persistence.entity.Follow;
import im.toduck.domain.mypage.presentation.dto.response.BlockedUsersResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.repository.BlockRepository;
import im.toduck.domain.user.persistence.repository.FollowRepository;
import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.fixtures.social.SocialFixtures;
import im.toduck.fixtures.user.BlockFixtures;
import im.toduck.fixtures.user.UserFixtures;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;

public class MyPageUseCaseTest extends ServiceTest {

	@Autowired
	private MyPageUseCase myPageUseCase;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountDeletionLogRepository accountDeletionLogRepository;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private BlockRepository blockRepository;

	@Autowired
	private SocialRepository socialRepository;

	@Nested
	@DisplayName("닉네임 변경 시")
	class UpdateNickname {
		@Test
		public void 닉네임을_변경할_수_있다() {
			// given
			User user = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
			String nickname = "변경할닉네임";

			// when
			NickNameUpdateRequest request = new NickNameUpdateRequest(nickname);
			myPageUseCase.updateNickname(user.getId(), request);

			// then
			assertThat(userRepository.findById(user.getId()).get().getNickname()).isEqualTo(nickname);
		}

		@Test
		public void 이미_존재하는_닉네임으로_변경할_수_없다() {
			// given
			User existingUser = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
			User updatingUser = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());

			String nickname = existingUser.getNickname();

			// when & then
			NickNameUpdateRequest request = new NickNameUpdateRequest(nickname);

			assertThatThrownBy(() -> myPageUseCase.updateNickname(updatingUser.getId(), request))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.EXISTS_USER_NICKNAME.getMessage());
		}
	}

	@Nested
	@DisplayName("프로필 이미지 변경 시")
	class UpdateProfileImage {
		@Test
		public void 프로필_사진을_변경할_수_있다() {
			// given
			User user = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
			String imageUrl = "https://cdn.toduck.app/example.jpg";

			// when
			ProfileImageUpdateRequest request = new ProfileImageUpdateRequest(imageUrl);
			myPageUseCase.updateProfileImage(user.getId(), request);

			// then
			assertThat(userRepository.findById(user.getId()).get().getImageUrl()).isEqualTo(imageUrl);
		}
	}

	@Nested
	@DisplayName("회원 탈퇴 시")
	class DeleteAccount {
		private User user;
		private UserDeleteRequest request;

		@BeforeEach
		void setUp() {
			user = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());

			AccountDeletionReason reasonCode = AccountDeletionReason.OTHER;
			String reasonText = "파이팅";
			request = new UserDeleteRequest(reasonCode, reasonText);
		}

		@Test
		public void 회원_탈퇴를_할_수_있다() {
			// when
			myPageUseCase.deleteAccount(user.getId(), request);

			// then
			assertSoftly(softly -> {
				User deletedUser = userRepository.findById(user.getId()).get();
				assertThat(deletedUser.getNickname()).isEqualTo(User.DELETED_MEMBER_NICKNAME);
				assertThat(deletedUser.getImageUrl()).isNull();
				assertThat(deletedUser.getDeletedAt()).isNotNull();

				AccountDeletionLog log = accountDeletionLogRepository.findByUser(deletedUser).get();
				assertThat(log.getReasonCode()).isEqualTo(request.reasonCode());
				assertThat(log.getReasonText()).isEqualTo(request.reasonText());
			});
		}

		@Test
		public void 회원_탈퇴_시_공개된_관련_데이터가_삭제된다() {
			// given
			User followingUser = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
			Follow following = testFixtureBuilder.buildFollow(user, followingUser);

			User blockedUser = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
			Block block = testFixtureBuilder.buildBlock(BLOCK_USER(user, blockedUser));

			Social social = testFixtureBuilder.buildSocial(SocialFixtures.SINGLE_SOCIAL(user, false));

			// when
			myPageUseCase.deleteAccount(user.getId(), request);

			// then
			assertSoftly(softly -> {
				assertThat(followRepository.findById(following.getId())).isEmpty();
				assertThat(blockRepository.findById(block.getId())).isEmpty();
				assertThat(socialRepository.findById(social.getId())).isEmpty();
			});
		}

		@Test
		public void 회원_탈퇴_시_비공개된_관련_데이터가_삭제된다() {
			// TODO. diary, routine, concentration, schedule 관련 테스트
		}
	}

  @Nested
	@DisplayName("차단한 유저 조회 시")
	class GetBlockedUsers {

		private User BLOCKER;
		private User BLOCKED_1;
		private User BLOCKED_2;
		private User BLOCKED_DELETED;

		@BeforeEach
		void setUp() {
			BLOCKER = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
			BLOCKED_1 = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
			BLOCKED_2 = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
			BLOCKED_DELETED = testFixtureBuilder.buildDeletedUser(
				UserFixtures.GENERAL_USER(),
				LocalDateTime.now()
			);
		}

		@Test
		void 차단한_유저를_조회할_수_있다() {
			// given
			testFixtureBuilder.buildBlock(BlockFixtures.BLOCK_USER(BLOCKER, BLOCKED_1));
			testFixtureBuilder.buildBlock(BlockFixtures.BLOCK_USER(BLOCKER, BLOCKED_2));
			testFixtureBuilder.buildBlock(BlockFixtures.BLOCK_USER(BLOCKER, BLOCKED_DELETED));

			// when
			BlockedUsersResponse response = myPageUseCase.getBlockedUsers(BLOCKER.getId());

			// then
			assertThat(response.blockedUsers()).hasSize(2);
			assertThat(response.blockedUsers())
				.extracting("userId")
				.containsExactlyInAnyOrder(BLOCKED_1.getId(), BLOCKED_2.getId());
		}

		@Test
		void 차단한_유저가_없으면_빈_리스트를_반환한다() {
			// when
			BlockedUsersResponse response = myPageUseCase.getBlockedUsers(BLOCKER.getId());

			// then
			assertThat(response.blockedUsers()).isEmpty();
		}
	}

}

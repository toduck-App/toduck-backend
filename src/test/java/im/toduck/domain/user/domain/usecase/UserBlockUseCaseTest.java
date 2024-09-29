package im.toduck.domain.user.domain.usecase;

import static im.toduck.global.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.ServiceTest;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.repository.BlockRepository;
import im.toduck.fixtures.user.UserFixtures;
import im.toduck.global.exception.CommonException;

class UserBlockUseCaseTest extends ServiceTest {

	@Autowired
	private UserBlockUseCase userBlockUseCase;

	@Autowired
	private BlockRepository blockRepository;

	private User blocker;
	private User blockedUser;

	@BeforeEach
	void setUp() {
		blocker = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
		blockedUser = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
	}

	@Nested
	@DisplayName("유저 차단시")
	class BlockUserTest {

		@Test
		@DisplayName("유저 차단이 성공적으로 이루어진다")
		void blockUser_success() {
			// when
			userBlockUseCase.blockUser(blocker.getId(), blockedUser.getId());

			// then
			assertThat(blockRepository.existsByBlockerAndBlocked(blocker, blockedUser)).isTrue();

		}

		@Test
		@DisplayName("이미 차단된 유저를 차단 시도 시 예외가 발생한다")
		void blockUser_alreadyBlocked() {
			// given
			userBlockUseCase.blockUser(blocker.getId(), blockedUser.getId());

			// when & then
			assertThatThrownBy(() -> userBlockUseCase.blockUser(blocker.getId(), blockedUser.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(ALREADY_BLOCKED.getMessage());
		}

		@Test
		@DisplayName("자기 자신을 차단 시도 시 예외가 발생한다")
		void blockUser_selfBlock() {
			// when & then
			assertThatThrownBy(() -> userBlockUseCase.blockUser(blocker.getId(), blocker.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(CANNOT_BLOCK_SELF.getMessage());
		}
	}

	@Nested
	@DisplayName("unblockUser 메서드 테스트")
	class UnblockUserTest {
		User anotherUser = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());

		@BeforeEach
		void setUp() {
			userBlockUseCase.blockUser(blocker.getId(), blockedUser.getId());
		}

		@Test
		@DisplayName("유저 차단 해제가 성공적으로 이루어진다")
		void unblockUser_success() {
			// when
			userBlockUseCase.unblockUser(blocker.getId(), blockedUser.getId());

			// then
			assertThat(blockRepository.existsByBlockerAndBlocked(blocker, blockedUser)).isFalse();
		}

		@Test
		@DisplayName("차단 관계가 존재하지 않을 시 예외가 발생한다")
		void unblockUser_notFoundBlock() {
			// when & then
			assertThatThrownBy(() -> userBlockUseCase.unblockUser(blocker.getId(), anotherUser.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(NOT_FOUND_BLOCK.getMessage());
		}
	}
}

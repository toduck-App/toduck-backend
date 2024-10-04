package im.toduck.domain.user.domain.usecase;

import static im.toduck.global.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
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
	class BlockUserTest {

		@Test
		void 유저_차단을_할_수_있다() {
			// when
			userBlockUseCase.blockUser(blocker.getId(), blockedUser.getId());

			// then
			assertThat(blockRepository.existsByBlockerAndBlocked(blocker, blockedUser)).isTrue();
		}

		@Test
		void 이미_차단된_유저_차단_시도시_차단에_실패한다() {
			// given
			userBlockUseCase.blockUser(blocker.getId(), blockedUser.getId());

			// when & then
			assertThatThrownBy(() -> userBlockUseCase.blockUser(blocker.getId(), blockedUser.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(ALREADY_BLOCKED.getMessage());
		}

		@Test
		void 자기_자신을_차단_시도시_차단에_실패한다() {
			// when & then
			assertThatThrownBy(() -> userBlockUseCase.blockUser(blocker.getId(), blocker.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(CANNOT_BLOCK_SELF.getMessage());
		}
	}

	@Nested
	class UnblockUserTest {
		User anotherUser = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());

		@BeforeEach
		void setUp() {
			userBlockUseCase.blockUser(blocker.getId(), blockedUser.getId());
		}

		@Test
		void 유저_차단_해제를_할_수_있다() {
			// when
			userBlockUseCase.unblockUser(blocker.getId(), blockedUser.getId());

			// then
			assertThat(blockRepository.existsByBlockerAndBlocked(blocker, blockedUser)).isFalse();
		}

		@Test
		void 차단_관계가_존재하지_않을_시_차단_해제에_실패한다() {
			// when & then
			assertThatThrownBy(() -> userBlockUseCase.unblockUser(blocker.getId(), anotherUser.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(NOT_FOUND_BLOCK.getMessage());
		}
	}
}

package im.toduck.domain.mypage.domain.usecase;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.ServiceTest;
import im.toduck.domain.mypage.presentation.dto.request.NickNameUpdateRequest;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.fixtures.user.UserFixtures;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;

public class MyPageUseCaseTest extends ServiceTest {

	@Autowired
	private MyPageUseCase myPageUseCase;

	@Autowired
	private UserRepository userRepository;

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
}

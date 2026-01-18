package im.toduck.domain.badge.domain.checker;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.social.persistence.repository.SocialRepository;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.fixtures.user.UserFixtures;

@ExtendWith(MockitoExtension.class)
class QuackQuackBadgeCheckerTest {

	@InjectMocks
	private QuackQuackBadgeChecker quackQuackBadgeChecker;

	@Mock
	private SocialRepository socialRepository;

	@Test
	@DisplayName("뱃지 코드는 QUACK_QUACK이어야 한다")
	void getBadgeCode() {
		assertThat(quackQuackBadgeChecker.getBadgeCode()).isEqualTo(BadgeCode.QUACK_QUACK);
	}

	@Test
	@DisplayName("소셜 게시글이 15개 이상이면 true를 반환한다")
	void checkCondition_True() {
		// given
		User user = UserFixtures.GENERAL_USER();
		given(socialRepository.countByUserId(user.getId())).willReturn(15L);

		// when
		boolean result = quackQuackBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("소셜 게시글이 15개 미만이면 false를 반환한다")
	void checkCondition_False() {
		// given
		User user = UserFixtures.GENERAL_USER();
		given(socialRepository.countByUserId(user.getId())).willReturn(14L);

		// when
		boolean result = quackQuackBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isFalse();
	}
}

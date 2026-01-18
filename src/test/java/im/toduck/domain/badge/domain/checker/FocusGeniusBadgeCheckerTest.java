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
import im.toduck.domain.concentration.persistence.repository.ConcentrationRepository;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.fixtures.user.UserFixtures;

@ExtendWith(MockitoExtension.class)
class FocusGeniusBadgeCheckerTest {

	@InjectMocks
	private FocusGeniusBadgeChecker focusGeniusBadgeChecker;

	@Mock
	private ConcentrationRepository concentrationRepository;

	@Test
	@DisplayName("뱃지 코드는 FOCUS_GENIUS여야 한다")
	void getBadgeCode() {
		assertThat(focusGeniusBadgeChecker.getBadgeCode()).isEqualTo(BadgeCode.FOCUS_GENIUS);
	}

	@Test
	@DisplayName("타이머 달성 횟수 합계가 15회 이상이면 true를 반환한다")
	void checkCondition_True() {
		// given
		User user = UserFixtures.GENERAL_USER();
		given(concentrationRepository.sumTargetCountByUser(user)).willReturn(15L);

		// when
		boolean result = focusGeniusBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("타이머 달성 횟수 합계가 15회 미만이면 false를 반환한다")
	void checkCondition_False() {
		// given
		User user = UserFixtures.GENERAL_USER();
		given(concentrationRepository.sumTargetCountByUser(user)).willReturn(14L);

		// when
		boolean result = focusGeniusBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isFalse();
	}
}

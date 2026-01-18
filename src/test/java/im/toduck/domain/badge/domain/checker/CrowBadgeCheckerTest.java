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
import im.toduck.domain.routine.persistence.repository.RoutineRepository;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.fixtures.user.UserFixtures;

@ExtendWith(MockitoExtension.class)
class CrowBadgeCheckerTest {

	@InjectMocks
	private CrowBadgeChecker crowBadgeChecker;

	@Mock
	private RoutineRepository routineRepository;

	@Test
	@DisplayName("뱃지 코드는 CROW여야 한다")
	void getBadgeCode() {
		assertThat(crowBadgeChecker.getBadgeCode()).isEqualTo(BadgeCode.CROW);
	}

	@Test
	@DisplayName("루틴 카테고리가 5개 이상이면 true를 반환한다")
	void checkCondition_True() {
		// given
		User user = UserFixtures.GENERAL_USER();
		given(routineRepository.countDistinctCategoryByUser(user)).willReturn(5L);

		// when
		boolean result = crowBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("루틴 카테고리가 5개 미만이면 false를 반환한다")
	void checkCondition_False() {
		// given
		User user = UserFixtures.GENERAL_USER();
		given(routineRepository.countDistinctCategoryByUser(user)).willReturn(4L);

		// when
		boolean result = crowBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isFalse();
	}
}

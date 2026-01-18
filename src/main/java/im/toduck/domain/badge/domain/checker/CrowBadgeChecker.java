package im.toduck.domain.badge.domain.checker;

import org.springframework.stereotype.Component;

import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.routine.persistence.repository.RoutineRepository;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;

/**
 * 까마귀 뱃지 체커: 기억력 카테고리(Routine Category) 5개 이상 사용 시 지급
 */
@Component
@RequiredArgsConstructor
public class CrowBadgeChecker implements BadgeConditionChecker {

	private final RoutineRepository routineRepository;

	@Override
	public BadgeCode getBadgeCode() {
		return BadgeCode.CROW;
	}

	@Override
	public boolean checkCondition(final User user) {
		return routineRepository.countDistinctCategoryByUser(user) >= 5;
	}
}

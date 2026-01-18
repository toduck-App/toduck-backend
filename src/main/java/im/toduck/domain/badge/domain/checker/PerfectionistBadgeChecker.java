package im.toduck.domain.badge.domain.checker;

import org.springframework.stereotype.Component;

import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.routine.persistence.repository.RoutineRepository;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;

/**
 * 완벽주의 뱃지 체커: 루틴 10개 이상 등록 시 지급
 */
@Component
@RequiredArgsConstructor
public class PerfectionistBadgeChecker implements BadgeConditionChecker {

	private final RoutineRepository routineRepository;

	@Override
	public BadgeCode getBadgeCode() {
		return BadgeCode.PERFECTIONIST;
	}

	@Override
	public boolean checkCondition(final User user) {
		return routineRepository.countByUserAndDeletedAtIsNull(user) >= 10;
	}
}

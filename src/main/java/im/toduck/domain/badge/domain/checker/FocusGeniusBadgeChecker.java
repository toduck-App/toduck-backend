package im.toduck.domain.badge.domain.checker;

import org.springframework.stereotype.Component;

import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.concentration.persistence.repository.ConcentrationRepository;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;

/**
 * 집중천재 뱃지 체커: 타이머 15회 이상 사용 시 지급
 */
@Component
@RequiredArgsConstructor
public class FocusGeniusBadgeChecker implements BadgeConditionChecker {

	private final ConcentrationRepository concentrationRepository;

	@Override
	public BadgeCode getBadgeCode() {
		return BadgeCode.FOCUS_GENIUS;
	}

	@Override
	public boolean checkCondition(final User user) {
		return concentrationRepository.sumTargetCountByUser(user) >= 15;
	}
}

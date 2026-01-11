package im.toduck.domain.badge.domain.checker;

import org.springframework.stereotype.Component;

import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.user.persistence.entity.User;

/**
 * 아기오리 뱃지 체커: 회원가입 시 지급되는 뱃지입니다.
 * 별도의 추가 조건 없이 항상 true를 반환합니다.
 */
@Component
public class BabyDuckBadgeChecker implements BadgeConditionChecker {

	@Override
	public BadgeCode getBadgeCode() {
		return BadgeCode.BABY_DUCK;
	}

	@Override
	public boolean checkCondition(final User user) {
		return true;
	}
}

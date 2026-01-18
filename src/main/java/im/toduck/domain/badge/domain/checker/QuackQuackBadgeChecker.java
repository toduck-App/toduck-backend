package im.toduck.domain.badge.domain.checker;

import org.springframework.stereotype.Component;

import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.social.persistence.repository.SocialRepository;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;

/**
 * 꽥꽥 뱃지 체커: 소셜 글 15개 이상 작성 시 지급
 */
@Component
@RequiredArgsConstructor
public class QuackQuackBadgeChecker implements BadgeConditionChecker {

	private final SocialRepository socialRepository;

	@Override
	public BadgeCode getBadgeCode() {
		return BadgeCode.QUACK_QUACK;
	}

	@Override
	public boolean checkCondition(final User user) {
		return socialRepository.countByUserId(user.getId()) >= 15;
	}
}

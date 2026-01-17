package im.toduck.domain.badge.domain.checker;

import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.user.persistence.entity.User;

/**
 * 뱃지 획득 조건을 검사하는 인터페이스입니다.
 * 새로운 뱃지 조건이 추가될 때마다 이 인터페이스의 구현체를 생성합니다.
 */
public interface BadgeConditionChecker {

	/**
	 * 해당 체커가 담당하는 뱃지의 코드를 반환합니다.
	 */
	BadgeCode getBadgeCode();

	/**
	 * 사용자가 뱃지 획득 조건을 충족했는지 검사합니다.
	 *
	 * @param user 검사 대상 사용자
	 * @return 조건 충족 시 true, 미충족 시 false
	 */
	boolean checkCondition(User user);
}

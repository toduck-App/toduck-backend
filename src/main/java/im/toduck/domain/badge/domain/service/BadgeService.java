package im.toduck.domain.badge.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.badge.common.mapper.UserBadgeMapper;
import im.toduck.domain.badge.persistence.entity.Badge;
import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.badge.persistence.entity.UserBadge;
import im.toduck.domain.badge.persistence.repository.BadgeRepository;
import im.toduck.domain.badge.persistence.repository.UserBadgeRepository;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeService {

	private final BadgeRepository badgeRepository;
	private final UserBadgeRepository userBadgeRepository;

	/**
	 * 사용자에게 특정 뱃지를 지급합니다.
	 * 이미 해당 뱃지를 보유하고 있다면 지급하지 않습니다.
	 *
	 * @param user 뱃지를 받을 사용자
	 * @param badgeCode 지급할 뱃지 코드
	 */
	@Transactional
	public void grantBadge(final User user, final BadgeCode badgeCode) {
		Badge badge = badgeRepository.findByCode(badgeCode)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_BADGE));

		if (userBadgeRepository.existsByUserAndBadge(user, badge)) {
			return;
		}

		UserBadge userBadge = UserBadgeMapper.toUserBadge(user, badge);

		userBadgeRepository.save(userBadge);
		log.info("뱃지 획득 - UserId: {}, Badge: {}", user.getId(), badge.getName());

		// TODO: 뱃지 획득 알림 이벤트 발행
	}
}

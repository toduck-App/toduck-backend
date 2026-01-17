package im.toduck.domain.badge.domain.service;

import java.util.List;

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
	 * @return 새로 지급된 뱃지 정보
	 * @throws CommonException 이미 배지를 보유하고 있을 경우
	 */
	@Transactional
	public UserBadge grantBadge(final User user, final BadgeCode badgeCode) {
		Badge badge = badgeRepository.findByCode(badgeCode)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_BADGE));

		if (userBadgeRepository.existsByUserAndBadge(user, badge)) {
			throw CommonException.from(ExceptionCode.ALREADY_ACQUIRED_BADGE);
		}

		UserBadge userBadge = UserBadgeMapper.toUserBadge(user, badge);
		UserBadge savedBadge = userBadgeRepository.save(userBadge);

		log.info("뱃지 획득 - UserId: {}, Badge: {}", user.getId(), badge.getName());
		return savedBadge;
	}

	/**
	 * 사용자가 아직 확인하지 않은 획득 뱃지 목록을 조회합니다.
	 *
	 * @param user 조회할 사용자
	 * @return 미확인 획득 뱃지 목록
	 */
	@Transactional(readOnly = true)
	public List<UserBadge> getUnseenBadges(final User user) {
		return userBadgeRepository.findAllByUserAndIsSeenFalse(user);
	}

	@Transactional
	public void setRepresentativeBadge(final User user, final Long badgeId) {
		Badge badge = badgeRepository.findById(badgeId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_BADGE));

		UserBadge newRepresentativeBadge = userBadgeRepository.findByUserAndBadge(user, badge)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_OWNED_BADGE));

		userBadgeRepository.findByUserAndIsRepresentativeTrue(user)
			.ifPresent(oldBadge -> oldBadge.updateRepresentativeStatus(false));

		newRepresentativeBadge.updateRepresentativeStatus(true);
	}
}

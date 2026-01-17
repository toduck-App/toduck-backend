package im.toduck.domain.badge.domain.usecase;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.badge.common.dto.response.BadgeResponse;
import im.toduck.domain.badge.domain.service.BadgeService;
import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.badge.persistence.entity.UserBadge;
import im.toduck.domain.notification.domain.data.BadgeAcquiredNotificationData;
import im.toduck.domain.notification.domain.event.BadgeAcquiredNotificationEvent;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class BadgeUseCase {

	private final BadgeService badgeService;
	private final UserService userService;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * 사용자에게 특정 뱃지를 지급합니다.
	 *
	 * @param user 뱃지를 받을 사용자
	 * @param badgeCode 지급할 뱃지 코드
	 */
	@Transactional
	public void grantBadge(final User user, final BadgeCode badgeCode) {
		UserBadge userBadge = badgeService.grantBadge(user, badgeCode);
		eventPublisher.publishEvent(
			BadgeAcquiredNotificationEvent.of(user.getId(),
				BadgeAcquiredNotificationData.from(userBadge.getBadge().getName()))
		);
	}

	/**
	 * 사용자가 아직 확인하지 않은 획득 뱃지 목록을 조회하고, 읽음 처리합니다.
	 *
	 * @param userId 조회할 사용자 ID
	 * @return 새로 획득한 배지 정보 목록
	 */
	@Transactional
	public List<BadgeResponse> getNewBadges(final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		List<UserBadge> newBadges = badgeService.getUnseenBadges(user);

		List<BadgeResponse> responses = newBadges.stream()
			.map(UserBadge::getBadge)
			.map(BadgeResponse::from)
			.collect(Collectors.toList());

		newBadges.forEach(UserBadge::markAsSeen);

		return responses;
	}

	/**
	 * 사용자의 대표 뱃지를 설정합니다.
	 *
	 * @param userId 뱃지를 설정할 사용자 ID
	 * @param badgeId 설정할 뱃지 ID
	 */
	@Transactional
	public void setRepresentativeBadge(final Long userId, final Long badgeId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		badgeService.setRepresentativeBadge(user, badgeId);
	}
}

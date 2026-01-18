package im.toduck.domain.badge.domain.event;

import java.util.List;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.badge.domain.checker.BadgeConditionChecker;
import im.toduck.domain.badge.domain.usecase.BadgeUseCase;
import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.concentration.domain.event.ConcentrationSavedEvent;
import im.toduck.domain.routine.domain.event.RoutineCreatedEvent;
import im.toduck.domain.social.domain.event.SocialCreatedEvent;
import im.toduck.domain.user.domain.event.UserSignedUpEvent;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BadgeEventListener {

	private final BadgeUseCase badgeUseCase;
	private final UserService userService;
	private final List<BadgeConditionChecker> badgeConditionCheckers;

	@Async
	@EventListener
	@Transactional
	public void handleUserSignedUp(final UserSignedUpEvent event) {
		log.info("회원가입 이벤트 수신 - UserId: {}", event.getUserId());
		User user = userService.getUserById(event.getUserId())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		checkAndGrantBadge(user, BadgeCode.BABY_DUCK);
	}

	@Async
	@EventListener
	@Transactional
	public void handleRoutineCreated(final RoutineCreatedEvent event) {
		log.info("루틴 생성 이벤트 수신 - UserId: {}", event.getUserId());
		User user = userService.getUserById(event.getUserId())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		checkAndGrantBadge(user, BadgeCode.PERFECTIONIST);
		checkAndGrantBadge(user, BadgeCode.CROW);
	}

	@Async
	@EventListener
	@Transactional
	public void handleSocialCreated(final SocialCreatedEvent event) {
		log.info("소셜 게시글 생성 이벤트 수신 - UserId: {}", event.getUserId());
		User user = userService.getUserById(event.getUserId())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		checkAndGrantBadge(user, BadgeCode.QUACK_QUACK);
	}

	@Async
	@EventListener
	@Transactional
	public void handleConcentrationSaved(final ConcentrationSavedEvent event) {
		log.info("집중 저장 이벤트 수신 - UserId: {}", event.getUserId());
		User user = userService.getUserById(event.getUserId())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		checkAndGrantBadge(user, BadgeCode.FOCUS_GENIUS);
	}

	private void checkAndGrantBadge(final User user, final BadgeCode badgeCode) {
		findCheckerByBadgeCode(badgeCode)
			.filter(checker -> checker.checkCondition(user))
			.ifPresent(checker -> {
				badgeUseCase.grantBadge(user, badgeCode);
			});

		log.info("배지 부여 완료 - UserId: {}, BadgeCode: {}", user.getId(), badgeCode);
	}

	private Optional<BadgeConditionChecker> findCheckerByBadgeCode(final BadgeCode badgeCode) {
		return badgeConditionCheckers.stream()
			.filter(checker -> checker.getBadgeCode() == badgeCode)
			.findFirst();
	}
}

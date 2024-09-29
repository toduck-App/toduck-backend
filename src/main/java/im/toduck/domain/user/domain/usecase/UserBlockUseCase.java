package im.toduck.domain.user.domain.usecase;

import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class UserBlockUseCase {

	private final UserService userService;

	@Transactional
	public void blockUser(Long blockerId, Long blockedUserId) {
		User blocker = userService.getUserById(blockerId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		User blockedUser = userService.getUserById(blockedUserId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		if (blockerId.equals(blockedUserId)) {
			throw CommonException.from(ExceptionCode.CANNOT_BLOCK_SELF);
		}

		userService.blockUser(blocker, blockedUser);

		log.info("사용자 차단 - BlockerId: {}, BlockedUserId: {}", blockerId, blockedUserId);
	}

	@Transactional
	public void unblockUser(Long blockerId, Long blockedUserId) {
		User blocker = userService.getUserById(blockerId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		User blockedUser = userService.getUserById(blockedUserId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		userService.unblockUser(blocker, blockedUser);

		log.info("사용자 차단 해제 - BlockerId: {}, BlockedUserId: {}", blockerId, blockedUserId);
	}
}

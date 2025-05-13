package im.toduck.domain.notification.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.notification.persistence.entity.DeviceToken;
import im.toduck.domain.notification.persistence.entity.DeviceType;
import im.toduck.domain.notification.persistence.repository.DeviceTokenRepository;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTokenService {
	private final DeviceTokenRepository deviceTokenRepository;
	private final UserService userService;

	@Transactional
	public void registerDeviceToken(final Long userId, final String token, final DeviceType deviceType) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		deviceTokenRepository.findByUserAndToken(user, token)
			.ifPresentOrElse(
				existingToken -> log.debug("디바이스 토큰 이미 등록됨 - 사용자: {}, 토큰: {}", userId, token),
				() -> {
					DeviceToken deviceToken = DeviceToken.builder()
						.user(user)
						.token(token)
						.deviceType(deviceType)
						.build();
					deviceTokenRepository.save(deviceToken);
					log.info("디바이스 토큰 등록 성공 - 사용자: {}, 디바이스: {}", userId, deviceType);
				}
			);
	}

	/**
	 * 디바이스 토큰을 삭제합니다.
	 * 사용자가 로그아웃하거나 앱을 제거할 때 호출됩니다.
	 */
	@Transactional
	public void removeDeviceToken(final Long userId, final String token) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		deviceTokenRepository.findByUserAndToken(user, token)
			.ifPresent(deviceToken -> {
				deviceTokenRepository.delete(deviceToken);
				log.info("디바이스 토큰 삭제 성공 - 사용자: {}, 토큰: {}", userId, token);
			});
	}

	/**
	 * 무효화된 토큰을 삭제합니다.
	 * FCM에서 토큰이 무효화되었다고 통보받았을 때 호출됩니다.
	 */
	@Transactional
	public void removeInvalidToken(final String token) {
		deviceTokenRepository.deleteByToken(token);
		log.info("무효화된 토큰 삭제 - 토큰: {}", token);
	}

	/**
	 * 사용자의 모든 디바이스 토큰을 조회합니다.
	 */
	@Transactional(readOnly = true)
	public List<DeviceToken> getUserDeviceTokens(final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		return deviceTokenRepository.findAllByUser(user);
	}
}

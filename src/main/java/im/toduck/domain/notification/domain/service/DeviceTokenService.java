package im.toduck.domain.notification.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.notification.persistence.entity.DeviceToken;
import im.toduck.domain.notification.persistence.entity.DeviceType;
import im.toduck.domain.notification.persistence.repository.DeviceTokenRepository;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTokenService {
	private final DeviceTokenRepository deviceTokenRepository;

	@Transactional
	public void registerDeviceToken(final User user, final String token, final DeviceType deviceType) {
		if (deviceTokenRepository.existsByUserAndToken(user, token)) {
			return;
		}

		DeviceToken deviceToken = DeviceToken.builder()
			.user(user)
			.token(token)
			.deviceType(deviceType)
			.build();
		deviceTokenRepository.save(deviceToken);
	}

	/**
	 * 디바이스 토큰을 삭제합니다.
	 */
	@Transactional
	public void removeDeviceToken(final User user, final String token) {
		deviceTokenRepository.findByUserAndToken(user, token)
			.ifPresent(deviceTokenRepository::delete);
	}

	/**
	 * 무효화된 토큰을 삭제합니다.
	 * FCM에서 토큰이 무효화되었다고 통보받았을 때 호출됩니다.
	 */
	@Transactional
	public void removeInvalidToken(final String token) {
		deviceTokenRepository.deleteByToken(token);
	}

	/**
	 * 사용자의 모든 디바이스 토큰을 조회합니다.
	 */
	@Transactional(readOnly = true)
	public List<DeviceToken> getUserDeviceTokens(final User user) {
		return deviceTokenRepository.findAllByUser(user);
	}
}

package im.toduck.infra.redis.forbidden;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Service
public class ForbiddenTokenService {
	private final ForbiddenTokenRepository forbiddenTokenRepository;

	/**
	 * 토큰을 블랙 리스트에 등록합니다.
	 *
	 * @param accessToken String : 블랙 리스트에 등록할 액세스 토큰
	 * @param userId      Long : 블랙 리스트에 등록할 유저 아이디
	 * @param expiresAt   LocalDateTime : 블랙 리스트에 등록할 토큰의 만료 시간 (등록할 access token의 만료시간을 추출한 값)
	 */
	public void createForbiddenToken(String accessToken, Long userId, LocalDateTime expiresAt) {
		final LocalDateTime now = LocalDateTime.now();
		final long timeToLive = Duration.between(now, expiresAt).toSeconds();

		ForbiddenToken forbiddenToken = ForbiddenToken.of(accessToken, userId, timeToLive);
		forbiddenTokenRepository.save(forbiddenToken);
	}

	/**
	 * 토큰이 블랙 리스트에 등록되어 있는지 확인합니다.
	 *
	 * @return : 블랙 리스트에 등록되어 있으면 true, 아니면 false
	 */
	public boolean isForbidden(String accessToken) {
		return forbiddenTokenRepository.existsById(accessToken);
	}
}

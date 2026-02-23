package im.toduck.infra.redis.weblogin;

import java.util.Optional;

public interface WebLoginSessionService {
	/**
	 * 새로운 웹 로그인 세션을 생성하고 저장합니다.
	 *
	 * @return 생성된 {@link WebLoginSession}
	 */
	WebLoginSession createSession();

	/**
	 * 세션 토큰으로 웹 로그인 세션을 조회합니다.
	 *
	 * @param sessionToken 세션 토큰
	 * @return {@link Optional} of {@link WebLoginSession}
	 */
	Optional<WebLoginSession> findBySessionToken(String sessionToken);

	/**
	 * 세션을 삭제합니다. (토큰 발급 후 일회용으로 삭제)
	 *
	 * @param sessionToken 세션 토큰
	 */
	void deleteSession(String sessionToken);

	/**
	 * 세션을 저장합니다.
	 *
	 * @param session 저장할 세션
	 */
	void save(WebLoginSession session);
}

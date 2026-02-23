package im.toduck.infra.redis.weblogin;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebLoginSessionServiceImpl implements WebLoginSessionService {
	private static final int TOKEN_BYTE_LENGTH = 36; // 48자 Base64 결과
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private final WebLoginSessionRepository webLoginSessionRepository;

	@Override
	public WebLoginSession createSession() {
		String sessionToken = generateSecureToken();
		WebLoginSession session = WebLoginSession.createPending(sessionToken);
		return webLoginSessionRepository.save(session);
	}

	@Override
	public Optional<WebLoginSession> findBySessionToken(final String sessionToken) {
		return webLoginSessionRepository.findById(sessionToken);
	}

	@Override
	public void deleteSession(final String sessionToken) {
		webLoginSessionRepository.deleteById(sessionToken);
	}

	@Override
	public void save(final WebLoginSession session) {
		webLoginSessionRepository.save(session);
	}

	private String generateSecureToken() {
		byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];
		SECURE_RANDOM.nextBytes(randomBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
	}
}

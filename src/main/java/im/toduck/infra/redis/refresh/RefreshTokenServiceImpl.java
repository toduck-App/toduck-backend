package im.toduck.infra.redis.refresh;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;

	@Override
	public void save(RefreshToken refreshToken) {
		refreshTokenRepository.save(refreshToken);
	}

	@Override
	public RefreshToken refresh(Long userId, String oldRefreshToken, String newRefreshToken) throws
		IllegalArgumentException,
		IllegalStateException {
		RefreshToken refreshToken = findOrElseThrow(userId);

		validateToken(oldRefreshToken, refreshToken);

		refreshToken.rotation(newRefreshToken);
		refreshTokenRepository.save(refreshToken);
		return refreshToken;
	}

	@Override
	public void delete(Long userId, String refreshToken) throws IllegalArgumentException {
		RefreshToken token = findOrElseThrow(userId);
		refreshTokenRepository.delete(token);
	}

	private RefreshToken findOrElseThrow(Long userId) {
		return refreshTokenRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("refresh token not found"));
	}

	private void validateToken(String requestRefreshToken, RefreshToken expectedRefreshToken) throws
		IllegalStateException {
		if (isTakenAway(requestRefreshToken, expectedRefreshToken.getToken())) {
			refreshTokenRepository.delete(expectedRefreshToken);
			throw new IllegalStateException("refresh token mismatched");
		}
	}

	private boolean isTakenAway(String requestRefreshToken, String expectedRefreshToken) {
		return !requestRefreshToken.equals(expectedRefreshToken);
	}
}

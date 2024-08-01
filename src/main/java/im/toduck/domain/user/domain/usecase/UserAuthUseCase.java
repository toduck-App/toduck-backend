package im.toduck.domain.user.domain.usecase;

import im.toduck.domain.auth.domain.service.JwtService;
import im.toduck.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class UserAuthUseCase {

	private final JwtService jwtService;

	public void signOut(Long userId, String authHeader, String refreshToken) {
		jwtService.removeAccessTokenAndRefreshToken(userId, authHeader, refreshToken);
	}

}

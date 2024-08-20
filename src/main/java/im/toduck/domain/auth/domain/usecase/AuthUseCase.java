package im.toduck.domain.auth.domain.usecase;

import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.auth.domain.service.GeneralAuthService;
import im.toduck.domain.auth.domain.service.JwtService;
import im.toduck.domain.auth.presentation.dto.JwtPair;
import im.toduck.domain.auth.presentation.dto.request.LoginRequest;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class AuthUseCase {
	private final GeneralAuthService generalAuthService;
	private final JwtService jwtService;

	@Transactional(readOnly = true)
	public Pair<Long, JwtPair> signIn(LoginRequest request) {
		User user = generalAuthService.getUserIfValid(request.phoneNumber(), request.password());

		return Pair.of(user.getId(), jwtService.createToken(user));
	}

	public Pair<Long, JwtPair> refresh(String refreshToken) {
		return jwtService.refresh(refreshToken);
	}
}

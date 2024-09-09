package im.toduck.domain.auth.domain.usecase;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.auth.common.helper.OAuthOidcHelper;
import im.toduck.domain.auth.common.mapper.OAuthMapper;
import im.toduck.domain.auth.domain.service.JwtService;
import im.toduck.domain.auth.domain.service.NickNameGenerateService;
import im.toduck.domain.auth.presentation.dto.JwtPair;
import im.toduck.domain.auth.presentation.dto.request.SignUpRequest;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.oauth.OidcProvider;
import im.toduck.global.oauth.oidc.OidcPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UseCase {
	private final JwtService jwtService;
	private final OAuthOidcHelper oauthOidcHelper;
	private final UserService userService;
	private final NickNameGenerateService nickNameGenerateService;

	@Transactional
	public Pair<Long, JwtPair> signUp(OidcProvider provider, SignUpRequest.Oidc request) {
		OidcPayload payload = oauthOidcHelper.getPayload(provider, request.oauthId(), request.idToken(),
			request.nonce());
		userService.findByProviderAndEmail(OAuthMapper.fromOidcProvider(provider), payload.email()).ifPresent(user -> {
			throw CommonException.from(ExceptionCode.EXISTS_EMAIL);
		});
		User oAuthUser = User.createOAuthUser(nickNameGenerateService.generateRandomNickname(),
			OAuthMapper.fromOidcProvider(provider), payload.email()); // TODO : 팀원 pr 받은 후 mapper 로 변경
		User user = userService.registerOAuthUser(oAuthUser);
		return Pair.of(user.getId(), jwtService.createToken(user));
	}
}

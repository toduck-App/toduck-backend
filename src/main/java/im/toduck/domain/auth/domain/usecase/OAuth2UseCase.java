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
import im.toduck.domain.user.common.mapper.UserMapper;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.infra.oauth.OidcProvider;
import im.toduck.infra.oauth.oidc.dto.OidcPayload;
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

		return userService.findByProviderAndEmail(OAuthMapper.fromOidcProvider(provider), payload.email())
			.map(user -> Pair.of(user.getId(), jwtService.createToken(user))) // 이메일이 존재할 경우
			.orElseGet(() -> { // 이메일이 존재하지 않을 경우
				User oAuthUser = UserMapper.toOAuthUser(nickNameGenerateService.generateRandomNickname(),
					OAuthMapper.fromOidcProvider(provider), payload.email());
				User newUser = userService.registerOAuthUser(oAuthUser);
				return Pair.of(newUser.getId(), jwtService.createToken(newUser));
			});
	}
}

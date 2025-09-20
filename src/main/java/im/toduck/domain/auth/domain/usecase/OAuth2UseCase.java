package im.toduck.domain.auth.domain.usecase;

import java.util.List;

import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.auth.common.helper.OAuthOidcHelper;
import im.toduck.domain.auth.common.mapper.OAuthMapper;
import im.toduck.domain.auth.domain.service.JwtService;
import im.toduck.domain.auth.domain.service.NickNameGenerateService;
import im.toduck.domain.auth.presentation.dto.JwtPair;
import im.toduck.domain.auth.presentation.dto.request.SignUpRequest;
import im.toduck.domain.diary.domain.service.MasterKeywordService;
import im.toduck.domain.diary.domain.service.UserKeywordService;
import im.toduck.domain.diary.persistence.entity.MasterKeyword;
import im.toduck.domain.user.common.mapper.UserMapper;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.infra.oauth.OidcProvider;
import im.toduck.infra.oauth.oidc.dto.OidcPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@UseCase
@RequiredArgsConstructor
@Slf4j
public class OAuth2UseCase {
	private final JwtService jwtService;
	private final OAuthOidcHelper oauthOidcHelper;
	private final UserService userService;
	private final NickNameGenerateService nickNameGenerateService;
	private final UserKeywordService userKeywordService;
	private final MasterKeywordService masterKeywordService;

	@Transactional
	public Pair<Long, JwtPair> signUp(OidcProvider provider, SignUpRequest.Oidc request) {
		OidcPayload payload = oauthOidcHelper.getPayload(provider, request.oauthId(), request.idToken(),
			request.nonce());

		return userService.findByProviderAndEmail(OAuthMapper.fromOidcProvider(provider), payload.email())
			.map(user -> {
				if (user.isSuspended()) {
					log.warn("정지된 사용자 OAuth 로그인 시도 - 사용자 ID: {}", user.getId());
					throw CommonException.from(ExceptionCode.USER_SUSPENDED);
				}

				return Pair.of(user.getId(), jwtService.createToken(user));
			}) // 이메일이 존재할 경우
			.orElseGet(() -> { // 이메일이 존재하지 않을 경우
				User oAuthUser = UserMapper.toOAuthUser(nickNameGenerateService.generateRandomNickname(),
					OAuthMapper.fromOidcProvider(provider), payload.email());
				User newUser = userService.registerOAuthUser(oAuthUser);

				List<MasterKeyword> masterKeywords = masterKeywordService.findAll();
				userKeywordService.setupKeywordsFromMaster(newUser, masterKeywords);

				return Pair.of(newUser.getId(), jwtService.createToken(newUser));
			});
	}
}

package im.toduck.domain.social.domain.usecase;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.social.domain.service.SocialService;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.presentation.dto.request.CreateSocialRequest;
import im.toduck.domain.social.presentation.dto.response.CreateSocialResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class SocialUseCase {
	private final SocialService socialService;
	private final UserService userService;

	@Transactional
	public CreateSocialResponse createSocialBoard(Long userId, CreateSocialRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social social = socialService.createSocialBoard(user, request);

		return CreateSocialResponse.from(social.getId());
	}

	public void deleteSocialBoard(Long userId, Long socialId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		socialService.deleteSocialBoard(user, socialId);
	}
}


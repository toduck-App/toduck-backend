package im.toduck.domain.social.domain.usecase;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.social.domain.service.SocialService;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
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
		List<SocialCategory> socialCategories = socialService.findAllSocialCategories(request.socialCategoryIds());

		if (isInvalidCategoryIncluded(request, socialCategories)) {
			throw CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY);
		}

		Social social = socialService.createSocialBoard(user, socialCategories, request);

		return CreateSocialResponse.from(social.getId());
	}

	private boolean isInvalidCategoryIncluded(CreateSocialRequest request, List<SocialCategory> socialCategories) {
		return socialCategories.size() != request.socialCategoryIds().size();
	}

	@Transactional
	public void deleteSocialBoard(Long userId, Long socialId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));

		socialService.deleteSocialBoard(user, socialBoard);
	}
}


package im.toduck.domain.social.domain.usecase;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.social.domain.service.SocialService;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.presentation.dto.request.CreateSocialRequest;
import im.toduck.domain.social.presentation.dto.request.UpdateSocialRequest;
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
		Social socialBoard = socialService.createSocialBoard(user, request);
		List<SocialCategory> socialCategories = socialService.findAllSocialCategories(request.socialCategoryIds());
		socialService.addSocialCategoryLinks(request.socialCategoryIds(), socialCategories, socialBoard);
		socialService.addSocialImageFiles(request.socialImageUrls(), socialBoard);

		return CreateSocialResponse.from(socialBoard.getId());
	}

	@Transactional
	public void deleteSocialBoard(Long userId, Long socialId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));

		socialService.deleteSocialBoard(user, socialBoard);
	}

	@Transactional
	public void updateSocialBoard(Long userId, Long socialId, UpdateSocialRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));

		socialService.updateSocialBoard(user, socialBoard, request);
	}
}


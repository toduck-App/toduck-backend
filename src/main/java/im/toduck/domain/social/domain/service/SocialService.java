package im.toduck.domain.social.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.entity.SocialTag;
import im.toduck.domain.social.persistence.repository.SocialCategoryRepository;
import im.toduck.domain.social.persistence.repository.SocialRepository;
import im.toduck.domain.social.presentation.dto.request.CreateSocialRequest;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialService {
	private final SocialRepository socialRepository;
	private final SocialCategoryRepository socialCategoryRepository;

	@Transactional
	public Social createSocialBoard(User user, CreateSocialRequest request) {
		Social socialBoard = Social.of(user, request.content(), request.socialTag(), request.isAnonymous());
		List<SocialCategory> socialCategories = socialCategoryRepository.findAllById(request.socialCategoryIds());
		socialBoard.addSocialCategoryLinks(socialCategories);

		if (isCommunication(request.socialTag())) {
			socialBoard.addSocialImageFiles(request.socialImageUrls());
		}

		return socialRepository.save(socialBoard);
	}

	@Transactional
	public void deleteSocialBoard(User user, Long socialId) {
		Social socialBoard = socialRepository.findById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));

		if (!isOwner(socialBoard, user)) {
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD);
		}

		socialBoard.remove();
	}

	private boolean isOwner(Social socialBoard, User user) {
		return socialBoard.isOwner(user);
	}

	private boolean isCommunication(SocialTag socialTag) {
		return socialTag.equals(SocialTag.COMMUNICATION);
	}
}

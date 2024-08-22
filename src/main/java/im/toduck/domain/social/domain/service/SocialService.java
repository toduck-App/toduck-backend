package im.toduck.domain.social.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
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

	@Transactional(readOnly = true)
	public Optional<Social> getSocialById(Long socialId) {
		return socialRepository.findById(socialId);
	}

	@Transactional
	public Social createSocialBoard(User user, List<SocialCategory> socialCategories, CreateSocialRequest request) {
		Social socialBoard = Social.of(user, request.content(), request.isAnonymous());
		socialBoard.addSocialCategoryLinks(socialCategories);
		socialBoard.addSocialImageFiles(request.socialImageUrls());

		return socialRepository.save(socialBoard);
	}

	@Transactional
	public void deleteSocialBoard(User user, Social socialBoard) {
		if (!isOwner(socialBoard, user)) {
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD);
		}

		socialRepository.delete(socialBoard);
	}

	private boolean isOwner(Social socialBoard, User user) {
		return socialBoard.isOwner(user);
	}

	@Transactional(readOnly = true)
	public List<SocialCategory> findAllSocialCategories(List<Long> socialCategoryIds) {
		return socialCategoryRepository.findAllById(socialCategoryIds);
	}
}

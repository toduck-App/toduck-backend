package im.toduck.domain.social.domain.usecase;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.social.domain.service.SocialService;
import im.toduck.domain.social.mapper.CommentMapper;
import im.toduck.domain.social.mapper.LikeMapper;
import im.toduck.domain.social.mapper.SocialMapper;
import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialUpdateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentCreateResponse;
import im.toduck.domain.social.presentation.dto.response.LikeCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialCreateResponse;
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
	public SocialCreateResponse createSocialBoard(Long userId, SocialCreateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialService.createSocialBoard(user, request);
		List<SocialCategory> socialCategories = socialService.findAllSocialCategories(request.socialCategoryIds());
		socialService.addSocialCategoryLinks(request.socialCategoryIds(), socialCategories, socialBoard);
		socialService.addSocialImageFiles(request.socialImageUrls(), socialBoard);

		return SocialMapper.toSocialCreateResponse(socialBoard);
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
	public void updateSocialBoard(Long userId, Long socialId, SocialUpdateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));

		socialService.updateSocialBoard(user, socialBoard, request);
	}

	@Transactional
	public CommentCreateResponse createComment(Long userId, Long socialId,
		CommentCreateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));
		Comment comment = socialService.createComment(user, socialBoard, request);

		return CommentMapper.toCommentCreateResponse(comment);
	}

	@Transactional
	public void deleteComment(Long userId, Long socialId, Long commentId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));
		Comment comment = socialService.getCommentById(commentId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_COMMENT));

		socialService.deleteComment(user, socialBoard, comment);
	}

	@Transactional
	public LikeCreateResponse createLike(Long userId, Long socialId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));
		Like like = socialService.createLike(user, socialBoard);
		socialBoard.incrementLikeCount();

		return LikeMapper.toLikeCreateResponse(like);
	}

	@Transactional
	public void deleteLike(Long userId, Long socialId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));
		Like like = socialService.getLikeByUserAndSocial(user, socialBoard)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_LIKE));
		socialBoard.decrementLikeCount();

		socialService.deleteLike(user, socialBoard, like);
	}
}


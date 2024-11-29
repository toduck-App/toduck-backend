package im.toduck.domain.social.domain.usecase;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.social.common.mapper.CommentLikeMapper;
import im.toduck.domain.social.common.mapper.CommentMapper;
import im.toduck.domain.social.common.mapper.LikeMapper;
import im.toduck.domain.social.common.mapper.ReportMapper;
import im.toduck.domain.social.domain.service.SocialBoardService;
import im.toduck.domain.social.domain.service.SocialInteractionService;
import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.CommentLike;
import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Report;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.request.ReportCreateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentCreateResponse;
import im.toduck.domain.social.presentation.dto.response.CommentLikeCreateResponse;
import im.toduck.domain.social.presentation.dto.response.LikeCreateResponse;
import im.toduck.domain.social.presentation.dto.response.ReportCreateResponse;
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
public class SocialInteractionUseCase {
	private final SocialBoardService socialBoardService;
	private final SocialInteractionService socialInteractionService;
	private final UserService userService;

	@Transactional
	public CommentCreateResponse createComment(Long userId, Long socialId, CommentCreateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialBoardService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));
		Comment comment = socialInteractionService.createComment(user, socialBoard, request);

		log.info("소셜 게시글 댓글 생성 - UserId: {}, SocialBoardId: {}, CommentId: {}", userId, socialId, comment.getId());
		return CommentMapper.toCommentCreateResponse(comment);
	}

	@Transactional
	public void deleteComment(Long userId, Long socialId, Long commentId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialBoardService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));
		Comment comment = socialInteractionService.getCommentById(commentId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_COMMENT));

		log.info("소셜 게시글 댓글 삭제 - UserId: {}, SocialBoardId: {}, CommentId: {}", userId, socialId, commentId);
		socialInteractionService.deleteComment(user, socialBoard, comment);
	}

	@Transactional
	public LikeCreateResponse createLike(Long userId, Long socialId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialBoardService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));
		Like like = socialInteractionService.createLike(user, socialBoard);
		socialBoard.incrementLikeCount();

		log.info("소셜 게시글 좋아요 생성 - UserId: {}, SocialBoardId: {}, LikeId: {}", userId, socialId, like.getId());
		return LikeMapper.toLikeCreateResponse(like);
	}

	@Transactional
	public void deleteLike(Long userId, Long socialId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialBoardService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));
		Like like = socialInteractionService.getLikeByUserAndSocial(user, socialBoard)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_LIKE));
		socialBoard.decrementLikeCount();

		log.info("소셜 게시글 좋아요 삭제 - UserId: {}, SocialBoardId: {}, LikeId: {}", userId, socialId, like.getId());
		socialInteractionService.deleteLike(user, socialBoard, like);
	}

	@Transactional
	public ReportCreateResponse reportSocial(Long userId, Long socialId, ReportCreateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialBoardService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));

		if (user.equals(socialBoard.getUser())) {
			log.warn("자신의 게시글을 신고할 수 없습니다. UserId: {}, SocialBoardId: {}", userId, socialId);
			throw CommonException.from(ExceptionCode.CANNOT_REPORT_OWN_POST);
		}

		boolean alreadyReported = socialInteractionService.existsByUserAndSocial(user, socialBoard);
		if (alreadyReported) {
			log.warn("이미 신고된 게시글 - UserId: {}, SocialBoardId: {}", userId, socialId);
			throw CommonException.from(ExceptionCode.ALREADY_REPORTED);
		}

		if (request.blockAuthor()) {
			blockAuthorIfNotAlreadyBlocked(user, socialBoard.getUser());
		}

		Report report = socialInteractionService.createReport(user, socialBoard, request.reportType(),
			request.reason());

		log.info("게시글 신고 - UserId: {}, SocialBoardId: {}, ReportId: {}", userId, socialId, report.getId());
		return ReportMapper.toReportCreateResponse(report);
	}

	private void blockAuthorIfNotAlreadyBlocked(User blocker, User blockedUser) {
		if (!userService.isBlockedUser(blocker, blockedUser)) {
			userService.blockUser(blocker, blockedUser);
		}
	}

	@Transactional
	public CommentLikeCreateResponse createCommentLike(Long userId, Long commentId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Comment comment = socialInteractionService.getCommentById(commentId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_COMMENT));

		CommentLike commentLike = socialInteractionService.createCommentLike(user, comment);

		log.info("댓글 좋아요 생성 - UserId: {}, CommentId: {}, LikeId: {}", userId, commentId, commentLike.getId());
		return CommentLikeMapper.toCommentLikeCreateResponse(commentLike);
	}

}

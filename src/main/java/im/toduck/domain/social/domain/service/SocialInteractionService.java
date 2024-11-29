package im.toduck.domain.social.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.social.common.mapper.CommentLikeMapper;
import im.toduck.domain.social.common.mapper.CommentMapper;
import im.toduck.domain.social.common.mapper.LikeMapper;
import im.toduck.domain.social.common.mapper.ReportMapper;
import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.CommentLike;
import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Report;
import im.toduck.domain.social.persistence.entity.ReportType;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.repository.CommentLikeRepository;
import im.toduck.domain.social.persistence.repository.CommentRepository;
import im.toduck.domain.social.persistence.repository.LikeRepository;
import im.toduck.domain.social.persistence.repository.ReportRepository;
import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialInteractionService {
	private final CommentRepository commentRepository;
	private final LikeRepository likeRepository;
	private final ReportRepository reportRepository;
	private final CommentLikeRepository commentLikeRepository;

	@Transactional
	public Comment createComment(User user, Social socialBoard, CommentCreateRequest request) {
		Comment comment = CommentMapper.toComment(user, socialBoard, request);
		return commentRepository.save(comment);
	}

	@Transactional(readOnly = true)
	public Optional<Comment> getCommentById(Long commentId) {
		return commentRepository.findById(commentId);
	}

	@Transactional
	public void deleteComment(User user, Social socialBoard, Comment comment) {
		if (!isCommentOwner(comment, user)) {
			log.warn("권한이 없는 유저가 댓글 삭제 시도 - UserId: {}, CommentId: {}", user.getId(), comment.getId());
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_COMMENT);
		}

		if (!isCommentInSocialBoard(socialBoard, comment)) {
			log.warn("댓글이 소셜 게시글에 속하지 않음 - SocialBoardId: {}, CommentId: {}", socialBoard.getId(), comment.getId());
			throw CommonException.from(ExceptionCode.INVALID_COMMENT_FOR_BOARD);
		}

		commentRepository.delete(comment);
	}

	@Transactional
	public Like createLike(User user, Social socialBoard) {
		Optional<Like> existedLike = likeRepository.findByUserAndSocial(user, socialBoard);

		if (existedLike.isPresent()) {
			log.warn("이미 좋아요가 존재 - UserId: {}, SocialBoardId: {}", user.getId(), socialBoard.getId());
			throw CommonException.from(ExceptionCode.EXISTS_LIKE);
		}

		Like like = LikeMapper.toLike(user, socialBoard);
		return likeRepository.save(like);
	}

	@Transactional(readOnly = true)
	public Optional<Like> getLikeByUserAndSocial(User user, Social socialBoard) {
		return likeRepository.findByUserAndSocial(user, socialBoard);
	}

	@Transactional
	public void deleteLike(User user, Social socialBoard, Like like) {
		if (!isLikeOwner(like, user)) {
			log.warn("권한이 없는 유저가 좋아요 삭제 시도 - UserId: {}, LikeId: {}", user.getId(), like.getId());
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_LIKE);
		}

		if (!isLikeInSocialBoard(socialBoard, like)) {
			log.warn("좋아요가 소셜 게시글에 속하지 않음 - SocialBoardId: {}, LikeId: {}", socialBoard.getId(), like.getId());
			throw CommonException.from(ExceptionCode.INVALID_LIKE_FOR_BOARD);
		}

		likeRepository.delete(like);
	}

	@Transactional(readOnly = true)
	public List<Comment> getCommentsBySocial(Social socialBoard, Long userId) {
		return commentRepository.findAllBySocialExcludingBlocked(socialBoard, userId);
	}

	@Transactional(readOnly = true)
	public boolean getIsLiked(User user, Social socialBoard) {
		Optional<Like> like = likeRepository.findByUserAndSocial(user, socialBoard);
		return like.isPresent();
	}

	@Transactional
	public Report createReport(User user, Social social, ReportType reportType, String reason) {
		Report report = ReportMapper.toReport(user, social, reportType, reason);
		return reportRepository.save(report);
	}

	@Transactional(readOnly = true)
	public boolean existsByUserAndSocial(User user, Social social) {
		return reportRepository.existsByUserAndSocial(user, social);
	}

	private boolean isCommentOwner(Comment comment, User user) {
		return comment.isOwner(user);
	}

	private boolean isCommentInSocialBoard(Social socialBoard, Comment comment) {
		return comment.isInSocialBoard(socialBoard);
	}

	private boolean isLikeInSocialBoard(Social socialBoard, Like like) {
		return like.isInSocialBoard(socialBoard);
	}

	private boolean isLikeOwner(Like like, User user) {
		return like.isOwner(user);
	}

	@Transactional
	public CommentLike createCommentLike(final User user, final Comment comment) {
		Optional<CommentLike> existingCommentLike = commentLikeRepository.findCommentLikeByUserAndComment(
			user,
			comment
		);

		if (existingCommentLike.isPresent()) {
			log.warn("이미 댓글에 좋아요가 존재 - UserId: {}, CommentId: {}", user.getId(), comment.getId());
			throw CommonException.from(ExceptionCode.EXISTS_COMMENT_LIKE);
		}

		CommentLike commentLike = CommentLikeMapper.toCommentLike(user, comment);
		comment.incrementLikeCount();

		return commentLikeRepository.save(commentLike);
	}
}


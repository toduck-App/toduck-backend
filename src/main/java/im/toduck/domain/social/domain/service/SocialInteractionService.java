package im.toduck.domain.social.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.social.common.mapper.CommentImageFileMapper;
import im.toduck.domain.social.common.mapper.CommentLikeMapper;
import im.toduck.domain.social.common.mapper.CommentMapper;
import im.toduck.domain.social.common.mapper.ReportMapper;
import im.toduck.domain.social.common.mapper.SocialLikeMapper;
import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.CommentImageFile;
import im.toduck.domain.social.persistence.entity.CommentLike;
import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Report;
import im.toduck.domain.social.persistence.entity.ReportType;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.repository.CommentImageFileRepository;
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
	private final CommentImageFileRepository commentImageFileRepository;

	@Transactional
	public Comment createComment(
		final User user,
		final Social socialBoard,
		final Comment parentComment,
		final CommentCreateRequest request
	) {
		Comment comment = CommentMapper.toComment(user, socialBoard, parentComment, request);
		return commentRepository.save(comment);
	}

	@Transactional(readOnly = true)
	public Optional<Comment> getCommentById(final Long commentId) {
		return commentRepository.findById(commentId);
	}

	@Transactional
	public void deleteComment(
		final User user,
		final Social socialBoard,
		final Comment comment
	) {
		if (!isCommentOwner(comment, user)) {
			log.warn("권한이 없는 유저가 댓글 삭제 시도 - UserId: {}, CommentId: {}", user.getId(), comment.getId());
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_COMMENT);
		}

		if (!isCommentInSocialBoard(socialBoard, comment)) {
			log.warn("댓글이 소셜 게시글에 속하지 않음 - SocialBoardId: {}, CommentId: {}", socialBoard.getId(), comment.getId());
			throw CommonException.from(ExceptionCode.INVALID_COMMENT_FOR_BOARD);
		}

		List<CommentLike> commentLikes = commentLikeRepository.findAllByComment(comment);
		commentLikeRepository.deleteAll(commentLikes);
		commentRepository.delete(comment);
	}

	@Transactional
	public Like createSocialLike(final User user, final Social socialBoard) {
		Optional<Like> existedLike = likeRepository.findByUserAndSocial(user, socialBoard);

		if (existedLike.isPresent()) {
			log.warn("이미 좋아요가 존재 - UserId: {}, SocialBoardId: {}", user.getId(), socialBoard.getId());
			throw CommonException.from(ExceptionCode.EXISTS_LIKE);
		}
		Like like = SocialLikeMapper.toLike(user, socialBoard);
		socialBoard.incrementLikeCount();

		return likeRepository.save(like);
	}

	@Transactional(readOnly = true)
	public Optional<Like> getLikeByUserAndSocial(final User user, final Social socialBoard) {
		return likeRepository.findByUserAndSocial(user, socialBoard);
	}

	@Transactional
	public void deleteSocialLike(
		final User user,
		final Social socialBoard,
		final Like like
	) {
		if (!isLikeOwner(like, user)) {
			log.warn("권한이 없는 유저가 좋아요 삭제 시도 - UserId: {}, LikeId: {}", user.getId(), like.getId());
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_LIKE);
		}

		if (!isLikeInSocialBoard(socialBoard, like)) {
			log.warn("좋아요가 소셜 게시글에 속하지 않음 - SocialBoardId: {}, LikeId: {}", socialBoard.getId(), like.getId());
			throw CommonException.from(ExceptionCode.INVALID_LIKE_FOR_BOARD);
		}

		likeRepository.delete(like);
		socialBoard.decrementLikeCount();
	}

	@Transactional(readOnly = true)
	public List<Comment> getCommentsBySocial(final Social socialBoard) {
		return commentRepository.findCommentsBySocial(socialBoard);
	}

	@Transactional(readOnly = true)
	public boolean getSocialBoardIsLiked(final User user, final Social socialBoard) {
		Optional<Like> like = likeRepository.findByUserAndSocial(user, socialBoard);
		return like.isPresent();
	}

	@Transactional(readOnly = true)
	public boolean getCommentIsLiked(final User user, final Comment comment) {
		Optional<CommentLike> commentLike = commentLikeRepository.findCommentLikeByUserAndComment(user, comment);
		return commentLike.isPresent();
	}

	@Transactional
	public Report createReport(
		final User user,
		final Social social,
		final ReportType reportType,
		final String reason
	) {
		Report report = ReportMapper.toReport(user, social, reportType, reason);
		return reportRepository.save(report);
	}

	@Transactional(readOnly = true)
	public boolean existsByUserAndSocial(final User user, final Social social) {
		return reportRepository.existsByUserAndSocial(user, social);
	}

	private boolean isCommentOwner(final Comment comment, final User user) {
		return comment.isOwner(user);
	}

	private boolean isCommentInSocialBoard(final Social socialBoard, final Comment comment) {
		return comment.isInSocialBoard(socialBoard);
	}

	private boolean isLikeInSocialBoard(final Social socialBoard, final Like like) {
		return like.isInSocialBoard(socialBoard);
	}

	private boolean isLikeOwner(final Like like, final User user) {
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

	@Transactional(readOnly = true)
	public Optional<CommentLike> getCommentLikeByUserAndComment(final User user, final Comment comment) {
		return commentLikeRepository.findCommentLikeByUserAndComment(user, comment);
	}

	@Transactional
	public void deleteCommentLike(final Comment comment, final CommentLike commentLike) {
		commentLikeRepository.delete(commentLike);
		comment.decrementLikeCount();
	}

	@Transactional
	public void addCommentImageFile(final String imageUrl, final Comment comment) {
		if (imageUrl == null) {
			return;
		}
		CommentImageFile commentImageFile = CommentImageFileMapper.toCommentImageFile(comment, imageUrl);
		commentImageFileRepository.save(commentImageFile);
	}

	@Transactional(readOnly = true)
	public Optional<CommentImageFile> getCommentImageByComment(final Comment comment) {
		return commentImageFileRepository.findByComment(comment);
	}
}


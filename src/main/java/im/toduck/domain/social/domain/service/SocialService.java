package im.toduck.domain.social.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.social.mapper.CommentMapper;
import im.toduck.domain.social.mapper.LikeMapper;
import im.toduck.domain.social.mapper.SocialCategoryLinkMapper;
import im.toduck.domain.social.mapper.SocialImageFileMapper;
import im.toduck.domain.social.mapper.SocialMapper;
import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.entity.SocialCategoryLink;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.social.persistence.repository.CommentRepository;
import im.toduck.domain.social.persistence.repository.LikeRepository;
import im.toduck.domain.social.persistence.repository.SocialCategoryLinkRepository;
import im.toduck.domain.social.persistence.repository.SocialCategoryRepository;
import im.toduck.domain.social.persistence.repository.SocialImageFileRepository;
import im.toduck.domain.social.persistence.repository.SocialRepository;
import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialUpdateRequest;
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
	private final SocialImageFileRepository socialImageFileRepository;
	private final SocialCategoryLinkRepository socialCategoryLinkRepository;
	private final CommentRepository commentRepository;
	private final LikeRepository likeRepository;

	@Transactional(readOnly = true)
	public Optional<Social> getSocialById(Long socialId) {
		return socialRepository.findById(socialId);
	}

	@Transactional
	public Social createSocialBoard(User user, SocialCreateRequest request) {
		Social socialBoard = SocialMapper.toSocial(user, request.content(), request.isAnonymous());

		return socialRepository.save(socialBoard);
	}

	@Transactional
	public void deleteSocialBoard(User user, Social socialBoard) {
		if (!isBoardOwner(socialBoard, user)) {
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD);
		}

		List<SocialImageFile> imageFiles = socialImageFileRepository.findAllBySocial(socialBoard);
		imageFiles.forEach(SocialImageFile::softDelete);

		List<SocialCategoryLink> socialCategoryLinks = socialCategoryLinkRepository.findAllBySocial(socialBoard);
		socialCategoryLinks.forEach(SocialCategoryLink::softDelete);

		List<Comment> comments = commentRepository.findAllBySocial(socialBoard);
		comments.forEach(Comment::softDelete);

		List<Like> likes = likeRepository.findAllBySocial(socialBoard);
		likes.forEach(Like::softDelete);

		socialRepository.delete(socialBoard);
	}

	@Transactional
	public void updateSocialBoard(User user, Social socialBoard, SocialUpdateRequest request) {
		if (!isBoardOwner(socialBoard, user)) {
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD);
		}

		if (request.socialCategoryIds() != null) {
			List<SocialCategory> socialCategories = findAllSocialCategories(request.socialCategoryIds());

			if (isInvalidCategoryIncluded(request.socialCategoryIds(), socialCategories)) {
				throw CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY);
			}

			socialCategoryLinkRepository.deleteAllBySocial(socialBoard);
			addSocialCategoryLinks(request.socialCategoryIds(), socialCategories, socialBoard);
		}

		if (request.content() != null) {
			socialBoard.updateContent(request.content());
		}

		if (request.isAnonymous() != null) {
			socialBoard.updateIsAnonymous(request.isAnonymous());
		}

		if (request.socialImageUrls() != null) {
			socialImageFileRepository.deleteAllBySocial(socialBoard);
			addSocialImageFiles(request.socialImageUrls(), socialBoard);
		}
	}

	@Transactional(readOnly = true)
	public List<SocialCategory> findAllSocialCategories(List<Long> socialCategoryIds) {
		return socialCategoryRepository.findAllById(socialCategoryIds);
	}

	@Transactional
	public void addSocialImageFiles(List<String> imageUrls, Social socialBoard) {
		List<SocialImageFile> socialImageFiles = imageUrls.stream()
			.map(url -> SocialImageFileMapper.toSocialImageFile(socialBoard, url))
			.toList();

		socialImageFileRepository.saveAll(socialImageFiles);
	}

	@Transactional
	public void addSocialCategoryLinks(List<Long> categoryIds, List<SocialCategory> socialCategories,
		Social socialBoard) {
		if (isInvalidCategoryIncluded(categoryIds, socialCategories)) {
			throw CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY);
		}

		List<SocialCategoryLink> socialCategoryLinks = socialCategories.stream()
			.map(sc -> SocialCategoryLinkMapper.toSocialCategoryLink(socialBoard, sc))
			.toList();

		socialCategoryLinkRepository.saveAll(socialCategoryLinks);
	}

	private boolean isBoardOwner(Social socialBoard, User user) {
		return socialBoard.isOwner(user);
	}

	private boolean isInvalidCategoryIncluded(List<Long> socialCategoryIds, List<SocialCategory> socialCategories) {
		return socialCategories.size() != socialCategoryIds.size();
	}

	@Transactional
	public Comment createComment(User user, Social socialBoard, CommentCreateRequest request) {
		Comment comment = CommentMapper.toComment(user, socialBoard, request);
		return commentRepository.save(comment);
	}

	@Transactional(readOnly = true)
	public Optional<Comment> getCommentById(Long commentId) {
		return commentRepository.findById(commentId);
	}

	public void deleteComment(User user, Social socialBoard, Comment comment) {
		if (!isCommentOwner(comment, user)) {
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_COMMENT);
		}

		if (!isCommentInSocialBoard(socialBoard, comment)) {
			throw CommonException.from(ExceptionCode.INVALID_COMMENT_FOR_BOARD);
		}

		commentRepository.delete(comment);
	}

	private boolean isCommentOwner(Comment comment, User user) {
		return comment.isOwner(user);
	}

	private boolean isCommentInSocialBoard(Social socialBoard, Comment comment) {
		return comment.isInSocialBoard(socialBoard);
	}

	@Transactional
	public Like createLike(User user, Social socialBoard) {
		Optional<Like> existedLike = likeRepository.findByUserAndSocial(user, socialBoard);

		if (existedLike.isPresent()) {
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
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_LIKE);
		}

		if (!isLikeInSocialBoard(socialBoard, like)) {
			throw CommonException.from(ExceptionCode.INVALID_LIKE_FOR_BOARD);
		}

		likeRepository.delete(like);
	}

	private boolean isLikeInSocialBoard(Social socialBoard, Like like) {
		return like.isInSocialBoard(socialBoard);
	}

	private boolean isLikeOwner(Like like, User user) {
		return like.isOwner(user);
	}

}

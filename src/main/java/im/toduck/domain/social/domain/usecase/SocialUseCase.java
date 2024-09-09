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
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialUpdateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentCreateResponse;
import im.toduck.domain.social.presentation.dto.response.LikeCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialDetailResponse;
import im.toduck.domain.social.presentation.dto.response.SocialResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.dto.response.CursorPaginationResponse;
import im.toduck.global.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class SocialUseCase {
	private static final int DEFAULT_SOCIAL_PAGE_SIZE = 10;

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

	@Transactional(readOnly = true)
	public SocialDetailResponse getSocialDetail(Long userId, Long socialId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));
		List<SocialImageFile> imageFiles = socialService.getSocialImagesBySocial(socialBoard);
		List<Comment> comments = socialService.getCommentsBySocial(socialBoard);
		boolean isLiked = socialService.getIsLiked(user, socialBoard);

		return SocialMapper.toSocialDetailResponse(socialBoard, imageFiles, comments, isLiked);
	}

	@Transactional(readOnly = true)
	public CursorPaginationResponse<SocialResponse> getSocials(Long userId, Long cursor, Integer limit) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		int actualLimit = PaginationUtil.resolveLimit(limit, DEFAULT_SOCIAL_PAGE_SIZE);
		int fetchLimit = PaginationUtil.calculateTotalFetchSize(actualLimit);

		List<Social> socialBoards = fetchSocialBoards(cursor, fetchLimit);
		boolean hasMore = PaginationUtil.hasMore(socialBoards, actualLimit);
		Long nextCursor = PaginationUtil.getNextCursor(hasMore, socialBoards, actualLimit, Social::getId);

		List<SocialResponse> socialResponses = createSocialResponses(socialBoards, user, actualLimit);

		return PaginationUtil.toCursorPaginationResponse(hasMore, nextCursor, socialResponses);
	}

	private List<Social> fetchSocialBoards(Long cursor, int fetchLimit) {
		if (cursor == null) {
			return socialService.findLatestSocials(fetchLimit);
		} else {
			return socialService.getSocials(cursor, fetchLimit);
		}
	}

	private List<SocialResponse> createSocialResponses(List<Social> socialBoards, User user, int actualLimit) {
		return socialBoards.stream()
			.limit(actualLimit)
			.map(sb -> {
				List<SocialImageFile> imageFiles = socialService.getSocialImagesBySocial(sb);
				List<Comment> comments = socialService.getCommentsBySocial(sb);
				boolean isLiked = socialService.getIsLiked(user, sb);
				return SocialMapper.toSocialResponse(sb, imageFiles, comments.size(), isLiked);
			})
			.toList();
	}
}


package im.toduck.domain.social.domain.usecase;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import im.toduck.domain.routine.domain.service.RoutineService;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.social.common.mapper.CommentMapper;
import im.toduck.domain.social.common.mapper.SocialCategoryMapper;
import im.toduck.domain.social.common.mapper.SocialMapper;
import im.toduck.domain.social.domain.service.SocialBoardService;
import im.toduck.domain.social.domain.service.SocialInteractionService;
import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.CommentImageFile;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.social.presentation.dto.request.SocialCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialUpdateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentDto;
import im.toduck.domain.social.presentation.dto.response.SocialCategoryResponse;
import im.toduck.domain.social.presentation.dto.response.SocialCategoryResponse.SocialCategoryDto;
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
public class SocialBoardUseCase {
	private static final int DEFAULT_SOCIAL_PAGE_SIZE = 10;

	private final SocialBoardService socialBoardService;
	private final SocialInteractionService socialInteractionService;
	private final UserService userService;
	private final RoutineService routineService;

	@Transactional
	public SocialCreateResponse createSocialBoard(final Long userId, final SocialCreateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Routine routine = findRoutineForCreate(user, request.routineId());

		Social socialBoard = socialBoardService.createSocialBoard(user, routine, request);
		List<SocialCategory> socialCategories = socialBoardService.findSocialCategoriesByIds(
			request.socialCategoryIds());
		socialBoardService.addSocialCategoryLinks(request.socialCategoryIds(), socialCategories, socialBoard);
		socialBoardService.addSocialImageFiles(request.socialImageUrls(), socialBoard);

		log.info("소셜 게시글 생성 - UserId: {}, SocialBoardId: {}", userId, socialBoard.getId());
		return SocialMapper.toSocialCreateResponse(socialBoard);
	}

	private Routine findRoutineForCreate(final User user, final Long routineId) {
		if (routineId == null) {
			return null;
		}

		return findAndValidateRoutine(user, routineId);
	}

	@Transactional
	public void updateSocialBoard(
		final Long userId,
		final Long socialId,
		final SocialUpdateRequest request
	) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialBoardService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));

		if (!isBoardOwner(socialBoard, user)) {
			log.warn("권한이 없는 유저가 소셜 게시판 수정 시도 - UserId: {}, SocialBoardId: {}", user.getId(), socialBoard.getId());
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD);
		}

		Routine routine = findRoutineForUpdate(user, request.routineId());

		socialBoardService.updateSocialBoard(user, socialBoard, routine, request);
		log.info("소셜 게시글 수정 - UserId: {}, SocialBoardId: {}", userId, socialId);
	}

	private boolean isBoardOwner(final Social socialBoard, final User user) {
		return socialBoard.isOwner(user);
	}

	private Routine findRoutineForUpdate(
		final User user,
		final Long routineId
	) {
		if (routineId == null) {
			return null;
		}

		return findAndValidateRoutine(user, routineId);
	}

	private Routine findAndValidateRoutine(final User user, final Long routineId) {
		Routine routine = routineService.getUserRoutine(user, routineId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_ROUTINE));

		if (!routine.getIsPublic()) {
			throw CommonException.from(ExceptionCode.PRIVATE_ROUTINE);
		}
		return routine;
	}

	@Transactional
	public void deleteSocialBoard(Long userId, Long socialId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialBoardService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));

		if (!isBoardOwner(socialBoard, user)) {
			log.warn("권한이 없는 유저가 소셜 게시판 삭제 시도 - UserId: {}, SocialBoardId: {}", user.getId(), socialBoard.getId());
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD);
		}

		socialBoardService.deleteSocialBoard(socialBoard);
		log.info("소셜 게시글 삭제 - UserId: {}, SocialBoardId: {}", userId, socialId);
	}

	@Transactional(readOnly = true)
	public SocialDetailResponse getSocialDetail(final Long userId, final Long socialId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialBoardService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));

		boolean isBlockedUser = userService.isBlockedUser(user, socialBoard.getUser());
		if (isBlockedUser) {
			log.warn("차단된 사용자에 대한 게시글 접근 시도 - UserId: {}, BlockedUserId: {}, SocialBoardId: {}", userId,
				socialBoard.getUser().getId(), socialId);
			throw CommonException.from(ExceptionCode.BLOCKED_USER_SOCIAL_ACCESS);
		}

		List<SocialImageFile> imageFiles = socialBoardService.getSocialImagesBySocial(socialBoard);
		List<Comment> comments = socialInteractionService.getCommentsBySocial(socialBoard);
		List<CommentDto> commentDtos = convertCommentsToDto(user, comments);
		boolean isSocialBoardLiked = socialInteractionService.getSocialBoardIsLiked(user, socialBoard);

		log.info("소셜 게시글 단건 상세 조회 - UserId: {}, SocialBoardId: {}", userId, socialId);
		return SocialMapper.toSocialDetailResponse(socialBoard, imageFiles, commentDtos, isSocialBoardLiked);
	}

	@Transactional(readOnly = true)
	public CursorPaginationResponse<SocialResponse> getSocials(
		final Long userId,
		final Long cursor,
		final Integer limit,
		final List<Long> categoryIds
	) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		int actualLimit = PaginationUtil.resolveLimit(limit, DEFAULT_SOCIAL_PAGE_SIZE);
		int fetchLimit = PaginationUtil.calculateTotalFetchSize(actualLimit);

		validateCategories(userId, categoryIds);

		List<Social> socialBoards = socialBoardService.getSocials(cursor, fetchLimit, user.getId(), categoryIds);
		boolean hasMore = PaginationUtil.hasMore(socialBoards, actualLimit);
		Long nextCursor = PaginationUtil.getNextCursor(hasMore, socialBoards, actualLimit, Social::getId);

		List<SocialResponse> socialResponses = createSocialResponses(socialBoards, user, actualLimit);

		log.info("소셜 게시글 목록 조회 성공 - UserId: {}, HasMore: {}, NextCursor: {}", userId, hasMore, nextCursor);
		return PaginationUtil.toCursorPaginationResponse(hasMore, nextCursor, socialResponses);
	}

	private void validateCategories(Long userId, List<Long> categoryIds) {
		if (CollectionUtils.isEmpty(categoryIds)) {
			return;
		}

		List<SocialCategory> socialCategories = socialBoardService.findSocialCategoriesByIds(categoryIds);
		if (isInvalidCategoryIncluded(categoryIds, socialCategories)) {
			log.warn("유효하지 않은 카테고리 포함 - UserId: {}, 요청된 카테고리 IDs: {}", userId, categoryIds);
			throw CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY);
		}
	}

	private boolean isInvalidCategoryIncluded(
		final List<Long> socialCategoryIds,
		final List<SocialCategory> socialCategories
	) {
		return socialCategories.size() != socialCategoryIds.size();
	}

	private List<CommentDto> convertCommentsToDto(final User user, final List<Comment> comments) {
		return comments.stream()
			.map(comment -> createCommentDto(user, comment))
			.toList();
	}

	private CommentDto createCommentDto(final User user, final Comment comment) {
		Optional<CommentImageFile> commentImageFile = socialInteractionService.getCommentImageByComment(comment);
		boolean hasImage = commentImageFile.isPresent();
		String imageUrl = hasImage ? commentImageFile.get().getUrl() : null;

		boolean isCommentLike = socialInteractionService.getCommentIsLiked(user, comment);
		boolean isBlocked = userService.isBlockedUser(user, comment.getUser());

		return CommentMapper.toCommentDto(comment, hasImage, imageUrl, isCommentLike, isBlocked);
	}

	private List<SocialResponse> createSocialResponses(
		final List<Social> socialBoards,
		final User user,
		final int actualLimit
	) {
		return socialBoards.stream()
			.limit(actualLimit)
			.map(sb -> {
				List<SocialImageFile> imageFiles = socialBoardService.getSocialImagesBySocial(sb);
				int commentCounts = socialInteractionService.countCommentsBySocial(sb);
				boolean isSocialBoardLiked = socialInteractionService.getSocialBoardIsLiked(user, sb);
				return SocialMapper.toSocialResponse(sb, imageFiles, commentCounts, isSocialBoardLiked);
			})
			.toList();
	}

	@Transactional(readOnly = true)
	public SocialCategoryResponse getAllCategories() {
		List<SocialCategory> socialCategories = socialBoardService.findAllSocialCategories();

		List<SocialCategoryDto> socialCategoryDtos = socialCategories.stream()
			.map(SocialCategoryMapper::toSocialCategoryDto)
			.toList();

		return SocialCategoryMapper.toSocialCategoryResponse(socialCategoryDtos);
	}

	@Transactional(readOnly = true)
	public CursorPaginationResponse<SocialResponse> searchSocials(
		final Long userId,
		final String keyword,
		final Long cursor,
		final Integer limit,
		final List<Long> categoryIds
	) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		int actualLimit = PaginationUtil.resolveLimit(limit, DEFAULT_SOCIAL_PAGE_SIZE);
		int fetchLimit = PaginationUtil.calculateTotalFetchSize(actualLimit);

		validateCategories(userId, categoryIds);

		List<Social> searchResults = socialBoardService.searchSocialsWithFilters(
			userId,
			keyword,
			cursor,
			fetchLimit,
			categoryIds
		);

		boolean hasMore = PaginationUtil.hasMore(searchResults, actualLimit);
		Long nextCursor = PaginationUtil.getNextCursor(hasMore, searchResults, actualLimit, Social::getId);

		List<SocialResponse> searchResponses = createSocialResponses(searchResults, user, actualLimit);
		return PaginationUtil.toCursorPaginationResponse(hasMore, nextCursor, searchResponses);
	}

}

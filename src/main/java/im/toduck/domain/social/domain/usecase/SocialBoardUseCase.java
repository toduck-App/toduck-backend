package im.toduck.domain.social.domain.usecase;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.routine.domain.service.RoutineService;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.social.common.mapper.SocialMapper;
import im.toduck.domain.social.domain.service.SocialBoardService;
import im.toduck.domain.social.domain.service.SocialInteractionService;
import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.social.presentation.dto.request.SocialCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialUpdateRequest;
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

		Routine routine = null;
		if (request.routineId() != null) {
			routine = routineService.getUserRoutine(user, request.routineId())
				.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_ROUTINE));
		}

		Social socialBoard = socialBoardService.createSocialBoard(user, routine, request);
		List<SocialCategory> socialCategories = socialBoardService.findAllSocialCategories(request.socialCategoryIds());
		socialBoardService.addSocialCategoryLinks(request.socialCategoryIds(), socialCategories, socialBoard);
		socialBoardService.addSocialImageFiles(request.socialImageUrls(), socialBoard);

		log.info("소셜 게시글 생성 - UserId: {}, SocialBoardId: {}", userId, socialBoard.getId());
		return SocialMapper.toSocialCreateResponse(socialBoard);
	}

	@Transactional
	public void deleteSocialBoard(Long userId, Long socialId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialBoardService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));

		log.info("소셜 게시글 삭제 - UserId: {}, SocialBoardId: {}", userId, socialId);
		socialBoardService.deleteSocialBoard(user, socialBoard);
	}

	@Transactional
	public void updateSocialBoard(Long userId, Long socialId, SocialUpdateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Social socialBoard = socialBoardService.getSocialById(socialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));

		log.info("소셜 게시글 수정 - UserId: {}, SocialBoardId: {}", userId, socialId);
		socialBoardService.updateSocialBoard(user, socialBoard, request);
	}

	@Transactional(readOnly = true)
	public SocialDetailResponse getSocialDetail(Long userId, Long socialId) {
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
		List<Comment> comments = socialInteractionService.getCommentsBySocial(socialBoard, user.getId());
		boolean isLiked = socialInteractionService.getIsLiked(user, socialBoard);

		log.info("소셜 게시글 단건 상세 조회 - UserId: {}, SocialBoardId: {}", userId, socialId);
		return SocialMapper.toSocialDetailResponse(socialBoard, imageFiles, comments, isLiked);
	}

	@Transactional(readOnly = true)
	public CursorPaginationResponse<SocialResponse> getSocials(Long userId, Long cursor, Integer limit) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		int actualLimit = PaginationUtil.resolveLimit(limit, DEFAULT_SOCIAL_PAGE_SIZE);
		int fetchLimit = PaginationUtil.calculateTotalFetchSize(actualLimit);

		List<Social> socialBoards = fetchSocialBoards(cursor, fetchLimit, user.getId());
		boolean hasMore = PaginationUtil.hasMore(socialBoards, actualLimit);
		Long nextCursor = PaginationUtil.getNextCursor(hasMore, socialBoards, actualLimit, Social::getId);

		List<SocialResponse> socialResponses = createSocialResponses(socialBoards, user, actualLimit);

		log.info("소셜 게시글 목록 조회 - UserId: {}, HasMore: {}, NextCursor: {}", userId, hasMore, nextCursor);
		return PaginationUtil.toCursorPaginationResponse(hasMore, nextCursor, socialResponses);
	}

	private List<Social> fetchSocialBoards(Long cursor, int fetchLimit, Long currentUserId) {
		if (cursor == null) {
			return socialBoardService.findLatestSocials(fetchLimit, currentUserId);
		}
		return socialBoardService.getSocials(cursor, fetchLimit, currentUserId);
	}

	private List<SocialResponse> createSocialResponses(List<Social> socialBoards, User user, int actualLimit) {
		return socialBoards.stream()
			.limit(actualLimit)
			.map(sb -> {
				List<SocialImageFile> imageFiles = socialBoardService.getSocialImagesBySocial(sb);
				List<Comment> comments = socialInteractionService.getCommentsBySocial(sb, user.getId());
				boolean isLiked = socialInteractionService.getIsLiked(user, sb);
				return SocialMapper.toSocialResponse(sb, imageFiles, comments.size(), isLiked);
			})
			.toList();
	}
}

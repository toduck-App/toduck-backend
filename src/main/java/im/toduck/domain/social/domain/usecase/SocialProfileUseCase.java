package im.toduck.domain.social.domain.usecase;

import static im.toduck.domain.social.presentation.dto.response.SocialCategoryResponse.*;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.routine.common.mapper.RoutineMapper;
import im.toduck.domain.routine.domain.service.RoutineService;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.social.common.mapper.SocialMapper;
import im.toduck.domain.social.common.mapper.SocialProfileMapper;
import im.toduck.domain.social.domain.service.SocialBoardService;
import im.toduck.domain.social.domain.service.SocialCategoryService;
import im.toduck.domain.social.domain.service.SocialInteractionService;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.social.presentation.dto.response.SocialProfileResponse;
import im.toduck.domain.social.presentation.dto.response.SocialResponse;
import im.toduck.domain.social.presentation.dto.response.UserProfileRoutineListResponse;
import im.toduck.domain.user.domain.service.FollowService;
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
public class SocialProfileUseCase {
	private static final int DEFAULT_PROFILE_SOCIAL_PAGE_SIZE = 10;

	private final UserService userService;
	private final FollowService followService;
	private final SocialBoardService socialBoardService;
	private final SocialInteractionService socialInteractionService;
	private final RoutineService routineService;
	private final SocialCategoryService socialCategoryService;

	@Transactional(readOnly = true)
	public SocialProfileResponse getUserProfile(final Long profileUserId, final Long authUserId) {
		User authUser = userService.getUserById(authUserId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		User profileUser = userService.getUserById(profileUserId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		int followingCount = followService.countFollowing(profileUserId);
		int followerCount = followService.countFollowers(profileUserId);
		int postCount = socialBoardService.countSocialPostsByUserId(profileUserId);
		int totalRoutineShareCount = routineService.getTotalRoutineShareCount(profileUser);
		int commentCount = socialInteractionService.countActiveCommentsByUser(profileUser);
		boolean isMe = profileUserId.equals(authUserId);
		boolean isFollowing = !isMe && followService.isFollowing(authUser, profileUser);

		log.info("프로필 조회 - 요청자 UserId: {}, 대상 UserId: {}", authUserId, profileUserId);
		return SocialProfileMapper.toSocialProfileResponse(
			profileUser,
			followingCount,
			followerCount,
			postCount,
			totalRoutineShareCount,
			commentCount,
			isMe,
			isFollowing
		);
	}

	@Transactional(readOnly = true)
	public CursorPaginationResponse<SocialResponse> getUserSocials(
		final Long profileUserId,
		final Long authUserId,
		final Long cursor,
		final Integer limit
	) {
		User profileUser = userService.getUserById(profileUserId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		// TODO: profileUser의 프로필 공개 여부 필드 추가 후 처리 예외 처리

		User authUser = userService.getUserById(authUserId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		int actualLimit = PaginationUtil.resolveLimit(limit, DEFAULT_PROFILE_SOCIAL_PAGE_SIZE);
		int fetchLimit = PaginationUtil.calculateTotalFetchSize(actualLimit);

		List<Social> userSocials = socialBoardService.getSocialsByUserId(
			profileUser.getId(),
			authUser.getId(),
			cursor,
			fetchLimit
		);

		boolean hasMore = PaginationUtil.hasMore(userSocials, actualLimit);
		Long nextCursor = PaginationUtil.getNextCursor(hasMore, userSocials, actualLimit, Social::getId);

		List<SocialResponse> socialResponses = createSocialResponsesForUser(userSocials, authUser, actualLimit);

		log.info("유저 게시글 목록 조회 - 대상 UserId: {}, 요청자 UserId: {}, HasMore: {}, NextCursor: {}",
			profileUserId, authUserId, hasMore, nextCursor);

		return PaginationUtil.toCursorPaginationResponse(hasMore, nextCursor, socialResponses);
	}

	private List<SocialResponse> createSocialResponsesForUser(
		final List<Social> socialBoards,
		final User requestingUser,
		final int actualLimit
	) {
		return socialBoards.stream()
			.limit(actualLimit)
			.map(social -> {
				List<SocialImageFile> imageFiles = socialBoardService.getSocialImagesBySocial(social);
				int commentCount = socialInteractionService.countCommentsBySocial(social);
				boolean isLikedByRequestingUser = socialInteractionService.getSocialBoardIsLiked(requestingUser,
					social);
				List<SocialCategoryDto> socialCategoryDtos = socialCategoryService.getSocialCategoryDtosBySocial(
					social);
				return SocialMapper.toSocialResponse(
					social,
					imageFiles,
					socialCategoryDtos,
					commentCount,
					isLikedByRequestingUser
				);
			})
			.toList();
	}

	@Transactional(readOnly = true)
	public UserProfileRoutineListResponse readUserAvailableRoutines(final Long profileUserId, final Long authUserId) {
		User profileUser = userService.getUserById(profileUserId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		// TODO: profileUser의 프로필 공개 여부 필드 추가 후 처리 예외 처리

		List<Routine> routines = routineService.getSocialProfileRoutines(profileUser);

		log.info("사용자 ID {}의 공개 루틴 목록 조회 - 요청자 Id: {}", profileUserId, authUserId);
		return RoutineMapper.toUserProfileRoutineListResponse(routines);
	}

	@Transactional
	public RoutineCreateResponse saveSharedRoutine(
		final Long userId,
		final Long sourceRoutineId,
		final RoutineCreateRequest request
	) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Routine sourceRoutine = routineService.findAvailablePublicRoutineById(sourceRoutineId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_ROUTINE));

		RoutineCreateResponse routineCreateResponse = routineService.create(user, request);
		routineService.incrementSharedCountAtomically(sourceRoutine);
		return routineCreateResponse;
	}
}

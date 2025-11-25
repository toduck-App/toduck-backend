package im.toduck.domain.social.domain.usecase;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.notification.domain.event.RoutineShareMilestoneNotificationEvent;
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
import im.toduck.domain.social.presentation.dto.response.SocialCategoryResponse.SocialCategoryDto;
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
	private static final int ROUTINE_SHARE_MILESTONE = 100;

	private final ApplicationEventPublisher eventPublisher;
	private final UserService userService;
	private final FollowService followService;
	private final SocialBoardService socialBoardService;
	private final SocialInteractionService socialInteractionService;
	private final SocialCategoryService socialCategoryService;
	private final RoutineService routineService;

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
		int totalCommentCount = socialInteractionService.countActiveCommentsByUser(profileUser);
		boolean isMe = profileUserId.equals(authUserId);
		boolean isFollowing = !isMe && followService.isFollowing(authUser, profileUser);

		log.info("프로필 조회 - 요청자 UserId: {}, 대상 UserId: {}", authUserId, profileUserId);
		return SocialProfileMapper.toSocialProfileResponse(
			profileUser,
			followingCount,
			followerCount,
			postCount,
			totalRoutineShareCount,
			totalCommentCount,
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

		List<Social> limitedSocials = userSocials.stream().limit(actualLimit).toList();
		List<SocialResponse> socialResponses = buildSocialResponses(limitedSocials, authUser);

		log.info("유저 게시글 목록 조회 - 대상 UserId: {}, 요청자 UserId: {}, HasMore: {}, NextCursor: {}",
			profileUserId, authUserId, hasMore, nextCursor);

		return PaginationUtil.toCursorPaginationResponse(hasMore, nextCursor, socialResponses);
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

		int newSharedCount = routineService.incrementSharedCountAndGetNewCount(sourceRoutine);
		if (newSharedCount == ROUTINE_SHARE_MILESTONE) {
			eventPublisher.publishEvent(
				RoutineShareMilestoneNotificationEvent.of(
					sourceRoutine.getUser().getId(),
					sourceRoutine.getTitle(),
					newSharedCount
				)
			);
			log.info("루틴 공유 마일스톤 달성 - 루틴 ID: {}, 공유 횟수: {}", sourceRoutineId, newSharedCount);
		}

		return routineCreateResponse;
	}

	private List<SocialResponse> buildSocialResponses(final List<Social> socialBoards, final User requestingUser) {
		if (socialBoards.isEmpty()) {
			return List.of();
		}

		List<Long> socialIds = socialBoards.stream()
			.map(Social::getId)
			.toList();

		Map<Long, List<SocialImageFile>> imageFilesBySocialId = socialBoardService.getSocialImagesBySocialIds(socialIds)
			.stream()
			.collect(Collectors.groupingBy(sif -> sif.getSocial().getId()));

		Map<Long, Integer> commentCountsBySocialId = socialInteractionService.countCommentsBySocialIds(socialIds);
		Set<Long> likedSocialIds = socialInteractionService.getLikedSocialIdsByUserAndSocialIds(
			requestingUser,
			socialIds
		);
		Map<Long, List<SocialCategoryDto>> categoryDtosBySocialId =
			socialCategoryService.getSocialCategoryDtosBySocialIds(socialIds);

		return SocialMapper.toSocialResponses(
			socialBoards,
			imageFilesBySocialId,
			commentCountsBySocialId,
			likedSocialIds,
			categoryDtosBySocialId
		);
	}
}

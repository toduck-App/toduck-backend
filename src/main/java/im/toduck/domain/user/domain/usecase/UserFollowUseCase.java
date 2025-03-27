package im.toduck.domain.user.domain.usecase;

import im.toduck.domain.user.domain.service.FollowService;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class UserFollowUseCase {

	private final UserService userService;
	private final FollowService followService;

	@Transactional
	public void followUser(final Long followerId, final Long followedUserId) {
		User follower = userService.getUserById(followerId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		User followed = userService.getUserById(followedUserId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		if (followerId.equals(followedUserId)) {
			log.warn("팔로우 실패 - 사용자가 자신을 팔로우하려고 시도했습니다. UserId: {}", followerId);
			throw CommonException.from(ExceptionCode.CANNOT_FOLLOW_SELF);
		}

		if (followService.isFollowing(follower, followed)) {
			log.warn("팔로우 실패 - 이미 팔로우 중입니다. FollowerId: {}, FollowedUserId: {}", followerId, followedUserId);
			throw CommonException.from(ExceptionCode.ALREADY_FOLLOWING);
		}

		followService.followUser(follower, followed);
		log.info("팔로우 성공 - FollowerId: {}, FollowedUserId: {}", followerId, followedUserId);
	}

	@Transactional
	public void unfollowUser(final Long followerId, final Long followedUserId) {
		User follower = userService.getUserById(followerId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		User followed = userService.getUserById(followedUserId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		if (!followService.isFollowing(follower, followed)) {
			log.warn("언팔로우 실패 - 팔로우 관계를 찾을 수 없습니다. FollowerId: {}, FollowedUserId: {}", followerId, followedUserId);
			throw CommonException.from(ExceptionCode.NOT_FOUND_FOLLOW);
		}

		followService.unfollowUser(follower, followed);
		log.info("언팔로우 성공 - FollowerId: {}, FollowedUserId: {}", followerId, followedUserId);
	}
}

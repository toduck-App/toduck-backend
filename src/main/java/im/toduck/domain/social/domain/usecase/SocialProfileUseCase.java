package im.toduck.domain.social.domain.usecase;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.social.common.mapper.SocialProfileMapper;
import im.toduck.domain.social.domain.service.SocialBoardService;
import im.toduck.domain.social.presentation.dto.response.SocialProfileResponse;
import im.toduck.domain.user.domain.service.FollowService;
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
public class SocialProfileUseCase {
	private final UserService userService;
	private final FollowService followService;
	private final SocialBoardService socialBoardService;

	@Transactional(readOnly = true)
	public SocialProfileResponse getUserProfile(final Long profileUserId, final Long authUserId) {
		User profileUser = userService.getUserById(profileUserId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		int followingCount = followService.countFollowing(profileUserId);
		int followerCount = followService.countFollowers(profileUserId);
		int postCount = socialBoardService.countSocialPostsByUserId(profileUserId);
		boolean isMe = profileUserId.equals(authUserId);

		log.info("프로필 조회 - 요청자 UserId: {}, 대상 UserId: {}", authUserId, profileUserId);
		return SocialProfileMapper.toSocialProfileResponse(
			profileUser.getNickname(),
			followingCount,
			followerCount,
			postCount,
			isMe
		);
	}
}

package im.toduck.domain.user.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.user.common.mapper.FollowMapper;
import im.toduck.domain.user.persistence.entity.Follow;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.repository.FollowRepository;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {

	private final FollowRepository followRepository;

	@Transactional
	public void followUser(final User follower, final User followed) {
		Follow follow = FollowMapper.toFollow(follower, followed);
		followRepository.save(follow);
	}

	@Transactional
	public void unfollowUser(final User follower, final User followed) {
		Follow follow = followRepository.findByFollowerAndFollowed(follower, followed)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_FOLLOW));
		followRepository.delete(follow);
	}

	@Transactional(readOnly = true)
	public boolean isFollowing(final User follower, final User followed) {
		return followRepository.existsByFollowerAndFollowed(follower, followed);
	}

	@Transactional(readOnly = true)
	public int countFollowing(final Long userId) {
		return (int)followRepository.countByFollower_Id(userId);
	}

	@Transactional(readOnly = true)
	public int countFollowers(final Long userId) {
		return (int)followRepository.countByFollowed_Id(userId);
	}
}

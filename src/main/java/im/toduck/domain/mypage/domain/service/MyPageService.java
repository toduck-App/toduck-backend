package im.toduck.domain.mypage.domain.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.mypage.presentation.dto.response.MyCommentsResponse;
import im.toduck.domain.social.persistence.repository.CommentRepository;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.util.PaginationUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;

	@Transactional
	public void updateUniqueNickname(User user, String nickname) {
		try {
			userRepository.updateNickname(user, nickname);
		} catch (DataIntegrityViolationException e) {
			throw CommonException.from(ExceptionCode.EXISTS_USER_NICKNAME);
		}
	}

	@Transactional
	public void updateProfileImage(User user, String imageUrl) {
		userRepository.updateProfileImageUrl(user, imageUrl);
	}

	@Transactional(readOnly = true)
	public List<User> getBlockedUsers(final User user) {
		return userRepository.findBlockedUsersByUser(user);
	}

	@Transactional(readOnly = true)
	public List<MyCommentsResponse> getMyCommentsResponse(final Long userId, final Long cursor, final int limit) {
		PageRequest pageRequest = PageRequest.of(PaginationUtil.FIRST_PAGE_INDEX, limit);
		return commentRepository.findMyCommentsWithProjection(userId, cursor, pageRequest);
	}
}

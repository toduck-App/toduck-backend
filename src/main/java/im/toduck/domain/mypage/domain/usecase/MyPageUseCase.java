package im.toduck.domain.mypage.domain.usecase;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.mypage.domain.service.MyPageService;
import im.toduck.domain.mypage.presentation.dto.request.NickNameUpdateRequest;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class MyPageUseCase {
	private final UserService userService;
	private final MyPageService myPageService;

	private static final long UPDATE_SUCCESS = 1L;

	@Transactional
	public void updateNickname(Long userId, NickNameUpdateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		if (myPageService.updateUniqueNickname(user, request.nickname()) != UPDATE_SUCCESS) {
			throw CommonException.from(ExceptionCode.EXISTS_USER_NICKNAME);
		}
	}
}

package im.toduck.domain.mypage.domain.usecase;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.mypage.common.mapper.MyPageMapper;
import im.toduck.domain.mypage.domain.service.MyPageService;
import im.toduck.domain.mypage.presentation.dto.request.NickNameUpdateRequest;
import im.toduck.domain.mypage.presentation.dto.request.ProfileImageUpdateRequest;
import im.toduck.domain.mypage.presentation.dto.request.UserDeleteRequest;
import im.toduck.domain.mypage.presentation.dto.response.BlockedUsersResponse;
import im.toduck.domain.mypage.presentation.dto.response.NickNameResponse;
import im.toduck.domain.social.domain.service.SocialBoardService;
import im.toduck.domain.user.domain.service.FollowService;
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
	private final FollowService followService;
	private final SocialBoardService socialBoardService;

	@Transactional
	public void updateNickname(Long userId, NickNameUpdateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		myPageService.updateUniqueNickname(user, request.nickname());
	}

	public NickNameResponse getMyNickname(Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		return NickNameResponse.builder()
			.nickname(user.getNickname())
			.build();
	}

	@Transactional
	public void updateProfileImage(Long userId, ProfileImageUpdateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		myPageService.updateProfileImage(user, request.imageUrl());
	}

	@Transactional
	public void deleteAccount(Long userId, UserDeleteRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		myPageService.recordUserDeletionLog(user, request);
		this.deleteUserData(user);
	}

	private void deleteUserData(User user) {
		followService.deleteAllFollowsByUser(user);
		userService.deleteAllBlocksByUser(user);

		socialBoardService.deleteAllSocialBoardsByUser(user);

		// TODO. 즉각 반영되지 않아도 되는 데이터(diary, 공유되지 않은 routine, concentration) 배치 처리
		// diaryService.deleteAllDiariesByUser(user);
		//
		// List<Routine> routines = routineService.findAllUnsharedRoutineByUser(user);
		// routines.forEach(routineRecordService::removeAllByRoutine);
		// routineService.deleteAllUnsharedRoutinesByUser(user);
		//
		// concentrationService.deleteAllConcentrationsByUser(user);

		// TODO. schedule 관련 삭제 추가

		userService.softDelete(user);
  }

	@Transactional(readOnly = true)
	public BlockedUsersResponse getBlockedUsers(final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		List<User> blockedUsers = myPageService.getBlockedUsers(user);

		return MyPageMapper.toBlockedUsersResponse(blockedUsers);
	}
}

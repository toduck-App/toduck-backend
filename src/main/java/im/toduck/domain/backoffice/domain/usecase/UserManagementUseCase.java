package im.toduck.domain.backoffice.domain.usecase;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.backoffice.common.mapper.AccountDeletionLogMapper;
import im.toduck.domain.backoffice.presentation.dto.request.UserSuspendRequest;
import im.toduck.domain.backoffice.presentation.dto.response.AccountDeletionLogListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.DeletionReasonStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UserDetailResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UserListResponse;
import im.toduck.domain.mypage.domain.service.MyPageService;
import im.toduck.domain.mypage.persistence.entity.AccountDeletionLog;
import im.toduck.domain.mypage.persistence.entity.AccountDeletionReason;
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
public class UserManagementUseCase {

	private final MyPageService myPageService;
	private final UserService userService;

	@Transactional(readOnly = true)
	public AccountDeletionLogListResponse getAllDeletionLogs() {
		List<AccountDeletionLog> logs = myPageService.getAllAccountDeletionLogs();

		log.info("백오피스 회원 탈퇴 사유 목록 조회 - 총 탈퇴 회원수: {}", logs.size());

		return AccountDeletionLogMapper.toAccountDeletionLogListResponse(logs);
	}

	@Transactional(readOnly = true)
	public DeletionReasonStatisticsResponse getDeletionReasonStatistics() {
		Map<AccountDeletionReason, Long> reasonCounts = myPageService.getDeletionReasonStatistics();
		long totalCount = reasonCounts.values().stream().mapToLong(Long::longValue).sum();

		log.info("백오피스 탈퇴 사유별 통계 조회 - 총 탈퇴 회원수: {}, 사유 종류: {}",
			totalCount, reasonCounts.size());

		return AccountDeletionLogMapper.toDeletionReasonStatisticsResponse(reasonCounts, totalCount);
	}

	@Transactional(readOnly = true)
	public UserListResponse getAllUsers() {
		List<User> users = userService.getAllUsers();

		List<UserListResponse.UserInfo> userInfos = users.stream()
			.map(this::toUserInfo)
			.toList();

		log.info("백오피스 전체 유저 목록 조회 - 총 유저수: {}", users.size());

		return UserListResponse.builder()
			.users(userInfos)
			.totalCount((long) users.size())
			.build();
	}

	@Transactional(readOnly = true)
	public UserDetailResponse getUserDetail(Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		log.info("백오피스 유저 상세 조회 - 유저ID: {}, 닉네임: {}", userId, user.getNickname());

		return toUserDetailResponse(user);
	}

	@Transactional
	public void suspendUser(Long userId, UserSuspendRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUserLoginId = authentication.getName();

		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		if (user.getLoginId().equals(currentUserLoginId)) {
			throw CommonException.from(ExceptionCode.CANNOT_SUSPEND_SELF);
		}

		user.suspend(request.getSuspendedUntil(), request.getSuspensionReason());

		log.info("백오피스 유저 정지 - 유저ID: {}, 닉네임: {}, 정지 해제일: {}, 사유: {}",
			userId, user.getNickname(), request.getSuspendedUntil(), request.getSuspensionReason());
	}

	@Transactional
	public void unsuspendUser(Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		user.unsuspend();

		log.info("백오피스 유저 정지 해제 - 유저ID: {}, 닉네임: {}", userId, user.getNickname());
	}

	private UserListResponse.UserInfo toUserInfo(User user) {
		return UserListResponse.UserInfo.builder()
			.id(user.getId())
			.nickname(user.getNickname())
			.phoneNumber(user.getPhoneNumber())
			.email(user.getEmail())
			.role(user.getRole())
			.suspended(user.isSuspended())
			.suspendedUntil(user.getSuspendedUntil())
			.suspensionReason(user.getSuspensionReason())
			.createdAt(user.getCreatedAt())
			.build();
	}

	private UserDetailResponse toUserDetailResponse(User user) {
		return UserDetailResponse.builder()
			.id(user.getId())
			.nickname(user.getNickname())
			.phoneNumber(user.getPhoneNumber())
			.loginId(user.getLoginId())
			.email(user.getEmail())
			.imageUrl(user.getImageUrl())
			.role(user.getRole())
			.provider(user.getProvider())
			.suspended(user.isSuspended())
			.suspendedUntil(user.getSuspendedUntil())
			.suspensionReason(user.getSuspensionReason())
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getUpdatedAt())
			.build();
	}
}

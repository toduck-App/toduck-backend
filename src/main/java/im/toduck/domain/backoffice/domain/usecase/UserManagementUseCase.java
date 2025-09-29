package im.toduck.domain.backoffice.domain.usecase;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.backoffice.common.mapper.AccountDeletionLogMapper;
import im.toduck.domain.backoffice.common.mapper.UserManagementMapper;
import im.toduck.domain.backoffice.presentation.dto.request.UserSearchRequest;
import im.toduck.domain.backoffice.presentation.dto.request.UserSuspendRequest;
import im.toduck.domain.backoffice.presentation.dto.response.AccountDeletionLogListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.DeletionReasonStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UserListPaginationResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UserStatisticsResponse;
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
	public UserListPaginationResponse getUsersWithFilters(final UserSearchRequest searchRequest) {
		Pageable pageable = PageRequest.of(searchRequest.page(), searchRequest.size());

		Page<User> userPage = userService.getUsersWithFilters(
			searchRequest.keyword(),
			searchRequest.searchType(),
			searchRequest.status(),
			searchRequest.role(),
			searchRequest.provider(),
			searchRequest.sortBy(),
			searchRequest.sortDirection(),
			pageable
		);

		log.info("백오피스 회원 목록 조회 - 페이지: {}, 크기: {}, 총 회원수: {}, 검색어: '{}', 필터: status={}, role={}, provider={}",
			searchRequest.page(), searchRequest.size(), userPage.getTotalElements(),
			searchRequest.keyword(), searchRequest.status(), searchRequest.role(), searchRequest.provider());

		return UserManagementMapper.toUserListPaginationResponse(userPage);
	}

	@Transactional
	public void suspendUser(final Long currentUserId, final Long userId, final UserSuspendRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		if (Objects.equals(user.getId(), currentUserId)) {
			throw CommonException.from(ExceptionCode.CANNOT_SUSPEND_SELF);
		}

		user.suspend(request.getSuspendedUntil(), request.getSuspensionReason());

		log.info("백오피스 유저 정지 - 현재 관리자ID: {}, 대상 유저ID: {}, 닉네임: {}, 정지 해제일: {}, 사유: {}",
			currentUserId, userId, user.getNickname(), request.getSuspendedUntil(), request.getSuspensionReason());
	}

	@Transactional
	public void unsuspendUser(Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		user.unsuspend();

		log.info("백오피스 유저 정지 해제 - 유저ID: {}, 닉네임: {}", userId, user.getNickname());
	}

	@Transactional(readOnly = true)
	public UserStatisticsResponse getUserStatistics() {
		long totalUsers = userService.getCountByProvider(null);
		long generalUsers = userService.getCountByProvider("GENERAL");
		long kakaoUsers = userService.getCountByProvider("KAKAO");
		long appleUsers = userService.getCountByProvider("APPLE");

		long calculatedTotal = generalUsers + kakaoUsers + appleUsers;

		log.info("백오피스 회원 유형별 통계 조회 - 전체: {}, 일반: {}, 카카오: {}, 애플: {}",
			calculatedTotal, generalUsers, kakaoUsers, appleUsers);

		return new UserStatisticsResponse(calculatedTotal, generalUsers, kakaoUsers, appleUsers);
	}
}

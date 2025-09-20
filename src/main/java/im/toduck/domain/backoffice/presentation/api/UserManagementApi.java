package im.toduck.domain.backoffice.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.backoffice.presentation.dto.request.UserSuspendRequest;
import im.toduck.domain.backoffice.presentation.dto.response.AccountDeletionLogListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.DeletionReasonStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.NotificationStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UserListPaginationResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UserStatisticsResponse;
import im.toduck.domain.user.persistence.entity.UserRole;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "BackOffice User Management")
public interface UserManagementApi {
	@Operation(
		summary = "회원 탈퇴 사유 목록 조회",
		description = "모든 회원의 탈퇴 사유 목록을 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = AccountDeletionLogListResponse.class,
			description = "회원 탈퇴 사유 목록 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<AccountDeletionLogListResponse>> getDeletionLogs();

	@Operation(
		summary = "탈퇴 사유별 통계 조회",
		description = "탈퇴 사유별 건수 통계를 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = DeletionReasonStatisticsResponse.class,
			description = "탈퇴 사유별 통계 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<DeletionReasonStatisticsResponse>> getDeletionReasonStatistics();

	@Operation(
		summary = "회원 유형별 통계 조회",
		description = "회원 유형별(일반회원, 카카오, 애플) 통계를 조회합니다. 탈퇴한 회원은 집계에서 제외됩니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = UserStatisticsResponse.class,
			description = "회원 유형별 통계 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<UserStatisticsResponse>> getUserStatistics();

	@Operation(
		summary = "알림 발송 통계 조회",
		description = "알림 유형별 발송 개수, 전체 발송 개수, 오늘 발송 개수 등 알림 발송 통계를 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = NotificationStatisticsResponse.class,
			description = "알림 발송 통계 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<NotificationStatisticsResponse>> getNotificationStatistics();

	@Operation(
		summary = "회원 목록 조회 (페이지네이션, 검색, 필터링)",
		description = """
			백오피스에서 회원 목록을 페이지네이션과 함께 조회합니다.

			**검색 기능:**
			- keyword: 검색 키워드
			- searchType: 검색 대상 필드 (nickname, email, phone, loginid 중 하나, 미입력 시 전체 필드 검색)

			**필터 기능:**
			- status: 회원 상태 (all: 전체, active: 활성, suspended: 정지)
			- role: 회원 역할 (USER, ADMIN 등)
			- provider: 회원 유형 (GENERAL: 일반회원, KAKAO: 카카오 로그인, APPLE: 애플 로그인)

			**정렬 기능:**
			- sortBy: 정렬 기준 (createdAt, nickname, email, role, suspendedAt, updatedAt)
			- sortDirection: 정렬 방향 (asc: 오름차순, desc: 내림차순)

			**회원 정보 구조:**
			- 일반 회원: phoneNumber, loginId 존재, email/provider null
			- 소셜 로그인 회원: email, provider 존재, phoneNumber/loginId null
			- 현재 정지된 회원: suspended=true, suspendedUntil(정지 해제일), suspensionReason(정지 사유) 제공
			- 정지 기간이 만료된 회원: suspended=false, suspendedUntil=null, suspensionReason=null (과거 정지 정보는 숨김)

			**주의사항:**
			- 탈퇴한 회원(deletedAt이 null이 아닌 회원)은 조회 결과에서 제외됩니다.
			- 닉네임이 null인 경우 "탈퇴한 회원"으로 표시됩니다.
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = UserListPaginationResponse.class,
			description = "회원 목록 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<UserListPaginationResponse>> getUsers(
		@Parameter(description = "검색 키워드", example = "홍길동")
		@RequestParam(required = false) String keyword,

		@Parameter(description = "검색 필드 유형 (nickname: 닉네임, email: 이메일, phone: 전화번호, loginid: 로그인ID, 미입력: 전체)",
			example = "nickname")
		@RequestParam(required = false) String searchType,

		@Parameter(description = "회원 상태 (all: 전체, active: 활성, suspended: 정지)",
			example = "all")
		@RequestParam(defaultValue = "all") String status,

		@Parameter(description = "회원 역할", example = "USER")
		@RequestParam(required = false) UserRole role,

		@Parameter(description = "회원 유형 필터 (GENERAL: 일반회원, KAKAO: 카카오 로그인, APPLE: 애플 로그인)", example = "KAKAO")
		@RequestParam(required = false) String provider,

		@Parameter(description = "정렬 기준 (createdAt: 생성일, nickname: 닉네임, email: 이메일, role: 역할,"
			+ "suspendedAt: 정지일, updatedAt: 수정일)",
			example = "createdAt")
		@RequestParam(defaultValue = "createdAt") String sortBy,

		@Parameter(description = "정렬 방향 (asc: 오름차순, desc: 내림차순)",
			example = "desc")
		@RequestParam(defaultValue = "desc") String sortDirection,

		@Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
		@RequestParam(defaultValue = "0") Integer page,

		@Parameter(description = "페이지 크기 (최대 100)", example = "20")
		@RequestParam(defaultValue = "20") Integer size
	);

	@Operation(
		summary = "유저 정지",
		description = "특정 유저를 정지 상태로 변경합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "유저 정지 성공"
		)
	)
	ResponseEntity<ApiResponse<?>> suspendUser(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@Parameter(description = "유저 ID", example = "1")
		@PathVariable final Long userId,
		@RequestBody @Valid final UserSuspendRequest request
	);

	@Operation(
		summary = "유저 정지 해제",
		description = "정지된 유저의 정지 상태를 해제합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "유저 정지 해제 성공"
		)
	)
	ResponseEntity<ApiResponse<?>> unsuspendUser(
		@Parameter(description = "유저 ID", example = "1")
		@PathVariable final Long userId
	);
}

package im.toduck.domain.backoffice.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.backoffice.presentation.dto.request.UserSuspendRequest;
import im.toduck.domain.backoffice.presentation.dto.response.AccountDeletionLogListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.DeletionReasonStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UserDetailResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UserListResponse;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.presentation.ApiResponse;
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
		summary = "유저 목록 조회",
		description = "모든 유저의 목록을 조회합니다. 정지 상태 정보도 포함됩니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = UserListResponse.class,
			description = "유저 목록 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<UserListResponse>> getUsers();

	@Operation(
		summary = "유저 상세 조회",
		description = "특정 유저의 상세 정보를 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = UserDetailResponse.class,
			description = "유저 상세 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(
		@Parameter(description = "유저 ID", example = "1")
		@PathVariable final Long userId
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

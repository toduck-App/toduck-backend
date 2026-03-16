package im.toduck.domain.admin.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.admin.presentation.dto.request.AdminCreateRequest;
import im.toduck.domain.admin.presentation.dto.request.AdminUpdateRequest;
import im.toduck.domain.admin.presentation.dto.response.AdminListResponse;
import im.toduck.domain.admin.presentation.dto.response.AdminResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin")
public interface AdminApi {
	@Operation(
		summary = "관리자 조회",
		description = "관리자를 조회합니다. 관리자 권한을 가진 사용자만 요청할 수 있습니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = AdminResponse.class,
			description = "관리자 조회 성공. 관리자 데이터를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_ADMIN)
		}
	)
	ResponseEntity<ApiResponse<AdminResponse>> getAdmin(
		@PathVariable final Long userId
	);

	@Operation(
		summary = "관리자 목록 조회",
		description = "관리자 목록을 조회합니다. 관리자 권한을 가진 사용자만 요청할 수 있습니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = AdminListResponse.class,
			description = "관리자 목록 조회 성공. 관리자 목록 데이터를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<AdminListResponse>> getAdmins();

	@Operation(
		summary = "관리자 생성",
		description = """
			관리자를 생성합니다. 관리자 권한을 가진 사용자만 요청할 수 있습니다.<br/>
			"userId": 관리자에 등록할 사용자 ID를 입력합니다. Role이 User에서 Admin으로 승격됩니다.<br/>
			"displayName": 문의 답변 등에서 사용자들에게 보여질 이름입니다.<br/>
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "관리자 생성 성공. 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_USER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.DUPLICATE_ADMIN)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> createAdmin(
		@RequestBody final AdminCreateRequest request
	);

	@Operation(
		summary = "관리자 정보 수정",
		description = """
			관리자 정보를 수정합니다. 관리자 권한을 가진 사용자만 요청할 수 있습니다.<br/>
			Path Variable에는 관리자의 사용자 ID를 입력합니다.<br/>
			<br/>
			"displayName": 문의 답변 등에서 사용자들에게 보여질 이름입니다. null인 경우 수정되지 않습니다.<br/>
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "관리자 정보 수정 성공. 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_USER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_ADMIN)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> updateAdmin(
		@Parameter(
			name = "userId",
			description = "수정할 관리자의 사용자 ID",
			required = true,
			example = "1"
		)
		@PathVariable final Long userId,
		@RequestBody final AdminUpdateRequest request
	);

	@Operation(
		summary = "관리자 정보 삭제",
		description = """
			관리자 정보를 삭제합니다. 관리자 권한을 가진 사용자만 요청할 수 있습니다.<br/>
			Path Variable에는 관리자의 사용자 ID를 입력합니다.<br/>
			해당 사용자의 관리자 정보가 논리 삭제되며 관리자 권한 또한 제거됩니다.<br/>
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "관리자 정보 삭제 성공. 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_USER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_ADMIN)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> deleteAdmin(
		@Parameter(
			name = "userId",
			description = "삭제할 관리자의 사용자 ID",
			required = true,
			example = "1"
		)
		@PathVariable final Long userId
	);
}

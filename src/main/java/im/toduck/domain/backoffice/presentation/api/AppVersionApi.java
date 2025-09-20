package im.toduck.domain.backoffice.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.backoffice.presentation.dto.request.AppVersionCreateRequest;
import im.toduck.domain.backoffice.presentation.dto.request.UpdatePolicyRequest;
import im.toduck.domain.backoffice.presentation.dto.response.AppVersionListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.AppVersionResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UpdatePolicyResponse;
import im.toduck.domain.backoffice.presentation.dto.response.VersionCheckResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "App Version", description = "앱 버전 관리 API")
public interface AppVersionApi {

	@Operation(
		summary = "앱 버전 체크",
		description = """
			클라이언트 앱의 현재 버전을 확인하여 업데이트 정책을 반환합니다.

			**업데이트 상태 설명:**
			- `FORCE`: 강제 업데이트 필요 (앱 사용 불가)
			- `RECOMMENDED`: 권장 업데이트 (앱 사용 가능, 업데이트 권장)
			- `NONE`: 업데이트 불필요 (최신 버전 또는 업데이트 정책 없음)

			**처리 로직:**
			1. 클라이언트 버전을 DB에서 조회
			2. 해당 버전의 updateType 확인
			3. FORCE/RECOMMENDED → 해당 상태 반환
			4. LATEST/NONE 또는 DB에 없는 버전 → `NONE` 반환
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = VersionCheckResponse.class,
			description = "버전 체크 성공"
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_VERSION_FORMAT)
		}
	)
	ResponseEntity<ApiResponse<VersionCheckResponse>> checkVersion(
		@Parameter(
			description = "플랫폼 (대소문자 구분 없음)",
			example = "ios",
			schema = @Schema(allowableValues = {"ios", "android", "IOS", "ANDROID"})
		)
		@RequestParam String platform,

		@Parameter(
			description = "현재 앱 버전 (semantic versioning)",
			example = "1.2.3",
			schema = @Schema(pattern = "^\\d+\\.\\d+\\.\\d+$")
		)
		@RequestParam String version
	);

	@Operation(
		summary = "앱 버전 목록 조회",
		description = """
			플랫폼별 등록된 모든 앱 버전을 조회합니다.

			**응답 형식:**
			- iOS, Android 플랫폼별로 구분하여 반환
			- 각 버전별로 ID, 버전, 출시일, 업데이트 정책 포함
			- 출시일 기준 내림차순 정렬
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = AppVersionListResponse.class,
			description = "앱 버전 목록 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<AppVersionListResponse>> getAllVersions();

	@Operation(
		summary = "앱 버전 등록",
		description = """
			새로운 앱 버전을 등록합니다.

			**주의사항:**
			- 동일한 플랫폼에 같은 버전 번호는 중복 등록할 수 없습니다
			- 버전은 x.y.z 형식(semantic versioning)을 따라야 합니다
			- 새로 등록된 버전의 초기 정책은 `NONE`으로 설정됩니다
			- 업데이트 정책은 별도 API를 통해 설정해야 합니다
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = AppVersionResponse.class,
			description = "앱 버전 등록 성공"
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.DUPLICATE_APP_VERSION),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_VERSION_FORMAT)
		}
	)
	ResponseEntity<ApiResponse<AppVersionResponse>> createVersion(
		@RequestBody @Valid AppVersionCreateRequest request
	);

	@Operation(
		summary = "앱 버전 삭제",
		description = """
			등록된 앱 버전을 삭제합니다.

			**삭제 조건:**
			- 업데이트 정책이 `NONE`인 버전만 삭제 가능
			- `LATEST`, `RECOMMENDED`, `FORCE` 정책이 설정된 버전은 삭제 불가
			- 삭제하려면 먼저 정책을 `NONE`으로 변경 후 삭제해야 함

			**주의사항:**
			- 삭제된 버전은 복구할 수 없습니다
			- 클라이언트에서 해당 버전으로 API 호출 시 정상 동작합니다
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "앱 버전 삭제 성공"
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_APP_VERSION),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.CANNOT_DELETE_APP_VERSION)
		}
	)
	ResponseEntity<ApiResponse<?>> deleteVersion(
		@Parameter(description = "삭제할 앱 버전 ID", example = "1")
		@PathVariable Long id
	);

	@Operation(
		summary = "업데이트 정책 조회",
		description = """
			플랫폼별 업데이트 정책 설정을 조회합니다.

			**정책 타입 설명:**
			- `LATEST`: 최신 버전 (플랫폼당 1개만 설정 가능)
			- `RECOMMENDED`: 권장 업데이트 버전 (복수 설정 가능)
			- `FORCE`: 강제 업데이트 버전 (복수 설정 가능)
			- `NONE`: 업데이트 정책 없음

			**응답 형식:**
			- iOS, Android 플랫폼별로 구분
			- 각 버전의 ID, 버전, 업데이트 정책 포함
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = UpdatePolicyResponse.class,
			description = "업데이트 정책 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<UpdatePolicyResponse>> getUpdatePolicies();

	@Operation(
		summary = "업데이트 정책 수정",
		description = """
			플랫폼별 버전의 업데이트 정책을 일괄 수정합니다.

			**수정 규칙:**
			1. **LATEST 정책**: 각 플랫폼당 최대 1개만 설정 가능
			2. **RECOMMENDED 정책**: 복수 설정 가능
			3. **FORCE 정책**: 복수 설정 가능 (일반적으로 구버전들)
			4. **NONE 정책**: 제한 없음

			**요청 형식:**
			- iOS, Android 키로 구분
			- 각 배열에는 {id, updateType} 객체들 포함
			- 모든 버전의 정책을 명시적으로 설정해야 함

			**처리 로직:**
			1. LATEST 정책 중복 검증
			2. 존재하지 않는 버전 ID 검증
			3. 모든 검증 통과 시 일괄 업데이트
			4. 검증 실패 시 전체 롤백
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "업데이트 정책 수정 성공"
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_APP_VERSION),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.DUPLICATE_LATEST_VERSION),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_UPDATE_POLICY)
		}
	)
	ResponseEntity<ApiResponse<?>> updatePolicies(
		@RequestBody @Valid UpdatePolicyRequest request
	);
}

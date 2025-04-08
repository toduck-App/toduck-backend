package im.toduck.domain.social.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialProfileResponse;
import im.toduck.domain.social.presentation.dto.response.SocialResponse;
import im.toduck.domain.social.presentation.dto.response.UserProfileRoutineListResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.annotation.valid.PaginationLimit;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.presentation.dto.response.CursorPaginationResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Social Profile")
public interface SocialProfileApi {

	@Operation(
		summary = "유저 프로필 조회",
		description = "userId를 이용하여 사용자의 프로필 정보를 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = SocialProfileResponse.class,
			description = "소셜 유저 프로필 조회 성공, 유저 프로필 정보를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<SocialProfileResponse>> getUserProfile(
		@PathVariable Long userId,
		@AuthenticationPrincipal CustomUserDetails authUser
	);

	@Operation(
		summary = "특정 유저가 작성한 게시글 목록 조회",
		description = """
			<b>특정 유저(userId)가 작성한 소셜 게시글 목록을 커서 기반 페이지네이션으로 조회합니다.</b><br/><br/>
			<p><b>커서 페이지네이션 사용법:</b></p>
			<p>Notion > API 개요 > 페이지네이션을 확인해주세요.</p><br/>
			<p><b>파라미터:</b><br/>
			<p>- <b>userId:</b> 조회할 대상 유저의 ID (Path Variable)</p>
			<p>- <b>cursor:</b> 조회를 시작할 커서 값 (게시글 ID)</p>
			<p>- <b>limit:</b> 한 페이지에 표시할 게시글 수</p><br/>
			<p>차단한 유저의 게시글은 조회되지 않습니다.</p>
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = SocialResponse.class,
			description = "특정 유저의 게시글 목록 조회 성공, 커서 기반으로 조회된 게시글 목록을 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_USER)
		}
	)
	ResponseEntity<ApiResponse<CursorPaginationResponse<SocialResponse>>> getUserSocials(
		@Parameter(description = "게시글을 조회할 유저 ID") @PathVariable Long userId,
		@AuthenticationPrincipal CustomUserDetails authUser,
		@Parameter(description = "조회를 시작할 커서 값 (게시글 ID)") @RequestParam(required = false) Long cursor,
		@Parameter(description = "한 페이지에 표시할 항목 수") @PaginationLimit @RequestParam(required = false) Integer limit
	);

	@Operation(
		summary = "특정 유저의 공개된 루틴 목록 조회",
		description = "userId에 해당하는 사용자의 루틴 목록 중, **공개(isPublic=true) 상태이고 삭제되지 않은 루틴**만 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = UserProfileRoutineListResponse.class,
			description = "루틴 목록 조회 성공. 루틴 시간(time) 기준으로 오름차순 정렬됩니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_USER)
		}
	)
	ResponseEntity<ApiResponse<UserProfileRoutineListResponse>> getUserProfileRoutines(
		@Parameter(description = "루틴 목록을 조회할 유저 ID") @PathVariable Long userId,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails authUser
	);

	@Operation(
		summary = "루틴 저장 (공유 루틴 추가)",
		description = """
			<b>다른 사용자의 공개된 루틴을 내 목록으로 저장(공유)합니다.</b><br/><br/>
			<p>다른 사용자의 프로필 등에서 발견한 유용한 루틴을 자신의 루틴 목록에 추가(저장)할 때 사용됩니다.</p><br/>
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "루틴 저장 성공. 생성된 루틴의 Id를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_ROUTINE),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.PRIVATE_ROUTINE),
		}
	)
	ResponseEntity<ApiResponse<RoutineCreateResponse>> saveSharedRoutine(
		@Parameter(description = "저장할 루틴의 ID", required = true, example = "1")
		@PathVariable final Long routineId,
		@Parameter(hidden = true) @AuthenticationPrincipal final CustomUserDetails authUser,
		@RequestBody @Valid final RoutineCreateRequest request
	);
}

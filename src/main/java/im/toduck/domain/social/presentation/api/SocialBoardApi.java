package im.toduck.domain.social.presentation.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.social.presentation.dto.request.SocialCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialUpdateRequest;
import im.toduck.domain.social.presentation.dto.response.SocialCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialDetailResponse;
import im.toduck.domain.social.presentation.dto.response.SocialResponse;
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

@Tag(name = "Social Board")
public interface SocialBoardApi {
	@Operation(
		summary = "소셜 게시글 생성",
		description = """
			소셜 게시글을 작성합니다.
			- 공유할 루틴이 없는 경우: routineId = null
			- 비공개 루틴은 게시글에 공유할 수 없습니다. (PRIVATE_ROUTINE 예외 발생)
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = SocialCreateResponse.class,
			description = "소셜 게시글 생성 성공, 생성된 게시글의 Id를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_ROUTINE),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.PRIVATE_ROUTINE),
		}
	)
	ResponseEntity<ApiResponse<SocialCreateResponse>> createSocialBoard(
		@RequestBody @Valid SocialCreateRequest request,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "소셜 게시글 삭제",
		description = "소셜 게시글을 삭제합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "소셜 게시글 삭제 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_BOARD),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> deleteSocialBoard(
		@PathVariable Long socialId,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "소셜 게시글 수정",
		description =
			"""
				<b>게시글 수정 API는 변경이 필요한 필드만 포함하여 요청할 수 있습니다.</b><br/><br/>
				<p>예시 요청:</p><br/>
				{<br/>
				"content": "수정된 게시글 내용입니다.",<br/>
				"isRemoveRoutine": false,<br/>
				"routineId": null,<br/>
				"isAnonymous": null,<br/>
				"socialCategoryIds": null,<br/>
				"socialImageUrls": ["https://cdn.toduck.app/new-image.jpg"]<br/>
				}<br/><br/>
				<p>위 예시는 게시글의 내용과 이미지만 변경하는 경우입니다.</p><br/>
				<b>필드별 주의사항:</b><br/>
				<p>- content가 null인 경우 내용을 수정하지 않습니다</p>
				<p>- isAnonymous가 null인 경우 익명 여부를 수정하지 않습니다</p>
				<p>- socialCategoryIds가 null인 경우 카테고리를 수정하지 않습니다</p>
				<p>- socialImageUrls가 null인 경우 이미지를 수정하지 않습니다</p>
				<p>- 이미지 모두 제거: socialImageUrls를 빈 배열( [ ] )로 전송</p>
				<p>- 카테고리 수정 시 최소 1개 이상 필수 (빈 배열 불가)</p>
				<p>- 비공개 루틴은 게시글에 공유할 수 없음</p><br/>
				<b>루틴 공유 관련 (isRemoveRoutine은 필수값):</b><br/>
				<p>1. 공유 루틴 제거: isRemoveRoutine = true, routineId = null (routineId가 null이 아닐 경우 예외 발생)</p>
				<p>2. 공유 루틴 유지: isRemoveRoutine = false, routineId = null</p>
				<p>3. 공유 루틴 변경: isRemoveRoutine = false, routineId = 변경할 루틴 ID</p>
				"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "소셜 게시글 수정 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_BOARD),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.EMPTY_SOCIAL_CATEGORY_LIST),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_ROUTINE),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.PRIVATE_ROUTINE),
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> updateSocialBoard(
		@PathVariable Long socialId,
		@RequestBody @Valid SocialUpdateRequest request,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "게시글 단건 조회",
		description = "게시글 단건 세부사항을 조회합니다. </br></br>"
			+ "공유할 루틴이 존재하지 않는 경우 routine 필드에 null이 반환 됩니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = SocialDetailResponse.class,
			description = "게시글 조회 성공, 게시글의 세부 정보를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_BOARD),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.BLOCKED_USER_SOCIAL_ACCESS),
		}
	)
	ResponseEntity<ApiResponse<SocialDetailResponse>> getSocialDetail(
		@PathVariable Long socialId,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "게시글 목록 조회 (카테고리 필터 가능)",
		description = "게시글을 커서 기반 페이지네이션으로 조회합니다.</br></br>"
			+ "카테고리를 기준으로 필터링할 수 있습니다.</br></br>"
			+ "커서 페이지네이션 사용법은 Notion > API 개요 > 페이지네이션을 확인해주세요.</br></br>"
			+ "공유할 루틴이 존재하지 않는 경우 routine 필드에 null이 반환 됩니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = SocialResponse.class,
			description = "게시글 목록 조회 성공, 커서 기반으로 조회된 게시글 목록을 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<CursorPaginationResponse<SocialResponse>>> getSocials(
		@AuthenticationPrincipal CustomUserDetails user,
		@Parameter(description = "조회를 시작할 커서 값") @RequestParam(required = false) Long cursor,
		@Parameter(description = "한 페이지에 표시할 항목 수") @PaginationLimit @RequestParam(required = false) Integer limit,
		@Parameter(description = "카테고리 ID 목록") @RequestParam(required = false) List<Long> categoryIds
	);
}

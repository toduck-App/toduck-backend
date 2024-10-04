package im.toduck.domain.social.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.request.ReportCreateRequest;
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
		description = "소셜 게시글을 작성합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = SocialCreateResponse.class,
			description = "소셜 게시글 생성 성공, 생성된 게시글의 Id를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY),
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
				<b>게시글 수정 API는 모든 필드를 전송할 필요가 없습니다.</b><br/><br/>
				<p>소셜 게시글의 특정 필드만 선택적으로 수정할 수 있습니다. 요청 시 수정하고자 하는 필드만 포함하면 됩니다.</p><br/>
				{<br/>
					"content": "수정된 게시글 내용입니다.",<br/>
					"socialImageUrls": ["https://cdn.toduck.app/new-image.jpg"]<br/>
				}<br/><br/>
				<p>위 예시는 게시글의 내용과 이미지를 변경하는 경우입니다. 익명 여부나 다른 필드를 수정하지 않으려면 해당 필드를 생략할 수 있습니다.</p><br/>
				<b>주의사항:</b><br/>
				<p>- 이미지를 모두 지우고 싶다면 socialImageUrls를 빈 배열( [ ] )로 전송하면 됩니다.</p>
				<p>- socialCategoryIds는 최소한 하나의 카테고리 ID가 존재해야 합니다. 빈 배열로 전송할 경우 에러가 발생합니다.</p>
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
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> updateSocialBoard(
		@PathVariable Long socialId,
		@RequestBody @Valid SocialUpdateRequest request,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "게시글 단건 조회",
		description = "게시글 단건 세부사항을 조회합니다."
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
		summary = "게시글 목록 조회",
		description = "게시글을 커서 기반 페이지네이션으로 조회합니다.</br></br>커서 페이지네이션 사용법은 Notion > API 개요 > 페이지네이션을 확인해주세요."
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
		@Parameter(description = "한 페이지에 표시할 항목 수") @PaginationLimit @RequestParam(required = false) Integer limit
	);

	@Operation(
		summary = "게시글 신고",
		description = "지정된 게시글을 신고합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "게시글 신고 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_BOARD),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.ALREADY_REPORTED)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> reportSocialBoard(
		@RequestBody ReportCreateRequest request,
		@PathVariable Long socialId,
		@AuthenticationPrincipal CustomUserDetails user
	);
}

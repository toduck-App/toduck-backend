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
import im.toduck.domain.social.presentation.dto.response.SocialCategoryResponse;
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
import jakarta.validation.constraints.NotBlank;

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
				<b>소셜 게시글 수정 API는 수정이 필요한 필드만 포함하여 요청할 수 있습니다.</b><br/><br/>
				<p>예시 요청:</p><br/>
				{<br/>
				"isChangeTitle": true,<br/>
				"title": "새로운 제목입니다.",<br/>
				"isChangeRoutine": false,<br/>
				"routineId": null,<br/>
				"content": "수정된 게시글 내용입니다.",<br/>
				"isAnonymous": true,<br/>
				"socialCategoryIds": [3, 4],<br/>
				"socialImageUrls": ["https://cdn.toduck.app/new-image.jpg"]<br/>
				}<br/><br/>
				<p>위 예시는 제목, 내용, 익명 여부, 카테고리, 이미지를 수정하며, 루틴은 변경하지 않는 경우입니다.</p><br/>
				<b>필드별 동작 방식:</b><br/>
				<p>- <b>content</b>: null이 아닌 경우에만 내용을 수정합니다. null인 경우 기존 내용이 유지됩니다.</p>
				<p>- <b>isAnonymous</b>: null이 아닌 경우에만 익명 여부를 수정합니다. null인 경우 기존 익명 여부가 유지됩니다.</p>
				<p>- <b>socialCategoryIds</b>: null인 경우 카테고리를 수정하지 않습니다. 최소 1개의 유효한 ID가 필요하며, 빈 배열은 허용되지 않습니다.</p>
				<p>- <b>socialImageUrls</b>: null인 경우 이미지를 수정하지 않습니다. 빈 배열([])을 전달하면 이미지를 모두 제거합니다.</p><br/>
				<b>루틴 관련 시나리오 (isChangeRoutine은 필수값):</b><br/>
				<p>1. <b>루틴 제거</b>: isChangeRoutine = true, routineId = null (routineId가 null이 아니면 예외 발생)</p>
				<p>2. <b>루틴 유지</b>: isChangeRoutine = false, routineId = null</p>
				<p>3. <b>루틴 변경</b>: isChangeRoutine = true, routineId = 변경할 루틴 ID</p><br/>
				<b>제목 관련 시나리오 (isChangeTitle은 필수값):</b><br/>
				<p>1. <b>제목 변경</b>: isChangeTitle = true, title = 변경할 제목</p>
				<p>2. <b>제목 유지</b>: isChangeTitle = false, title = null (title이 null이 아니면 예외 발생)</p>
				<p>3. <b>제목 제거</b>: isChangeTitle = true, title = null (제목을 제거하는 경우)</p><br/>
				<b>카테고리 관련 시나리오:</b><br/>
				<p>1. <b>카테고리 유지</b>: socialCategoryIds = null (카테고리를 수정하지 않음)</p>
				<p>2. <b>카테고리 수정</b>: socialCategoryIds = [유효한 카테고리 ID 리스트]</p>
				<p>3. <b>잘못된 카테고리 수정</b>: socialCategoryIds = 빈 배열([]) (예외 발생)</p>
				<p>4. <b>유효하지 않은 ID 포함</b>: socialCategoryIds = [유효하지 않은 카테고리 ID 포함] (예외 발생)</p><br/>
				<b>이미지 관련 시나리오:</b><br/>
				<p>1. <b>이미지 유지</b>: socialImageUrls = null (이미지를 수정하지 않음)</p>
				<p>2. <b>이미지 추가/수정</b>: socialImageUrls = [새로운 이미지 URL 리스트]</p>
				<p>3. <b>이미지 모두 제거</b>: socialImageUrls = [] (기존 이미지를 모두 제거)</p>
				<p>4. <b>최대 이미지 초과</b>: socialImageUrls = [이미지 URL 6개 이상] (예외 발생)</p><br/>
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
		description =
			"""
				<b>게시글을 커서 기반 페이지네이션으로 조회합니다.</b><br/><br/>
				<p><b>카테고리 필터를 적용하는 방법:</b></p>
				<p>예시: /v1/socials?cursor=100&limit=10&categoryIds=1,2,3</p><br/>
				<p><b>커서 페이지네이션 사용법:</b></p>
				<p>Notion > API 개요 > 페이지네이션을 확인해주세요.</p><br/>
				<p><b>필터링 파라미터:</b><br/>
				<p>- <b>cursor:</b> 조회를 시작할 커서 값 (게시글 ID)</p>
				<p>- <b>limit:</b> 한 페이지에 표시할 게시글 수</p>
				<p>- <b>categoryIds:</b> 필터링할 카테고리 ID 목록 (쉼표로 구분하여 지정)</p><br/>
				<p>공유할 루틴이 존재하지 않는 경우 <b>routine</b> 필드에 <b>null</b>이 반환됩니다.</p>
				"""
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

	@Operation(
		summary = "모든 카테고리 조회",
		description = "모든 소셜 카테고리의 ID와 이름을 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = SocialCategoryResponse.class,
			description = "모든 카테고리 조회 성공, 카테고리 목록 반환"
		)
	)
	ResponseEntity<ApiResponse<SocialCategoryResponse>> getAllCategories();

	@Operation(
		summary = "게시글 검색",
		description = "키워드로 게시글을 검색합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = SocialResponse.class,
			description = "검색 결과 반환"
		)
	)
	ResponseEntity<ApiResponse<CursorPaginationResponse<SocialResponse>>> searchSocials(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestParam(name = "keyword") @NotBlank String keyword,
		@Parameter(description = "조회를 시작할 커서 값") @RequestParam(required = false) Long cursor,
		@Parameter(description = "한 페이지에 표시할 항목 수") @PaginationLimit @RequestParam(required = false) Integer limit
	);

}

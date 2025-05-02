package im.toduck.domain.mypage.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.mypage.presentation.dto.request.NickNameUpdateRequest;
import im.toduck.domain.mypage.presentation.dto.request.ProfileImageUpdateRequest;
import im.toduck.domain.mypage.presentation.dto.response.BlockedUsersResponse;
import im.toduck.domain.mypage.presentation.dto.response.MyCommentsResponse;
import im.toduck.domain.mypage.presentation.dto.response.NickNameResponse;
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

@Tag(name = "My Page")
public interface MyPageApi {
	@Operation(
		summary = "닉네임 변경",
		description = "사용자 닉네임을 변경합니다. 닉네임은 알파벳, 한글, 숫자로만 이루어진 2~8자의 문자열이어야 합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "닉네임 변경 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.EXISTS_USER_NICKNAME)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> updateNickname(
		@RequestBody @Valid NickNameUpdateRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(
		summary = "자신의 닉네임 조회",
		description = "사용자 자신의 닉네임을 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = NickNameResponse.class,
			description = "닉네임 조회 성공, 자신의 닉네임을 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<NickNameResponse>> getMyNickname(
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(
		summary = "프로필 사진 변경",
		description = "사용자 프로필 사진을 변경합니다. null을 보낼 경우 프로필 사진 삭제를 의미합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "프로필 사진 변경 성공, 빈 content 객체를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> updateProfileImage(
		@RequestBody @Valid ProfileImageUpdateRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(
		summary = "차단한 유저 목록 조회",
		description = "자신이 차단한 유저들의 목록을 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "차단한 유저 목록 조회 성공, 차단한 유저들의 정보를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<BlockedUsersResponse>> getBlockedUsers(
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(
		summary = "내가 작성한 댓글 목록 조회",
		description = """
			<b>내가 작성한 댓글을 커서 기반 페이지네이션으로 조회합니다.</b><br/><br/>
			<p><b>커서 페이지네이션 사용법:</b></p>
			<p>예시: /v1/my-page/comments?cursor=100&limit=10</p><br/>
			<p><b>파라미터:</b><br/>
			<p>- <b>cursor:</b> 조회를 시작할 커서 값 (댓글 ID)</p>
			<p>- <b>limit:</b> 한 페이지에 표시할 댓글 수</p>
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = MyCommentsResponse.class,
			description = "댓글 목록 조회 성공, 커서 기반으로 조회된 댓글 목록을 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<CursorPaginationResponse<MyCommentsResponse>>> getMyComments(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "조회를 시작할 커서 값") @RequestParam(required = false) Long cursor,
		@Parameter(description = "한 페이지에 표시할 항목 수") @PaginationLimit @RequestParam(required = false) Integer limit
	);
}

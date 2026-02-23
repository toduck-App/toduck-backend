package im.toduck.domain.auth.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.auth.presentation.dto.request.WebLoginAuthorizeRequest;
import im.toduck.domain.auth.presentation.dto.response.WebSessionCreateResponse;
import im.toduck.domain.auth.presentation.dto.response.WebSessionStatusResponse;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Web Login", description = "QR 코드 기반 웹 로그인 API")
public interface WebLoginApi {

	@Operation(
		summary = "웹 로그인 세션 생성",
		description = "QR 코드를 포함한 웹 로그인 세션을 생성합니다. "
			+ "웹 브라우저에서 호출하며, 반환된 QR 코드를 화면에 표시합니다. "
			+ "세션은 5분간 유효합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = WebSessionCreateResponse.class,
			description = "세션 토큰, QR 코드 이미지(Base64), 유니버셜 링크, 만료 시간이 반환됩니다."
		)
	)
	ResponseEntity<ApiResponse<WebSessionCreateResponse>> createWebSession();

	@Operation(
		summary = "웹 로그인 승인",
		description = "iOS 앱에서 QR 코드를 스캔한 후 로그인을 승인합니다. "
			+ "인증된 사용자만 호출할 수 있습니다. "
			+ "세션이 만료되었거나 이미 승인된 경우에도 성공 응답을 반환합니다 (idempotent)."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "승인 요청이 처리되었습니다. authorized 필드로 실제 승인 여부를 확인할 수 있습니다."
		)
	)
	ResponseEntity<ApiResponse<?>> authorizeWebSession(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid WebLoginAuthorizeRequest request
	);

	@Operation(
		summary = "웹 로그인 세션 상태 확인",
		description = "웹 브라우저에서 세션 상태를 폴링하여 확인합니다. "
			+ "PENDING: 대기 중, APPROVED: 승인됨 (토큰 발급), EXPIRED: 만료됨 (새 QR 발급 필요)"
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = WebSessionStatusResponse.class,
			description = "세션 상태와 승인 시 액세스 토큰이 반환됩니다. "
				+ "APPROVED 상태일 때 accessToken과 userId가 포함됩니다. "
				+ "토큰 발급 후 세션은 자동 삭제됩니다."
		)
	)
	ResponseEntity<ApiResponse<WebSessionStatusResponse>> getWebSessionStatus(
		@PathVariable String sessionToken
	);
}

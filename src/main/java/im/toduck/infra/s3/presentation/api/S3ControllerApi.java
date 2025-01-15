package im.toduck.infra.s3.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import im.toduck.infra.s3.presentation.dto.request.PreSignedUrlRequest;
import im.toduck.infra.s3.presentation.dto.response.PreSignedUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "AWS S3")
public interface S3ControllerApi {
	@Operation(
		summary = "파일 업로드를 위한 pre-signed URL 발급",
		description = "S3에 파일을 업로드하기 위한 pre-signed URL을 발급합니다. URL의 유효시간은 2분입니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = PreSignedUrlResponse.class,
			description = "pre-signed URL 발급 성공. URL과 파일 접근 경로를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<PreSignedUrlResponse>> getPresignedUrl(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid PreSignedUrlRequest request
	);
}

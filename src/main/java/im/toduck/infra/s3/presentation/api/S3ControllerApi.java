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
		description = """
			S3에 파일을 업로드하기 위한 pre-signed URL을 발급합니다.<br>
			발급된 URL의 유효시간은 2분입니다.

			**Content-Type 주의사항**
			- 서버에서 pre-signed URL을 생성할 때 특정 Content-Type(예: "image/png")을 기대하면,<br>
			실제 PUT 업로드 시에도 **동일한 Content-Type** 헤더를 지정해야 합니다.<br><br>
			**단, jpg, jpeg의 Content-Type은 image/jpeg로 동일합니다.**

			**자세한 사용 방법과 Swift 예제**는 노션 문서에서 확인하세요:<br>
			[➡️ API 문서 바로가기](https://www.notion.so/kyxxn/API-e775e161efa6459583a0ee0d586c4d19?pvs=97#8fd87c48767741158067eda8a2cc4ad2)
			"""
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

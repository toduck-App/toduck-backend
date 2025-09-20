package im.toduck.domain.backoffice.presentation.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "업데이트 정책 수정 요청")
public record UpdatePolicyRequest(
	@NotNull(message = "iOS 정책 목록은 필수입니다.")
	@Valid
	@Schema(description = "iOS 플랫폼 버전별 정책 설정")
	List<UpdatePolicyItemRequest> ios,

	@NotNull(message = "Android 정책 목록은 필수입니다.")
	@Valid
	@Schema(description = "Android 플랫폼 버전별 정책 설정")
	List<UpdatePolicyItemRequest> android
) {
}

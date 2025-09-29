package im.toduck.domain.backoffice.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "업데이트 정책 응답")
@Builder
public record UpdatePolicyResponse(
	@Schema(description = "iOS 플랫폼 정책 목록")
	List<UpdatePolicyItemResponse> ios,

	@Schema(description = "Android 플랫폼 정책 목록")
	List<UpdatePolicyItemResponse> android
) {
}

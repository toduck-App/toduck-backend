package im.toduck.domain.backoffice.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "앱 버전 목록 응답")
@Builder
public record AppVersionListResponse(
	@Schema(description = "iOS 플랫폼 버전 목록")
	List<AppVersionResponse> ios,

	@Schema(description = "Android 플랫폼 버전 목록")
	List<AppVersionResponse> android
) {
}

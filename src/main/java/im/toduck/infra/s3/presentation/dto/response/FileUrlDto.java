package im.toduck.infra.s3.presentation.dto.response;

import java.net.URL;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record FileUrlDto(
	@Schema(
		description = "파일 업로드를 위한 pre-signed URL",
		example = "https://toduck-uploads.s3.ap-northeast-2.amazonaws.com/users/1/2025/1/15/917c5d17-..."
	)
	URL presignedUrl,

	@Schema(
		description = "업로드 완료 후 파일 접근 URL",
		example = "https://cdn.toduck.app/users/1/2025/1/15/917c5d17-9a69-4438-91d1-400651f45b81_test2.png"
	)
	String fileUrl
) {
}

package im.toduck.infra.s3.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FileNameDto(
	@NotBlank(message = "파일 이름을 입력해주세요.")
	@Pattern(regexp = ".*\\.[^\\.]+$", message = "파일 이름에는 확장자가 포함되어야 합니다.")
	@Schema(description = "파일 이름", example = "profile.jpg")
	String fileName
) {
}

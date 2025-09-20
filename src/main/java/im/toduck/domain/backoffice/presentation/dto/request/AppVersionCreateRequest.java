package im.toduck.domain.backoffice.presentation.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import im.toduck.domain.backoffice.persistence.entity.AppVersionPlatform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "앱 버전 등록 요청")
public record AppVersionCreateRequest(
	@NotNull(message = "플랫폼은 필수입니다.")
	@Schema(description = "앱 플랫폼", example = "IOS", allowableValues = {"IOS", "ANDROID"})
	AppVersionPlatform platform,

	@NotBlank(message = "버전은 필수입니다.")
	@Size(max = 20, message = "버전은 20자를 초과할 수 없습니다.")
	@Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "버전은 x.y.z 형식이어야 합니다.")
	@Schema(description = "앱 버전 (semantic versioning)", example = "1.6.0")
	String version,

	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "출시일은 필수입니다.")
	@PastOrPresent(message = "출시일은 과거 또는 현재 날짜여야 합니다.")
	@Schema(description = "앱 출시일", example = "2024-03-20")
	LocalDate releaseDate
) {
}

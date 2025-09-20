package im.toduck.domain.backoffice.presentation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import im.toduck.domain.backoffice.persistence.entity.AppVersionPlatform;
import im.toduck.domain.backoffice.persistence.entity.UpdateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "앱 버전 정보")
@Builder
public record AppVersionResponse(
	@Schema(description = "버전 ID", example = "1")
	Long id,

	@Schema(description = "플랫폼", example = "IOS")
	AppVersionPlatform platform,

	@Schema(description = "버전", example = "1.5.0")
	String version,

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "출시일", example = "2024-03-15")
	LocalDate releaseDate,

	@Schema(description = "업데이트 타입", example = "LATEST")
	UpdateType updateType,

	@Schema(description = "업데이트 타입 설명", example = "최신 버전")
	String updateTypeDescription,

	@Schema(description = "삭제 가능 여부", example = "false")
	boolean canDelete,

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@Schema(description = "등록일시", type = "string", pattern = "yyyy-MM-dd HH:mm", example = "2024-03-15 10:30")
	LocalDateTime createdAt
) {
}

package im.toduck.domain.social.presentation.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CommentDto(
	@Schema(description = "댓글 ID", example = "1")
	Long commentId,
	@Schema(description = "작성자 정보")
	OwnerDto owner,
	@Schema(description = "댓글 내용", example = "루틴 너무 좋네요!")
	String content,
	@Schema(description = "댓글 작성 시간", type = "string", pattern = "yyyy-MM-dd HH:mm", example = "2024-09-11 10:30")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime createdAt
) {
}

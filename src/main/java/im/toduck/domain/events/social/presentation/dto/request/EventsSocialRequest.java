package im.toduck.domain.events.social.presentation.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "소셜 이벤트 생성 요청 DTO")
public record EventsSocialRequest(
	@NotNull(message = "소셜 게시글 id는 비어있을 수 없습니다.")
	@Schema(description = "소셜 게시글 id", example = "1")
	Long socialId,

	@NotNull(message = "전화번호는 비어있을 수 없습니다.")
	@Schema(description = "전화번호", example = "01012345678")
	String phone,

	@NotNull(message = "날짜는 비어있을 수 없습니다.")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@Schema(description = "날짜", example = "2025-09-19")
	LocalDate date
) {
}

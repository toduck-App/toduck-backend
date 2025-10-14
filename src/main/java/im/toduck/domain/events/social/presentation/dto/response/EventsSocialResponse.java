package im.toduck.domain.events.social.presentation.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import im.toduck.domain.events.social.persistence.entity.EventsSocial;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "소셜 이벤트 응답")
public record EventsSocialResponse(
	@Schema(description = "소셜 이벤트 ID")
	Long id,

	@Schema(description = "소셜 게시물 제목")
	String socialTitle,

	@Schema(description = "작성자 닉네임")
	String userNickname,

	@Schema(description = "전화번호")
	String phone,

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "참여 날짜")
	LocalDate participationDate
) {
	public static List<EventsSocialResponse> toEventsSocialResponse(List<EventsSocial> eventsSocials) {
		return eventsSocials.stream()
			.map(e -> EventsSocialResponse.builder()
				.id(e.getId())
				.socialTitle(e.getSocial().getTitle())
				.userNickname(e.getUser().getNickname())
				.phone(e.getPhone())
				.participationDate(e.getDate())
				.build())
			.toList();
	}
}

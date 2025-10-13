package im.toduck.domain.diary.presentation.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import im.toduck.domain.user.persistence.entity.Emotion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "다이어리 생성 요청 DTO")
public record DiaryCreateRequest(
	@NotNull(message = "날짜는 비어있을 수 없습니다.")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Schema(description = "일기 날짜", example = "2025-03-12")
	LocalDate date,

	@NotNull(message = "감정은 비어있을 수 없습니다.")
	@Schema(description = "감정", example = "SAD")
	Emotion emotion,

	@Size(max = 16, message = "제목은 16자를 초과할 수 없습니다.")
	@Schema(description = "일기 제목", example = "슬퍼")
	String title,

	@Size(max = 5000, message = "일기는 5000자를 초과할 수 없습니다.")
	@Schema(description = "일기", example = "출근 전에 지갑을 두고 나오는 바람에 다시 돌아갔다")
	String memo,

	@Size(max = 5, message = "이미지는 최대 5개까지만 등록할 수 있습니다.")
	@Schema(description = "이미지 URL 목록", example = "[\"https://cdn.toduck.app/image1.jpg\"]")
	List<String> diaryImageUrls
) {

}

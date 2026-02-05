package im.toduck.domain.inquiry.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import im.toduck.domain.inquiry.persistence.entity.Status;
import im.toduck.domain.inquiry.persistence.entity.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "문의 내역 응답")
public record InquiryResponse(
	@Schema(description = "문의 ID", example = "1")
	Long inquiryId,

	@Schema(description = "유형", example = "이용 문의")
	Type type,

	@Schema(description = "내용", example = "문의 내용")
	String content,

	@Schema(description = "상태", example = "PENDING")
	Status status,

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(description = "생성 날짜", example = "2026-01-22T14:30:00")
	LocalDateTime createdAt,

	@Schema(description = "문의 내역 이미지 url 목록", example = "[\"https://cdn.toduck.app/image1.jpg\"]")
	List<String> inquiryImgUrl,

	@Schema(description = "문의 답변 ID", example = "1")
	Long inquiryAnswerId,

	@Schema(description = "답변 작성자 이름", example = "토덕 관리자")
	String answerAdminName,

	@Schema(description = "문의 내용", example = "현재로써는 루틴을 반복할 기간을 따로 설정할 수 있는 기능은 없습니다!")
	String answerContent,

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(description = "답변 생성 날짜", example = "2026-01-22T14:30:00")
	LocalDateTime answerCreatedAt
) {
}

package im.toduck.domain.backoffice.presentation.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import im.toduck.domain.mypage.persistence.entity.AccountDeletionReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "회원 탈퇴 사유 응답 DTO")
@Builder
public record AccountDeletionLogResponse(
	@Schema(description = "탈퇴 로그 ID", example = "1")
	Long id,

	@Schema(description = "탈퇴한 회원 ID", example = "123")
	Long userId,

	@Schema(description = "탈퇴 사유 코드", example = "HARD_TO_USE")
	AccountDeletionReason reasonCode,

	@Schema(description = "탈퇴 사유 설명", example = "사용 방법이 어려워요")
	String reasonDescription,

	@Schema(description = "추가 탈퇴 사유 텍스트", example = "UI가 복잡해서 사용이 어려웠습니다")
	String reasonText,

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@Schema(description = "탈퇴 일시", example = "2024-01-01T10:30:00")
	LocalDateTime deletedAt
) {
}

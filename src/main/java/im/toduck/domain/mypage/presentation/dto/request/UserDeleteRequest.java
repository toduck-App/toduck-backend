package im.toduck.domain.mypage.presentation.dto.request;

import im.toduck.domain.mypage.persistence.entity.AccountDeletionReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDeleteRequest(
	@Schema(description = "탈퇴 사유 코드", example = "OTHER")
	@NotNull(message = "탈퇴 사유 코드는 필수입니다.")
	AccountDeletionReason reasonCode,

	@Schema(description = "탈퇴 사유 입력값", example = "파이팅")
	@NotNull(message = "탈퇴 사유는 필수입니다.")
	@Size(max = 130, message = "탈퇴 사유는 최대 130자까지 입력 가능합니다.")
	String reasonText
) {
}

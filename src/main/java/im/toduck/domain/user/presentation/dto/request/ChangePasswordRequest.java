package im.toduck.domain.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "비밀번호 변경 요청 DTO")
@Builder
public record ChangePasswordRequest(
	@Schema(description = "사용자 아이디", example = "toduck")
	String loginId,
	@Schema(description = "변경할 비밀번호", example = "Password2025@")
	String changedPassword,
	@Schema(description = "사용자 전화번호", example = "01012345678")
	String phoneNumber
) {
}

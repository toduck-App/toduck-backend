package im.toduck.domain.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "로그인 아이디, 전화번호 검증 요청")
@Builder
public record VerifyLoginIdPhoneNumberRequest(
	@Schema(description = "로그인 아이디", example = "toduck")
	String loginId,
	@Schema(description = "전화번호", example = "01012345678")
	String phoneNumber
) {
}

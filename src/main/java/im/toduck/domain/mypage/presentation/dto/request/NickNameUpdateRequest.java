package im.toduck.domain.mypage.presentation.dto.request;

import static im.toduck.global.regex.UserRegex.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NickNameUpdateRequest(
	@Schema(description = "닉네임", example = "꽥꽥")
	@NotBlank(message = "닉네임은 공백일 수 없습니다.")
	@Pattern(regexp = NICKNAME_REGEXP, message = "올바른 닉네임을 입력해주세요.")
	String nickname
) {
}

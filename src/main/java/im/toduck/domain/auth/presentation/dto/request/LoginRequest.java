package im.toduck.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
	@NotBlank(message = "전화번호를 입력해주세요.")
	String phoneNumber,

	@NotBlank(message = "비밀번호를 입력해주세요.")
	String password
) {
}

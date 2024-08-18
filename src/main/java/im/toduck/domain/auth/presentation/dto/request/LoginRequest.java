package im.toduck.domain.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 DTO")
public record LoginRequest(
	// TODO: 추후 정책에 따른 전화번호 양식 검증 필요, 전화번호 예시 업데이트 필요
	@Schema(description = "로그인 할 사용자 전화번호", example = "01012345678")
	@NotBlank(message = "전화번호를 입력해주세요.")
	String phoneNumber,

	// TODO: 추후 정책에 따른 비밀번호 양식 검증 필요
	@Schema(description = "사용자 비밀번호", example = "password123")
	@NotBlank(message = "비밀번호를 입력해주세요.")
	String password
) {
}

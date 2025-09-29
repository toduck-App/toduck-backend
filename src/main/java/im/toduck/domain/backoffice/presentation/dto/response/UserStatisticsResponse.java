package im.toduck.domain.backoffice.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "백오피스 회원 유형별 통계 응답 DTO")
public record UserStatisticsResponse(
	@Schema(description = "전체 회원 수 (탈퇴한 회원 제외)", example = "1500")
	long totalUsers,

	@Schema(description = "일반 회원 수 (자체 가입, 탈퇴한 회원 제외)", example = "800")
	long generalUsers,

	@Schema(description = "카카오 로그인 회원 수 (탈퇴한 회원 제외)", example = "600")
	long kakaoUsers,

	@Schema(description = "애플 로그인 회원 수 (탈퇴한 회원 제외)", example = "100")
	long appleUsers
) {
}

package im.toduck.domain.diary.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "사용자 키워드 목록 응답")
@Builder
public record UserKeywordListResponse(
	@Schema(description = "사용자 키워드 목록")
	List<UserKeywordResponse> userKeywordDtos
) {
	public static UserKeywordListResponse toListUserKeywordResponse(List<UserKeywordResponse> userKeywords) {
		return UserKeywordListResponse.builder()
			.userKeywordDtos(userKeywords)
			.build();
	}
}


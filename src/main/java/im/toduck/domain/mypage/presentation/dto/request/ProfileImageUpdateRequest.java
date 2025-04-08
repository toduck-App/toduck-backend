package im.toduck.domain.mypage.presentation.dto.request;

import org.springframework.lang.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProfileImageUpdateRequest(
	@Nullable
	@Schema(description = "이미지 URL", nullable = true, example = "https://cdn.toduck.app/example.jpg")
	String imageUrl
) {
}

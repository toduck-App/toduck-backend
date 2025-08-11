package im.toduck.domain.diary.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.diary.presentation.dto.request.DiaryKeywordCreateRequest;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "DiaryKeyword")
public interface DiaryKeywordApi {
	@Operation(
		summary = "일기 키워드 생성",
		description = "선택된 키워드를 일기에 저장합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "일기 키워드 생성 성공. 빈 content 객체를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> createDiaryKeyword(
		@RequestBody @Valid final DiaryKeywordCreateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);
}

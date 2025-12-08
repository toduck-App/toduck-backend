package im.toduck.domain.diary.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import im.toduck.domain.diary.presentation.dto.response.DiaryStreakResponse;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "DiaryStreak")
public interface DiaryStreakApi {
	@Operation(
		summary = "일기 스트릭과 최근에 작성한 일기 날짜",
		description =
			"""
				<b>해당 유저의 일기 스트릭과 최근에 작성한 일기 날짜를 반환합니다.</b><br><br>
				<b>일기 날짜가 없는 경우 lastDiaryDate는 null을 반환합니다.</b><br><br>
				<b>일기 스트릭 조회시 스트릭 갱신이 될 수 있습니다.</b>
				<p>스트릭이 끊어지는 기준은 "어제 일기를 작성했는지" 입니다.</p>
				<p>4일전 + 3일전 + 2일전 작성 => 0</p>
				<p>4일전 + 3일전 + 1일전 작성 => 1</p>
				<p>3일전 + 1일전 + 0일전 작성 => 2</p>
				<p>1일전 + 0일전 작성 => 2</p>
				<p>1일전만 작성 => 1</p>
				<p>0일전만 작성 => 1</p>
				"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = DiaryStreakResponse.class,
			description = "일기 스트릭 조회 성공, 해당 유저의 일기 스트릭과 최근에 작성한 일기 날짜를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<DiaryStreakResponse>> getDiaryStreak(
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "캐시에 저장된 일기 스트릭과 최근에 작성한 일기 날짜를 불러옵니다",
		description =
			"""
				바탕화면의 위젯과 같이 자주 사용되는 곳에서 사용해 주세요.
				"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = DiaryStreakResponse.class,
			description = "일기 스트릭 조회 성공, 해당 유저의 일기 스트릭과 최근에 작성한 일기 날짜를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<DiaryStreakResponse>> getCachedDiaryStreak(
		@AuthenticationPrincipal CustomUserDetails user
	);
}

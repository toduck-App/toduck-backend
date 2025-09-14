package im.toduck.domain.backoffice.presentation.api;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.backoffice.presentation.dto.response.DailyStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.OverallStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.PeriodStatisticsResponse;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.presentation.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "BackOffice Statistics")
public interface StatisticsApi {

	@Operation(
		summary = "전체 통계 조회",
		description = "전체 회원수, 일기수, 루틴수 등 전체 통계를 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = OverallStatisticsResponse.class,
			description = "전체 통계 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<OverallStatisticsResponse>> getOverallStatistics();

	@Operation(
		summary = "기간별 통계 조회",
		description = "지정된 기간 동안의 가입자, 탈퇴자, 신규 일기, 신규 루틴 통계를 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = PeriodStatisticsResponse.class,
			description = "기간별 통계 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<PeriodStatisticsResponse>> getPeriodStatistics(
		@Parameter(description = "시작 날짜", example = "2024-01-01")
		@RequestParam final LocalDate startDate,
		@Parameter(description = "종료 날짜", example = "2024-01-31")
		@RequestParam final LocalDate endDate
	);

	@Operation(
		summary = "일일 통계 조회",
		description = "특정 날짜의 가입자, 탈퇴자, 신규 일기, 신규 루틴 통계를 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = DailyStatisticsResponse.class,
			description = "일일 통계 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<DailyStatisticsResponse>> getDailyStatistics(
		@Parameter(description = "조회 날짜", example = "2024-01-01")
		@RequestParam final LocalDate date
	);
}

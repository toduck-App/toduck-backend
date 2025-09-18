package im.toduck.domain.backoffice.presentation.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.backoffice.presentation.dto.request.StatisticsType;
import im.toduck.domain.backoffice.presentation.dto.response.MultiDateStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.OverallStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.PeriodStatisticsResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "BackOffice Statistics")
public interface StatisticsApi {

	@Operation(
		summary = "전체 통계 조회",
		description = "선택된 통계 타입들의 전체 누적 수치를 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = OverallStatisticsResponse.class,
			description = "전체 통계 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<OverallStatisticsResponse>> getOverallStatistics(
		@Parameter(description = "통계 타입 목록", example = "NEW_USERS,NEW_ROUTINES,NEW_DIARIES")
		@RequestParam final List<StatisticsType> types
	);

	@Operation(
		summary = "기간별 통계 조회",
		description = "지정된 기간 동안의 선택된 통계 타입별 수치를 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = PeriodStatisticsResponse.class,
			description = "기간별 통계 조회 성공"
		),
		errors = {@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_STATISTICS_DATE_RANGE)}
	)
	ResponseEntity<ApiResponse<PeriodStatisticsResponse>> getPeriodStatistics(
		@Parameter(description = "시작 날짜", example = "2024-01-01")
		@RequestParam final LocalDate startDate,
		@Parameter(description = "종료 날짜", example = "2024-01-31")
		@RequestParam final LocalDate endDate,
		@Parameter(description = "통계 타입 목록", example = "NEW_USERS,DELETED_USERS,NEW_DIARIES")
		@RequestParam final List<StatisticsType> types
	);

	@Operation(
		summary = "멀티 날짜 통계 조회",
		description = "지정된 기간 동안의 선택된 통계 유형별 일일 데이터를 조회합니다. 하루치만 가져오고 싶다면 시작일과 종료일을 같게 설정하면 됩니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = MultiDateStatisticsResponse.class,
			description = "멀티 날짜 통계 조회 성공"
		),
		errors = {@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_STATISTICS_DATE_RANGE)}
	)
	ResponseEntity<ApiResponse<MultiDateStatisticsResponse>> getMultiDateStatistics(
		@Parameter(description = "시작 날짜", example = "2024-01-01")
		@RequestParam final LocalDate startDate,
		@Parameter(description = "종료 날짜", example = "2024-01-07")
		@RequestParam final LocalDate endDate,
		@Parameter(description = "통계 타입 목록", example = "NEW_USERS,NEW_ROUTINES,NEW_DIARIES")
		@RequestParam final List<StatisticsType> types
	);
}

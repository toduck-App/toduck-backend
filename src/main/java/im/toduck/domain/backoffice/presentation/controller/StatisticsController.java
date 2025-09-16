package im.toduck.domain.backoffice.presentation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.backoffice.domain.usecase.StatisticsUseCase;
import im.toduck.domain.backoffice.presentation.api.StatisticsApi;
import im.toduck.domain.backoffice.presentation.dto.request.StatisticsType;
import im.toduck.domain.backoffice.presentation.dto.response.MultiDateStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.OverallStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.PeriodStatisticsResponse;
import im.toduck.global.presentation.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/backoffice/statistics")
@RequiredArgsConstructor
public class StatisticsController implements StatisticsApi {

	private final StatisticsUseCase statisticsUseCase;

	@Override
	@GetMapping("/overall")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<OverallStatisticsResponse>> getOverallStatistics() {
		OverallStatisticsResponse response = statisticsUseCase.getOverallStatistics();
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@GetMapping("/period")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<PeriodStatisticsResponse>> getPeriodStatistics(
		@RequestParam final LocalDate startDate,
		@RequestParam final LocalDate endDate
	) {
		PeriodStatisticsResponse response = statisticsUseCase.getPeriodStatistics(startDate, endDate);
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@GetMapping("/multi-date")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<MultiDateStatisticsResponse>> getMultiDateStatistics(
		@RequestParam final LocalDate startDate,
		@RequestParam final LocalDate endDate,
		@RequestParam final List<StatisticsType> types
	) {
		MultiDateStatisticsResponse response = statisticsUseCase.getMultiDateStatistics(startDate, endDate, types);
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}
}

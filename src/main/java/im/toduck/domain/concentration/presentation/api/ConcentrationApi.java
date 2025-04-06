package im.toduck.domain.concentration.presentation.api;

import java.time.YearMonth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.concentration.presentation.dto.request.ConcentrationRequest;
import im.toduck.domain.concentration.presentation.dto.response.ConcentrationListResponse;
import im.toduck.domain.concentration.presentation.dto.response.ConcentrationSaveResponse;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Concentration")
public interface ConcentrationApi {
	@Operation(
		summary = "집중 저장",
		description = """
			<b>완료된 집중을 저장합니다. 날짜, 달성횟수, 설정횟수, 집중시간을 포함하여 요청합니다.</b><br/><br/>
			<p>날짜, 달성횟수, 설정횟수, 집중시간은 모두 필수 값입니다.</p><br/>
			<p>동일한 날짜에 또 저장하면 기존에 저장된 수치에 더해집니다.</p><br/>
			<p>집중 시간의 단위는 초(s)입니다.</p><br/><br/>
			<p>예시 요청:</p><br/>
			{<br/>
			"concentration_date": "2025-03-12",<br/>
			"targetCount": 2,<br/>
			"settingCount": 5,<br/>
			"time": 1200,<br/>
			}<br/><br/>
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = ConcentrationSaveResponse.class,
			description = "집중 저장 성공, 저장된 집중의 Id를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<ConcentrationSaveResponse>> saveConcentration(
		@RequestBody @Valid ConcentrationRequest request,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "집중 조회",
		description = """
			특정 달의 집중을 조회합니다.
			- 조회된 집중 데이터를 반환합니다.
			- 조회를 원하는 연월을(yyyy-MM) /v1/concentration/monthly 엔드포인트에 yearMonth 파라미터로 요청합니다.
			- 기간에 해당하는 집중 기록들을 반환합니다.
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = ConcentrationListResponse.class,
			description = "집중 조회 성공, 해당 달의 집중 정보를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<ConcentrationListResponse>> getMonthlyConcentration(
		@RequestParam YearMonth yearMonth,
		@AuthenticationPrincipal CustomUserDetails user
	);
}

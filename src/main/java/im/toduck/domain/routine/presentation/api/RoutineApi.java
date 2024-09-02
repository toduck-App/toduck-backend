package im.toduck.domain.routine.presentation.api;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.response.MyRoutineReadListResponse;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Routine")
public interface RoutineApi {
	@Operation(
		summary = "루틴 생성",
		description = "내 루틴을 생성합니다. Request body 에서 필수 값 여부를 확인할 수 있습니다. 예를들어, time 필드가 null 인 경우에는 종일 루틴으로 간주합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = RoutineCreateResponse.class,
			description = "루틴 생성 성공, 생성된 루틴의 Id를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<RoutineCreateResponse>> postRoutine(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestBody @Valid final RoutineCreateRequest request
	);

	@Operation(
		summary = "특정 날짜 본인 루틴 목록 조회",
		description = "특정 날짜에 대한 자신의 루틴 목록을 조회합니다. 루틴 목록은 시간순으로 정렬되어 있지 않을 수 있습니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = MyRoutineReadListResponse.class,
			description = "루틴 목록 조회 성공, 루틴의 고유 Id, 루틴 Color, 완료 여부를 반환합니다."
		),
		errors = {

		}
	)
	ResponseEntity<ApiResponse<MyRoutineReadListResponse>> getMyRoutineList(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	);
}

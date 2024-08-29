package im.toduck.domain.routine.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
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
	ResponseEntity<ApiResponse<RoutineCreateResponse>> createRoutine(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestBody @Valid final RoutineCreateRequest request
	);
}

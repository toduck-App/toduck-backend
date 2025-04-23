package im.toduck.domain.routine.presentation.api;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.request.RoutinePutCompletionRequest;
import im.toduck.domain.routine.presentation.dto.request.RoutineUpdateRequest;
import im.toduck.domain.routine.presentation.dto.response.MyRoutineAvailableListResponse;
import im.toduck.domain.routine.presentation.dto.response.MyRoutineRecordReadListResponse;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.routine.presentation.dto.response.RoutineDetailResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
		summary = "특정 날짜 본인 루틴 기록 목록 조회",
		description = "특정 날짜에 대한 자신의 루틴 기록 목록을 조회합니다. 루틴 목록은 시간순으로 정렬되어 있지 않을 수 있습니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = MyRoutineRecordReadListResponse.class,
			description = "루틴 목록 조회 성공, 루틴의 고유 Id, 루틴 Color, 완료 여부를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<MyRoutineRecordReadListResponse>> getMyRoutineList(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@Parameter(description = "조회할 루틴의 날짜 (형식: YYYY-MM-DD)", required = true, example = "2024-09-02")
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	);

	@Operation(
		summary = "루틴 완료 상태 변경",
		description = "루틴 완료 상태를 변경합니다. "
			+ "특정 날짜에 대한 별도 응답을 제공하지 않습니다. 변경 상태에 대한 멱등성을 보장합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "루틴 완료 상태 변경 성공"
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_ROUTINE),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.ROUTINE_INVALID_DATE)
		}
	)
	ResponseEntity<ApiResponse<?>> putRoutineCompletion(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@Parameter(description = "변경할 루틴 고유 Id", required = true, example = "1") @PathVariable final Long routineId,
		@RequestBody @Valid final RoutinePutCompletionRequest request
	);

	@Operation(
		summary = "본인 루틴 상세 조회",
		description = "본인 루틴의 상세 정보를 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = RoutineDetailResponse.class,
			description = "상세 루틴 조회 성공"
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_ROUTINE)
		}
	)
	ResponseEntity<ApiResponse<RoutineDetailResponse>> getMyRoutineDetail(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@Parameter(description = "상세 조회할 루틴의 Id", required = true, example = "1")
		@PathVariable final Long routineId
	);

	@Operation(
		summary = "사용가능한 본인 루틴 목록 조회",
		description = "자신의 루틴 목록을 조회합니다. 소셜 게시글에서 루틴을 공유하는 경우에 루틴의 목록을 조회할 때 사용될 수 있습니다. 이미 삭제되었거나 비공개로 설정된 루틴은 제외됩니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = MyRoutineAvailableListResponse.class,
			description = "루틴 목록 조회 성공, 마지막으로 수정된 일시를 기준으로 내림차순 정렬됩니다."
		)
	)
	ResponseEntity<ApiResponse<MyRoutineAvailableListResponse>> getMyAvailableRoutineList(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "루틴 수정",
		description = """
			루틴의 내용을 수정합니다.
			1. 모든 필드는 수정 여부와 관계없이 항상 전체 데이터를 전송해야 합니다.
			2. 각 필드의 변경 여부는 is{FieldName}Changed 필드를 통해 명시적으로 표시해야 합니다.
			3. null 값은 유효한 데이터로 취급될 수 있으며(예: 카테고리 삭제), 변경 여부 필드를 통해 의도적인 수정인지 구분합니다.
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "루틴 수정 성공"
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_ROUTINE)
		}
	)
	ResponseEntity<ApiResponse<?>> putRoutine(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@Parameter(description = "수정할 루틴의 Id", required = true, example = "1")
		@PathVariable final Long routineId,
		@RequestBody @Valid final RoutineUpdateRequest request
	);

	@Operation(
		summary = "개별 루틴 삭제",
		description = "개별 루틴 기록을 삭제합니다. "
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "개별 루틴 삭제 성공"
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_ROUTINE),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.ROUTINE_INVALID_DATE)
		}
	)
	ResponseEntity<ApiResponse<?>> deleteIndividualRoutine(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@Parameter(description = "삭제할 루틴의 Id", required = true, example = "1")
		@PathVariable final Long routineId,
		@Parameter(description = "삭제할 개별 루틴이 포함되는 날짜 (형식: YYYY-MM-DD)", required = true, example = "2024-09-02")
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	);

	@Operation(
		summary = "루틴 삭제",
		description = "루틴을 삭제합니다. keepRecords 파라미터를 통해 이전 기록 유지 여부를 결정할 수 있습니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "루틴 삭제 성공"
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_ROUTINE)
		}
	)
	ResponseEntity<ApiResponse<?>> deleteRoutine(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@Parameter(description = "삭제할 루틴의 Id", required = true, example = "1")
		@PathVariable final Long routineId,
		@Parameter(description = "기록 유지 여부 (true: 기록 유지, false: 모두 삭제)", required = true)
		@RequestParam final Boolean keepRecords
	);
}

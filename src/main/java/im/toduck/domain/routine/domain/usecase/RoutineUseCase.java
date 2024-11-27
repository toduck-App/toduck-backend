package im.toduck.domain.routine.domain.usecase;

import java.time.LocalDate;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.routine.common.mapper.RoutineMapper;
import im.toduck.domain.routine.domain.service.RoutineRecordService;
import im.toduck.domain.routine.domain.service.RoutineService;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.request.RoutinePutCompletionRequest;
import im.toduck.domain.routine.presentation.dto.response.MyRoutineAvailableListResponse;
import im.toduck.domain.routine.presentation.dto.response.MyRoutineRecordReadListResponse;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.routine.presentation.dto.response.RoutineDetailResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class RoutineUseCase {
	private final UserService userService;
	private final RoutineService routineService;
	private final RoutineRecordService routineRecordService;

	@Transactional
	public RoutineCreateResponse createRoutine(final Long userId, final RoutineCreateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		RoutineCreateResponse routineCreateResponse = routineService.create(user, request);

		log.info("루틴 생성 - UserId: {}, RoutineId:{}", userId, routineCreateResponse.routineId());
		return routineCreateResponse;
	}

	@Transactional(readOnly = true)
	public MyRoutineRecordReadListResponse readMyRoutineRecordList(final Long userId, final LocalDate date) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		List<RoutineRecord> routineRecords = routineRecordService.getRecords(user, date);
		List<Routine> routines = routineService.getUnrecordedRoutinesForDate(user, date, routineRecords);

		log.info("본인 루틴 기록 목록 조회 - UserId: {}, 조회한 날짜: {}", userId, date);
		return RoutineMapper.toMyRoutineRecordReadListResponse(date, routines, routineRecords);
	}

	@Transactional
	public void updateRoutineCompletion(
		final Long userId,
		final Long routineId,
		final RoutinePutCompletionRequest request
	) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Routine routine = routineService.getUserRoutineIncludingDeleted(user, routineId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_ROUTINE));

		LocalDate date = request.routineDate();
		boolean isCompleted = request.isCompleted();

		if (routineRecordService.updateIfPresent(routine, date, isCompleted)) {
			log.info(
				"루틴 상태 변경 성공(기록 수정) - 사용자 Id: {}, 루틴 Id: {}, 루틴 날짜: {}, 완료상태: {}",
				userId, routineId, date, isCompleted
			);
			return;
		}

		// TODO: 이때는 루틴이 삭제 상태인 경우 삭제시점 이전 데이터만 반복 가능
		if (!routineService.canCreateRecordForDate(routine, date)) {
			log.info("루틴 상태 변경 실패 - 사용자 Id: {}, 루틴 Id: {}, 루틴 날짜: {}", userId, routineId, date);
			throw CommonException.from(ExceptionCode.ROUTINE_INVALID_DATE);
		}

		routineRecordService.create(routine, date, isCompleted);
		log.info(
			"루틴 상태 변경 성공(기록 생성) - 사용자 Id: {}, 루틴 Id: {}, 루틴 날짜: {}, 완료상태: {}",
			userId, routineId, date, isCompleted
		);
	}

	@Transactional(readOnly = true)
	public RoutineDetailResponse readDetail(final Long userId, final Long routineId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Routine routine = routineService.getUserRoutineIncludingDeleted(user, routineId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_ROUTINE));

		return RoutineMapper.toRoutineDetailResponse(routine);
	}

	@Transactional(readOnly = true)
	public MyRoutineAvailableListResponse readMyAvailableRoutineList(final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		List<Routine> routines = routineService.getAvailableRoutine(user);

		log.info("사용가능한 본인 루틴 목록 조회 - 사용자 Id: {}", userId);
		return RoutineMapper.toMyRoutineAvailableListResponse(routines);
	}

	@Transactional
	public void deleteRoutine(final Long userId, final Long routineId, final boolean keepRecords) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Routine routine = routineService.getUserRoutineIncludingDeleted(user, routineId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_ROUTINE));

		if (keepRecords) {
			routineRecordService.removeIncompletedFuturesByRoutine(routine);
			routineService.remove(routine);
			log.info("루틴 삭제 성공(기록 유지) - 사용자 Id: {}, 루틴 Id: {}", userId, routineId);
			return;
		}

		routineRecordService.removeAllByRoutine(routine);
		routineService.remove(routine);
		log.info("루틴 삭제 성공(기록 포함 삭제) - 사용자 Id: {}, 루틴 Id: {}", userId, routineId);
	}

	// TODO: 삭제된 루틴에 대해서는 루틴 수정 금지 필요
}

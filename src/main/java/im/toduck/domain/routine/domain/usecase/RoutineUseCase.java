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
import im.toduck.domain.routine.presentation.dto.response.MyRoutineReadListResponse;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
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
	public MyRoutineReadListResponse readMyRoutineList(final Long userId, final LocalDate date) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		List<RoutineRecord> routineRecords = routineRecordService.getRecords(user, date);
		List<Routine> routines = routineService.getUnrecordedRoutinesForDate(user, date, routineRecords);

		log.info("본인 루틴 목록 조회 - UserId: {}, 조회한 날짜: {}", userId, date);
		return RoutineMapper.toMyRoutineReadResponse(date, routines, routineRecords);
	}
}
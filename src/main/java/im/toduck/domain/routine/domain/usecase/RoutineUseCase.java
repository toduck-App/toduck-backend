package im.toduck.domain.routine.domain.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.routine.common.dto.DailyRoutineData;
import im.toduck.domain.routine.common.mapper.RoutineMapper;
import im.toduck.domain.routine.common.mapper.RoutineRecordMapper;
import im.toduck.domain.routine.domain.service.RoutineRecordService;
import im.toduck.domain.routine.domain.service.RoutineService;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.request.RoutinePutCompletionRequest;
import im.toduck.domain.routine.presentation.dto.request.RoutineUpdateRequest;
import im.toduck.domain.routine.presentation.dto.response.MyRoutineAvailableListResponse;
import im.toduck.domain.routine.presentation.dto.response.MyRoutineRecordReadListResponse;
import im.toduck.domain.routine.presentation.dto.response.MyRoutineRecordReadMultipleDatesResponse;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.routine.presentation.dto.response.RoutineDetailResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class RoutineUseCase {
	private static final int MAX_ROUTINE_DATE_RANGE_DAYS = 13;

	private final UserService userService;
	private final RoutineService routineService;
	private final RoutineRecordService routineRecordService;
	private final DistributedLock distributedLock;

	/**
	 * Creates a new routine for the specified user.
	 *
	 * @param userId the ID of the user for whom the routine is created
	 * @param request the routine creation request containing routine details
	 * @return the response containing information about the created routine
	 * @throws CommonException if the user is not found
	 */
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

		List<RoutineRecord> routineRecords = routineRecordService.getRecordsIncludingDeleted(user, date);

		List<Routine> routines = routineService.getUnrecordedRoutinesForDate(user, date, routineRecords);
		List<RoutineRecord> activeRoutineRecords = routineRecords.stream()
			.filter(record -> !record.isInDeletedState())
			.toList();

		log.info("본인 루틴 기록 목록 조회 - UserId: {}, 조회한 날짜: {}", userId, date);
		return RoutineMapper.toMyRoutineRecordReadListResponse(
			DailyRoutineData.of(date, routines, activeRoutineRecords)
		);
	}

	/**
	 * Retrieves a user's routine records for each day within a specified date range.
	 *
	 * @param userId the ID of the user whose routine records are to be retrieved
	 * @param startDate the start date of the range (inclusive)
	 * @param endDate the end date of the range (inclusive)
	 * @return a response containing daily routine data for the specified date range
	 * @throws CommonException if the user is not found or the date range is invalid
	 */
	@Transactional(readOnly = true)
	public MyRoutineRecordReadMultipleDatesResponse readMyRoutineRecordListMultipleDates(
		final Long userId,
		final LocalDate startDate,
		final LocalDate endDate
	) {
		if (startDate.isAfter(endDate) || ChronoUnit.DAYS.between(startDate, endDate) > MAX_ROUTINE_DATE_RANGE_DAYS) {
			throw CommonException.from(ExceptionCode.EXCEED_ROUTINE_DATE_RANGE);
		}

		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		List<RoutineRecord> allRoutineRecords = routineRecordService.getRecordsBetweenDates(user, startDate, endDate);

		List<DailyRoutineData> dailyRoutineDatas = routineService
			.getRoutineDataByDateRange(user, startDate, endDate, allRoutineRecords);

		log.info("본인 루틴 기록 기간 조회 - UserId: {}, 조회 기간: {} ~ {}", userId, startDate, endDate);
		return RoutineMapper.toMyRoutineRecordReadMultipleDatesResponse(
			startDate, endDate, dailyRoutineDatas
		);
	}

	/**
	 * Updates the completion status of a user's routine for a specific date, creating a new record if one does not exist.
	 *
	 * Acquires a distributed lock to ensure concurrency safety for the given routine and date. If a record exists for the date, its completion status is updated; otherwise, a new record is created if allowed. Throws an exception if the date is invalid for the routine.
	 *
	 * @param userId the ID of the user
	 * @param routineId the ID of the routine
	 * @param request contains the target date and completion status
	 * @throws CommonException if the user or routine is not found, or if the date is invalid for the routine
	 */
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

		String lockKey = "routine:" + routineId + ":date:" + date;
		distributedLock.executeWithLock(lockKey, () -> {
			if (routineRecordService.updateIfPresent(routine, date, isCompleted)) {
				log.info("루틴 상태 변경 성공(기록 수정) - 사용자 Id: {}, 루틴 Id: {}, 루틴 날짜: {}, 완료상태: {}",
					userId, routineId, date, isCompleted
				);
				return;
			}

			if (!routineService.canCreateRecordForDate(routine, date)) {
				log.info("루틴 상태 변경 실패 - 사용자 Id: {}, 루틴 Id: {}, 루틴 날짜: {}", userId, routineId, date);
				throw CommonException.from(ExceptionCode.ROUTINE_INVALID_DATE);
			}

			routineRecordService.create(routine, date, isCompleted);
			log.info(
				"루틴 상태 변경 성공(기록 생성) - 사용자 Id: {}, 루틴 Id: {}, 루틴 날짜: {}, 완료상태: {}", userId, routineId, date, isCompleted
			);
		});
	}

	/**
	 * Retrieves detailed information for a specific routine belonging to a user.
	 *
	 * @param userId the ID of the user
	 * @param routineId the ID of the routine
	 * @return a detailed response containing routine information
	 * @throws CommonException if the user or routine is not found
	 */
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
	public void updateRoutine(final Long userId, final Long routineId, final RoutineUpdateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		Routine routine = routineService.getUserRoutine(user, routineId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_ROUTINE));

		if (request.isTimeChanged() || request.isDaysOfWeekChanged()) {
			LocalDateTime deletionTime = LocalDateTime.now();
			routineRecordService.removeIncompletedFuturesByRoutine(routine, deletionTime);
			saveMissingIncompleteRecordsInBulk(routine, routine.getScheduleModifiedAt(), deletionTime);
		}
		routineService.updateFields(routine, request);

		log.info("루틴 수정 성공 - 사용자 Id: {}, 루틴 Id: {}", userId, routineId);
	}

	/**
	 * Deletes a routine for a user, with an option to retain or remove associated routine records.
	 *
	 * If {@code keepRecords} is true, removes only incomplete future routine records and preserves historical incomplete records before deleting the routine. If false, deletes all routine records along with the routine.
	 *
	 * @param userId the ID of the user
	 * @param routineId the ID of the routine to delete
	 * @param keepRecords whether to retain historical routine records
	 *
	 * @throws CommonException if the user or routine is not found
	 */
	@Transactional
	public void deleteRoutine(final Long userId, final Long routineId, final boolean keepRecords) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Routine routine = routineService.getUserRoutineIncludingDeleted(user, routineId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_ROUTINE));

		if (keepRecords) {
			LocalDateTime deletionTime = LocalDateTime.now();
			routineRecordService.removeIncompletedFuturesByRoutine(routine, deletionTime);
			saveMissingIncompleteRecordsInBulk(routine, routine.getScheduleModifiedAt(), deletionTime);
			routineService.remove(routine);
			log.info("루틴 삭제 성공(기록 유지) - 사용자 Id: {}, 루틴 Id: {}", userId, routineId);
			return;
		}

		routineRecordService.removeAllByRoutine(routine);
		routineService.remove(routine);
		log.info("루틴 삭제 성공(기록 포함 삭제) - 사용자 Id: {}, 루틴 Id: {}", userId, routineId);
	}

	/**
	 * Deletes an individual routine record for a specific date, ensuring concurrency safety.
	 *
	 * If a routine record exists for the given date, it is removed. If not, and the date is valid for the routine, a deleted record is created for that date. The operation is performed within a distributed lock to prevent concurrent modifications for the same routine and date.
	 *
	 * @param userId the ID of the user
	 * @param routineId the ID of the routine
	 * @param date the date of the routine record to delete
	 * @throws CommonException if the user or routine is not found, or if the date is invalid for the routine
	 */
	@Transactional
	public void deleteIndividualRoutine(
		final Long userId,
		final Long routineId,
		final LocalDate date
	) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Routine routine = routineService.getUserRoutineIncludingDeleted(user, routineId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_ROUTINE));

		String lockKey = "routine:" + routineId + ":date:" + date;
		distributedLock.executeWithLock(lockKey, () -> {
			if (routineRecordService.removeIfPresent(routine, date)) {
				log.info("개별 루틴 삭제 성공(기존 기록 삭제) - 사용자 Id: {}, 루틴 Id: {}, 루틴 날짜: {}", userId, routineId, date);
				return;
			}

			if (!routineService.canCreateRecordForDate(routine, date)) {
				log.info("개별 루틴 삭제 실패(유효하지 않은 날짜) - 사용자 Id: {}, 루틴 Id: {}, 루틴 날짜: {}", userId, routineId, date);
				throw CommonException.from(ExceptionCode.ROUTINE_INVALID_DATE);
			}

			routineRecordService.createAsDeleted(routine, date);
			log.info("개별 루틴 삭제 성공(삭제 기록 생성) - 사용자 Id: {}, 루틴 Id: {}, 루틴 날짜: {}", userId, routineId, date);
		});
	}

	/**
	 * 특정 기간 동안 루틴에 대한 누락된 기록을 생성합니다.
	 * <p>
	 * 이 메서드는 루틴의 수정이나 삭제 시 호출되어, 스케줄 변경 이전의 기록을 보존합니다.
	 * 루틴의 반복 설정(요일, 시간)이 변경되더라도 과거의 데이터는 독립적으로 유지하여
	 * 사용자의 루틴 이행 이력을 정확하게 기록하는 역할을 합니다.
	 * <p>
	 * 예를 들어, 사용자가 월/수/금 루틴을 화/목 루틴으로 변경한 경우,
	 * 변경 이전의 월/수/금 날짜들에 대한 기록은 그대로 유지되도록 합니다.
	 *
	 * @param routine 대상 루틴
	 * @param startTime 기록 시작 시간(보통 이전 스케줄 변경 시간)
	 * @param endTime 기록 종료 시간(보통 현재 시간)
	 */
	private void saveMissingIncompleteRecordsInBulk(
		final Routine routine,
		final LocalDateTime startTime,
		final LocalDateTime endTime
	) {
		Set<LocalDate> existingDates
			= routineRecordService.getExistingRecordDatesIncludingDeleted(routine, startTime, endTime);

		LocalDate startDate = startTime.toLocalDate();
		LocalDate endDate = endTime.toLocalDate();

		List<RoutineRecord> newRecords = routine.getDaysOfWeekBitmask()
			.streamMatchingDatesInRange(startDate, endDate.minusDays(1))
			.filter(date -> !existingDates.contains(date))
			.map(date -> RoutineRecordMapper.toRoutineRecord(routine, date, false))
			.toList();

		if (!newRecords.isEmpty()) {
			routineRecordService.saveAll(newRecords);
		}
	}
}

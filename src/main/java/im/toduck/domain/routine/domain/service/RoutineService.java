package im.toduck.domain.routine.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.routine.common.dto.DailyRoutineData;
import im.toduck.domain.routine.common.mapper.RoutineMapper;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.routine.persistence.repository.RoutineRepository;
import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.request.RoutineUpdateRequest;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.persistence.projection.DailyCount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoutineService {
	private final RoutineRepository routineRepository;

	@Transactional
	public RoutineCreateResponse create(final User user, final RoutineCreateRequest request) {
		Routine routine = RoutineMapper.toRoutine(user, request);

		return RoutineMapper.toRoutineCreateResponse(
			routineRepository.save(routine)
		);
	}

	@Transactional(readOnly = true)
	public List<Routine> getUnrecordedRoutinesForDate(
		final User user,
		final LocalDate date,
		final List<RoutineRecord> routineRecords
	) {
		return routineRepository.findUnrecordedRoutinesByDateMatchingDayOfWeek(user, date, routineRecords);
	}

	@Transactional(readOnly = true)
	public List<DailyRoutineData> getRoutineDataByDateRange(
		final User user,
		final LocalDate startDate,
		final LocalDate endDate,
		final List<RoutineRecord> routineRecords
	) {
		List<Routine> periodRoutines = routineRepository.findRoutinesByDateBetween(
			user, startDate, endDate
		);

		Map<LocalDate, List<RoutineRecord>> recordsByDate = routineRecords.stream()
			.collect(Collectors.groupingBy(record -> record.getRecordAt().toLocalDate()));

		List<DailyRoutineData> result = new ArrayList<>();
		LocalDate currentDate = startDate;

		while (!currentDate.isAfter(endDate)) {
			final LocalDate date = currentDate;

			List<RoutineRecord> dateRoutineRecords = recordsByDate.getOrDefault(date, Collections.emptyList());

			Set<Long> recordedRoutineIds = dateRoutineRecords.stream()
				.map(record -> record.getRoutine().getId())
				.collect(Collectors.toSet());

			List<Routine> dateUnrecordedRoutines = periodRoutines.stream()
				.filter(routine -> !recordedRoutineIds.contains(routine.getId()))
				.filter(routine -> routine.getDaysOfWeekBitmask().includesDayOf(date))
				.filter(routine -> !routine.getScheduleModifiedAt().isAfter(date.atTime(LocalTime.MAX)))
				.toList();

			List<RoutineRecord> activeDateRoutineRecords = dateRoutineRecords.stream()
				.filter(record -> !record.isInDeletedState())
				.toList();

			result.add(DailyRoutineData.of(date, dateUnrecordedRoutines, activeDateRoutineRecords));
			currentDate = currentDate.plusDays(1);
		}

		return result;
	}

	public Optional<Routine> getUserRoutine(final User user, final Long id) {
		return routineRepository.findByIdAndUserAndDeletedAtIsNull(id, user);
	}

	public Optional<Routine> getUserRoutineIncludingDeleted(final User user, final Long id) {
		return routineRepository.findByIdAndUser(id, user);
	}

	public boolean canCreateRecordForDate(final Routine routine, final LocalDate date) {
		return routineRepository.isActiveForDate(routine, date);
	}

	public List<Routine> getAvailableRoutine(final User user) {
		return routineRepository.findAllByUserAndIsPublicTrueAndDeletedAtIsNullOrderByUpdatedAtDesc(user);
	}

	public List<Routine> getSocialProfileRoutines(final User user) {
		return routineRepository.findAllByUserAndIsPublicTrueAndDeletedAtIsNullOrderByTimeAsc(user);
	}

	@Transactional(readOnly = true)
	public Optional<Routine> findAvailablePublicRoutineById(final Long routineId) {
		return routineRepository.findByIdAndIsPublicTrueAndDeletedAtIsNull(routineId);
	}

	@Transactional
	public void updateFields(final Routine routine, final RoutineUpdateRequest request) {
		routine.updateFromRequest(request);
		routineRepository.save(routine);
	}

	@Transactional
	public void remove(final Routine routine) {
		routine.delete();
		routineRepository.save(routine);
	}

	@Transactional
	public int incrementSharedCountAndGetNewCount(final Routine sourceRoutine) {
		routineRepository.incrementSharedCountAtomically(sourceRoutine.getId());

		Routine updatedRoutine = routineRepository.findById(sourceRoutine.getId())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_ROUTINE));
		return updatedRoutine.getSharedCount();
	}

	@Transactional(readOnly = true)
	public int getTotalRoutineShareCount(final User user) {
		return routineRepository.sumRoutineSharedCountByUser(user);
	}

	@Transactional
	public void deleteAllUnsharedRoutinesByUser(final User user) {
		routineRepository.deleteAllUnsharedRoutinesByUser(user);
	}

	public List<Routine> findAllUnsharedRoutineByUser(User user) {
		return routineRepository.findAllUnsharedRoutinesByUser(user);
	}

	@Transactional(readOnly = true)
	public List<Routine> findActiveRoutinesWithReminderForDates(LocalDate startDate, LocalDate endDate) {
		return routineRepository.findActiveRoutinesWithReminderForDates(startDate, endDate);
	}

	@Transactional(readOnly = true)
	public long getTotalRoutineCount() {
		return routineRepository.count();
	}

	@Transactional(readOnly = true)
	public long getRoutineCountByDateRange(final LocalDate startDate, final LocalDate endDate) {
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
		return routineRepository.countByCreatedAtBetween(startDateTime, endDateTime);
	}

	@Transactional(readOnly = true)
	public long getActiveRoutineUsersCount() {
		return routineRepository.countDistinctUsers();
	}

	@Transactional(readOnly = true)
	public Map<LocalDate, Long> getRoutineCountByDateRangeGroupByDate(
		final LocalDate startDate,
		final LocalDate endDate
	) {
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

		List<DailyCount> dailyCounts = routineRepository.countByCreatedAtBetweenGroupByDate(
			startDateTime, endDateTime
		);

		return dailyCounts.stream()
			.collect(Collectors.toMap(DailyCount::date, DailyCount::count));
	}
}

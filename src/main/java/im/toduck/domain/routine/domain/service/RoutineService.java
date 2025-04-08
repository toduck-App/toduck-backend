package im.toduck.domain.routine.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.routine.common.mapper.RoutineMapper;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.routine.persistence.repository.RoutineRepository;
import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.request.RoutineUpdateRequest;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.user.persistence.entity.User;
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

	public List<Routine> getUnrecordedRoutinesForDate(
		final User user,
		final LocalDate date,
		final List<RoutineRecord> routineRecords
	) {
		List<Routine> routines = routineRepository.findUnrecordedRoutinesForDate(user, date, routineRecords);

		return routines.stream().filter(routine -> {
			LocalDateTime compareTime = routine.isAllDay()
				? date.minusDays(1).atTime(LocalTime.MAX)
				: date.atTime(routine.getTime());

			return !routine.getScheduleModifiedAt().isAfter(compareTime);
		}).toList();
	}

	public Optional<Routine> getUserRoutine(final User user, final Long id) {
		return routineRepository.findByIdAndUserAndDeletedAtIsNull(id, user);
	}

	public Optional<Routine> getUserRoutineIncludingDeleted(final User user, final Long id) {
		return routineRepository.findByIdAndUser(id, user);
	}

	public boolean canCreateRecordForDate(final Routine routine, final LocalDate date) {
		if (routineRepository.isActiveForDate(routine, date)) {
			LocalDateTime compareTime = routine.isAllDay()
				? date.minusDays(1).atTime(LocalTime.MAX)
				: date.atTime(routine.getTime());

			return !routine.getScheduleModifiedAt().isAfter(compareTime);
		}

		return false;
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
	public void incrementSharedCountAtomically(final Routine sourceRoutine) {
		routineRepository.incrementSharedCountAtomically(sourceRoutine.getId());
	}

	@Transactional(readOnly = true)
	public int getTotalRoutineShareCount(final User user) {
		return routineRepository.sumRoutineSharedCountByUser(user);
	}
}

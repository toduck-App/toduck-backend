package im.toduck.domain.routine.domain.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.routine.common.mapper.RoutineMapper;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.routine.persistence.repository.RoutineRepository;
import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
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
		// TODO: 여기에선 삭제되면 조회되면 안됨, 근데 삭제 시점 미래 기준으로는 조회가 안되는거고 과거 시점이면 조회가 되어야함
		return routineRepository.findUnrecordedRoutinesForDate(user, date, routineRecords);
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

	@Transactional
	public void remove(final Routine routine) {
		routine.delete();
		routineRepository.save(routine);
	}
}

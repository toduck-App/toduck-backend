package im.toduck.domain.routine.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.routine.mapper.RoutineMapper;
import im.toduck.domain.routine.persistence.entity.Routine;
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

}

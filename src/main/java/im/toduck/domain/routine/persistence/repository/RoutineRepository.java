package im.toduck.domain.routine.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.routine.persistence.entity.Routine;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
}

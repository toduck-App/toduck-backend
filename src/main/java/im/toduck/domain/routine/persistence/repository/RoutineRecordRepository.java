package im.toduck.domain.routine.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.routine.persistence.repository.querydsl.RoutineRecordRepositoryCustom;

@Repository
public interface RoutineRecordRepository extends JpaRepository<RoutineRecord, Long>, RoutineRecordRepositoryCustom {
	void deleteAllByRoutine(final Routine routine);
}

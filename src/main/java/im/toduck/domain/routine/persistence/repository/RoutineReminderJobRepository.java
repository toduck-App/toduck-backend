package im.toduck.domain.routine.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.routine.persistence.entity.RoutineReminderJob;
import im.toduck.domain.routine.persistence.repository.querydsl.RoutineReminderJobRepositoryCustom;

@Repository
public interface RoutineReminderJobRepository
	extends JpaRepository<RoutineReminderJob, Long>, RoutineReminderJobRepositoryCustom {
}

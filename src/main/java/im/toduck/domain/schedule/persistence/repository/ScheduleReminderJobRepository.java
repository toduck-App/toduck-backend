package im.toduck.domain.schedule.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.schedule.persistence.entity.ScheduleReminderJob;
import im.toduck.domain.schedule.persistence.repository.querydsl.ScheduleReminderJobRepositoryCustom;

@Repository
public interface ScheduleReminderJobRepository
	extends JpaRepository<ScheduleReminderJob, Long>, ScheduleReminderJobRepositoryCustom {
}

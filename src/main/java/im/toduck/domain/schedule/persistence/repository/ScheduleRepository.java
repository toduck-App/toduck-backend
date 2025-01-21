package im.toduck.domain.schedule.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.schedule.persistence.entity.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}

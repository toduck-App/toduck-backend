package im.toduck.domain.schedule.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.repository.querydsl.ScheduleRepositoryCustom;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositoryCustom {
}

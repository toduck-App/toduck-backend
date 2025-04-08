package im.toduck.domain.schedule.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import im.toduck.domain.schedule.persistence.repository.querydsl.ScheduleRecordRepositoryCustom;

public interface ScheduleRecordRepository
	extends JpaRepository<ScheduleRecord, Long>, ScheduleRecordRepositoryCustom {
}

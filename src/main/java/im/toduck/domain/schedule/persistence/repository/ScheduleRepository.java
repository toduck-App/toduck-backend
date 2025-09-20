package im.toduck.domain.schedule.persistence.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.repository.querydsl.ScheduleRepositoryCustom;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositoryCustom {

	@Query("SELECT COUNT(s) FROM Schedule s WHERE s.createdAt BETWEEN :startDateTime AND :endDateTime")
	long countByCreatedAtBetween(
		@Param("startDateTime") LocalDateTime startDateTime,
		@Param("endDateTime") LocalDateTime endDateTime
	);
}

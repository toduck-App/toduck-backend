package im.toduck.domain.diary.persistence.repository.querydsl;

import java.time.LocalDateTime;
import java.util.List;

import im.toduck.global.persistence.projection.DailyCount;

public interface DiaryRepositoryCustom {
	List<DailyCount> countByCreatedAtBetweenGroupByDate(
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	);
}

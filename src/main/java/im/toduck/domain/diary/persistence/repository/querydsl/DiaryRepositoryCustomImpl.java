package im.toduck.domain.diary.persistence.repository.querydsl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.diary.persistence.entity.QDiary;
import im.toduck.global.persistence.helper.DailyCountQueryHelper;
import im.toduck.global.persistence.projection.DailyCount;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DiaryRepositoryCustomImpl implements DiaryRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QDiary qDiary = QDiary.diary;

	@Override
	public List<DailyCount> countByCreatedAtBetweenGroupByDate(
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	) {
		return DailyCountQueryHelper.countGroupByDate(
			queryFactory, qDiary, qDiary.createdAt, qDiary.count(), startDateTime, endDateTime
		);
	}
}

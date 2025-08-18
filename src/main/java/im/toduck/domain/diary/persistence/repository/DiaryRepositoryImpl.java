package im.toduck.domain.diary.persistence.repository;

import java.time.LocalDate;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.diary.persistence.entity.QDiary;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DiaryRepositoryImpl implements DiaryRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public LocalDate findLastDiaryDate(Long userId) {
		QDiary diary = QDiary.diary;

		return queryFactory
			.select(diary.date)
			.from(diary)
			.where(diary.user.id.eq(userId))
			.orderBy(diary.date.desc())
			.limit(1)
			.fetchFirst();
	}
}

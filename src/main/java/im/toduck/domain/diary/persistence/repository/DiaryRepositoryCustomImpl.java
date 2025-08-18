package im.toduck.domain.diary.persistence.repository;

import java.time.LocalDate;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.diary.persistence.entity.QDiary;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DiaryRepositoryCustomImpl implements DiaryRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public LocalDate findLastDiaryDate(Long userId) {
		if (userId == null) {
			throw CommonException.from(ExceptionCode.NOT_FOUND_USER);
		}
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

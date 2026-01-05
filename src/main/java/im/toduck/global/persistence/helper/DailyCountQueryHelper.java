package im.toduck.global.persistence.helper;

import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.global.persistence.projection.DailyCount;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 일별 집계 통계 쿼리를 위한 공통 헬퍼 클래스.
 * QueryDSL GROUP BY DATE() 쿼리의 중복 코드를 제거합니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DailyCountQueryHelper {

	/**
	 * 지정된 기간 동안의 일별 카운트를 조회합니다.
	 *
	 * @param queryFactory JPAQueryFactory 인스턴스
	 * @param entity       조회 대상 엔티티 (예: QUser.user)
	 * @param dateTimePath 집계 기준이 되는 datetime 필드 (예: qUser.createdAt)
	 * @param countExpr    카운트 표현식 (예: qUser.count())
	 * @param startDateTime 조회 시작 일시
	 * @param endDateTime   조회 종료 일시
	 * @return 일별 카운트 목록
	 */
	public static List<DailyCount> countGroupByDate(
		final JPAQueryFactory queryFactory,
		final EntityPath<?> entity,
		final DateTimePath<LocalDateTime> dateTimePath,
		final NumberExpression<Long> countExpr,
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	) {
		DateTemplate<java.sql.Date> dateExpr = Expressions.dateTemplate(
			java.sql.Date.class, "DATE({0})", dateTimePath);

		List<Tuple> tuples = queryFactory
			.select(dateExpr, countExpr)
			.from(entity)
			.where(dateTimePath.between(startDateTime, endDateTime))
			.groupBy(dateExpr)
			.orderBy(dateExpr.asc())
			.fetch();

		return tuples.stream()
			.map(tuple -> new DailyCount(
				tuple.get(dateExpr).toLocalDate(),
				tuple.get(countExpr)
			))
			.toList();
	}
}

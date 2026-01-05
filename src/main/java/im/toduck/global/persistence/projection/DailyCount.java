package im.toduck.global.persistence.projection;

import java.time.LocalDate;

/**
 * 일별 집계 통계를 위한 Projection record.
 * GROUP BY DATE() 쿼리 결과를 매핑하는 데 사용됩니다.
 */
public record DailyCount(
	LocalDate date,
	Long count
) {
}

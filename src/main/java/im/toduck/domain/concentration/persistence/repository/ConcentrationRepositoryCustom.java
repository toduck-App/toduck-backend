package im.toduck.domain.concentration.persistence.repository;

import java.time.YearMonth;

public interface ConcentrationRepositoryCustom {
	Integer getAverageConcentrationPercentageByMonth(Long userId, YearMonth yearMonth);
}

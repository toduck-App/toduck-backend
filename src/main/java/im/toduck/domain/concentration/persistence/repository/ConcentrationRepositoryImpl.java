package im.toduck.domain.concentration.persistence.repository;

import java.time.YearMonth;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.concentration.persistence.entity.QConcentration;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ConcentrationRepositoryImpl implements ConcentrationRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Integer getAverageConcentrationPercentageByMonth(Long userId, YearMonth yearMonth) {
		QConcentration concentration = QConcentration.concentration;

		Double avgPercentage = queryFactory
			.select(concentration.targetCount.multiply(100).divide(concentration.settingCount).avg())
			.from(concentration)
			.where(
				concentration.user.id.eq(userId),
				concentration.date.year().eq(yearMonth.getYear()),
				concentration.date.month().eq(yearMonth.getMonthValue())
			)
			.fetchOne();

		return avgPercentage != null ? avgPercentage.intValue() : null;
	}
}

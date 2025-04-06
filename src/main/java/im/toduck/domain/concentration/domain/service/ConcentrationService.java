package im.toduck.domain.concentration.domain.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import im.toduck.domain.concentration.common.mapper.ConcentrationMapper;
import im.toduck.domain.concentration.persistence.entity.Concentration;
import im.toduck.domain.concentration.persistence.repository.ConcentrationRepository;
import im.toduck.domain.concentration.presentation.dto.request.ConcentrationRequest;
import im.toduck.domain.user.persistence.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcentrationService {
	private final ConcentrationRepository concentrationRepository;

	@Transactional
	public Concentration saveConcentration(User user, ConcentrationRequest request) {
		Concentration concentration = concentrationRepository.findByUserAndDate(user, request.date())
			.orElseGet((() -> ConcentrationMapper.concentration(user, request)));

		concentration.addTargetCount(request.targetCount());
		concentration.addSettingCount(request.settingCount());
		concentration.addTime(request.time());

		return concentrationRepository.save(concentration);
	}

	@Transactional
	public List<Concentration> getMonthlyConcentration(User user, YearMonth yearMonth) {
		LocalDate startDate = yearMonth.atDay(1);
		LocalDate endDate = yearMonth.atEndOfMonth();

		return concentrationRepository.findByUserAndDateBetween(user, startDate, endDate);
	}

	public int getMonthConcentrationPercent(Long userId, YearMonth yearMonth) {
		Integer totalPercentage = concentrationRepository.getAverageConcentrationPercentageByMonth(userId, yearMonth);

		if (totalPercentage == null) {
			return 0;
		}

		return totalPercentage;
	}
}

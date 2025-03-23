package im.toduck.domain.concentration.domain.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

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
			.orElseGet((() -> new Concentration(user, request.date())));

		concentration.addTargetCount(request.targetCount());
		concentration.addSettingCount(request.settingCount());
		concentration.addTime(request.time());

		return concentrationRepository.save(concentration);
	}

	@Transactional
	public List<Concentration> getMonthlyConcentration(User user, String yearMonth) {
		LocalDate startDate = LocalDate.parse(yearMonth + "-01");
		LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

		return concentrationRepository.findByUserAndDateBetween(user, startDate, endDate);
	}
}

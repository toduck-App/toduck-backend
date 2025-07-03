package im.toduck.domain.routine.domain.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import im.toduck.domain.routine.domain.service.RoutineReminderSchedulerService;
import im.toduck.domain.routine.persistence.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoutineReminderBatchSchedulerUseCase {

	private final RoutineRepository routineRepository;
	private final RoutineReminderSchedulerService routineReminderSchedulerService;

	@Scheduled(cron = "0 58 3 * * *", zone = "Asia/Seoul")
	@SchedulerLock(
		name = "RoutineReminderBatchScheduler_scheduleDailyRoutineReminders",
		lockAtMostFor = "55m",
		lockAtLeastFor = "1m"
	)
	@Transactional(readOnly = true)
	public void scheduleDailyRoutineReminders() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDate today = currentDateTime.toLocalDate();
		LocalDate tomorrow = today.plusDays(1);

		routineRepository.findActiveRoutinesWithReminderForDates(today, tomorrow)
			.forEach(routine -> routineReminderSchedulerService.scheduleRoutineReminders(routine, currentDateTime));

		log.info("일일 루틴 알림 배치 작업 완료");
	}
}

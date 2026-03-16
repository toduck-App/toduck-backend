package im.toduck.domain.schedule.domain.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import im.toduck.domain.schedule.domain.service.ScheduleReadService;
import im.toduck.domain.schedule.domain.service.ScheduleReminderSchedulerService;
import im.toduck.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.quartz.auto-startup", havingValue = "true", matchIfMissing = true)
public class ScheduleReminderBatchSchedulerUseCase {

    private final ScheduleReadService scheduleReadService;
    private final ScheduleReminderSchedulerService scheduleReminderSchedulerService;

    @Scheduled(cron = "0 58 3 * * *", zone = "Asia/Seoul")
    @SchedulerLock(name = "ScheduleReminderBatchScheduler_scheduleDailyScheduleReminders", lockAtMostFor = "55m", lockAtLeastFor = "1m")
    @Transactional
    public void scheduleDailyScheduleReminders() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDate today = currentDateTime.toLocalDate();
        LocalDate tomorrow = today.plusDays(1);

        log.info("일일 일정 알림 배치 작업 시작 - 현재시간: {}", currentDateTime);

        scheduleReadService.findActiveSchedulesWithAlarmForDates(today, tomorrow)
                .forEach(
                        schedule -> scheduleReminderSchedulerService.scheduleScheduleReminders(
                                schedule, currentDateTime, true));

        log.info("일일 일정 알림 배치 작업 완료");
    }
}

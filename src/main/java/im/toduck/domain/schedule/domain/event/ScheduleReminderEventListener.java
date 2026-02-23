package im.toduck.domain.schedule.domain.event;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import im.toduck.domain.schedule.domain.service.ScheduleReadService;
import im.toduck.domain.schedule.domain.service.ScheduleReminderSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.quartz.auto-startup", havingValue = "true", matchIfMissing = true)
public class ScheduleReminderEventListener {

    private final ScheduleReadService scheduleReadService;
    private final ScheduleReminderSchedulerService scheduleReminderSchedulerService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleScheduleCreated(final ScheduleCreatedEvent event) {
        log.info("일정 생성 이벤트 처리 시작 - ScheduleId: {}", event.getScheduleId());

        try {
            scheduleReadService.getScheduleById(event.getScheduleId())
                    .ifPresent(schedule -> {
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        scheduleReminderSchedulerService.scheduleScheduleReminders(schedule, currentDateTime, false);
                    });
        } catch (Exception e) {
            log.error("일정 생성 이벤트 처리 중 오류 발생 - ScheduleId: {}", event.getScheduleId(), e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleScheduleUpdated(final ScheduleUpdatedEvent event) {
        log.info("일정 수정 이벤트 처리 시작 - ScheduleId: {}", event.getScheduleId());

        try {
            if (event.isReminderRelatedChanged()) {
                scheduleReminderSchedulerService.cancelFutureScheduleReminders(
                        event.getScheduleId(), LocalDate.now());

                scheduleReadService.getScheduleById(event.getScheduleId())
                        .ifPresent(schedule -> {
                            LocalDateTime currentDateTime = LocalDateTime.now();
                            scheduleReminderSchedulerService.scheduleScheduleReminders(
                                    schedule, currentDateTime, false);
                        });
            }
        } catch (Exception e) {
            log.error("일정 수정 이벤트 처리 중 오류 발생 - ScheduleId: {}", event.getScheduleId(), e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleScheduleDeleted(final ScheduleDeletedEvent event) {
        log.info("일정 삭제 이벤트 처리 시작 - ScheduleId: {}", event.getScheduleId());

        try {
            scheduleReminderSchedulerService.cancelAllScheduleReminders(event.getScheduleId());
        } catch (Exception e) {
            log.error("일정 삭제 이벤트 처리 중 오류 발생 - ScheduleId: {}", event.getScheduleId(), e);
        }
    }
}

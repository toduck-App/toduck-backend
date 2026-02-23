package im.toduck.domain.schedule.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.schedule.infrastructure.scheduler.job.ScheduleReminderQuartzJob;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleReminderJob;
import im.toduck.domain.schedule.persistence.repository.ScheduleReminderJobRepository;
import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.quartz.auto-startup", havingValue = "true", matchIfMissing = true)
public class ScheduleReminderSchedulerService {

    private final Scheduler scheduler;
    private final ScheduleReminderJobRepository scheduleReminderJobRepository;

    private static final String JOB_GROUP = "SCHEDULE_REMINDER";
    private static final String TRIGGER_GROUP = "SCHEDULE_REMINDER_TRIGGER";
    private static final LocalTime ALL_DAY_REMINDER_TIME = LocalTime.of(10, 0);
    private static final LocalTime BATCH_EXECUTION_TIME = LocalTime.of(3, 58);

    @Transactional
    public void scheduleScheduleReminders(
            final Schedule schedule,
            final LocalDateTime currentDateTime,
            final boolean isBatchScheduling) {
        ScheduleAlram alarm = schedule.getScheduleTime().getAlarm();
        if (alarm == null) {
            log.debug("알림이 비활성화된 일정 스킵 - ScheduleId: {}", schedule.getId());
            return;
        }

        try {
            LocalDateTime nextBatchTime = calculateNextBatchExecutionTime(currentDateTime);
            LocalDate startDate = schedule.getScheduleDate().getStartDate();
            LocalDate endDate = schedule.getScheduleDate().getEndDate();

            // 현재 날짜 이전의 날짜는 스킵
            LocalDate effectiveStartDate = startDate.isBefore(currentDateTime.toLocalDate())
                    ? currentDateTime.toLocalDate()
                    : startDate;

            // 다음 배치 시간까지만 스케줄링 (배치가 아닌 경우 하루 더)
            LocalDateTime scheduleUntil = isBatchScheduling ? nextBatchTime : nextBatchTime.plusDays(1);

            scheduleRemindersInDateRange(schedule, effectiveStartDate, endDate, currentDateTime, scheduleUntil);

            log.debug("일정 알림 스케줄링 완료 - ScheduleId: {}, isBatch: {}", schedule.getId(), isBatchScheduling);
        } catch (Exception e) {
            log.error("일정 알림 스케줄링 실패 - ScheduleId: {}", schedule.getId(), e);
            throw new RuntimeException("일정 알림 스케줄링 중 오류가 발생했습니다", e);
        }
    }

    private LocalDateTime calculateNextBatchExecutionTime(final LocalDateTime currentTime) {
        LocalDate currentDate = currentTime.toLocalDate();
        LocalDateTime todayBatchTime = currentDate.atTime(BATCH_EXECUTION_TIME);

        if (currentTime.isBefore(todayBatchTime)) {
            return todayBatchTime;
        }
        return currentDate.plusDays(1).atTime(BATCH_EXECUTION_TIME);
    }

    private void scheduleRemindersInDateRange(
            final Schedule schedule,
            final LocalDate startDate,
            final LocalDate endDate,
            final LocalDateTime currentDateTime,
            final LocalDateTime scheduleUntil) {
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (shouldScheduleForDate(schedule, currentDate)) {
                LocalDateTime reminderTime = calculateReminderTime(schedule, currentDate);

                if (shouldScheduleReminder(reminderTime, currentDateTime, scheduleUntil)) {
                    scheduleReminderForDate(schedule, currentDate, reminderTime);
                }
            }
            currentDate = currentDate.plusDays(1);
        }
    }

    private boolean shouldScheduleForDate(final Schedule schedule, final LocalDate date) {
        if (schedule.getDaysOfWeekBitmask() == null) {
            return true; // 반복 없는 일정은 모든 날짜에서 스케줄링
        }
        return schedule.getDaysOfWeekBitmask().includesDayOf(date);
    }

    private LocalDateTime calculateReminderTime(final Schedule schedule, final LocalDate scheduleDate) {
        ScheduleAlram alarm = schedule.getScheduleTime().getAlarm();

        if (schedule.getScheduleTime().getIsAllDay()) {
            // 종일 일정: 하루 전 10시에 알림
            return scheduleDate.minusDays(1).atTime(ALL_DAY_REMINDER_TIME);
        }

        // 일반 일정: 일정 시간에서 알림 분만큼 이전
        LocalDateTime scheduleDateTime = scheduleDate.atTime(schedule.getScheduleTime().getTime());
        return scheduleDateTime.minusMinutes(alarm.getMinutes());
    }

    private boolean shouldScheduleReminder(
            final LocalDateTime reminderTime,
            final LocalDateTime currentTime,
            final LocalDateTime scheduleUntil) {
        return !reminderTime.isBefore(currentTime) && reminderTime.isBefore(scheduleUntil);
    }

    private void scheduleReminderForDate(
            final Schedule schedule,
            final LocalDate scheduleDate,
            final LocalDateTime reminderDateTime) {
        try {
            if (isReminderAlreadyScheduled(schedule.getId(), reminderDateTime)) {
                log.debug("이미 스케줄링된 알림 스킵 - ScheduleId: {}, DateTime: {}",
                        schedule.getId(), reminderDateTime);
                return;
            }

            String jobKey = createJobKey(schedule.getId(), scheduleDate, reminderDateTime.toLocalTime());
            JobDetail jobDetail = createJobDetail(schedule, jobKey);
            Trigger trigger = createTrigger(jobDetail, reminderDateTime, jobKey);

            scheduler.scheduleJob(jobDetail, trigger);
            saveReminderJobRecord(schedule, reminderDateTime, jobKey);

            log.debug("일정 알림 스케줄링 성공 - ScheduleId: {}, ScheduleDate: {}, ReminderDateTime: {}",
                    schedule.getId(), scheduleDate, reminderDateTime);

        } catch (SchedulerException e) {
            log.error("일정 알림 스케줄링 실패 - ScheduleId: {}, ScheduleDate: {}",
                    schedule.getId(), scheduleDate, e);
        }
    }

    private boolean isReminderAlreadyScheduled(final Long scheduleId, final LocalDateTime reminderDateTime) {
        return scheduleReminderJobRepository.existsByScheduleIdAndReminderDateAndReminderTime(
                scheduleId, reminderDateTime.toLocalDate(), reminderDateTime.toLocalTime());
    }

    private String createJobKey(final Long scheduleId, final LocalDate scheduleDate, final LocalTime reminderTime) {
        return String.format("schedule_%d_%s_%s",
                scheduleId,
                scheduleDate.toString(),
                reminderTime.toString().replace(":", ""));
    }

    private JobDetail createJobDetail(final Schedule schedule, final String jobKey) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("userId", schedule.getUser().getId());
        jobDataMap.put("scheduleId", schedule.getId());
        jobDataMap.put("scheduleTitle", schedule.getTitle());
        jobDataMap.put("reminderType", schedule.getScheduleTime().getAlarm().name());
        jobDataMap.put("isAllDay", schedule.getScheduleTime().getIsAllDay());

        return JobBuilder.newJob(ScheduleReminderQuartzJob.class)
                .withIdentity(new JobKey(jobKey, JOB_GROUP))
                .withDescription("일정 알림: " + schedule.getTitle())
                .usingJobData(jobDataMap)
                .build();
    }

    private Trigger createTrigger(
            final JobDetail jobDetail,
            final LocalDateTime reminderDateTime,
            final String jobKey) {
        Date triggerTime = Date.from(reminderDateTime.atZone(ZoneId.systemDefault()).toInstant());

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(new TriggerKey(jobKey, TRIGGER_GROUP))
                .startAt(triggerTime)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .build();
    }

    private void saveReminderJobRecord(
            final Schedule schedule,
            final LocalDateTime reminderDateTime,
            final String jobKey) {
        ScheduleReminderJob reminderJob = ScheduleReminderJob.builder()
                .scheduleId(schedule.getId())
                .userId(schedule.getUser().getId())
                .reminderDate(reminderDateTime.toLocalDate())
                .reminderTime(reminderDateTime.toLocalTime())
                .jobKey(jobKey)
                .build();

        scheduleReminderJobRepository.save(reminderJob);
    }

    @Transactional
    public void cancelAllScheduleReminders(final Long scheduleId) {
        log.info("일정의 모든 알림 취소 시작 - ScheduleId: {}", scheduleId);

        List<ScheduleReminderJob> reminderJobs = scheduleReminderJobRepository.findByScheduleId(scheduleId);
        int cancelledCount = deleteScheduledJobs(reminderJobs);

        scheduleReminderJobRepository.deleteByScheduleId(scheduleId);
        log.info("일정의 모든 알림 취소 완료 - ScheduleId: {}, 취소된 알림 수: {}",
                scheduleId, cancelledCount);
    }

    @Transactional
    public void cancelFutureScheduleReminders(final Long scheduleId, final LocalDate fromDate) {
        log.info("일정의 미래 알림 취소 시작 - ScheduleId: {}, FromDate: {}", scheduleId, fromDate);

        List<ScheduleReminderJob> reminderJobs = scheduleReminderJobRepository
                .findByScheduleIdAndReminderDateGreaterThanEqual(scheduleId, fromDate);

        int cancelledCount = deleteScheduledJobs(reminderJobs);

        scheduleReminderJobRepository.deleteByScheduleIdAndReminderDateAfter(scheduleId, fromDate);
        log.info("일정의 미래 알림 취소 완료 - ScheduleId: {}, 취소된 알림 수: {}",
                scheduleId, cancelledCount);
    }

    private int deleteScheduledJobs(final List<ScheduleReminderJob> reminderJobs) {
        int successCount = 0;

        for (ScheduleReminderJob reminderJob : reminderJobs) {
            try {
                JobKey jobKey = new JobKey(reminderJob.getJobKey(), JOB_GROUP);
                if (scheduler.checkExists(jobKey)) {
                    scheduler.deleteJob(jobKey);
                    successCount++;
                    log.debug("Quartz Job 삭제 성공 - JobKey: {}", reminderJob.getJobKey());
                }
            } catch (SchedulerException e) {
                log.error("Quartz Job 삭제 실패 - JobKey: {}", reminderJob.getJobKey(), e);
            }
        }

        return successCount;
    }
}

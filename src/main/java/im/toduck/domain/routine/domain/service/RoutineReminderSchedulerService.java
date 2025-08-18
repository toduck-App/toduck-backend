package im.toduck.domain.routine.domain.service;

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

import im.toduck.domain.routine.infrastructure.scheduler.job.RoutineReminderQuartzJob;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineReminderJob;
import im.toduck.domain.routine.persistence.repository.RoutineReminderJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.quartz.auto-startup", havingValue = "true", matchIfMissing = true)
public class RoutineReminderSchedulerService {

	private final Scheduler scheduler;
	private final RoutineReminderJobRepository routineReminderJobRepository;

	private static final String JOB_GROUP = "ROUTINE_REMINDER";
	private static final String TRIGGER_GROUP = "ROUTINE_REMINDER_TRIGGER";
	private static final LocalTime ALL_DAY_REMINDER_TIME = LocalTime.of(10, 0);
	private static final LocalTime BATCH_EXECUTION_TIME = LocalTime.of(3, 58);

	@Transactional
	public void scheduleRoutineReminders(
		final Routine routine,
		final LocalDateTime currentDateTime,
		final boolean isBatchScheduling
	) {
		if (routine.getReminderMinutes() == null || routine.getReminderMinutes() <= 0) {
			log.debug("리마인더가 비활성화된 루틴 스킵 - RoutineId: {}, reminderMinutes: {}",
				routine.getId(), routine.getReminderMinutes());
			return;
		}

		try {
			LocalDateTime nextBatchTime = calculateNextBatchExecutionTime(currentDateTime);
			LocalDate currentDate = currentDateTime.toLocalDate();
			LocalDate endDate = nextBatchTime.toLocalDate().plusDays(1);

			LocalDateTime scheduleUntil = isBatchScheduling ? nextBatchTime : nextBatchTime.plusDays(1);
			scheduleRemindersInDateRange(routine, currentDate, endDate, currentDateTime, scheduleUntil);

			log.debug("루틴 알림 스케줄링 완료 - RoutineId: {}, isBatch: {}", routine.getId(), isBatchScheduling);
		} catch (Exception e) {
			log.error("루틴 알림 스케줄링 실패 - RoutineId: {}", routine.getId(), e);
			throw new RuntimeException("루틴 알림 스케줄링 중 오류가 발생했습니다", e);
		}
	}

	private LocalDateTime calculateNextBatchExecutionTime(LocalDateTime currentTime) {
		LocalDate currentDate = currentTime.toLocalDate();
		LocalDateTime todayBatchTime = currentDate.atTime(BATCH_EXECUTION_TIME);

		if (currentTime.isBefore(todayBatchTime)) {
			return todayBatchTime;
		}
		return currentDate.plusDays(1).atTime(BATCH_EXECUTION_TIME);
	}

	private void scheduleRemindersInDateRange(
		final Routine routine,
		final LocalDate startDate,
		final LocalDate endDate,
		final LocalDateTime currentDateTime,
		final LocalDateTime scheduleUntil
	) {
		LocalDate currentDate = startDate;
		while (!currentDate.isAfter(endDate)) {
			if (routine.getDaysOfWeekBitmask().includesDayOf(currentDate)) {
				LocalDateTime reminderTime = calculateReminderTime(routine, currentDate);

				if (shouldScheduleReminder(reminderTime, currentDateTime, scheduleUntil)) {
					scheduleReminderForDate(routine, currentDate, reminderTime);
				}
			}
			currentDate = currentDate.plusDays(1);
		}
	}

	private LocalDateTime calculateReminderTime(final Routine routine, final LocalDate routineDate) {
		if (routine.isAllDay()) {
			return routineDate.minusDays(1).atTime(ALL_DAY_REMINDER_TIME);
		}

		LocalDateTime routineDateTime = routineDate.atTime(routine.getTime());
		return routineDateTime.minusMinutes(routine.getReminderMinutes());
	}

	private boolean shouldScheduleReminder(
		final LocalDateTime reminderTime,
		final LocalDateTime currentTime,
		final LocalDateTime scheduleUntil
	) {
		return !reminderTime.isBefore(currentTime) && reminderTime.isBefore(scheduleUntil);
	}

	private void scheduleReminderForDate(Routine routine, LocalDate routineDate, LocalDateTime reminderDateTime) {
		try {
			if (isReminderAlreadyScheduled(routine.getId(), reminderDateTime)) {
				log.debug("이미 스케줄링된 알림 스킵 - RoutineId: {}, DateTime: {}", routine.getId(), reminderDateTime);
				return;
			}

			String jobKey = createJobKey(routine.getId(), routineDate, reminderDateTime.toLocalTime());
			JobDetail jobDetail = createJobDetail(routine, jobKey);
			Trigger trigger = createTrigger(jobDetail, reminderDateTime, jobKey);

			scheduler.scheduleJob(jobDetail, trigger);
			saveReminderJobRecord(routine, reminderDateTime, jobKey);

			log.debug("루틴 알림 스케줄링 성공 - RoutineId: {}, RoutineDate: {}, ReminderDateTime: {}",
				routine.getId(), routineDate, reminderDateTime);

		} catch (SchedulerException e) {
			log.error("루틴 알림 스케줄링 실패 - RoutineId: {}, RoutineDate: {}",
				routine.getId(), routineDate, e);
		}
	}

	private boolean isReminderAlreadyScheduled(Long routineId, LocalDateTime reminderDateTime) {
		return routineReminderJobRepository.existsByRoutineIdAndReminderDateAndReminderTime(
			routineId, reminderDateTime.toLocalDate(), reminderDateTime.toLocalTime()
		);
	}

	private String createJobKey(Long routineId, LocalDate routineDate, LocalTime reminderTime) {
		return String.format("routine_%d_%s_%s",
			routineId,
			routineDate.toString(),
			reminderTime.toString().replace(":", "")
		);
	}

	private JobDetail createJobDetail(Routine routine, String jobKey) {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("userId", routine.getUser().getId());
		jobDataMap.put("routineId", routine.getId());
		jobDataMap.put("routineTitle", routine.getTitle());
		jobDataMap.put("reminderMinutes", routine.getReminderMinutes());
		jobDataMap.put("isAllDay", routine.isAllDay());

		return JobBuilder.newJob(RoutineReminderQuartzJob.class)
			.withIdentity(new JobKey(jobKey, JOB_GROUP))
			.withDescription("루틴 알림: " + routine.getTitle())
			.usingJobData(jobDataMap)
			.build();
	}

	private Trigger createTrigger(JobDetail jobDetail, LocalDateTime reminderDateTime, String jobKey) {
		Date triggerTime = Date.from(reminderDateTime.atZone(ZoneId.systemDefault()).toInstant());

		return TriggerBuilder.newTrigger()
			.forJob(jobDetail)
			.withIdentity(new TriggerKey(jobKey, TRIGGER_GROUP))
			.startAt(triggerTime)
			.withSchedule(SimpleScheduleBuilder.simpleSchedule()
				.withMisfireHandlingInstructionFireNow())
			.build();
	}

	private void saveReminderJobRecord(Routine routine, LocalDateTime reminderDateTime, String jobKey) {
		RoutineReminderJob reminderJob = RoutineReminderJob.builder()
			.routineId(routine.getId())
			.userId(routine.getUser().getId())
			.reminderDate(reminderDateTime.toLocalDate())
			.reminderTime(reminderDateTime.toLocalTime())
			.jobKey(jobKey)
			.build();

		routineReminderJobRepository.save(reminderJob);
	}

	@Transactional
	public void cancelAllRoutineReminders(Long routineId) {
		log.info("루틴의 모든 알림 취소 시작 - RoutineId: {}", routineId);

		List<RoutineReminderJob> reminderJobs = routineReminderJobRepository.findByRoutineId(routineId);
		int cancelledCount = deleteScheduledJobs(reminderJobs);

		routineReminderJobRepository.deleteByRoutineId(routineId);
		log.info("루틴의 모든 알림 취소 완료 - RoutineId: {}, 취소된 알림 수: {}",
			routineId, cancelledCount);
	}

	@Transactional
	public void cancelFutureRoutineReminders(Long routineId, LocalDate fromDate) {
		log.info("루틴의 미래 알림 취소 시작 - RoutineId: {}, FromDate: {}", routineId, fromDate);

		List<RoutineReminderJob> reminderJobs = routineReminderJobRepository
			.findByRoutineIdAndReminderDateGreaterThanEqual(routineId, fromDate);

		int cancelledCount = deleteScheduledJobs(reminderJobs);

		routineReminderJobRepository.deleteByRoutineIdAndReminderDateAfter(routineId, fromDate);
		log.info("루틴의 미래 알림 취소 완료 - RoutineId: {}, 취소된 알림 수: {}",
			routineId, cancelledCount);
	}

	private int deleteScheduledJobs(List<RoutineReminderJob> reminderJobs) {
		int successCount = 0;

		for (RoutineReminderJob reminderJob : reminderJobs) {
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

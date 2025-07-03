package im.toduck.domain.routine.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.routine.infrastructure.scheduler.job.RoutineReminderQuartzJob;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineReminderJob;
import im.toduck.domain.routine.persistence.repository.RoutineReminderJobRepository;
import im.toduck.global.helper.DaysOfWeekBitmask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoutineReminderSchedulerService {

	private final Scheduler scheduler;
	private final RoutineReminderJobRepository routineReminderJobRepository;

	private static final String JOB_GROUP = "ROUTINE_REMINDER";
	private static final String TRIGGER_GROUP = "ROUTINE_REMINDER_TRIGGER";
	private static final LocalTime ALL_DAY_REMINDER_TIME = LocalTime.of(10, 0); // 오전 10시
	private static final LocalTime BATCH_EXECUTION_TIME = LocalTime.of(3, 58); // 배치 실행 시간: 새벽 3시 58분

	@Transactional
	public void scheduleRoutineReminders(Routine routine, LocalDateTime currentDateTime) {
		try {
			LocalDateTime scheduleUntil = calculateNextBatchExecutionTime(currentDateTime);
			LocalDate currentDate = currentDateTime.toLocalDate();
			LocalDate endDate = scheduleUntil.toLocalDate();

			// Repository에서 조회된 루틴이 실행될 수 있는 모든 날짜에 대해 확인
			for (LocalDate routineDate = currentDate; !routineDate.isAfter(endDate); routineDate = routineDate.plusDays(
				1)) {
				if (isRoutineActiveOnDate(routine, routineDate)) {
					LocalDateTime reminderDateTime = calculateReminderDateTime(routine, routineDate);

					// 알림시간이 현재 배치 처리 범위에 포함되는 경우만 스케줄링
					if (!reminderDateTime.isBefore(currentDateTime) && !reminderDateTime.isAfter(scheduleUntil)) {
						scheduleReminderForDate(routine, routineDate, currentDateTime, scheduleUntil);
					}
				}
			}

			log.debug("루틴 알림 스케줄링 완료 - RoutineId: {}", routine.getId());

		} catch (Exception e) {
			log.error("루틴 알림 스케줄링 실패 - RoutineId: {}", routine.getId(), e);
		}
	}

	/**
	 * 특정 날짜에 루틴이 활성화되어 있는지 확인
	 */
	private boolean isRoutineActiveOnDate(Routine routine, LocalDate date) {
		DaysOfWeekBitmask routineBitmask = routine.getDaysOfWeekBitmask();
		return routineBitmask.includesDayOf(date);
	}

	/**
	 * 특정 날짜에 대한 루틴 알림을 스케줄링
	 */
	private void scheduleReminderForDate(Routine routine, LocalDate routineDate, LocalDateTime currentDateTime,
		LocalDateTime scheduleUntil) {
		try {
			LocalDateTime reminderDateTime = calculateReminderDateTime(routine, routineDate);

			// 현재 시점 이전의 알림은 스킵
			if (reminderDateTime.isBefore(currentDateTime)) {
				log.debug("이미 지난 알림 시간 스킵 - RoutineId: {}, DateTime: {}", routine.getId(), reminderDateTime);
				return;
			}

			// 배치 실행 주기 이후의 알림은 스킵 (다음 배치에서 처리)
			if (reminderDateTime.isAfter(scheduleUntil)) {
				log.debug("배치 주기 이후 알림 스킵 - RoutineId: {}, DateTime: {}, ScheduleUntil: {}",
					routine.getId(), reminderDateTime, scheduleUntil);
				return;
			}

			if (isAlreadyScheduled(routine.getId(), reminderDateTime.toLocalDate(), reminderDateTime.toLocalTime())) {
				log.debug("이미 스케줄링된 알림 스킵 - RoutineId: {}, DateTime: {}", routine.getId(), reminderDateTime);
				return;
			}

			String jobKey = generateJobKey(routine.getId(), routineDate, reminderDateTime.toLocalTime());

			// Quartz Job 생성 및 스케줄링
			JobDetail jobDetail = createJobDetail(routine, jobKey);
			Trigger trigger = createTrigger(jobDetail, reminderDateTime, jobKey);

			scheduler.scheduleJob(jobDetail, trigger);

			// DB에 기록
			saveReminderJob(routine, reminderDateTime, jobKey);

			log.debug("루틴 알림 스케줄링 성공 - RoutineId: {}, RoutineDate: {}, ReminderDateTime: {}",
				routine.getId(), routineDate, reminderDateTime);

		} catch (SchedulerException e) {
			log.error("루틴 알림 스케줄링 실패 - RoutineId: {}, RoutineDate: {}",
				routine.getId(), routineDate, e);
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

	private LocalDateTime calculateReminderDateTime(Routine routine, LocalDate routineDate) {
		if (routine.isAllDay()) {
			return routineDate.minusDays(1).atTime(ALL_DAY_REMINDER_TIME);
		}

		LocalDateTime routineDateTime = routineDate.atTime(routine.getTime());
		return routineDateTime.minusMinutes(routine.getReminderMinutes());
	}

	/**
	 * 알림이 이미 스케줄링되어 있는지 확인합니다.
	 */
	private boolean isAlreadyScheduled(Long routineId, LocalDate reminderDate, LocalTime reminderTime) {
		return routineReminderJobRepository.existsByRoutineIdAndReminderDateAndReminderTime(
			routineId, reminderDate, reminderTime
		);
	}

	/**
	 * Job의 고유 키를 생성합니다.
	 */
	private String generateJobKey(Long routineId, LocalDate routineDate, LocalTime reminderTime) {
		return String.format("routine_%d_%s_%s_%s",
			routineId,
			routineDate.toString(),
			reminderTime.toString().replace(":", ""),
			UUID.randomUUID().toString().substring(0, 8)
		);
	}

	/**
	 * Quartz JobDetail을 생성합니다.
	 */
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

	/**
	 * Quartz Trigger를 생성합니다.
	 */
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

	/**
	 * 알림 작업 정보를 DB에 저장합니다.
	 */
	private void saveReminderJob(Routine routine, LocalDateTime reminderDateTime, String jobKey) {
		RoutineReminderJob reminderJob = RoutineReminderJob.builder()
			.routineId(routine.getId())
			.userId(routine.getUser().getId())
			.reminderDate(reminderDateTime.toLocalDate())
			.reminderTime(reminderDateTime.toLocalTime())
			.jobKey(jobKey)
			.build();

		routineReminderJobRepository.save(reminderJob);
	}

	/**
	 * 루틴의 모든 예약된 알림을 취소합니다.
	 */
	@Transactional
	public void cancelAllRoutineReminders(Long routineId) {
		log.info("루틴의 모든 알림 취소 시작 - RoutineId: {}", routineId);

		List<RoutineReminderJob> reminderJobs = routineReminderJobRepository.findByRoutineId(routineId);

		for (RoutineReminderJob reminderJob : reminderJobs) {
			try {
				JobKey jobKey = new JobKey(reminderJob.getJobKey(), JOB_GROUP);
				if (scheduler.checkExists(jobKey)) {
					scheduler.deleteJob(jobKey);
					log.debug("Quartz Job 삭제 성공 - JobKey: {}", reminderJob.getJobKey());
				}
			} catch (SchedulerException e) {
				log.error("Quartz Job 삭제 실패 - JobKey: {}", reminderJob.getJobKey(), e);
			}
		}

		routineReminderJobRepository.deleteByRoutineId(routineId);
		log.info("루틴의 모든 알림 취소 완료 - RoutineId: {}, 취소된 알림 수: {}",
			routineId, reminderJobs.size());
	}

	/**
	 * 특정 날짜 이후의 루틴 알림을 취소합니다.
	 */
	@Transactional
	public void cancelFutureRoutineReminders(Long routineId, LocalDate fromDate) {
		log.info("루틴의 미래 알림 취소 시작 - RoutineId: {}, FromDate: {}", routineId, fromDate);

		List<RoutineReminderJob> reminderJobs = routineReminderJobRepository
			.findByRoutineIdAndReminderDateGreaterThanEqual(routineId, fromDate);

		for (RoutineReminderJob reminderJob : reminderJobs) {
			try {
				JobKey jobKey = new JobKey(reminderJob.getJobKey(), JOB_GROUP);
				if (scheduler.checkExists(jobKey)) {
					scheduler.deleteJob(jobKey);
					log.debug("Quartz Job 삭제 성공 - JobKey: {}", reminderJob.getJobKey());
				}
			} catch (SchedulerException e) {
				log.error("Quartz Job 삭제 실패 - JobKey: {}", reminderJob.getJobKey(), e);
			}
		}

		routineReminderJobRepository.deleteByRoutineIdAndReminderDateAfter(routineId, fromDate);
		log.info("루틴의 미래 알림 취소 완료 - RoutineId: {}, 취소된 알림 수: {}",
			routineId, reminderJobs.size());
	}
}

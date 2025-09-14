package im.toduck.domain.backoffice.domain.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

import im.toduck.domain.backoffice.common.mapper.BroadcastNotificationMapper;
import im.toduck.domain.backoffice.infrastructure.scheduler.job.BroadcastNotificationQuartzJob;
import im.toduck.domain.backoffice.persistence.entity.BroadcastNotification;
import im.toduck.domain.backoffice.persistence.repository.BroadcastNotificationRepository;
import im.toduck.domain.user.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.quartz.auto-startup", havingValue = "true", matchIfMissing = true)
public class BroadcastNotificationService {

	private final BroadcastNotificationRepository broadcastNotificationRepository;
	private final UserService userService;
	private final Scheduler scheduler;

	private static final String JOB_GROUP = "BROADCAST_NOTIFICATION";
	private static final String TRIGGER_GROUP = "BROADCAST_NOTIFICATION_TRIGGER";

	@Transactional
	public BroadcastNotification createBroadcastNotification(
		final String title,
		final String message,
		final LocalDateTime scheduledAt
	) {
		List<Long> activeUserIds = userService.getAllActiveUserIds();
		String jobKey = generateJobKey();

		BroadcastNotification notification = BroadcastNotificationMapper.toBroadcastNotification(
			title,
			message,
			scheduledAt,
			activeUserIds.size(),
			jobKey
		);

		BroadcastNotification savedNotification = broadcastNotificationRepository.save(notification);

		if (scheduledAt == null) {
			// 즉시 발송은 유스케이스에서 처리 (이벤트 방식으로 변경될 예정)
			log.info("즉시 발송 브로드캐스트 알림 생성 - NotificationId: {}", savedNotification.getId());
		} else {
			scheduleNotification(savedNotification, scheduledAt);
		}

		return savedNotification;
	}

	@Transactional(readOnly = true)
	public List<BroadcastNotification> getAllBroadcastNotifications() {
		return broadcastNotificationRepository.findAllByOrderByCreatedAtDesc();
	}

	@Transactional(readOnly = true)
	public Optional<BroadcastNotification> getBroadcastNotificationById(final Long id) {
		return broadcastNotificationRepository.findById(id);
	}

	@Transactional
	public BroadcastNotification save(final BroadcastNotification notification) {
		return broadcastNotificationRepository.save(notification);
	}

	@Transactional
	public void cancelScheduledNotification(final Long broadcastId) {
		Optional<BroadcastNotification> optionalNotification = broadcastNotificationRepository.findById(broadcastId);
		if (optionalNotification.isEmpty()) {
			throw new IllegalArgumentException("브로드캐스트 알림을 찾을 수 없습니다: " + broadcastId);
		}

		BroadcastNotification notification = optionalNotification.get();
		if (!notification.canCancel()) {
			throw new IllegalStateException("취소할 수 없는 상태입니다: " + notification.getStatus());
		}

		// Quartz Job 취소
		try {
			JobKey jobKey = new JobKey(notification.getJobKey(), JOB_GROUP);
			if (scheduler.checkExists(jobKey)) {
				scheduler.deleteJob(jobKey);
				log.info("예약된 브로드캐스트 알림 Job 취소 - JobKey: {}", notification.getJobKey());
			}
		} catch (SchedulerException e) {
			log.error("브로드캐스트 알림 Job 취소 실패 - JobKey: {}", notification.getJobKey(), e);
		}

		notification.markAsCancelled();
		broadcastNotificationRepository.save(notification);

		log.info("브로드캐스트 알림 예약 취소 완료 - BroadcastId: {}", broadcastId);
	}

	private void scheduleNotification(final BroadcastNotification notification, final LocalDateTime scheduledAt) {
		try {
			JobDetail jobDetail = createJobDetail(notification);
			Trigger trigger = createTrigger(jobDetail, scheduledAt, notification.getJobKey());

			scheduler.scheduleJob(jobDetail, trigger);

			log.info("브로드캐스트 알림 예약 완료 - BroadcastId: {}, 예약 시간: {}",
				notification.getId(), scheduledAt);

		} catch (SchedulerException e) {
			log.error("브로드캐스트 알림 예약 실패 - BroadcastId: {}", notification.getId(), e);
			notification.markAsFailed("예약 실패: " + e.getMessage());
			broadcastNotificationRepository.save(notification);
			throw new RuntimeException("브로드캐스트 알림 예약 실패", e);
		}
	}

	private JobDetail createJobDetail(final BroadcastNotification notification) {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("broadcastId", notification.getId());
		jobDataMap.put("title", notification.getTitle());
		jobDataMap.put("message", notification.getMessage());

		return JobBuilder.newJob(BroadcastNotificationQuartzJob.class)
			.withIdentity(new JobKey(notification.getJobKey(), JOB_GROUP))
			.withDescription("브로드캐스트 알림: " + notification.getTitle())
			.usingJobData(jobDataMap)
			.build();
	}

	private Trigger createTrigger(final JobDetail jobDetail, final LocalDateTime scheduledAt, final String jobKey) {
		Date triggerTime = Date.from(scheduledAt.atZone(ZoneId.systemDefault()).toInstant());

		return TriggerBuilder.newTrigger()
			.forJob(jobDetail)
			.withIdentity(new TriggerKey(jobKey, TRIGGER_GROUP))
			.startAt(triggerTime)
			.withSchedule(SimpleScheduleBuilder.simpleSchedule()
				.withMisfireHandlingInstructionFireNow())
			.build();
	}

	private String generateJobKey() {
		return "broadcast_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
	}
}

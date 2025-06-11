package im.toduck.domain.diary.domain.usecase;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.notification.domain.event.DiaryReminderNotificationEvent;
import im.toduck.domain.notification.messaging.NotificationMessagePublisher;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class DiarySchedulerUseCase {

	private final UserService userService;
	private final NotificationMessagePublisher notificationMessagePublisher;

	@Scheduled(cron = "0 0 22 * * *")
	@Transactional(readOnly = true)
	public void sendDailyDiaryReminder() {
		log.info("일기 작성 유도 알림 발송 시작");

		List<Long> userIds = userService.getAllActiveUserIds();
		for (Long userId : userIds) {
			DiaryReminderNotificationEvent event = DiaryReminderNotificationEvent.of(userId);
			notificationMessagePublisher.publishNotificationEvent(event);
		}

		log.info("일기 작성 유도 알림 이벤트 발행 완료");
	}
}


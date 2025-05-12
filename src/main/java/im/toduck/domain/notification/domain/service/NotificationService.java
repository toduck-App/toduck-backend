package im.toduck.domain.notification.domain.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.notification.common.mapper.NotificationMapper;
import im.toduck.domain.notification.domain.data.NotificationData;
import im.toduck.domain.notification.domain.event.NotificationEvent;
import im.toduck.domain.notification.persistence.entity.Notification;
import im.toduck.domain.notification.persistence.repository.NotificationRepository;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final UserService userService;

	@Transactional
	public <T extends NotificationData> Notification createNotification(NotificationEvent<T> event) {
		User user = userService.getUserById(event.getUserId())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		Notification notification = NotificationMapper.toNotification(user, event);

		return notificationRepository.save(notification);
	}

	@Transactional(readOnly = true)
	public List<Notification> getUserInAppNotifications(Long userId, int page, int size) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		return notificationRepository.findByUserAndIsInAppShownTrueOrderByCreatedAtDesc(
			user, PageRequest.of(page, size));
	}

	@Transactional
	public void markAsRead(Long notificationId, Long userId) {
		Notification notification = notificationRepository.findByIdAndUser_Id(notificationId, userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_NOTIFICATION));

		notification.markAsRead();
	}

	@Transactional
	public void markAllAsRead(Long userId) {
		notificationRepository.markAllAsRead(userId);
	}

	@Transactional
	public void markAsSent(Notification notification) {
		notification.markAsSent();
	}
}

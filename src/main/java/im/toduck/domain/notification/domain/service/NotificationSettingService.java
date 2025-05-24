package im.toduck.domain.notification.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.notification.persistence.entity.NotificationSetting;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import im.toduck.domain.notification.persistence.repository.NotificationSettingRepository;
import im.toduck.domain.notification.presentation.dto.request.NotificationSettingUpdateRequest;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSettingService {
	private final NotificationSettingRepository notificationSettingRepository;
	private final UserService userService;

	@Transactional
	public NotificationSetting getOrCreateSettings(final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		return notificationSettingRepository.findByUser(user)
			.orElseGet(() -> {
				NotificationSetting setting = NotificationSetting.builder()
					.user(user)
					.build();
				return notificationSettingRepository.save(setting);
			});
	}

	@Transactional
	public NotificationSetting updateSettings(final Long userId, final NotificationSettingUpdateRequest request) {
		NotificationSetting setting = getOrCreateSettings(userId);

		setting.updateAllEnabled(request.allEnabled());
		setting.updateNotificationMethod(request.notificationMethod());
		setting.updateNoticeEnabled(request.noticeEnabled());
		setting.updateHomeEnabled(request.homeEnabled());
		setting.updateConcentrationEnabled(request.concentrationEnabled());
		setting.updateDiaryEnabled(request.diaryEnabled());
		setting.updateSocialEnabled(request.socialEnabled());

		return setting;
	}

	@Transactional(readOnly = true)
	public boolean isTypeEnabled(final Long userId, final NotificationType type) {
		NotificationSetting setting = getOrCreateSettings(userId);
		return setting.isTypeEnabled(type);
	}
}

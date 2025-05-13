package im.toduck.domain.notification.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.notification.persistence.entity.NotificationSetting;
import im.toduck.domain.user.persistence.entity.User;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
	Optional<NotificationSetting> findByUser(User user);
}

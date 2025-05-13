package im.toduck.domain.notification.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import im.toduck.domain.notification.persistence.entity.Notification;
import im.toduck.domain.notification.persistence.repository.querydsl.NotificationRepositoryCustom;
import im.toduck.domain.user.persistence.entity.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
	List<Notification> findByUserAndIsInAppShownTrueOrderByCreatedAtDesc(User user, Pageable pageable);

	Optional<Notification> findByIdAndUser_Id(Long id, Long userId);

	@Modifying
	@Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
	void markAllAsRead(@Param("userId") Long userId);
}

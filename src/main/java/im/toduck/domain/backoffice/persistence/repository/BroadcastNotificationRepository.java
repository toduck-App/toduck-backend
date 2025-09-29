package im.toduck.domain.backoffice.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import im.toduck.domain.backoffice.persistence.entity.BroadcastNotification;
import im.toduck.domain.backoffice.persistence.entity.BroadcastNotificationStatus;

@Repository
public interface BroadcastNotificationRepository extends JpaRepository<BroadcastNotification, Long> {

	List<BroadcastNotification> findAllByOrderByCreatedAtDesc();

	List<BroadcastNotification> findByStatusOrderByCreatedAtDesc(final BroadcastNotificationStatus status);

	Optional<BroadcastNotification> findByJobKey(final String jobKey);

	@Query("SELECT bn FROM BroadcastNotification bn WHERE bn.scheduledAt <= :now AND bn.status = :status")
	List<BroadcastNotification> findScheduledNotificationsToSend(
		@Param("now") final LocalDateTime now,
		@Param("status") final BroadcastNotificationStatus status
	);

	boolean existsByJobKey(final String jobKey);
}

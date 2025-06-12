package im.toduck.domain.notification.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import im.toduck.domain.notification.persistence.entity.DeviceToken;
import im.toduck.domain.user.persistence.entity.User;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
	List<DeviceToken> findAllByUser(User user);

	Optional<DeviceToken> findByUserAndToken(User user, String token);

	boolean existsByUserAndToken(User user, String token);

	@Modifying
	@Query("DELETE FROM DeviceToken dt WHERE dt.token = :token")
	void deleteByToken(@Param("token") String token);
}

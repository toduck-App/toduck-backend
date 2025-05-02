package im.toduck.domain.mypage.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.mypage.persistence.entity.AccountDeletionLog;
import im.toduck.domain.user.persistence.entity.User;

public interface AccountDeletionLogRepository extends JpaRepository<AccountDeletionLog, Long> {
	Optional<AccountDeletionLog> findByUser(User user);
}

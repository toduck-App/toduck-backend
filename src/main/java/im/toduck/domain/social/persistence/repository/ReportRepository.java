package im.toduck.domain.social.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.social.persistence.entity.Report;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.user.persistence.entity.User;

public interface ReportRepository extends JpaRepository<Report, Long> {
	boolean existsByUserAndSocial(User user, Social social);
}

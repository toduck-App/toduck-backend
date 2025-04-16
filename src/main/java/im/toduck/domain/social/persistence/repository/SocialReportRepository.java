package im.toduck.domain.social.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialReport;
import im.toduck.domain.user.persistence.entity.User;

public interface SocialReportRepository extends JpaRepository<SocialReport, Long> {
	boolean existsByUserAndSocial(User user, Social social);
}

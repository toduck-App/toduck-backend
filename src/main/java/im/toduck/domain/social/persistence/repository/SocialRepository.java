package im.toduck.domain.social.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.social.persistence.entity.Social;

public interface SocialRepository extends JpaRepository<Social, Long> {
}

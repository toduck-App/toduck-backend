package im.toduck.domain.social.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.social.persistence.entity.SocialCategory;

public interface SocialCategoryRepository extends JpaRepository<SocialCategory, Long> {
}

package im.toduck.domain.social.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.repository.querydsl.SocialRepositoryCustom;

public interface SocialRepository extends JpaRepository<Social, Long>, SocialRepositoryCustom {
}

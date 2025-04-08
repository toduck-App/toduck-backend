package im.toduck.domain.social.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.repository.querydsl.SocialRepositoryCustom;

public interface SocialRepository extends JpaRepository<Social, Long>, SocialRepositoryCustom {
	@Query("select count(s) from Social s where s.user.id = :userId and s.deletedAt is null")
	long countByUserId(@Param("userId") Long userId);
}

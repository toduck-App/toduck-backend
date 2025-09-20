package im.toduck.domain.social.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.repository.querydsl.SocialRepositoryCustom;
import im.toduck.domain.user.persistence.entity.User;

public interface SocialRepository extends JpaRepository<Social, Long>, SocialRepositoryCustom {
	@Query("select count(s) from Social s where s.user.id = :userId and s.deletedAt is null")
	long countByUserId(@Param("userId") Long userId);

	List<Social> findAllByUser(User user);

	@Query("SELECT COUNT(s) FROM Social s WHERE s.createdAt BETWEEN :startDateTime AND :endDateTime")
	long countByCreatedAtBetween(
		@Param("startDateTime") LocalDateTime startDateTime,
		@Param("endDateTime") LocalDateTime endDateTime
	);
}

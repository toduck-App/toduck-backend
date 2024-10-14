package im.toduck.domain.social.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Social;
import io.lettuce.core.dynamic.annotation.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	@Query("SELECT c FROM Comment c "
		+ "WHERE c.social = :socialBoard "
		+ "AND c.user.id NOT IN ("
		+ "  SELECT b.blocked.id FROM Block b WHERE b.blocker.id = :userId"
		+ ")")
	List<Comment> findAllBySocialExcludingBlocked(
		@Param("socialBoard") Social socialBoard,
		@Param("userId") Long userId);

	List<Comment> findAllBySocial(Social socialBoard);
}

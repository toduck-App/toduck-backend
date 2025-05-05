package im.toduck.domain.social.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.repository.querydsl.CommentRepositoryCustom;
import im.toduck.domain.user.persistence.entity.User;
import io.lettuce.core.dynamic.annotation.Param;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
	@Query("SELECT c FROM Comment c "
		+ "WHERE c.social = :social "
		+ "ORDER BY "
		+ "CASE WHEN c.parent IS NULL THEN c.id ELSE c.parent.id END ASC, "
		+ "CASE WHEN c.parent IS NOT NULL THEN c.id ELSE 0 END ASC")
	List<Comment> findCommentsBySocial(@Param("social") Social social);

	List<Comment> findAllBySocial(Social socialBoard);

	int countBySocial(Social social);

	@Query("SELECT COUNT(c) FROM Comment c WHERE c.user = :user AND c.deletedAt IS NULL")
	int countActiveCommentsByUser(@Param("user") User user);
} 

package im.toduck.domain.social.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Social;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findAllBySocial(Social socialBoard);
}

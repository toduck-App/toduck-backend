package im.toduck.domain.social.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.CommentLike;
import im.toduck.domain.user.persistence.entity.User;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
	Optional<CommentLike> findCommentLikeByUserAndComment(User user, Comment comment);

	List<CommentLike> findAllByComment(Comment comment);
}

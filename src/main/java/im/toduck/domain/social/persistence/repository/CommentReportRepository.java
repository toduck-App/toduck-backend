package im.toduck.domain.social.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.CommentReport;
import im.toduck.domain.user.persistence.entity.User;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
	boolean existsByUserAndComment(User user, Comment comment);
}

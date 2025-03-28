package im.toduck.domain.social.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.CommentImageFile;

public interface CommentImageFileRepository extends JpaRepository<CommentImageFile, Long> {
	Optional<CommentImageFile> findByComment(Comment comment);
}

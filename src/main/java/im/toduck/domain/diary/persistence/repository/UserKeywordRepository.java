package im.toduck.domain.diary.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.diary.persistence.entity.UserKeyword;
import im.toduck.domain.user.persistence.entity.User;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
	boolean existsByUser(User user);

	boolean existsByUserAndKeyword(User user, String keyword);
}

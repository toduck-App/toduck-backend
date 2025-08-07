package im.toduck.domain.diary.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import im.toduck.domain.diary.persistence.entity.UserKeyword;
import im.toduck.domain.user.persistence.entity.User;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
	boolean existsByUser(User user);

	boolean existsByUserAndKeyword(User user, String keyword);

	Optional<UserKeyword> findByUserAndKeyword(User user, String keyword);

	@Query(
		value = """
			SELECT *
			FROM user_keywords
			WHERE user_id = :#{#user.id}
				AND keyword = :keyword
			LIMIT 1""",
		nativeQuery = true
	)
	Optional<UserKeyword> findByUserAndKeywordIncludingDeleted(User user, String keyword);

	List<UserKeyword> findByUserId(Long userId);

	List<UserKeyword> findByUserIdAndIdIn(Long userId, List<Long> keywordIds);
}

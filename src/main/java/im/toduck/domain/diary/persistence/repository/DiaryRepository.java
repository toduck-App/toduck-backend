package im.toduck.domain.diary.persistence.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import feign.Param;
import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.user.persistence.entity.User;
import jakarta.validation.constraints.NotNull;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryRepositoryCustom {
	List<Diary> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

	Diary findByUserIdAndDate(Long userId, @NotNull(message = "날짜는 비어있을 수 없습니다.") LocalDate date);

	int countByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

	List<Diary> findAllByUser(User user);

	@Query(value = """
		SELECT
			CASE
				WHEN MAX(diary_date) >= DATE_SUB(CURDATE(), INTERVAL 1 DAY)
			THEN (
				SELECT COUNT(*) AS consecutive_count
					FROM (
						SELECT
							DATE_SUB(diary_date, INTERVAL (ROW_NUMBER() OVER (ORDER BY diary_date)) DAY) AS grp,
							diary_date
						FROM diary
						WHERE user_id = :userId
					) AS sub
					GROUP BY grp
					ORDER BY grp DESC
					LIMIT 1
				)
				ELSE 0
			END AS consecutive_count
		FROM diary
		WHERE user_id = :userId
		""",
		nativeQuery = true
	)
	Integer findRecentConsecutiveDays(@Param("userId") Long userId);
}

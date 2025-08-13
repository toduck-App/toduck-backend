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
public interface DiaryRepository extends JpaRepository<Diary, Long> {
	List<Diary> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

	Diary findByUserIdAndDate(Long userId, @NotNull(message = "날짜는 비어있을 수 없습니다.") LocalDate date);

	int countByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

	List<Diary> findAllByUser(User user);

	@Query(value = """
		SELECT consecutive_count
		FROM (
			SELECT
				grp,
				COUNT(*) AS consecutive_count,
				MAX(date) AS latest_date_in_group
			FROM (
				SELECT
					DATE_SUB(date, INTERVAL (ROW_NUMBER() OVER (ORDER BY date)) DAY) AS grp,
					date
				FROM diary
				WHERE user_id = :userId
			) AS sub
			GROUP BY grp
			ORDER BY latest_date_in_group DESC
			LIMIT 1
		) AS grouped
		""",
		nativeQuery = true
	)
	Integer findRecentConsecutiveDays(@Param("userId") Long userId);

	@Query(value = """
		SELECT date
		FROM diary
		WHERE user_id = :userId
		ORDER BY date DESC
		LIMIT 1
		""",
		nativeQuery = true
	)
	LocalDate findLastDiaryDate(@Param("userId") Long userId);
}

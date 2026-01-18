package im.toduck.domain.diary.persistence.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.diary.persistence.repository.querydsl.DiaryRepositoryCustom;
import im.toduck.domain.user.persistence.entity.User;
import jakarta.validation.constraints.NotNull;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryRepositoryCustom {
	List<Diary> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

	Diary findByUserIdAndDate(Long userId, @NotNull(message = "날짜는 비어있을 수 없습니다.") LocalDate date);

	int countByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

	List<Diary> findAllByUser(User user);

	long countByCreatedAtBetween(final LocalDateTime startDateTime, final LocalDateTime endDateTime);

	@Query("SELECT COUNT(DISTINCT d.user) FROM Diary d")
	long countDistinctUsers();

	Optional<Diary> getDiaryByUserIdAndId(Long userId, Long diaryId);

	@Query("SELECT COUNT(DISTINCT d.date) FROM Diary d WHERE d.user = :user AND d.date BETWEEN :startDate AND :endDate")
	long countDistinctDateByUserAndDateBetween(@Param("user") User user, @Param("startDate") LocalDate startDate,
		@Param("endDate") LocalDate endDate);
}

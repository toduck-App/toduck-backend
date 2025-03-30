package im.toduck.domain.diary.persistence.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.diary.persistence.entity.Diary;
import jakarta.validation.constraints.NotNull;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
	List<Diary> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

	Diary findByUserIdAndDate(Long userId, @NotNull(message = "날짜는 비어있을 수 없습니다.") LocalDate date);
}

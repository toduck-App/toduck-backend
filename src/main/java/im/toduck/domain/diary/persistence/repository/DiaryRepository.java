package im.toduck.domain.diary.persistence.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.diary.persistence.entity.Diary;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
	List<Diary> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

	List<Diary> findAllByUserId(Long userId);
}

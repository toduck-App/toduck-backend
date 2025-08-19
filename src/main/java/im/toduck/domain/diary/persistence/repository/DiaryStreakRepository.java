package im.toduck.domain.diary.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.diary.persistence.entity.DiaryStreak;

@Repository
public interface DiaryStreakRepository extends JpaRepository<DiaryStreak, Long> {
	Optional<DiaryStreak> findByUser_Id(Long userId);
}

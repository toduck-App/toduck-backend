package im.toduck.domain.diary.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.diary.persistence.entity.DiaryImage;

public interface DiaryImageRepository extends JpaRepository<DiaryImage, Long> {
	List<DiaryImage> findAllByDiary(Diary diary);
}

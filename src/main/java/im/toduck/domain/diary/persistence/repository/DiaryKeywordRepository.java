package im.toduck.domain.diary.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.diary.persistence.entity.DiaryKeyword;

public interface DiaryKeywordRepository extends JpaRepository<DiaryKeyword, Long> {

	List<DiaryKeyword> findByDiaryId(Long diaryId);
}

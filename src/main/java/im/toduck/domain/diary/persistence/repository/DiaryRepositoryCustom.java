package im.toduck.domain.diary.persistence.repository;

import java.time.LocalDate;

public interface DiaryRepositoryCustom {
	LocalDate findLastDiaryDate(Long userId);
}

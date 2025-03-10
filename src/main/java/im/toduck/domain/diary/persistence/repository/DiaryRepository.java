package im.toduck.domain.diary.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.diary.persistence.entity.Diary;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
}

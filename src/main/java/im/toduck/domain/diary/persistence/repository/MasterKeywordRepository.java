package im.toduck.domain.diary.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.diary.persistence.entity.MasterKeyword;

public interface MasterKeywordRepository extends JpaRepository<MasterKeyword, Long> {
}

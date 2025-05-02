package im.toduck.domain.routine.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.repository.querydsl.RoutineRepositoryCustom;
import im.toduck.domain.user.persistence.entity.User;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long>, RoutineRepositoryCustom {
	Optional<Routine> findByIdAndUser(Long id, User user);

	List<Routine> findAllByUserAndIsPublicTrueAndDeletedAtIsNullOrderByUpdatedAtDesc(User user);

	List<Routine> findAllByUserAndIsPublicTrueAndDeletedAtIsNullOrderByTimeAsc(User user);

	Optional<Routine> findByIdAndUserAndDeletedAtIsNull(Long id, User user);

	Optional<Routine> findByIdAndIsPublicTrueAndDeletedAtIsNull(Long routineId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Routine r SET r.sharedCount = r.sharedCount + 1 WHERE r.id = :id")
	void incrementSharedCountAtomically(@Param("id") Long id);

	@Query(
		"SELECT COALESCE(SUM(r.sharedCount), 0) "
			+ "FROM Routine r "
			+ "WHERE r.user = :user "
			+ "AND r.isPublic = true "
			+ "AND r.deletedAt IS NULL"
	)
	int sumRoutineSharedCountByUser(@Param("user") User user);

	@Query("SELECT r FROM Routine r WHERE r.user = :user AND r.sharedCount = 0 AND r.deletedAt IS null")
	List<Routine> findAllUnsharedRoutinesByUser(@Param("user") User user);
}

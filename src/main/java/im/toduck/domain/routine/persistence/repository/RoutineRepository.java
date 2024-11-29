package im.toduck.domain.routine.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.repository.querydsl.RoutineRepositoryCustom;
import im.toduck.domain.user.persistence.entity.User;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long>, RoutineRepositoryCustom {
	Optional<Routine> findByIdAndUser(Long id, User user);

	List<Routine> findAllByUserAndIsPublicTrueAndDeletedAtIsNullOrderByUpdatedAtDesc(User user);

	Optional<Routine> findByIdAndUserAndDeletedAtIsNull(Long id, User user);
}

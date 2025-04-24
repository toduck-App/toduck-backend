package im.toduck.domain.concentration.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import im.toduck.domain.concentration.persistence.entity.Concentration;
import im.toduck.domain.user.persistence.entity.User;

@Repository
public interface ConcentrationRepository extends JpaRepository<Concentration, Long>, ConcentrationRepositoryCustom {
	Optional<Concentration> findByUserAndDate(User user, LocalDate date);

	List<Concentration> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Concentration c SET c.deletedAt = NOW() WHERE c.user = :user")
	void deleteAllByUser(@Param("user") User user);
}

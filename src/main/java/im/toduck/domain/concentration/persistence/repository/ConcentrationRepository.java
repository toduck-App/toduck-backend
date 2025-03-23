package im.toduck.domain.concentration.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.concentration.persistence.entity.Concentration;
import im.toduck.domain.user.persistence.entity.User;

@Repository
public interface ConcentrationRepository extends JpaRepository<Concentration, Long> {
	Optional<Concentration> findByUserAndDate(User user, LocalDate date);

	List<Concentration> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}

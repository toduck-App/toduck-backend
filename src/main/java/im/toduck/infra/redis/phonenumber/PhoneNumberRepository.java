package im.toduck.infra.redis.phonenumber;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneNumberRepository extends CrudRepository<PhoneNumber, String> {
	Optional<PhoneNumber> findByPhoneNumber(String phoneNumber);
}

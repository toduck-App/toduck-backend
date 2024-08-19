package im.toduck.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import im.toduck.domain.user.persistence.entity.User;
import im.toduck.infra.redis.phonenumber.PhoneNumber;

@Component
public class TestFixtureBuilder {

	@Autowired
	private BuilderSupporter bs;

	public User buildUser(final User user) {
		return bs.userRepository().save(user);
	}

	public PhoneNumber buildPhoneNumber(final PhoneNumber phoneNumber) {
		return bs.phoneNumberRepository().save(phoneNumber);
	}

}

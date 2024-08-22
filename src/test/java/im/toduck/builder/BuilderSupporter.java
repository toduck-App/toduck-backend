package im.toduck.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.infra.redis.phonenumber.PhoneNumberRepository;

@Component
public class BuilderSupporter {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PhoneNumberRepository phoneNumberRepository;

	public UserRepository userRepository() {
		return userRepository;
	}

	public PhoneNumberRepository phoneNumberRepository() {
		return phoneNumberRepository;
	}

}

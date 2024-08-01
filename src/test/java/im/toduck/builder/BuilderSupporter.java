package im.toduck.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import im.toduck.domain.user.persistence.repository.UserRepository;

@Component
public class BuilderSupporter {

	@Autowired
	private UserRepository userRepository;

	public UserRepository userRepository() {
		return userRepository;
	}

}

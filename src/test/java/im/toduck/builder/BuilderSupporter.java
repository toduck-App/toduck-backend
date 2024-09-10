package im.toduck.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import im.toduck.domain.routine.persistence.repository.RoutineRecordRepository;
import im.toduck.domain.routine.persistence.repository.RoutineRepository;
import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.infra.redis.phonenumber.PhoneNumberRepository;

@Component
public class BuilderSupporter {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PhoneNumberRepository phoneNumberRepository;

	@Autowired
	private RoutineRepository routineRepository;

	@Autowired
	private RoutineRecordRepository routineRecordRepository;

	public UserRepository userRepository() {
		return userRepository;
	}

	public PhoneNumberRepository phoneNumberRepository() {
		return phoneNumberRepository;
	}

	public RoutineRepository routineRepository() {
		return routineRepository;
	}

	public RoutineRecordRepository routineRecord() {
		return routineRecordRepository;
	}

}

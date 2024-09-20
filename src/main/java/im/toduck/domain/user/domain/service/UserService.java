package im.toduck.domain.user.domain.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public Optional<User> getUserByPhoneNumber(String phoneNumber) {
		return userRepository.findByPhoneNumber(phoneNumber);
	}

	@Transactional(readOnly = true)
	public void validateUserByPhoneNumber(String phoneNumber) {
		userRepository.findByPhoneNumber(phoneNumber).ifPresent(user -> {
			throw CommonException.from(ExceptionCode.EXISTS_PHONE_NUMBER);
		});
	}

	@Transactional(readOnly = true)
	public void validateByLoginId(String loginId) {
		userRepository.findByLoginId(loginId).ifPresent(user -> {
			throw CommonException.from(ExceptionCode.EXISTS_USER_ID);
		});
	}

	@Transactional
	public void registerGeneralUser(User user) { // TODOD : refactor 필요
		userRepository.save(user);
	}

	@Transactional
	public User registerOAuthUser(User user) {
		return userRepository.save(user);
	}

	@Transactional(readOnly = true)
	public Optional<User> findByProviderAndEmail(OAuthProvider provider, String email) {
		return userRepository.findByProviderAndEmail(provider, email);
	}
}

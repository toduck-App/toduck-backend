package im.toduck.domain.user.domain.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.auth.presentation.dto.request.RegisterRequest;
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
	public void registerUser(RegisterRequest request, String nickName, String encodedPassword) {
		User user = User.createGeneralUser(nickName, request.loginId(), encodedPassword, request.phoneNumber());
		userRepository.save(user);
	}
}

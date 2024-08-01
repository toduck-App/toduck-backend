package im.toduck.domain.auth.domain.service;

import static im.toduck.global.exception.ExceptionCode.*;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneralAuthService {
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public User getUserIfValid(String phoneNumber, String password) {
		Optional<User> user = userService.getUserByPhoneNumber(phoneNumber);

		if (user.isEmpty()) {
			log.warn("존재하지 않는 유저 로그인 시도 - 유저 id: {}", phoneNumber);
			throw CommonException.from(INVALID_PHONE_NUMBER_OR_PASSWORD);
		}

		if (!isValidPassword(password, user.get())) {
			log.warn("잘못된 password 로그인 시도 - 유저 id: {}", user.get().getId());
			throw CommonException.from(INVALID_PHONE_NUMBER_OR_PASSWORD);
		}

		return user.get();
	}

	private boolean isValidPassword(String password, User user) {
		return passwordEncoder.matches(password, user.getPassword());
	}

}

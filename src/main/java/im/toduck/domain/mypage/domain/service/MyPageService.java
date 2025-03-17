package im.toduck.domain.mypage.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {
	private final UserRepository userRepository;

	@Transactional
	public long updateUniqueNickname(User user, String nickname) {
		return userRepository.updateUniqueNickname(user, nickname);
	}
}

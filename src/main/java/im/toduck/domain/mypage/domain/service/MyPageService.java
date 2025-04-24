package im.toduck.domain.mypage.domain.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.mypage.common.mapper.AccountDeletionLogMapper;
import im.toduck.domain.mypage.persistence.entity.AccountDeletionLog;
import im.toduck.domain.mypage.persistence.repository.AccountDeletionLogRepository;
import im.toduck.domain.mypage.presentation.dto.request.UserDeleteRequest;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {
	private final UserRepository userRepository;
	private final AccountDeletionLogRepository accountDeletionLogRepository;

	@Transactional
	public void updateUniqueNickname(User user, String nickname) {
		try {
			userRepository.updateNickname(user, nickname);
		} catch (DataIntegrityViolationException e) {
			throw CommonException.from(ExceptionCode.EXISTS_USER_NICKNAME);
		}
	}

	@Transactional
	public void updateProfileImage(User user, String imageUrl) {
		userRepository.updateProfileImageUrl(user, imageUrl);
	}

	@Transactional
	public void recordUserDeletionLog(User user, UserDeleteRequest request) {
		AccountDeletionLog accountDeletionLog = AccountDeletionLogMapper.toAccountDeletionLog(user, request);
		accountDeletionLogRepository.save(accountDeletionLog);
	}
}

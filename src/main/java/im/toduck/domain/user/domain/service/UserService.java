package im.toduck.domain.user.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.user.common.mapper.BlockMapper;
import im.toduck.domain.user.persistence.entity.Block;
import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.repository.BlockRepository;
import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final BlockRepository blockRepository;

	@Transactional(readOnly = true)
	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public User validateUserById(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
	}

	@Transactional(readOnly = true)
	public List<Long> getAllActiveUserIds() {
		return userRepository.findAllActiveUserIds();
	}

	@Transactional(readOnly = true)
	public Optional<User> getUserByLoginId(final String loginId) {
		return userRepository.findByLoginId(loginId);
	}

	@Transactional(readOnly = true)
	public Optional<User> findUserByPhoneNumber(String phoneNumber) {
		return userRepository.findByPhoneNumber(phoneNumber);
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

	@Transactional
	public void blockUser(User blocker, User blockedUser) {
		if (isBlockedUser(blocker, blockedUser)) {
			log.warn("차단 실패 - 이미 차단된 사용자입니다. BlockerId: {}, BlockedUserId: {}", blocker.getId(), blockedUser.getId());
			throw CommonException.from(ExceptionCode.ALREADY_BLOCKED);
		}

		Block block = BlockMapper.toBlock(blocker, blockedUser);

		blockRepository.save(block);
	}

	@Transactional
	public void unblockUser(User blocker, User blockedUser) {
		Block block = blockRepository.findByBlockerAndBlocked(blocker, blockedUser)
			.orElseThrow(() -> {
				log.warn("차단 해제 실패 - 차단 내역을 찾을 수 없습니다. BlockerId: {}, BlockedUserId: {}", blocker.getId(),
					blockedUser.getId());
				return CommonException.from(ExceptionCode.NOT_FOUND_BLOCK);
			});

		blockRepository.delete(block);
	}

	public boolean isBlockedUser(User blocker, User blockedUser) {
		return blockRepository.existsByBlockerAndBlocked(blocker, blockedUser);
	}

	@Transactional
	public void deleteAllBlocksByUser(User user) {
		blockRepository.deleteAllByUser(user);
	}

	@Transactional
	public void softDelete(User user) {
		userRepository.softDelete(user);
	}
}

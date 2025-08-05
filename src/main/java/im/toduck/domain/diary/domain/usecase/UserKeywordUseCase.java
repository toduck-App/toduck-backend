package im.toduck.domain.diary.domain.usecase;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.diary.domain.service.MasterKeywordService;
import im.toduck.domain.diary.domain.service.UserKeywordService;
import im.toduck.domain.diary.persistence.entity.MasterKeyword;
import im.toduck.domain.diary.persistence.entity.UserKeyword;
import im.toduck.domain.diary.presentation.dto.request.UserKeywordRequest;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class UserKeywordUseCase {
	private final UserService userService;
	private final UserKeywordService userKeywordService;
	private final MasterKeywordService masterKeywordService;

	@Transactional
	public void setupKeyword(final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		if (userKeywordService.existsAnyKeywordByUser(user)) {
			throw CommonException.from(ExceptionCode.ALREADY_SETUP_KEYWORD);
		}

		List<MasterKeyword> masterKeywords = masterKeywordService.findAll();

		userKeywordService.setupKeywordsFromMaster(user, masterKeywords);
	}

	@Transactional
	public void createKeyword(final Long userId, final UserKeywordRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		Optional<UserKeyword> existing = userKeywordService.findByUserAndKeywordIncludingDeleted(user,
			request.keyword());

		if (existing.isPresent()) {
			UserKeyword keyword = existing.get();
			if (keyword.getDeletedAt() == null) {
				throw CommonException.from(ExceptionCode.ALREADY_EXISTS_KEYWORD);
			} else {
				userKeywordService.restoreKeyword(keyword, request);
				return;
			}
		}

		userKeywordService.createKeyword(user, request);
	}

	@Transactional
	public void deleteKeyword(final Long userId, final UserKeywordRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		if (!userKeywordService.existKeyword(user, request)) {
			throw CommonException.from(ExceptionCode.NOT_FOUND_KEYWORD);
		}

		userKeywordService.deleteKeyword(user, request);
	}
}

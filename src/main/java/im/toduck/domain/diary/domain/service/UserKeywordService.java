package im.toduck.domain.diary.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.diary.common.mapper.UserKeywordMapper;
import im.toduck.domain.diary.persistence.entity.MasterKeyword;
import im.toduck.domain.diary.persistence.entity.UserKeyword;
import im.toduck.domain.diary.persistence.repository.UserKeywordRepository;
import im.toduck.domain.diary.presentation.dto.request.UserKeywordRequest;
import im.toduck.domain.diary.presentation.dto.response.UserKeywordResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserKeywordService {
	private final UserKeywordRepository userKeywordRepository;

	@Transactional(readOnly = true)
	public boolean existsAnyKeywordByUser(final User user) {
		return userKeywordRepository.existsByUser(user);
	}

	@Transactional
	public void setupKeywordsFromMaster(final User user, final List<MasterKeyword> masterKeywords) {
		List<UserKeyword> userKeywords = masterKeywords.stream()
			.map(mk -> UserKeywordMapper.fromMasterKeyword(user, mk))
			.toList();

		userKeywordRepository.saveAll(userKeywords);
	}

	@Transactional
	public boolean existKeyword(final User user, final UserKeywordRequest request) {
		return userKeywordRepository.existsByUserAndKeyword(user, request.keyword());
	}

	@Transactional
	public void createKeyword(final User user, final UserKeywordRequest request) {
		UserKeyword newKeyword = UserKeyword.builder()
			.user(user)
			.category(request.keywordCategory())
			.keyword(request.keyword())
			.count(0L)
			.build();

		userKeywordRepository.save(newKeyword);
	}

	@Transactional
	public void deleteKeyword(final User user, final UserKeywordRequest request) {
		UserKeyword keyword = userKeywordRepository
			.findByUserAndKeyword(user, request.keyword())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_KEYWORD));

		userKeywordRepository.delete(keyword);
	}

	@Transactional(readOnly = true)
	public Optional<UserKeyword> findByUserAndKeywordIncludingDeleted(User user, String keyword) {
		return userKeywordRepository.findByUserAndKeywordIncludingDeleted(user, keyword);
	}

	@Transactional
	public void restoreKeyword(UserKeyword keyword, UserKeywordRequest request) {
		keyword.restore(request.keywordCategory());
	}

	@Transactional(readOnly = true)
	public List<UserKeywordResponse> getUserKeywordsById(final Long userId) {
		List<UserKeyword> keywords = userKeywordRepository.findByUserId(userId);

		return keywords.stream()
			.map(uk -> new UserKeywordResponse(
				uk.getCategory(),
				uk.getKeyword(),
				uk.getCount()
			))
			.toList();
	}
}

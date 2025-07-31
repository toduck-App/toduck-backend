package im.toduck.domain.diary.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.diary.common.mapper.UserKeywordMapper;
import im.toduck.domain.diary.persistence.entity.MasterKeyword;
import im.toduck.domain.diary.persistence.entity.UserKeyword;
import im.toduck.domain.diary.persistence.repository.UserKeywordRepository;
import im.toduck.domain.diary.presentation.dto.request.UserKeywordCreateRequest;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserKeywordService {
	private final UserKeywordRepository userKeywordRepository;

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
	public boolean existKeyword(final User user, final UserKeywordCreateRequest request) {
		return userKeywordRepository.existsByUserAndKeyword(user, request.keyword());
	}

	@Transactional
	public void createKeyword(final User user, final UserKeywordCreateRequest request) {
		UserKeyword newKeyword = UserKeyword.builder()
			.user(user)
			.category(request.keywordCategory())
			.keyword(request.keyword())
			.count(0L)
			.build();

		userKeywordRepository.save(newKeyword);
	}
}

package im.toduck.domain.diary.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.diary.persistence.entity.DiaryKeyword;
import im.toduck.domain.diary.persistence.entity.UserKeyword;
import im.toduck.domain.diary.persistence.repository.DiaryKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryKeywordService {

	private final DiaryKeywordRepository diaryKeywordRepository;

	@Transactional
	public void createDiaryKeywords(Diary diary, List<UserKeyword> userKeywords) {
		List<DiaryKeyword> diaryKeywords = userKeywords.stream()
			.map(uk -> DiaryKeyword.builder()
				.diary(diary)
				.userKeyword(uk)
				.build()
			).toList();

		diaryKeywordRepository.saveAll(diaryKeywords);
	}
}

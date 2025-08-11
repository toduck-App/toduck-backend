package im.toduck.domain.diary.domain.usecase;

import java.util.List;

import im.toduck.domain.diary.domain.service.DiaryKeywordService;
import im.toduck.domain.diary.domain.service.DiaryService;
import im.toduck.domain.diary.domain.service.UserKeywordService;
import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.diary.persistence.entity.UserKeyword;
import im.toduck.domain.diary.presentation.dto.request.DiaryKeywordCreateRequest;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.global.annotation.UseCase;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class DiaryKeywordUseCase {
	private final UserService userService;
	private final DiaryService diaryService;
	private final DiaryKeywordService diaryKeywordService;
	private final UserKeywordService userKeywordService;

	@Transactional
	public void createDiaryKeyword(final Long userId, @Valid final DiaryKeywordCreateRequest request) {
		Diary diary = diaryService.getDiaryByIdAndUserId(userId, request.diaryId());

		List<UserKeyword> userKeywords = userKeywordService.getUserKeywordsByIds(userId, request.keywordIds());

		diaryKeywordService.createDiaryKeywords(diary, userKeywords);
	}
}

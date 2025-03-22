package im.toduck.domain.diary.domain.usecase;

import org.springframework.validation.annotation.Validated;

import im.toduck.domain.diary.common.mapper.DiaryMapper;
import im.toduck.domain.diary.domain.service.DiaryService;
import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.diary.presentation.dto.response.DiaryCreateResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class DiaryUseCase {
	private final UserService userService;
	private final DiaryService diaryService;

	@Transactional
	public DiaryCreateResponse createDiary(final Long userId, @Validated final DiaryCreateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		Diary diary = diaryService.createDiary(user, request);
		diaryService.addDiaryImageFiles(request.diaryImageUrls(), diary);

		log.info("일기 생성 - UserId: {}, DiaryId: {}", userId, diary.getId());
		return DiaryMapper.toDiaryCreateResponse(diary);
	}
}

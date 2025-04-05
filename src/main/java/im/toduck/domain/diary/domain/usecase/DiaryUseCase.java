package im.toduck.domain.diary.domain.usecase;

import java.time.YearMonth;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.diary.common.mapper.DiaryMapper;
import im.toduck.domain.diary.domain.service.DiaryService;
import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.diary.presentation.dto.request.DiaryUpdateRequest;
import im.toduck.domain.diary.presentation.dto.response.DiaryListResponse;
import im.toduck.domain.diary.presentation.dto.response.DiaryResponse;
import im.toduck.domain.diary.presentation.dto.response.MonthDiaryResponse;
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
public class DiaryUseCase {
	private final UserService userService;
	private final DiaryService diaryService;

	@Transactional
	public void createDiary(final Long userId, final DiaryCreateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		Diary checkDiary = diaryService.getDiaryByDate(userId, request.date());
		if (checkDiary != null) {
			throw CommonException.from(ExceptionCode.EXISTS_DATE_DIARY);
		}
		Diary diary = diaryService.createDiary(user, request);
		diaryService.addDiaryImageFiles(request.diaryImageUrls(), diary);

		log.info("일기 생성 - UserId: {}, DiaryId: {}", userId, diary.getId());
	}

	@Transactional
	public void deleteDiary(Long userId, Long diaryId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Diary diary = diaryService.getDiaryById(diaryId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_DIARY));

		if (!isDiaryOwner(diary, user)) {
			log.warn("권한이 없는 유저가 소셜 게시판 삭제 시도 - UserId: {}, DiaryId: {}, ", user.getId(), diary.getId());
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_DIARY);
		}

		diaryService.deleteDiary(diary);
		log.info("일기 삭제 - UserId: {}, DiaryId: {}", userId, diaryId);
	}

	@Transactional
	public void updateDiary(
		final Long userId,
		final Long diaryId,
		final DiaryUpdateRequest request
	) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Diary diary = diaryService.getDiaryById(diaryId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_DIARY));

		if (!isDiaryOwner(diary, user)) {
			log.warn("권한이 없는 유저가 일기 수정 시도 - UserId: {}, DiaryId: {}", user.getId(), diary.getId());
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_DIARY);
		}

		diaryService.updateDiary(user, diary, request);
		log.info("일기 수정 - UserId: {}, DiaryId: {}", userId, diaryId);
	}

	private boolean isDiaryOwner(final Diary diary, final User user) {
		return diary.isOwner(user);
	}

	@Transactional(readOnly = true)
	public DiaryListResponse getDiariesByMonth(final Long userId, final int year, final int month) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		List<DiaryResponse> diaries = diaryService.getDiariesByMonth(userId, year, month);
		return DiaryMapper.toListDiaryResponse(diaries);
	}

	@Transactional(readOnly = true)
	public MonthDiaryResponse getDiaryCountByMonth(Long userId, int year, int month) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		YearMonth currentMonth = YearMonth.of(year, month);
		YearMonth previousMonth = currentMonth.minusMonths(1);

		int thisMonthCount = diaryService.getDiaryCountByMonth(userId, currentMonth.getYear(),
			currentMonth.getMonthValue());
		int lastMonthCount = diaryService.getDiaryCountByMonth(userId, previousMonth.getYear(),
			previousMonth.getMonthValue());
		return DiaryMapper.toMonthDiaryResponse(thisMonthCount, lastMonthCount);
	}
}

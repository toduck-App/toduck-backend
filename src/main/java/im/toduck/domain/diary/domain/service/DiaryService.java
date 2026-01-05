package im.toduck.domain.diary.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.diary.common.mapper.DiaryImageFileMapper;
import im.toduck.domain.diary.common.mapper.DiaryMapper;
import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.diary.persistence.entity.DiaryImage;
import im.toduck.domain.diary.persistence.repository.DiaryImageRepository;
import im.toduck.domain.diary.persistence.repository.DiaryRepository;
import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.diary.presentation.dto.request.DiaryUpdateRequest;
import im.toduck.domain.diary.presentation.dto.response.DiaryResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.persistence.projection.DailyCount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {
	private final DiaryRepository diaryRepository;
	private final DiaryImageRepository diaryImageRepository;

	@Transactional
	public Diary createDiary(
		final User user,
		final DiaryCreateRequest request
	) {
		Diary diary = DiaryMapper.toDiary(user, request);
		return diaryRepository.save(diary);
	}

	@Transactional
	public void addDiaryImageFiles(final List<String> imageUrls, final Diary diary) {
		List<String> safeImageUrls = Optional.ofNullable(imageUrls).orElse(Collections.emptyList());

		List<DiaryImage> diaryImageFiles = safeImageUrls.stream()
			.map(url -> DiaryImageFileMapper.toDiaryImageFile(diary, url))
			.toList();
		diaryImageRepository.saveAll(diaryImageFiles);
	}

	@Transactional(readOnly = true)
	public Optional<Diary> getDiaryById(final Long diaryId) {
		return diaryRepository.findById(diaryId);
	}

	@Transactional
	public void deleteDiary(final Diary diary) {
		List<DiaryImage> imageFiles = diaryImageRepository.findAllByDiary(diary);
		imageFiles.forEach(DiaryImage::softDelete);

		diaryRepository.delete(diary);
	}

	@Transactional
	public void deleteAllDiariesByUser(final User user) {
		List<Diary> diaries = diaryRepository.findAllByUser(user);

		diaries.forEach(this::deleteDiary);
	}

	@Transactional
	public void updateDiary(
		User user,
		Diary diary,
		DiaryUpdateRequest request
	) {
		if (request.isChangeEmotion()) {
			diary.updateEmotion(request.emotion());
		}

		if (request.title() != null) {
			diary.updateTitle(request.title());
		}

		if (request.memo() != null) {
			diary.updateMemo(request.memo());
		}

		if (request.diaryImageUrls() != null) {
			diaryImageRepository.deleteAllByDiary(diary);
			addDiaryImageFiles(request.diaryImageUrls(), diary);
		}
	}

	@Transactional(readOnly = true)
	public List<DiaryResponse> getDiariesByMonth(final Long userId, YearMonth yearMonth) {
		LocalDate startDate = yearMonth.atDay(1);
		LocalDate endDate = yearMonth.atEndOfMonth();

		List<Diary> diaries = diaryRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

		return diaries.stream()
			.map(DiaryMapper::fromDiary)
			.toList();
	}

	@Transactional(readOnly = true)
	public Diary getDiaryByDate(Long userId, LocalDate date) {
		return diaryRepository.findByUserIdAndDate(userId, date);
	}

	@Transactional(readOnly = true)
	public int getDiaryCountByMonth(final Long userId, final int year, final int month) {
		LocalDate startDate = LocalDate.of(year, month, 1);
		LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth()).plusDays(1);

		return diaryRepository.countByUserIdAndDateBetween(userId, startDate, endDate);
	}

	@Transactional(readOnly = true)
	public long getTotalDiaryCount() {
		return diaryRepository.count();
	}

	@Transactional(readOnly = true)
	public long getDiaryCountByDateRange(final LocalDate startDate, final LocalDate endDate) {
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
		return diaryRepository.countByCreatedAtBetween(startDateTime, endDateTime);
	}

	@Transactional(readOnly = true)
	public long getActiveDiaryWritersCount() {
		return diaryRepository.countDistinctUsers();
	}

	@Transactional(readOnly = true)
	public Diary getDiaryByIdAndUserId(Long userId, Long diaryId) {
		return diaryRepository.getDiaryByUserIdAndId(userId, diaryId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_DIARY));
	}

	@Transactional(readOnly = true)
	public Map<LocalDate, Long> getDiaryCountByDateRangeGroupByDate(
		final LocalDate startDate,
		final LocalDate endDate
	) {
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

		List<DailyCount> dailyCounts = diaryRepository.countByCreatedAtBetweenGroupByDate(
			startDateTime, endDateTime
		);

		return dailyCounts.stream()
			.collect(Collectors.toMap(DailyCount::date, DailyCount::count));
	}
}

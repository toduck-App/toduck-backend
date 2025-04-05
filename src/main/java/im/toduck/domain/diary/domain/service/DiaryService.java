package im.toduck.domain.diary.domain.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
	public List<DiaryResponse> getDiariesByMonth(final Long userId, String yearMonth) {
		LocalDate startDate = LocalDate.parse(yearMonth + "-01");
		LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth()).plusDays(1);

		List<Diary> diaries = diaryRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

		return diaries.stream()
			.map(DiaryMapper::fromDiary)
			.toList();
	}

	public Diary getDiaryByDate(Long userId, LocalDate date) {
		return diaryRepository.findByUserIdAndDate(userId, date);
	}

	@Transactional(readOnly = true)
	public int getDiaryCountByMonth(final Long userId, final int year, final int month) {
		LocalDate startDate = LocalDate.of(year, month, 1);
		LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth()).plusDays(1);

		return diaryRepository.countByUserIdAndDateBetween(userId, startDate, endDate);
	}
}

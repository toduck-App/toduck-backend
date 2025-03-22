package im.toduck.domain.diary.domain.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import im.toduck.domain.diary.common.mapper.DiaryImageFileMapper;
import im.toduck.domain.diary.common.mapper.DiaryMapper;
import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.diary.persistence.entity.DiaryImage;
import im.toduck.domain.diary.persistence.repository.DiaryImageRepository;
import im.toduck.domain.diary.persistence.repository.DiaryRepository;
import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.diary.presentation.dto.request.DiaryUpdateRequest;
import im.toduck.domain.diary.presentation.dto.response.DiaryResponse;
import im.toduck.domain.user.persistence.entity.Emotion;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import jakarta.transaction.Transactional;
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

	@Transactional
	public Optional<Diary> getDiaryById(final Long diaryId) {
		return diaryRepository.findById(diaryId);
	}

	@Transactional
	public void deleteDiary(final Diary diary) {
		List<DiaryImage> imageFiles = diaryImageRepository.findAllByDiary(diary);
		imageFiles.forEach(DiaryImage::softDelete);
	}

	@Transactional
	public void updateDiary(
		User user,
		Diary diary,
		DiaryUpdateRequest request
	) {
		if (request.isChangeEmotion()) {
			if (request.emotion() == null) {
				log.warn("일기 업데이트시 감정을 null 값으로 일기 수정 시도 - UserId: {}, DiaryId: {}", user.getId(), diary.getId());
				throw CommonException.from(ExceptionCode.EMPTY_DIARY_EMOTION);
			}

			if (!EnumUtils.isValidEnum(Emotion.class, request.emotion().name())) {
				log.warn("일기 업데이트 시 잘못된 감정 값 입력 - UserId: {}, DiaryId: {}, Emotion: {}",
					user.getId(), diary.getId(), request.emotion());
				throw CommonException.from(ExceptionCode.INVALID_DIARY_EMOTION);
			}
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

	@Transactional
	public List<DiaryResponse> getDiariesByMonth(
		final Long userId,
		final int year,
		final int month
	) {
		LocalDate startDate = LocalDate.of(year, month, 1);
		LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

		List<Diary> diaries = diaryRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

		return diaries.stream()
			.map(DiaryResponse::fromEntity)
			.collect(Collectors.toList());
	}

	@Transactional
	public List<DiaryResponse> getAllDiaries(Long userId) {
		List<Diary> diaries = diaryRepository.findAllByUserId(userId);
		return diaries.stream()
			.map(DiaryResponse::fromEntity)
			.collect(Collectors.toList());
	}
}

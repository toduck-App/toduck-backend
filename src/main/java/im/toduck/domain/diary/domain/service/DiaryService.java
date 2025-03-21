package im.toduck.domain.diary.domain.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import im.toduck.domain.diary.common.mapper.DiaryImageFileMapper;
import im.toduck.domain.diary.common.mapper.DiaryMapper;
import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.diary.persistence.entity.DiaryImage;
import im.toduck.domain.diary.persistence.repository.DiaryImageRepository;
import im.toduck.domain.diary.persistence.repository.DiaryRepository;
import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.user.persistence.entity.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
		@Valid final DiaryCreateRequest request
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
}

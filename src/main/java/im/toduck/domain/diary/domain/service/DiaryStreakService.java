package im.toduck.domain.diary.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.diary.common.mapper.DiaryStreakMapper;
import im.toduck.domain.diary.persistence.entity.DiaryStreak;
import im.toduck.domain.diary.persistence.repository.DiaryStreakRepository;
import im.toduck.domain.diary.presentation.dto.response.DiaryStreakResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryStreakService {
	private DiaryStreakRepository diaryStreakRepository;

	private DiaryStreakMapper diaryStreakMapper;

	@Transactional(readOnly = true)
	public DiaryStreakResponse getDiaryStreakAndLastDiaryDate(final Long userId) {
		DiaryStreak diaryStreak = diaryStreakRepository.findByUserId(userId);
		return DiaryStreakMapper.toDiaryStreakResponse(diaryStreak);
	}
}

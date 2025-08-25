package im.toduck.domain.diary.domain.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.diary.common.mapper.DiaryStreakMapper;
import im.toduck.domain.diary.persistence.entity.DiaryStreak;
import im.toduck.domain.diary.persistence.repository.DiaryStreakRepository;
import im.toduck.domain.diary.presentation.dto.response.DiaryStreakResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryStreakService {
	private final DiaryStreakRepository diaryStreakRepository;

	@Transactional
	public DiaryStreakResponse getDiaryStreakAndLastDiaryDate(final Long userId) {
		Optional<DiaryStreak> diaryStreak = diaryStreakRepository.findByUser_Id(userId);
		return diaryStreak
			.map(DiaryStreakMapper::toDiaryStreakResponse)
			.orElseGet(DiaryStreakMapper::toDiaryStreakResponseEmpty);
	}

	@Transactional
	public void createDiaryStreak(final User user, final Long streak, final LocalDate today) {
		DiaryStreak diaryStreak = DiaryStreakMapper.toDiaryStreak(user, streak, today);
		diaryStreakRepository.save(diaryStreak);
	}

	@Transactional(readOnly = true)
	public Optional<DiaryStreak> getDiaryStreak(final Long userId) {
		return diaryStreakRepository.findByUser_Id(userId);
	}

	@Transactional
	public void updateDiaryStreak(final DiaryStreak diaryStreak, final Long streak, final LocalDate today) {
		if (diaryStreak == null) {
			throw CommonException.from(ExceptionCode.NOT_FOUND_DIARY_STREAK);
		}

		if (streak != null) {
			diaryStreak.updateStreak(streak);
		}

		if (today != null) {
			diaryStreak.updateLastDiaryDate(today);
		}
	}
}

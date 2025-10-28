package im.toduck.domain.diary.domain.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
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

	private final RedisTemplate<String, String> redisTemplate;

	private static final String CACHE_PREFIX = "diaryStreak::";

	private String serialize(final DiaryStreakResponse dto) {
		String dateStr = dto.lastDiaryDate() != null ? dto.lastDiaryDate().toString() : "";
		return dto.streak() + ":" + dateStr;
	}

	private DiaryStreakResponse deserialize(final String str) {
		String[] parts = str.split(":");
		Long streak = Long.parseLong(parts[0]);
		LocalDate lastDiaryDate = parts.length > 1 && !parts[1].isEmpty() ? LocalDate.parse(parts[1]) : null;
		return new DiaryStreakResponse(streak, lastDiaryDate);
	}

	@Transactional(readOnly = true)
	public DiaryStreakResponse getDiaryStreakAndLastDiaryDate(final Long userId) {
		String cached = redisTemplate.opsForValue().get(CACHE_PREFIX + userId);

		if (cached != null) {
			try {
				return deserialize(cached);
			} catch (Exception e) {
				log.warn("캐시 역직렬화 실패, DB에서 조회합니다. userId={}", userId, e);
				redisTemplate.delete(CACHE_PREFIX + userId);
			}
		}

		DiaryStreakResponse dto = diaryStreakRepository.findByUser_Id(userId)
			.map(DiaryStreakMapper::toDiaryStreakResponse)
			.orElse(DiaryStreakMapper.toDiaryStreakResponseEmpty());

		redisTemplate.opsForValue().set(CACHE_PREFIX + userId, serialize(dto));

		return dto;
	}

	@Transactional
	public DiaryStreakResponse createDiaryStreak(final User user, final Long streak, final LocalDate today) {
		DiaryStreak diaryStreak = DiaryStreakMapper.toDiaryStreak(user, streak, today);
		diaryStreakRepository.save(diaryStreak);

		DiaryStreakResponse dto = DiaryStreakMapper.toDiaryStreakResponse(diaryStreak);
		redisTemplate.opsForValue().set(CACHE_PREFIX + user.getId(), serialize(dto));

		return dto;
	}

	@Transactional
	public DiaryStreakResponse updateDiaryStreak(final DiaryStreak diaryStreak, final Long streak,
		final LocalDate today) {
		if (diaryStreak == null) {
			throw CommonException.from(ExceptionCode.NOT_FOUND_DIARY_STREAK);
		}

		if (streak != null) {
			diaryStreak.updateStreak(streak);
		}
		if (today != null) {
			diaryStreak.updateLastDiaryDate(today);
		}

		DiaryStreakResponse dto = DiaryStreakMapper.toDiaryStreakResponse(diaryStreak);
		redisTemplate.opsForValue().set(CACHE_PREFIX + diaryStreak.getUser().getId(), serialize(dto));

		return dto;
	}

	@Transactional
	public void resetStreak(final Long userId) {
		diaryStreakRepository.findByUser_Id(userId)
			.ifPresent(diaryStreak -> diaryStreak.updateStreak(0L));

		redisTemplate.delete(CACHE_PREFIX + userId);
	}

	@Transactional(readOnly = true)
	public Optional<DiaryStreak> getDiaryStreak(final Long userId) {
		return diaryStreakRepository.findByUser_Id(userId);
	}
}

package im.toduck.domain.badge.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BadgeCode {
	// 활동 기반
	BABY_DUCK("아기오리"),       // 회원가입
	PERFECTIONIST("완벽주의"),   // 루틴 10개
	CROW("까마귀"),            // 기억력 카테고리 5개
	QUACK_QUACK("꽥꽥"),       // 소셜 글 15개
	FOCUS_GENIUS("집중 천재"),  // 타이머 15회
	PAT_PAT("토닥토닥"),       // 감정일기 월 50%

	// 출석 기반
	THREE_DAYS_STREAK("작심삼일"),
	REGULAR_CUSTOMER("단골오리"),
	FIVE_STARS("별이다섯개");

	private final String defaultName;
}

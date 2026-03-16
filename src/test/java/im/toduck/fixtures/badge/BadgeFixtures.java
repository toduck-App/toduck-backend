package im.toduck.fixtures.badge;

import im.toduck.domain.badge.persistence.entity.Badge;
import im.toduck.domain.badge.persistence.entity.BadgeCode;

public class BadgeFixtures {
	public static Badge BABY_DUCK_BADGE() {
		return Badge.builder()
			.code(BadgeCode.BABY_DUCK)
			.name("아기 오리")
			.description("토덕에 가입하신 것을 환영합니다!")
			.imageUrl("https://example.com/images/badges/baby-duck.png")
			.build();
	}
}

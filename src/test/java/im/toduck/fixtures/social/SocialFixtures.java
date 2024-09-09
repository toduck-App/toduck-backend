package im.toduck.fixtures.social;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.user.persistence.entity.User;

public class SocialFixtures {

	/**
	 * 기본 content 값
	 */
	public static final String DEFAULT_CONTENT = "Test post";

	/**
	 * 여러 개의 Social 엔티티를 생성하며, IS_ANONYMOUS 값을 랜덤하게 설정
	 *
	 * @param user   게시글 작성자
	 * @param count  생성할 게시글 수
	 * @return 생성된 Social 엔티티 리스트
	 */
	public static List<Social> createMultipleSocials(User user, int count) {
		List<Social> socials = new ArrayList<>();
		Random random = new Random();

		for (int i = 1; i <= count; i++) {
			boolean isAnonymous = random.nextBoolean();
			socials.add(Social.builder()
				.user(user)
				.content(DEFAULT_CONTENT + " " + i)
				.isAnonymous(isAnonymous)
				.build());
		}

		return socials;
	}

	public static Social createSingleSocial(User user, String content, boolean isAnonymous, int likeCount) {
		return Social.builder()
			.user(user)
			.content(content)
			.isAnonymous(isAnonymous)
			.build();
	}

}

package im.toduck.fixtures.social;

import java.util.ArrayList;
import java.util.List;

import im.toduck.domain.social.persistence.entity.SocialCategory;

public class SocialCategoryFixtures {

	/**
	 * 기본 카테고리명 값
	 */
	public static final String DEFAULT_CATEGORY_NAME = "Category";

	/**
	 * 여러 개의 SocialCategory 엔티티를 생성
	 *
	 * @param count 생성할 카테고리 수
	 * @return 생성된 SocialCategory 엔티티 리스트
	 */
	public static List<SocialCategory> CREATE_MULTIPLE_CATEGORIES(int count) {
		List<SocialCategory> categories = new ArrayList<>();

		for (int i = 1; i <= count; i++) {
			categories.add(SocialCategory.builder()
				.name(DEFAULT_CATEGORY_NAME + " " + i)
				.build());
		}

		return categories;
	}
}

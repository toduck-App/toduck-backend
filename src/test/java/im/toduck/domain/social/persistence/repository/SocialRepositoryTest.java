package im.toduck.domain.social.persistence.repository;

import static im.toduck.fixtures.social.SocialFixtures.*;
import static im.toduck.fixtures.user.BlockFixtures.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import im.toduck.RepositoryTest;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.fixtures.user.UserFixtures;

public class SocialRepositoryTest extends RepositoryTest {

	@Autowired
	private SocialRepository socialRepository;

	private User USER;
	private User BLOCK_USER;

	private List<Social> SOCIAL_LIST;

	@BeforeEach
	public void setUp() {
		USER = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
		BLOCK_USER = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());

		testFixtureBuilder.buildSocials(MULTIPLE_SOCIALS(BLOCK_USER, 3));
		SOCIAL_LIST = testFixtureBuilder.buildSocials(MULTIPLE_SOCIALS(USER, 5));

		testFixtureBuilder.buildBlock(BLOCK_USER(USER, BLOCK_USER));
	}

	@Test
	void 페이지_사이즈가_유효하지_않을_경우_예외를_발생시킨다() {
		// when & then
		assertThatThrownBy(() -> PageRequest.of(0, -1))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 차단된_사용자의_게시물을_제외하고_요청받은_커서_이후의_게시물을_최신순으로_조회할_수_있다() {
		// given
		Pageable pageable = PageRequest.of(0, 6);
		Long cursor = SOCIAL_LIST.get(2).getId();

		// when
		List<Social> result = socialRepository.findByIdBeforeOrderByIdDescExcludingBlocked(cursor, USER.getId(),
			pageable);

		// then
		assertSoftly(softly -> {
			softly.assertThat(result).isNotEmpty();
			softly.assertThat(result).hasSize(2);

			softly.assertThat(result.get(0).getId()).isLessThan(cursor);
			softly.assertThat(result.get(0).getId()).isGreaterThan(result.get(1).getId());

			result.forEach(social -> {
				assertThat(social.getUser().getId()).isNotEqualTo(BLOCK_USER.getId());
			});
		});
	}

	@Test
	void 유효하지_않은_커서로_요청할_경우_빈_리스트를_반환한다() {
		// given
		Pageable pageable = PageRequest.of(0, 2);
		Long invalidCursor = -1L;

		// when
		List<Social> result = socialRepository.findByIdBeforeOrderByIdDescExcludingBlocked(invalidCursor, USER.getId(),
			pageable);

		// then
		assertSoftly(softly -> {
			softly.assertThat(result).isEmpty();

			softly.assertThat(result.size()).isEqualTo(0);
		});
	}

	@Test
	void 차단된_사용자의_게시물을_제외하고_최신_게시물을_조회할_수_있다() {
		// given
		Pageable pageable = PageRequest.of(0, 6);

		// when
		List<Social> result = socialRepository.findLatestSocialsExcludingBlocked(USER.getId(), pageable);

		// then
		assertSoftly(softly -> {
			softly.assertThat(result).isNotEmpty();
			softly.assertThat(result).hasSize(5); // Block User의 게시글 개수를 뺀 5개만 조회

			softly.assertThat(result.get(0).getId()).isGreaterThan(result.get(1).getId());
			softly.assertThat(result.get(1).getId()).isGreaterThan(result.get(2).getId());

			result.forEach(social -> {
				assertThat(social.getUser().getId()).isNotEqualTo(BLOCK_USER.getId());
			});
		});
	}
}

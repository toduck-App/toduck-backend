package im.toduck.domain.social.persistence.repository;

import static im.toduck.fixtures.social.SocialFixtures.*;
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

	private List<Social> socialList;

	@BeforeEach
	public void setUp() {
		User user = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());

		socialList = testFixtureBuilder.buildSocials(MULTIPLE_SOCIALS(user, 5));
	}

	@Test
	void 페이지_사이즈가_유효하지_않을_경우_예외를_발생시킨다() {
		// when & then
		assertThatThrownBy(() -> PageRequest.of(0, -1))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 요청받은_커서_이후의_게시물을_최신순으로_조회할_수_있다() {
		// given
		Pageable pageable = PageRequest.of(0, 2);
		Long cursor = socialList.get(2).getId();

		// when
		List<Social> result = socialRepository.findByIdBeforeOrderByIdDesc(cursor, pageable);

		// then
		assertSoftly(softly -> {
			softly.assertThat(result).isNotEmpty();
			softly.assertThat(result).hasSize(2);
			softly.assertThat(result.get(0).getContent()).isEqualTo("Test post 2");
			softly.assertThat(result.get(1).getContent()).isEqualTo("Test post 1");
		});
	}

	@Test
	void 유효하지_않은_커서로_요청할_경우_빈_리스트를_반환한다() {
		// given
		Pageable pageable = PageRequest.of(0, 2);
		Long invalidCursor = -1L;

		// when
		List<Social> result = socialRepository.findByIdBeforeOrderByIdDesc(invalidCursor, pageable);

		// then
		assertThat(result.size()).isEqualTo(0);
	}

	@Test
	void 최신_게시물을_조회할_수_있다() {
		// given
		Pageable pageable = PageRequest.of(0, 3);

		// when
		List<Social> result = socialRepository.findLatestSocials(pageable);

		// then
		assertSoftly(softly -> {
			softly.assertThat(result.size()).isEqualTo(3);
			softly.assertThat(result.get(0).getContent()).isEqualTo("Test post 5");
			softly.assertThat(result.get(1).getContent()).isEqualTo("Test post 4");
			softly.assertThat(result.get(2).getContent()).isEqualTo("Test post 3");
		});
	}
}

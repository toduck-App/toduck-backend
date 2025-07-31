package im.toduck.domain.diary.domain.usecase;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.ServiceTest;
import im.toduck.domain.diary.domain.service.MasterKeywordService;
import im.toduck.domain.diary.domain.service.UserKeywordService;
import im.toduck.domain.diary.persistence.entity.KeywordCategory;
import im.toduck.domain.diary.persistence.entity.MasterKeyword;
import im.toduck.domain.diary.persistence.entity.UserKeyword;
import im.toduck.domain.diary.persistence.repository.UserKeywordRepository;
import im.toduck.domain.user.persistence.entity.User;

class UserKeywordUseCaseTest extends ServiceTest {

	@Autowired
	private UserKeywordUseCase userKeywordUsecase;

	@Autowired
	private UserKeywordService userKeywordService;

	@Autowired
	private UserKeywordRepository userKeywordRepository;

	@Autowired
	private MasterKeywordService masterKeywordService;

	@Nested
	@DisplayName("사용자 키워드 초기 설정")
	class setupKeyword {
		private User savedUser;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
		}

		@Test
		void 성공적으로_생성한다() {
			// given
			List<MasterKeyword> masterKeywords = List.of(
				MasterKeyword.builder()
					.category(KeywordCategory.PLACE)
					.keyword("학교")
					.createdAt(LocalDateTime.now())
					.build(),
				MasterKeyword.builder()
					.category(KeywordCategory.SITUATION)
					.keyword("요리")
					.createdAt(LocalDateTime.now())
					.build(),
				MasterKeyword.builder()
					.category(KeywordCategory.RESULT)
					.keyword("불편한 대화")
					.createdAt(LocalDateTime.now())
					.build()
			);

			// when
			userKeywordUsecase.setupKeyword(savedUser.getId());

			// then
			List<UserKeyword> userKeywords = userKeywordRepository.findAll();
			List<MasterKeyword> savedMasterKeywords = masterKeywordService.findAll();

			assertThat(userKeywords).hasSize(savedMasterKeywords.size());

			assertThat(userKeywords)
				.allMatch(uk -> uk.getUser().getId().equals(savedUser.getId()));

			List<String> masterKeywordValues = savedMasterKeywords.stream()
				.map(uk -> uk.getCategory() + ":" + uk.getKeyword())
				.toList();

			List<String> userKeywordValues = userKeywords.stream()
				.map(uk -> uk.getCategory() + ":" + uk.getKeyword())
				.toList();

			assertThat(userKeywordValues)
				.containsExactlyInAnyOrderElementsOf(masterKeywordValues);
		}
	}
}

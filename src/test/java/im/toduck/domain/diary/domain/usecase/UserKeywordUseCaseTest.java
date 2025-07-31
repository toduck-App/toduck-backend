package im.toduck.domain.diary.domain.usecase;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.assertj.core.api.SoftAssertions.*;

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
import im.toduck.domain.diary.persistence.repository.MasterKeywordRepository;
import im.toduck.domain.diary.persistence.repository.UserKeywordRepository;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import jakarta.transaction.Transactional;

class UserKeywordUseCaseTest extends ServiceTest {

	@Autowired
	private UserKeywordUseCase userKeywordUsecase;

	@Autowired
	private UserKeywordService userKeywordService;

	@Autowired
	private UserKeywordRepository userKeywordRepository;

	@Autowired
	private MasterKeywordService masterKeywordService;

	@Autowired
	private MasterKeywordRepository masterKeywordRepository;

	@Nested
	@Transactional
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
			MasterKeyword mk1 = masterKeywordRepository.save(
				testFixtureBuilder.buildMasterKeyword(KeywordCategory.PLACE, "학교")
			);
			MasterKeyword mk2 = masterKeywordRepository.save(
				testFixtureBuilder.buildMasterKeyword(KeywordCategory.SITUATION, "요리")
			);
			MasterKeyword mk3 = masterKeywordRepository.save(
				testFixtureBuilder.buildMasterKeyword(KeywordCategory.RESULT, "불편한 대화")
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

		@Test
		void 이미_생성된_경우_예외를_던진다() {
			// given
			MasterKeyword mk1 = masterKeywordRepository.save(
				testFixtureBuilder.buildMasterKeyword(KeywordCategory.PLACE, "학교")
			);
			MasterKeyword mk2 = masterKeywordRepository.save(
				testFixtureBuilder.buildMasterKeyword(KeywordCategory.SITUATION, "요리")
			);
			MasterKeyword mk3 = masterKeywordRepository.save(
				testFixtureBuilder.buildMasterKeyword(KeywordCategory.RESULT, "불편한 대화")
			);
			userKeywordUsecase.setupKeyword(savedUser.getId());

			List<UserKeyword> userKeywords = userKeywordRepository.findAll();
			// when -> then
			assertSoftly(softly -> {
				softly.assertThatThrownBy(() -> userKeywordUsecase.setupKeyword(savedUser.getId()))
					.isInstanceOf(CommonException.class)
					.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.ALREADY_SETUP_KEYWORD.getHttpStatus())
					.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.ALREADY_SETUP_KEYWORD.getErrorCode())
					.hasFieldOrPropertyWithValue("message", ExceptionCode.ALREADY_SETUP_KEYWORD.getMessage());
			});
		}
	}
}

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
import im.toduck.domain.diary.presentation.dto.request.UserKeywordRequest;
import im.toduck.domain.diary.presentation.dto.response.UserKeywordListResponse;
import im.toduck.domain.diary.presentation.dto.response.UserKeywordResponse;
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

			MasterKeyword mk1 = masterKeywordRepository.save(
				testFixtureBuilder.buildMasterKeyword(KeywordCategory.PLACE, "학교")
			);
			MasterKeyword mk2 = masterKeywordRepository.save(
				testFixtureBuilder.buildMasterKeyword(KeywordCategory.SITUATION, "요리")
			);
			MasterKeyword mk3 = masterKeywordRepository.save(
				testFixtureBuilder.buildMasterKeyword(KeywordCategory.RESULT, "불편한 대화")
			);
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			void 성공적으로_생성한다() {
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

		@Nested
		@DisplayName("실패 케이스")
		class Fail {

			@Test
			void 이미_생성된_경우_예외를_던진다() {
				// when
				userKeywordUsecase.setupKeyword(savedUser.getId());

				// then
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

	@Nested
	@Transactional
	@DisplayName("사용자 키워드 생성")
	class createKeyword {
		private User savedUser;
		UserKeywordRequest userKeywordRequest, userKeywordRequest2;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
			userKeywordRequest = UserKeywordRequest.builder()
				.keywordCategory(KeywordCategory.PLACE)
				.keyword("서울역")
				.build();
			userKeywordRequest2 = UserKeywordRequest.builder()
				.keywordCategory(KeywordCategory.SITUATION)
				.keyword("서울역")
				.build();
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("정상적으로 키워드를 생성한다")
			void 성공적으로_생성한다() {
				// when
				userKeywordUsecase.createKeyword(savedUser.getId(), userKeywordRequest);

				// then
				List<UserKeyword> userKeywords = userKeywordRepository.findAll();
				assertThat(userKeywords).hasSize(1);

				UserKeyword createdKeyword = userKeywords.get(0);
				assertThat(createdKeyword.getUser().getId()).isEqualTo(savedUser.getId());
				assertThat(createdKeyword.getCategory()).isEqualTo(userKeywordRequest.keywordCategory());
				assertThat(createdKeyword.getKeyword()).isEqualTo(userKeywordRequest.keyword());
				assertThat(createdKeyword.getCount()).isEqualTo(0L);
			}

			@Test
			@DisplayName("동일한 키워드가 삭제된 경우 복구한다")
			void 동일한_키워드가_삭제된_경우_복구한다() {
				// given
				userKeywordUsecase.createKeyword(savedUser.getId(), userKeywordRequest);
				userKeywordUsecase.deleteKeyword(savedUser.getId(), userKeywordRequest);

				// when
				userKeywordUsecase.createKeyword(savedUser.getId(), userKeywordRequest2);

				// then
				List<UserKeyword> userKeywords = userKeywordRepository.findAll();
				assertThat(userKeywords).hasSize(1);

				UserKeyword createdKeyword = userKeywords.get(0);
				assertThat(createdKeyword.getUser().getId()).isEqualTo(savedUser.getId());
				assertThat(createdKeyword.getCategory()).isEqualTo(userKeywordRequest2.keywordCategory());
				assertThat(createdKeyword.getKeyword()).isEqualTo(userKeywordRequest2.keyword());
				assertThat(createdKeyword.getCount()).isEqualTo(0L);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class Fail {

			@Test
			@DisplayName("이미 동일한 키워드가 있는 경우 실패한다")
			void 동일한_키워드가_있는_경우_실패한다() {
				// given
				userKeywordUsecase.createKeyword(savedUser.getId(), userKeywordRequest);

				try {
					// when
					userKeywordUsecase.createKeyword(savedUser.getId(), userKeywordRequest2);
				} catch (CommonException e) {
					// then
					assertThat(e.getMessage()).isEqualTo("이미 존재하는 키워드입니다.");
				}
			}
		}
	}

	@Nested
	@Transactional
	@DisplayName("키워드 삭제")
	class deleteKeyword {
		private User savedUser;
		UserKeywordRequest userKeywordRequest;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
			userKeywordRequest = UserKeywordRequest.builder()
				.keywordCategory(KeywordCategory.PLACE)
				.keyword("서울역")
				.build();
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("성공적으로 키워드를 삭제한다")
			void 성공적으로_키워드를_삭제한다() {
				// given
				userKeywordUsecase.createKeyword(savedUser.getId(), userKeywordRequest);

				// when
				userKeywordUsecase.deleteKeyword(savedUser.getId(), userKeywordRequest);

				// then
				List<UserKeyword> userKeywords = userKeywordRepository.findAll();
				assertThat(userKeywords.size()).isEqualTo(0);
			}
		}
	}

	@Nested
	@Transactional
	@DisplayName("특정 사용자 키워드 목록 반환")
	class getKeyword {
		private User savedUser;
		UserKeywordRequest userKeywordRequest, userKeywordRequest2;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
			userKeywordRequest = UserKeywordRequest.builder()
				.keywordCategory(KeywordCategory.PLACE)
				.keyword("서울역")
				.build();
			userKeywordRequest2 = UserKeywordRequest.builder()
				.keywordCategory(KeywordCategory.SITUATION)
				.keyword("요리")
				.build();
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("특정 사용자의 키워드 목록을 성공적으로 가져온다")
			void 특정_사용자의_키워드_목록을_성공적으로_가져온다() {
				// given
				userKeywordUsecase.createKeyword(savedUser.getId(), userKeywordRequest);
				userKeywordUsecase.createKeyword(savedUser.getId(), userKeywordRequest2);

				// when
				UserKeywordListResponse userKeywordListResponse = userKeywordUsecase.getKeywords(savedUser.getId());

				// then
				assertThat(userKeywordListResponse).isNotNull();
				assertThat(userKeywordListResponse.userKeywordDtos()).hasSize(2);

				// DTO 내용 검증
				List<KeywordCategory> categories = userKeywordListResponse.userKeywordDtos()
					.stream()
					.map(UserKeywordResponse::category)
					.toList();

				List<String> keywords = userKeywordListResponse.userKeywordDtos()
					.stream()
					.map(UserKeywordResponse::keyword)
					.toList();

				boolean allCountZero = userKeywordListResponse.userKeywordDtos()
					.stream()
					.allMatch(dto -> dto.count() == 0);

				assertThat(categories)
					.containsExactlyInAnyOrder(KeywordCategory.PLACE, KeywordCategory.SITUATION);
				assertThat(keywords)
					.containsExactlyInAnyOrder("서울역", "요리");
				assertThat(allCountZero).isTrue();
			}
		}
	}
}

package im.toduck.domain.social.domain.usecase;

import static im.toduck.fixtures.routine.RoutineFixtures.*;
import static im.toduck.fixtures.social.CommentFixtures.*;
import static im.toduck.fixtures.social.LikeFixtures.*;
import static im.toduck.fixtures.social.SocialCategoryFixtures.*;
import static im.toduck.fixtures.social.SocialFixtures.*;
import static im.toduck.fixtures.user.BlockFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.repository.RoutineRepository;
import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.social.persistence.repository.CommentLikeRepository;
import im.toduck.domain.social.persistence.repository.CommentRepository;
import im.toduck.domain.social.persistence.repository.SocialCategoryLinkRepository;
import im.toduck.domain.social.persistence.repository.SocialImageFileRepository;
import im.toduck.domain.social.persistence.repository.SocialRepository;
import im.toduck.domain.social.presentation.dto.request.SocialCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialUpdateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentDto;
import im.toduck.domain.social.presentation.dto.response.SocialCategoryResponse;
import im.toduck.domain.social.presentation.dto.response.SocialCategoryResponse.SocialCategoryDto;
import im.toduck.domain.social.presentation.dto.response.SocialCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialDetailResponse;
import im.toduck.domain.social.presentation.dto.response.SocialImageDto;
import im.toduck.domain.social.presentation.dto.response.SocialResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.fixtures.social.CommentFixtures;
import im.toduck.fixtures.social.SocialFixtures;
import im.toduck.fixtures.social.SocialImageFileFixtures;
import im.toduck.fixtures.user.UserFixtures;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.dto.response.CursorPaginationResponse;

@Transactional
public class SocialBoardUseCaseTest extends ServiceTest {
	private User USER;

	@Autowired
	private SocialBoardUseCase socialBoardUseCase;

	@Autowired
	private SocialRepository socialRepository;

	@Autowired
	private SocialImageFileRepository socialImageFileRepository;

	@Autowired
	private SocialCategoryLinkRepository socialCategoryLinkRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private RoutineRepository routineRepository;

	@Autowired
	private CommentLikeRepository commentLikeRepository;

	@BeforeEach
	public void setUp() {
		USER = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Nested
	@DisplayName("게시글 작성시")
	class CreateSocialBoard {
		String content = "Test Content";
		Boolean isAnonymous = false;
		List<String> imageUrls = List.of("image1.jpg", "image2.jpg");
		List<Long> categoryIds = testFixtureBuilder.buildCategories(MULTIPLE_CATEGORIES(2))
			.stream()
			.map(SocialCategory::getId)
			.toList();

		SocialCreateRequest request = new SocialCreateRequest(
			null,
			content,
			null,
			isAnonymous,
			categoryIds,
			imageUrls
		);

		@Test
		void 주어진_요청에_따라_게시글을_생성할_수_있다() {
			// when
			SocialCreateResponse response = socialBoardUseCase.createSocialBoard(USER.getId(), request);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.socialId()).isNotNull();
			});
		}

		@Test
		void 사용자를_조회할_수_없는_경우_소셜_게시판_생성에_실패한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(() -> socialBoardUseCase.createSocialBoard(nonExistentUserId, request))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}

		@Test
		void 소셜_카테고리_ID가_유효하지_않는_경우_예외가_발생한다() {
			// given
			List<Long> invalidCategoryIds = List.of(1L, -1L);
			SocialCreateRequest invalidRequest = new SocialCreateRequest(
				null,
				content,
				null,
				isAnonymous,
				invalidCategoryIds,
				imageUrls
			);

			// when & then
			assertThatThrownBy(() -> socialBoardUseCase.createSocialBoard(USER.getId(), invalidRequest))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY.getMessage());
		}

		@Test
		void 루틴이_자신의_소유가_아닌_경우_게시글_생성에_실패한다() {
			// given
			User ANOTHER_USER = testFixtureBuilder.buildUser(GENERAL_USER());
			Routine ANOTHER_USER_ROUTINE = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(ANOTHER_USER));
			SocialCreateRequest requestWithAnotherUserRoutine = new SocialCreateRequest(
				null,
				content,
				ANOTHER_USER_ROUTINE.getId(),
				isAnonymous,
				categoryIds,
				imageUrls
			);

			// when & then
			assertThatThrownBy(() -> socialBoardUseCase.createSocialBoard(USER.getId(), requestWithAnotherUserRoutine))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_ROUTINE.getMessage());
		}

		@Test
		void 비공개_루틴으로_게시글_작성시_실패한다() {
			// given
			Routine PRIVATE_ROUTINE = testFixtureBuilder.buildRoutine(PRIVATE_ROUTINE(USER));
			SocialCreateRequest requestWithPrivateRoutine = new SocialCreateRequest(
				null,
				content,
				PRIVATE_ROUTINE.getId(),
				isAnonymous,
				categoryIds,
				imageUrls
			);

			// when & then
			assertThatThrownBy(() -> socialBoardUseCase.createSocialBoard(USER.getId(), requestWithPrivateRoutine))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.PRIVATE_ROUTINE.getMessage());
		}

		@ParameterizedTest
		@NullAndEmptySource
		void 이미지URL이_null이거나_빈_리스트일때_게시글_작성에_성공한다(List<String> nullAndEmptyImageUrls) {
			SocialCreateRequest requestWithoutImages = new SocialCreateRequest(
				null,
				content,
				null,
				isAnonymous,
				categoryIds,
				nullAndEmptyImageUrls
			);

			// when
			SocialCreateResponse response = socialBoardUseCase.createSocialBoard(USER.getId(), requestWithoutImages);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.socialId()).isNotNull();
			});
		}

	}

	@Nested
	@DisplayName("게시글 삭제시")
	class DeleteSocialBoard {

		Social SOCIAL_BOARD;
		Comment COMMENT;

		@BeforeEach
		void setUp() {
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, false));
			COMMENT = testFixtureBuilder.buildComment(SINGLE_COMMENT(USER, SOCIAL_BOARD));
		}

		@Test
		void 주어진_요청에_따라_게시글을_삭제할_수_있다() {
			// when
			socialBoardUseCase.deleteSocialBoard(USER.getId(), SOCIAL_BOARD.getId());

			// then
			assertThat(socialRepository.findById(SOCIAL_BOARD.getId())).isEmpty();
		}

		@Test
		void 게시글이_삭제되어도_루틴은_삭제되지_않는다() {
			// given
			Routine ROUTINE = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));
			Social SOCIAL_WITH_ROUTINE = testFixtureBuilder.buildSocial(
				SINGLE_SOCIAL_WITH_ROUTINE(USER, ROUTINE, false)
			);

			// when
			socialBoardUseCase.deleteSocialBoard(USER.getId(), SOCIAL_WITH_ROUTINE.getId());

			// then
			Optional<Routine> existingRoutine = routineRepository.findById(ROUTINE.getId());
			assertThat(existingRoutine).isPresent();
		}

		@Test
		void 사용자를_조회할_수_없는_경우_게시글_삭제에_실패한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(() -> socialBoardUseCase.deleteSocialBoard(nonExistentUserId, SOCIAL_BOARD.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}

		@Test
		void 게시글이_존재하지_않는_경우_삭제에_실패한다() {
			// given
			Long nonExistentSocialBoardId = -1L;

			// when & then
			assertThatThrownBy(() -> socialBoardUseCase.deleteSocialBoard(USER.getId(), nonExistentSocialBoardId))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 게시글의_소유자가_아닌_경우_삭제에_실패한다() {
			// given
			User ANOTHER_USER = testFixtureBuilder.buildUser(GENERAL_USER());
			Long socialBoardId = SOCIAL_BOARD.getId();

			// when & then
			assertThatThrownBy(() -> socialBoardUseCase.deleteSocialBoard(ANOTHER_USER.getId(), socialBoardId))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD.getMessage());
		}

		@Test
		@Transactional
		void 게시글이_삭제되면_댓글은_soft_delete_된다() {
			// when
			socialBoardUseCase.deleteSocialBoard(USER.getId(), SOCIAL_BOARD.getId());

			// then
			Optional<Comment> softDeletedComment = commentRepository.findById(COMMENT.getId());
			assertSoftly(softly -> {
				softly.assertThat(softDeletedComment).isPresent();
				softDeletedComment.ifPresent(value -> softly.assertThat(value.getDeletedAt()).isNotNull());

			});
		}

	}

	@Nested
	@DisplayName("게시글 수정시")
	class UpdateSocialBoard {
		Social SOCIAL_BOARD;
		String updateContent = "This is a test update.";
		Boolean isAnonymous = true;
		List<Long> invalidCategoryIds = List.of(-1L);
		List<String> imageUrls = List.of("updatedImage1.jpg", "updatedImage2.jpg");
		List<SocialCategory> createdCategories;
		List<Long> validCategoryIds;

		enum CategoryUpdateScenario {
			NO_CHANGE,
			USE_SETUP_IDS,
			USE_EMPTY_LIST
		}

		@BeforeEach
		void setUp() {
			createdCategories = testFixtureBuilder.buildCategories(MULTIPLE_CATEGORIES(2));
			validCategoryIds = createdCategories.stream().map(SocialCategory::getId).toList();
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, false));
			testFixtureBuilder.buildSocialCategoryLinks(createdCategories.get(0), SOCIAL_BOARD);
			testFixtureBuilder.buildSocialCategoryLinks(createdCategories.get(1), SOCIAL_BOARD);
		}

		static Stream<Arguments> provideUpdateRequests() {
			return Stream.of(
				Arguments.of(null, null, CategoryUpdateScenario.NO_CHANGE, null),
				Arguments.of(null, null, CategoryUpdateScenario.NO_CHANGE, List.of()),
				Arguments.of(null, null, CategoryUpdateScenario.NO_CHANGE, List.of("updatedImage.jpg")),
				Arguments.of(null, null, CategoryUpdateScenario.USE_SETUP_IDS, null),
				Arguments.of(null, null, CategoryUpdateScenario.USE_SETUP_IDS, List.of("updatedIm age.jpg")),
				Arguments.of(null, true, CategoryUpdateScenario.NO_CHANGE, null),
				Arguments.of(null, false, CategoryUpdateScenario.NO_CHANGE, List.of("updatedIm age.jpg")),
				Arguments.of(null, false, CategoryUpdateScenario.USE_SETUP_IDS, null),
				Arguments.of(null, false, CategoryUpdateScenario.USE_SETUP_IDS, List.of("updatedImage.jpg")),
				Arguments.of("Updated Content", null, CategoryUpdateScenario.NO_CHANGE, null),
				Arguments.of("Updated Content", null, CategoryUpdateScenario.NO_CHANGE, List.of("updatedImage.jpg")),
				Arguments.of("Updated Content", null, CategoryUpdateScenario.USE_SETUP_IDS, null),
				Arguments.of("Updated Content", null, CategoryUpdateScenario.USE_SETUP_IDS,
					List.of("updatedImage.jpg")),
				Arguments.of("Updated Content", true, CategoryUpdateScenario.NO_CHANGE, null),
				Arguments.of("Updated Content", true, CategoryUpdateScenario.NO_CHANGE, List.of()),
				Arguments.of("Updated Content", true, CategoryUpdateScenario.USE_SETUP_IDS, null),
				Arguments.of("Updated Content", true, CategoryUpdateScenario.USE_SETUP_IDS,
					List.of("updatedImage.jpg")),
				Arguments.of("Use Empty List", null, CategoryUpdateScenario.USE_EMPTY_LIST, null)
			);
		}

		@ParameterizedTest
		@MethodSource("provideUpdateRequests")
		void 주어진_요청에_따라_게시글을_수정할_수_있다(
			String updatedContent,
			Boolean updatedIsAnonymous,
			CategoryUpdateScenario categoryScenario,
			List<String> updatedImageUrls
		) {
			String originalContent = SOCIAL_BOARD.getContent();
			Boolean originalIsAnonymous = SOCIAL_BOARD.getIsAnonymous();
			List<SocialImageFile> originalImages = socialImageFileRepository.findAllBySocial(SOCIAL_BOARD);
			List<Long> originalCategoryIds = socialCategoryLinkRepository.findAllBySocial(SOCIAL_BOARD)
				.stream()
				.map(link -> link.getSocialCategory().getId())
				.toList();

			List<Long> categoryIdsToUpdate = switch (categoryScenario) {
				case USE_SETUP_IDS -> this.validCategoryIds;
				case USE_EMPTY_LIST -> List.of();
				default -> null;
			};

			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				true, null, true, null,
				updatedContent,
				updatedIsAnonymous,
				categoryIdsToUpdate,
				updatedImageUrls
			);

			if (categoryScenario == CategoryUpdateScenario.USE_EMPTY_LIST) {
				assertThatThrownBy(
					() -> socialBoardUseCase.updateSocialBoard(USER.getId(), SOCIAL_BOARD.getId(), updateRequest))
					.isInstanceOf(CommonException.class)
					.hasMessage(ExceptionCode.EMPTY_SOCIAL_CATEGORY_LIST.getMessage());
				return;
			}

			socialBoardUseCase.updateSocialBoard(USER.getId(), SOCIAL_BOARD.getId(), updateRequest);

			Social updatedSocialBoard = socialRepository.findById(SOCIAL_BOARD.getId()).orElseThrow();

			assertSoftly(softly -> {
				softly.assertThat(updatedSocialBoard.getContent())
					.isEqualTo(updatedContent == null ? originalContent : updatedContent);
				softly.assertThat(updatedSocialBoard.getIsAnonymous())
					.isEqualTo(updatedIsAnonymous == null ? originalIsAnonymous : updatedIsAnonymous);

				List<String> currentImageUrls = socialImageFileRepository.findAllBySocial(updatedSocialBoard)
					.stream().map(SocialImageFile::getUrl).toList();
				if (updatedImageUrls == null) {
					List<String> originalImageUrls = originalImages.stream().map(SocialImageFile::getUrl).toList();
					softly.assertThat(currentImageUrls)
						.containsExactlyInAnyOrderElementsOf(originalImageUrls);
				} else {
					softly.assertThat(currentImageUrls)
						.containsExactlyInAnyOrderElementsOf(updatedImageUrls);
				}

				List<Long> currentCategoryIds = socialCategoryLinkRepository.findAllBySocial(updatedSocialBoard)
					.stream()
					.map(link -> link.getSocialCategory().getId())
					.toList();
				if (categoryIdsToUpdate == null) {
					softly.assertThat(currentCategoryIds).containsExactlyInAnyOrderElementsOf(originalCategoryIds);
				} else {
					softly.assertThat(currentCategoryIds).containsExactlyInAnyOrderElementsOf(categoryIdsToUpdate);
				}
			});
		}

		@Test
		void 존재하지_않는_게시글을_수정하려고_하면_예외가_발생한다() {
			Long nonExistentSocialBoardId = -1L;
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				true, null, true, null,
				updateContent, isAnonymous, validCategoryIds, imageUrls
			);
			assertThatThrownBy(
				() -> socialBoardUseCase.updateSocialBoard(USER.getId(), nonExistentSocialBoardId, updateRequest))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 존재하지_않거나_권한이_없는_루틴_ID인_경우_수정에_실패한다() {
			Long invalidRoutineId = -1L;
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				true, null, false, invalidRoutineId,
				updateContent, isAnonymous, validCategoryIds, imageUrls
			);
			assertThatThrownBy(
				() -> socialBoardUseCase.updateSocialBoard(USER.getId(), SOCIAL_BOARD.getId(), updateRequest))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_ROUTINE.getMessage());
		}

		@Test
		void isChangeRoutine이_true이고_routineId가_null인_경우_해당_게시글의_공유_루틴을_제거한다() {
			Routine ROUTINE = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));
			Social SOCIAL_WITH_ROUTINE = testFixtureBuilder.buildSocial(
				SINGLE_SOCIAL_WITH_ROUTINE(USER, ROUTINE, false)
			);
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				true, null, true, null,
				updateContent, isAnonymous, validCategoryIds, imageUrls
			);
			socialBoardUseCase.updateSocialBoard(USER.getId(), SOCIAL_WITH_ROUTINE.getId(), updateRequest);

			Social socialBoard = socialRepository.findById(SOCIAL_WITH_ROUTINE.getId())
				.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));
			assertSoftly(softly -> {
				softly.assertThat(socialBoard.getContent()).isEqualTo(updateRequest.content());
				softly.assertThat(socialBoard.getRoutine()).isNull();
				softly.assertThat(socialBoard.getIsAnonymous()).isEqualTo(updateRequest.isAnonymous());
			});
		}

		@Test
		void isChangeRoutine이_false이고_routineId가_null이_아닌_경우_validation에_실패한다() {
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				true, null, false, 1L,
				updateContent, isAnonymous, validCategoryIds, imageUrls
			);
			assertFalse(updateRequest.isValidRoutineIdWhenRemoved());
		}

		@Test
		void isChangeRoutine이_false인_경우_해당_게시글의_공유_루틴을_변경하지_않는다() {
			Routine ROUTINE = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));
			Social SOCIAL_WITH_ROUTINE = testFixtureBuilder.buildSocial(
				SINGLE_SOCIAL_WITH_ROUTINE(USER, ROUTINE, false)
			);
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				true, null, false, null,
				updateContent, isAnonymous, validCategoryIds, imageUrls
			);
			socialBoardUseCase.updateSocialBoard(USER.getId(), SOCIAL_WITH_ROUTINE.getId(), updateRequest);

			Social socialBoard = socialRepository.findById(SOCIAL_WITH_ROUTINE.getId())
				.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));
			assertSoftly(softly -> {
				softly.assertThat(socialBoard.getContent()).isEqualTo(updateRequest.content());
				softly.assertThat(socialBoard.getRoutine().getId()).isEqualTo(ROUTINE.getId());
				softly.assertThat(socialBoard.getIsAnonymous()).isEqualTo(updateRequest.isAnonymous());
			});
		}

		@Test
		void isChangeTitle이_true이고_title이_유효한_경우_제목을_변경한다() {
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				true, "새로운 제목", false, null,
				updateContent, isAnonymous, validCategoryIds, imageUrls
			);
			socialBoardUseCase.updateSocialBoard(USER.getId(), SOCIAL_BOARD.getId(), updateRequest);

			Social socialBoard = socialRepository.findById(SOCIAL_BOARD.getId())
				.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));
			assertSoftly(softly -> {
				softly.assertThat(socialBoard.getTitle()).isEqualTo(updateRequest.title());
				softly.assertThat(socialBoard.getContent()).isEqualTo(updateRequest.content());
				softly.assertThat(socialBoard.getIsAnonymous()).isEqualTo(updateRequest.isAnonymous());
			});
		}

		@Test
		void isChangeTitle이_false이고_title이_null인_경우_제목을_변경하지_않는다() {
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				false, null, false, null,
				updateContent, isAnonymous, validCategoryIds, imageUrls
			);
			socialBoardUseCase.updateSocialBoard(USER.getId(), SOCIAL_BOARD.getId(), updateRequest);

			Social socialBoard = socialRepository.findById(SOCIAL_BOARD.getId())
				.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD));
			assertSoftly(softly -> {
				softly.assertThat(socialBoard.getTitle()).isEqualTo(SOCIAL_BOARD.getTitle());
				softly.assertThat(socialBoard.getContent()).isEqualTo(updateRequest.content());
				softly.assertThat(socialBoard.getIsAnonymous()).isEqualTo(updateRequest.isAnonymous());
			});
		}

		@Test
		void isChangeTitle이_false이고_title이_null이_아닌_경우_validation에_실패한다() {
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				false, "제목이 없어야 합니다", false, null,
				updateContent, isAnonymous, validCategoryIds, imageUrls
			);
			assertFalse(updateRequest.isValidTitleWhenRemoved());
		}

		@Test
		void 게시글_소유자가_아닌_경우_수정에_실패한다() {
			User ANOTHER_USER = testFixtureBuilder.buildUser(GENERAL_USER());
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				true, null, false, null,
				updateContent, isAnonymous, validCategoryIds, imageUrls
			);
			assertThatThrownBy(
				() -> socialBoardUseCase.updateSocialBoard(ANOTHER_USER.getId(), SOCIAL_BOARD.getId(), updateRequest))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 유효하지_않은_카테고리_ID가_포함된_경우_수정에_실패한다() {
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				true, null, false, null,
				updateContent, isAnonymous, invalidCategoryIds, imageUrls
			);
			assertThatThrownBy(
				() -> socialBoardUseCase.updateSocialBoard(USER.getId(), SOCIAL_BOARD.getId(), updateRequest))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY.getMessage());
		}

		@Test
		void 카테고리_ID_리스트가_빈_경우_수정에_실패한다() {
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				true, null, false, null,
				updateContent, isAnonymous, List.of(), imageUrls
			);
			assertThatThrownBy(
				() -> socialBoardUseCase.updateSocialBoard(USER.getId(), SOCIAL_BOARD.getId(), updateRequest))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.EMPTY_SOCIAL_CATEGORY_LIST.getMessage());
		}

		@Test
		void 비공개_루틴으로_게시글_수정시_실패한다() {
			Routine PRIVATE_ROUTINE = testFixtureBuilder.buildRoutine(PRIVATE_ROUTINE(USER));
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				true, null, false, PRIVATE_ROUTINE.getId(),
				updateContent, isAnonymous, validCategoryIds, imageUrls
			);
			assertThatThrownBy(
				() -> socialBoardUseCase.updateSocialBoard(USER.getId(), SOCIAL_BOARD.getId(), updateRequest))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.PRIVATE_ROUTINE.getMessage());
		}
	}

	@Nested
	@DisplayName("게시글 단건 조회시")
	class GetSocialDetail {
		Social SOCIAL_BOARD;
		Routine ROUTINE;
		Comment COMMENT;
		Like LIKE;
		List<SocialImageFile> IMAGE_FILES;
		List<String> imageUrls = List.of("image1.jpg", "image2.jpg");
		User BLOCK_USER;
		Social BLOCKED_USER_SOCIAL;
		Comment BLOCKED_USER_COMMENT;

		@BeforeEach
		void setUp() {
			ROUTINE = testFixtureBuilder.buildRoutine(WEEKDAY_MORNING_ROUTINE(USER));
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL_WITH_ROUTINE(USER, ROUTINE, false));
			COMMENT = testFixtureBuilder.buildComment(SINGLE_COMMENT(USER, SOCIAL_BOARD));
			LIKE = testFixtureBuilder.buildLike(LIKE(USER, SOCIAL_BOARD));
			IMAGE_FILES = testFixtureBuilder.buildSocialImageFiles(
				SocialImageFileFixtures.MULTIPLE_IMAGE_FILES(SOCIAL_BOARD, imageUrls)
			);
			BLOCK_USER = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
			testFixtureBuilder.buildBlock(BLOCK_USER(USER, BLOCK_USER));
			BLOCKED_USER_SOCIAL = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(BLOCK_USER, false));
			BLOCKED_USER_COMMENT = testFixtureBuilder.buildComment(SINGLE_COMMENT(BLOCK_USER, SOCIAL_BOARD));
		}

		@Test
		void 게시글_단건_조회에_성공한다() {
			// when
			SocialDetailResponse response = socialBoardUseCase.getSocialDetail(USER.getId(), SOCIAL_BOARD.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.routine().routineId()).isEqualTo(ROUTINE.getId());
				softly.assertThat(response.socialId()).isEqualTo(SOCIAL_BOARD.getId());
				softly.assertThat(response.content()).isEqualTo(SOCIAL_BOARD.getContent());
				softly.assertThat(response.hasImages()).isTrue();
				softly.assertThat(response.socialLikeInfo().isLikedByMe()).isTrue();
				softly.assertThat(response.images())
					.hasSize(IMAGE_FILES.size())
					.extracting(SocialImageDto::url)
					.containsExactlyInAnyOrderElementsOf(
						IMAGE_FILES.stream().map(SocialImageFile::getUrl).toList()
					);

				softly.assertThat(response.comments())
					.hasSize(2)
					.extracting(CommentDto::commentId)
					.containsExactly(COMMENT.getId(), BLOCKED_USER_COMMENT.getId());

			});
		}

		@Test
		void 댓글_정렬_테스트() {
			Social SOCIAL_BOARD_FOR_COMMENT = testFixtureBuilder.buildSocial(
				SINGLE_SOCIAL_WITH_ROUTINE(USER, ROUTINE, false)
			);

			Comment parent1 = testFixtureBuilder.buildComment(
				CommentFixtures.SINGLE_COMMENT(USER, SOCIAL_BOARD_FOR_COMMENT));
			Comment parent2 = testFixtureBuilder.buildComment(
				CommentFixtures.SINGLE_COMMENT(USER, SOCIAL_BOARD_FOR_COMMENT));

			Comment replyToParent1_1 = testFixtureBuilder.buildComment(
				CommentFixtures.REPLY_COMMENT(USER, SOCIAL_BOARD_FOR_COMMENT, parent1));
			Comment replyToParent1_2 = testFixtureBuilder.buildComment(
				CommentFixtures.REPLY_COMMENT(USER, SOCIAL_BOARD_FOR_COMMENT, parent1));

			Comment blockParent = testFixtureBuilder.buildComment(
				CommentFixtures.SINGLE_COMMENT(BLOCK_USER, SOCIAL_BOARD_FOR_COMMENT));

			Comment replyToBlockParent = testFixtureBuilder.buildComment(
				CommentFixtures.REPLY_COMMENT(USER, SOCIAL_BOARD_FOR_COMMENT, blockParent));

			Comment parent3 = testFixtureBuilder.buildComment(
				CommentFixtures.SINGLE_COMMENT(USER, SOCIAL_BOARD_FOR_COMMENT));

			Comment replyToParent3 = testFixtureBuilder.buildComment(
				CommentFixtures.REPLY_COMMENT(USER, SOCIAL_BOARD_FOR_COMMENT, parent3));

			SocialDetailResponse response = socialBoardUseCase.getSocialDetail(
				USER.getId(),
				SOCIAL_BOARD_FOR_COMMENT.getId()
			);

			assertSoftly(softly -> {
				softly.assertThat(response.comments())
					.extracting(CommentDto::commentId)
					.containsExactly(
						parent1.getId(), replyToParent1_1.getId(), replyToParent1_2.getId(),
						parent2.getId(),
						blockParent.getId(), replyToBlockParent.getId(),
						parent3.getId(), replyToParent3.getId());
				softly.assertThat(response.comments().get(4).owner().ownerId()).isEqualTo(0L);
				softly.assertThat(response.comments().get(4).owner().nickname()).isEqualTo("차단된 사용자");
				softly.assertThat(response.comments().get(4).content()).isEqualTo("차단한 작성자의 댓글입니다.");
			});
		}

		@Test
		void 게시글이_없는_경우_단건_조회에_실패_한다() {
			// given
			Long nonExistentSocialId = -1L;

			// when & then
			assertThatThrownBy(() -> socialBoardUseCase.getSocialDetail(USER.getId(), nonExistentSocialId))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 소유자를_찾을_수_없는_경우_게시글_단건_조회에_실패_한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(() -> socialBoardUseCase.getSocialDetail(nonExistentUserId, SOCIAL_BOARD.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}

		@Test
		void 차단된_사용자의_게시글을_조회하려_할_경우_예외를_발생시킨다() {
			// when & then
			assertThatThrownBy(() -> socialBoardUseCase.getSocialDetail(USER.getId(), BLOCKED_USER_SOCIAL.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.BLOCKED_USER_SOCIAL_ACCESS.getMessage());
		}
	}

	@Nested
	@DisplayName("게시글 목록 조회시")
	class GetSocials {
		@Test
		void 존재하지_않는_사용자일_경우_조회에_실패한다() {
			// given
			Long nonExistentUserId = -1L;
			Long cursor = null;
			Integer limit = 10;

			// when & then
			assertThatThrownBy(() -> socialBoardUseCase.getSocials(nonExistentUserId, cursor, limit, null))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}

		@Test
		void 커서가_존재할_경우_해당_커서_이후의_게시글을_조회한다() {
			// given
			int totalPosts = 15;
			List<Social> socials = testFixtureBuilder.buildSocials(MULTIPLE_SOCIALS(USER, totalPosts));

			Long cursor = socials.get(10).getId();
			int limit = 5;

			// when
			CursorPaginationResponse<SocialResponse> response = socialBoardUseCase.getSocials(
				USER.getId(),
				cursor,
				limit,
				null
			);

			List<Long> expectedIds = socials.stream()
				.filter(social -> social.getId() < cursor)
				.sorted(Comparator.comparing(Social::getId).reversed())
				.limit(limit)
				.map(Social::getId)
				.toList();

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.results()).hasSize(expectedIds.size());
				softly.assertThat(response.results())
					.extracting(SocialResponse::socialId)
					.containsExactlyElementsOf(expectedIds);
				softly.assertThat(response.hasMore()).isTrue();
				softly.assertThat(response.nextCursor()).isNotNull();
			});
		}

		@Test
		void 특정_카테고리_필터를_적용하여_게시글을_조회한다() {
			// given
			List<SocialCategory> categories = testFixtureBuilder.buildCategories(MULTIPLE_CATEGORIES(2));

			// 카테고리 1에 속한 게시글 4개 생성
			List<Social> category1Socials = testFixtureBuilder.buildSocials(MULTIPLE_SOCIALS(USER, 4));
			category1Socials.forEach(social ->
				testFixtureBuilder.buildSocialCategoryLinks(categories.get(0), social)
			);

			// 카테고리 2에 속한 게시글 5개 생성
			List<Social> category2Socials = testFixtureBuilder.buildSocials(MULTIPLE_SOCIALS(USER, 5));
			category2Socials.forEach(social ->
				testFixtureBuilder.buildSocialCategoryLinks(categories.get(1), social)
			);

			Long cursor = null;
			Integer limit = 10;

			// when
			CursorPaginationResponse<SocialResponse> response = socialBoardUseCase.getSocials(
				USER.getId(),
				cursor,
				limit,
				List.of(categories.get(0).getId()) // 카테고리 1 필터
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.results()).hasSize(4);
			});
		}

		@Test
		void 존재하지_않는_카테고리를_포함하면_예외가_발생한다() {
			// given
			Long cursor = null;
			Integer limit = 10;
			List<Long> invalidCategoryIds = List.of(1L, -1L);

			// when & then
			assertThatThrownBy(() -> socialBoardUseCase.getSocials(USER.getId(), cursor, limit, invalidCategoryIds))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY.getMessage());
		}

		@Test
		void 카테고리없이_전체_게시글을_조회한다() {
			// given
			int numberOfPosts = 10;
			testFixtureBuilder.buildSocials(MULTIPLE_SOCIALS(USER, numberOfPosts));
			Long cursor = null;
			Integer limit = 10;

			// when
			CursorPaginationResponse<SocialResponse> response = socialBoardUseCase.getSocials(
				USER.getId(),
				cursor,
				limit,
				null
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.results()).hasSize(numberOfPosts);
			});
		}
	}

	@Nested
	@DisplayName("카테고리 전체 조회시")
	class GetAllCategories {

		@Test
		void 모든_카테고리를_조회한다() {
			// given
			List<SocialCategory> categories = testFixtureBuilder.buildCategories(MULTIPLE_CATEGORIES(5));

			// when
			SocialCategoryResponse response = socialBoardUseCase.getAllCategories();

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.categories()).hasSize(categories.size());
				softly.assertThat(response.categories())
					.extracting(SocialCategoryDto::socialCategoryId)
					.containsExactlyInAnyOrderElementsOf(
						categories.stream().map(SocialCategory::getId).toList()
					);
				softly.assertThat(response.categories())
					.extracting(SocialCategoryDto::name)
					.containsExactlyInAnyOrderElementsOf(
						categories.stream().map(SocialCategory::getName).toList()
					);
			});
		}
	}

	@Nested
	@DisplayName("소셜 게시글 검색시")
	class SearchSocials {

		// 카테고리 필터 테스트를 위한 카테고리 미리 생성
		List<SocialCategory> categories;
		Long category1Id;
		Long category2Id;

		@BeforeEach
		void setupCategories() {
			categories = testFixtureBuilder.buildCategories(MULTIPLE_CATEGORIES(2));
			category1Id = categories.get(0).getId();
			category2Id = categories.get(1).getId();
		}

		@Test
		void 키워드가_제목에_포함된_게시글을_조회한다() {
			// given
			String keyword = "루틴";
			List<Social> socials = testFixtureBuilder.buildSocials(List.of(
				SocialFixtures.SINGLE_SOCIAL_WITH_TITLE(USER, "루틴 관리하기"),
				SocialFixtures.SINGLE_SOCIAL_WITH_TITLE(USER, "운동 계획"),
				SocialFixtures.SINGLE_SOCIAL_WITH_TITLE(USER, "루틴을 꾸준히!")
			));

			Long cursor = null;
			Integer limit = 10;

			// when
			// searchSocials 호출 시 마지막 인자로 null (또는 Collections.emptyList()) 추가
			CursorPaginationResponse<SocialResponse> response = socialBoardUseCase.searchSocials(
				USER.getId(),
				keyword,
				cursor,
				limit,
				null
			);

			List<Long> expectedIds = socials.stream()
				.filter(social -> social.getTitle() != null && social.getTitle().contains(keyword))
				.map(Social::getId)
				.sorted(Comparator.reverseOrder())
				.toList();

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.results())
					.extracting(SocialResponse::socialId)
					.containsExactlyElementsOf(expectedIds); // 순서까지 검증
			});
		}

		@Test
		void 키워드가_내용에_포함된_게시글을_조회한다() {
			// given
			String keyword = "운동";
			List<Social> socials = testFixtureBuilder.buildSocials(List.of(
				SocialFixtures.SINGLE_SOCIAL_WITH_CONTENT(USER, "운동 계획 세우기"),
				SocialFixtures.SINGLE_SOCIAL_WITH_CONTENT(USER, "오늘의 운동"),
				SocialFixtures.SINGLE_SOCIAL_WITH_CONTENT(USER, "건강이 가장 중요")
			));

			Long cursor = null;
			Integer limit = 10;

			// when
			CursorPaginationResponse<SocialResponse> response = socialBoardUseCase.searchSocials(
				USER.getId(),
				keyword,
				cursor,
				limit,
				null
			);

			// then
			List<Long> expectedIds = socials.stream()
				.filter(social -> social.getContent() != null && social.getContent().contains(keyword))
				.map(Social::getId)
				.sorted(Comparator.reverseOrder()) // ID 역순 정렬 추가 (Repository 정렬과 일치)
				.toList();

			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.results())
					.extracting(SocialResponse::socialId)
					.containsExactlyElementsOf(expectedIds); // 순서까지 검증
			});
		}

		@Test
		void 키워드와_카테고리_필터를_함께_적용하여_게시글을_조회한다() {
			// given
			String keyword = "운동";

			// 카테고리1에 속하고 키워드 포함 게시글 2개
			Social social1_cat1 = testFixtureBuilder.buildSocial(SINGLE_SOCIAL_WITH_CONTENT(USER, "오늘의 운동 루틴"));
			Social social2_cat1 = testFixtureBuilder.buildSocial(SINGLE_SOCIAL_WITH_CONTENT(USER, "운동 계획 중요"));
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(0), social1_cat1);
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(0), social2_cat1);

			// 카테고리2에 속하고 키워드 포함 게시글 1개
			Social social3_cat2 = testFixtureBuilder.buildSocial(SINGLE_SOCIAL_WITH_CONTENT(USER, "주말 운동 기록"));
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(1), social3_cat2);

			// 카테고리1에 속하지만 키워드 미포함 게시글 1개
			Social social4_cat1 = testFixtureBuilder.buildSocial(SINGLE_SOCIAL_WITH_CONTENT(USER, "독서 기록"));
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(0), social4_cat1);

			Long cursor = null;
			Integer limit = 10;
			List<Long> filterCategoryIds = List.of(category1Id); // 카테고리1로 필터링

			// when
			CursorPaginationResponse<SocialResponse> response = socialBoardUseCase.searchSocials(
				USER.getId(),
				keyword,
				cursor,
				limit,
				filterCategoryIds // 카테고리 필터 적용
			);

			List<Long> expectedIds = List.of(social2_cat1.getId(), social1_cat1.getId()); // ID 역순 정렬

			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.results()).hasSize(2);
				softly.assertThat(response.results())
					.extracting(SocialResponse::socialId)
					.containsExactlyElementsOf(expectedIds);
			});
		}

		@Test
		void 검색시_유효하지_않은_카테고리ID가_포함되면_예외가_발생한다() {
			// given
			String keyword = "테스트";
			Long cursor = null;
			Integer limit = 10;
			List<Long> invalidCategoryIds = List.of(category1Id, -999L); // 유효하지 않은 ID 포함

			// when & then
			assertThatThrownBy(() -> socialBoardUseCase.searchSocials(
				USER.getId(), keyword, cursor, limit, invalidCategoryIds))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY.getMessage());
		}

		@Test
		void 키워드와_카테고리가_모두_일치하는_게시글이_없으면_빈_결과를_반환한다() {
			// given
			String keyword = "존재하지않는키워드";

			Social social1_cat1 = testFixtureBuilder.buildSocial(SINGLE_SOCIAL_WITH_CONTENT(USER, "오늘의 운동 루틴"));
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(0), social1_cat1);

			Long cursor = null;
			Integer limit = 10;
			List<Long> filterCategoryIds = List.of(category1Id);

			// when
			CursorPaginationResponse<SocialResponse> response = socialBoardUseCase.searchSocials(
				USER.getId(),
				keyword,
				cursor,
				limit,
				filterCategoryIds
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.results()).isEmpty();
				softly.assertThat(response.hasMore()).isFalse();
				softly.assertThat(response.nextCursor()).isNull();
			});
		}

		@Test
		void 여러_카테고리_ID를_모두_만족하는_게시글을_검색한다() {
			// given
			// 게시글 1: 카테고리 1, 2
			Social social1 = testFixtureBuilder.buildSocial(SINGLE_SOCIAL_WITH_CONTENT(USER, "운동과 식단"));
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(0), social1);
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(1), social1);

			// 게시글 2: 카테고리 1
			Social social2 = testFixtureBuilder.buildSocial(SINGLE_SOCIAL_WITH_CONTENT(USER, "운동"));
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(0), social2);

			// 게시글 3: 카테고리 2
			Social social3 = testFixtureBuilder.buildSocial(SINGLE_SOCIAL_WITH_CONTENT(USER, "식단"));
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(1), social3);

			String keyword = null;
			Long cursor = null;
			Integer limit = 10;
			List<Long> filterCategoryIds = List.of(category1Id, category2Id); // 카테고리 1과 2를 모두 만족해야 함

			// when
			CursorPaginationResponse<SocialResponse> response = socialBoardUseCase.searchSocials(
				USER.getId(),
				keyword,
				cursor,
				limit,
				filterCategoryIds
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.results()).hasSize(1);
				softly.assertThat(response.results())
					.extracting(SocialResponse::socialId)
					.containsExactlyInAnyOrder(social1.getId());
			});
		}

		@Test
		void 키워드_검색과_여러_카테고리_ID를_모두_만족하는_게시글을_검색한다() {
			// given
			// 게시글 1: 카테고리 1, 2, "운동 식단" 포함
			Social social1 = testFixtureBuilder.buildSocial(SINGLE_SOCIAL_WITH_CONTENT(USER, "오늘의 운동 식단 관리"));
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(0), social1);
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(1), social1);

			// 게시글 2: 카테고리 1, "운동" 포함
			Social social2 = testFixtureBuilder.buildSocial(SINGLE_SOCIAL_WITH_CONTENT(USER, "즐거운 운동 시간"));
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(0), social2);

			// 게시글 3: 카테고리 2, "식단" 포함
			Social social3 = testFixtureBuilder.buildSocial(SINGLE_SOCIAL_WITH_CONTENT(USER, "건강한 식단 레시피"));
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(1), social3);

			// 게시글 4: 카테고리 1, 2, "운동" 포함
			Social social4 = testFixtureBuilder.buildSocial(SINGLE_SOCIAL_WITH_CONTENT(USER, "힘든 웨이트 운동"));
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(0), social4);
			testFixtureBuilder.buildSocialCategoryLinks(categories.get(1), social4);

			String keyword = "운동";
			Long cursor = null;
			Integer limit = 10;
			List<Long> filterCategoryIds = List.of(category1Id, category2Id); // 카테고리 1과 2를 모두 만족해야 함

			// when
			CursorPaginationResponse<SocialResponse> response = socialBoardUseCase.searchSocials(
				USER.getId(),
				keyword,
				cursor,
				limit,
				filterCategoryIds
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.results()).hasSize(2);
				softly.assertThat(response.results())
					.extracting(SocialResponse::socialId)
					.containsExactlyInAnyOrder(social1.getId(), social4.getId());
			});
		}
	}

}

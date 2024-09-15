package im.toduck.domain.social.domain.usecase;

import static im.toduck.fixtures.social.CommentFixtures.*;
import static im.toduck.fixtures.social.LikeFixtures.*;
import static im.toduck.fixtures.social.SocialCategoryFixtures.*;
import static im.toduck.fixtures.social.SocialFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.entity.SocialCategoryLink;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.social.persistence.repository.CommentRepository;
import im.toduck.domain.social.persistence.repository.LikeRepository;
import im.toduck.domain.social.persistence.repository.SocialCategoryLinkRepository;
import im.toduck.domain.social.persistence.repository.SocialImageFileRepository;
import im.toduck.domain.social.persistence.repository.SocialRepository;
import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialUpdateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentCreateResponse;
import im.toduck.domain.social.presentation.dto.response.LikeCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialDetailResponse;
import im.toduck.domain.social.presentation.dto.response.SocialImageDto;
import im.toduck.domain.social.presentation.dto.response.SocialResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.fixtures.social.SocialImageFileFixtures;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.exception.VoException;
import im.toduck.global.presentation.dto.response.CursorPaginationResponse;

public class SocialUseCaseTest extends ServiceTest {
	private User USER;

	@Autowired
	private SocialUseCase socialUseCase;

	@Autowired
	private SocialRepository socialRepository;

	@Autowired
	private SocialImageFileRepository socialImageFileRepository;

	@Autowired
	private SocialCategoryLinkRepository socialCategoryLinkRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private LikeRepository likeRepository;

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
			content,
			isAnonymous,
			categoryIds,
			imageUrls
		);

		@Test
		void 주어진_요청에_따라_게시글을_생성할_수_있다() {
			// when
			SocialCreateResponse response = socialUseCase.createSocialBoard(USER.getId(), request);

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
			assertThatThrownBy(() -> socialUseCase.createSocialBoard(nonExistentUserId, request))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}

		@Test
		void 소셜_카테고리_ID가_유효하지_않는_경우_예외가_발생한다() {
			// given
			List<Long> invalidCategoryIds = List.of(1L, -1L);
			SocialCreateRequest invalidRequest = new SocialCreateRequest(
				content, isAnonymous, invalidCategoryIds, imageUrls
			);

			// when & then
			assertThatThrownBy(() -> socialUseCase.createSocialBoard(USER.getId(), invalidRequest))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY.getMessage());
		}

	}

	@Nested
	@DisplayName("댓글 작성시")
	class CreateComment {
		Social SOCIAL_BOARD;
		String commentContent = "This is a test comment.";

		CommentCreateRequest request = new CommentCreateRequest(commentContent);

		@BeforeEach
		public void setUp() {
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, false));
		}

		@Test
		void 주어진_요청에_따라_댓글을_생성할_수_있다() {
			// when
			CommentCreateResponse response = socialUseCase.createComment(USER.getId(), SOCIAL_BOARD.getId(), request);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.commentId()).isNotNull();
			});
		}

		@Test
		void 사용자를_조회할_수_없는_경우_댓글_생성에_실패한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(() -> socialUseCase.createComment(nonExistentUserId, SOCIAL_BOARD.getId(), request))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}

		@Test
		void 소셜_게시글이_존재하지_않는_경우_댓글_생성에_실패한다() {
			// given
			Long nonExistentSocialBoardId = -1L;

			// when & then
			assertThatThrownBy(() -> socialUseCase.createComment(USER.getId(), nonExistentSocialBoardId, request))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 댓글_내용이_빈_경우_댓글_생성에_실패한다() {
			// given
			String emptyContent = "";
			CommentCreateRequest emptyRequest = new CommentCreateRequest(emptyContent);

			// when & then
			assertThatThrownBy(() -> socialUseCase.createComment(USER.getId(), SOCIAL_BOARD.getId(), emptyRequest))
				.isInstanceOf(VoException.class)
				.hasMessage("댓글 내용은 비어 있을 수 없습니다.");
		}

		@Test
		void 댓글_내용이_공백인_경우_댓글_생성에_실패한다() {
			// given
			String whitespaceContent = "   ";
			CommentCreateRequest whitespaceRequest = new CommentCreateRequest(whitespaceContent);

			// when & then
			assertThatThrownBy(() -> socialUseCase.createComment(USER.getId(), SOCIAL_BOARD.getId(), whitespaceRequest))
				.isInstanceOf(VoException.class)
				.hasMessage("댓글 내용은 비어 있을 수 없습니다.");
		}
	}

	@Nested
	@DisplayName("게시글 댓글 삭제시")
	class DeleteComment {
		Social SOCIAL_BOARD;
		Comment COMMENT;

		@BeforeEach
		void setUp() {
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, false));
			COMMENT = testFixtureBuilder.buildComment(SINGLE_COMMENT(USER, SOCIAL_BOARD));
		}

		@Test
		@Transactional
		void 게시글이_삭제되면_댓글은_soft_delete_된다() {
			// when
			socialUseCase.deleteSocialBoard(USER.getId(), SOCIAL_BOARD.getId());

			// then
			Optional<Comment> softDeletedComment = commentRepository.findById(COMMENT.getId());
			assertSoftly(softly -> {
				softly.assertThat(softDeletedComment).isPresent();
				softDeletedComment.ifPresent(value -> softly.assertThat(value.getDeletedAt()).isNotNull());

			});
		}

		@Test
		void 댓글이_직접_삭제되면_hard_delete_된다() {
			// when
			socialUseCase.deleteComment(USER.getId(), SOCIAL_BOARD.getId(), COMMENT.getId());

			// then
			assertThat(commentRepository.findById(COMMENT.getId())).isNotPresent();
		}

		@Test
		void 사용자를_조회할_수_없는_경우_댓글_삭제에_실패한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(
				() -> socialUseCase.deleteComment(nonExistentUserId, SOCIAL_BOARD.getId(), COMMENT.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}

		@Test
		void 게시글이_존재하지_않는_경우_댓글_삭제에_실패한다() {
			// given
			Long nonExistentSocialBoardId = -1L;

			// when & then
			assertThatThrownBy(
				() -> socialUseCase.deleteComment(USER.getId(), nonExistentSocialBoardId, COMMENT.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 댓글이_존재하지_않는_경우_댓글_삭제에_실패한다() {
			// given
			Long nonExistentCommentId = -1L;

			// when & then
			assertThatThrownBy(
				() -> socialUseCase.deleteComment(USER.getId(), SOCIAL_BOARD.getId(), nonExistentCommentId))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_COMMENT.getMessage());
		}

		@Test
		void 댓글의_소유자가_아닌_경우_삭제에_실패한다() {
			// given
			User ANOTHER_USER = testFixtureBuilder.buildUser(GENERAL_USER());
			Comment ANOTHER_USER_COMMENT = testFixtureBuilder.buildComment(
				SINGLE_COMMENT(ANOTHER_USER, SOCIAL_BOARD)
			);

			// when & then
			assertThatThrownBy(
				() -> socialUseCase.deleteComment(USER.getId(), SOCIAL_BOARD.getId(), ANOTHER_USER_COMMENT.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.UNAUTHORIZED_ACCESS_COMMENT.getMessage());
		}

		@Test
		void 댓글이_게시글에_속하지_않는_경우_삭제에_실패한다() {
			// given
			Social ANOTHER_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, false));
			Comment ANOTHER_BOARD_COMMENT = testFixtureBuilder.buildComment(SINGLE_COMMENT(USER, ANOTHER_BOARD));

			// when & then
			assertThatThrownBy(
				() -> socialUseCase.deleteComment(USER.getId(), SOCIAL_BOARD.getId(), ANOTHER_BOARD_COMMENT.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.INVALID_COMMENT_FOR_BOARD.getMessage());
		}
	}

	@Nested
	@DisplayName("게시글 삭제시")
	class DeleteSocialBoard {

		Social SOCIAL_BOARD;

		@BeforeEach
		void setUp() {
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, false));
		}

		@Test
		void 주어진_요청에_따라_게시글을_삭제할_수_있다() {
			// when
			socialUseCase.deleteSocialBoard(USER.getId(), SOCIAL_BOARD.getId());

			// then
			assertThat(socialRepository.findById(SOCIAL_BOARD.getId())).isEmpty();
		}

		@Test
		void 사용자를_조회할_수_없는_경우_게시글_삭제에_실패한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(() -> socialUseCase.deleteSocialBoard(nonExistentUserId, SOCIAL_BOARD.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}

		@Test
		void 게시글이_존재하지_않는_경우_삭제에_실패한다() {
			// given
			Long nonExistentSocialBoardId = -1L;

			// when & then
			assertThatThrownBy(() -> socialUseCase.deleteSocialBoard(USER.getId(), nonExistentSocialBoardId))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 게시글의_소유자가_아닌_경우_삭제에_실패한다() {
			// given
			User ANOTHER_USER = testFixtureBuilder.buildUser(GENERAL_USER());
			Long socialBoardId = SOCIAL_BOARD.getId();

			// when & then
			assertThatThrownBy(() -> socialUseCase.deleteSocialBoard(ANOTHER_USER.getId(), socialBoardId))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD.getMessage());
		}
	}

	@Nested
	@DisplayName("게시글 수정시")
	class UpdateSocialBoard {
		Social SOCIAL_BOARD;
		String updateContent = "This is a test update.";
		Boolean isAnonymous = true;
		List<SocialCategory> categories = testFixtureBuilder.buildCategories(MULTIPLE_CATEGORIES(2));
		List<Long> validCategoryIds = List.of(categories.get(0).getId(), categories.get(1).getId());
		List<Long> invalidCategoryIds = List.of(-1L);
		List<String> imageUrls = List.of("updatedImage1.jpg", "updatedImage2.jpg");

		@BeforeEach
		void setUp() {
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, false));
		}

		// 여러 케이스를 위한 데이터 제공 메소드
		static Stream<Arguments> provideUpdateRequests() {
			return Stream.of(
				Arguments.of(null, null, null, null),
				Arguments.of(null, null, null, List.of()),
				Arguments.of(null, null, null, List.of("updatedImage.jpg")),
				Arguments.of(null, null, List.of(1L), null),
				Arguments.of(null, null, List.of(1L), List.of("updatedIm age.jpg")),
				Arguments.of(null, true, null, null),
				Arguments.of(null, false, null, List.of("updatedIm age.jpg")),
				Arguments.of(null, false, List.of(1L), null),
				Arguments.of(null, false, List.of(1L), List.of("updatedImage.jpg")),
				Arguments.of("Updated Content", null, null, null),
				Arguments.of("Updated Content", null, null, List.of("updatedImage.jpg")),
				Arguments.of("Updated Content", null, List.of(1L), null),
				Arguments.of("Updated Content", null, List.of(1L), List.of("updatedImage.jpg")),
				Arguments.of("Updated Content", true, null, null),
				Arguments.of("Updated Content", true, null, List.of()),
				Arguments.of("Updated Content", true, List.of(1L), null),
				Arguments.of("Updated Content", true, List.of(1L), List.of("updatedImage.jpg"))
			);
		}

		@ParameterizedTest
		@MethodSource("provideUpdateRequests")
		void 주어진_요청에_따라_게시글을_수정할_수_있다(
			String updatedContent,
			Boolean updatedIsAnonymous,
			List<Long> updatedCategoryIds,
			List<String> updatedImageUrls
		) {
			// given
			String originalContent = SOCIAL_BOARD.getContent();
			Boolean originalIsAnonymous = SOCIAL_BOARD.getIsAnonymous();
			List<SocialImageFile> originalImages = socialImageFileRepository.findAllBySocial(SOCIAL_BOARD);
			List<SocialCategoryLink> originalCategories = socialCategoryLinkRepository.findAllBySocial(SOCIAL_BOARD);

			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				updatedContent,
				updatedIsAnonymous,
				updatedCategoryIds,
				updatedImageUrls
			);

			// when
			socialUseCase.updateSocialBoard(USER.getId(), SOCIAL_BOARD.getId(), updateRequest);

			// then
			Social updatedSocialBoard = socialRepository.findById(SOCIAL_BOARD.getId()).orElseThrow();
			assertSoftly(softly -> {
				if (updatedContent == null) {
					softly.assertThat(updatedSocialBoard.getContent()).isEqualTo(originalContent);
				} else {
					softly.assertThat(updatedSocialBoard.getContent()).isEqualTo(updatedContent);
				}

				if (updatedIsAnonymous == null) {
					softly.assertThat(updatedSocialBoard.getIsAnonymous()).isEqualTo(originalIsAnonymous);
				} else {
					softly.assertThat(updatedSocialBoard.getIsAnonymous()).isEqualTo(updatedIsAnonymous);
				}

				if (updatedImageUrls == null) {
					softly.assertThat(socialImageFileRepository.findAllBySocial(updatedSocialBoard))
						.hasSize(originalImages.size())
						.extracting(SocialImageFile::getUrl)
						.containsExactlyInAnyOrderElementsOf(
							originalImages.stream().map(SocialImageFile::getUrl).toList()
						);
				} else {
					softly.assertThat(socialImageFileRepository.findAllBySocial(updatedSocialBoard))
						.hasSize(updatedImageUrls.size())
						.extracting(SocialImageFile::getUrl)
						.containsAll(updatedImageUrls);
				}

				if (updatedCategoryIds == null) {
					softly.assertThat(socialCategoryLinkRepository.findAllBySocial(updatedSocialBoard))
						.hasSize(originalCategories.size())
						.extracting(link -> link.getSocialCategory().getId())
						.containsExactlyInAnyOrderElementsOf(
							originalCategories.stream().map(link -> link.getSocialCategory().getId()).toList()
						);
				} else {
					softly.assertThat(socialCategoryLinkRepository.findAllBySocial(updatedSocialBoard))
						.hasSize(updatedCategoryIds.size())
						.extracting(link -> link.getSocialCategory().getId())
						.containsAll(updatedCategoryIds);
				}
			});
		}

		@Test
		void 존재하지_않는_게시글을_수정하려고_하면_예외가_발생한다() {
			// given
			Long nonExistentSocialBoardId = -1L;
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				updateContent,
				isAnonymous,
				validCategoryIds,
				imageUrls
			);

			// when & then
			assertThatThrownBy(
				() -> socialUseCase.updateSocialBoard(USER.getId(), nonExistentSocialBoardId, updateRequest))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 게시글_소유자가_아닌_경우_수정에_실패한다() {
			// given
			User ANOTHER_USER = testFixtureBuilder.buildUser(GENERAL_USER());
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				updateContent,
				isAnonymous,
				validCategoryIds,
				imageUrls
			);

			// when & then
			assertThatThrownBy(
				() -> socialUseCase.updateSocialBoard(ANOTHER_USER.getId(), SOCIAL_BOARD.getId(), updateRequest))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 유효하지_않은_카테고리_ID가_포함된_경우_수정에_실패한다() {
			// given
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				updateContent,
				isAnonymous,
				invalidCategoryIds,
				imageUrls
			);

			// when & then
			assertThatThrownBy(() -> socialUseCase.updateSocialBoard(USER.getId(), SOCIAL_BOARD.getId(), updateRequest))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY.getMessage());
		}

		@Test
		void 카테고리_ID_리스트가_빈_경우_수정에_실패한다() {
			// given
			SocialUpdateRequest updateRequest = new SocialUpdateRequest(
				updateContent,
				isAnonymous,
				List.of(),
				imageUrls
			);

			// when & then
			assertThatThrownBy(() -> socialUseCase.updateSocialBoard(USER.getId(), SOCIAL_BOARD.getId(), updateRequest))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.EMPTY_SOCIAL_CATEGORY_LIST.getMessage());
		}
	}

	@Nested
	@DisplayName("게시글 좋아요 생성시")
	class CreateLikeTest {
		Social SOCIAL_BOARD;

		@BeforeEach
		void setUp() {
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, false));
		}

		@Test
		void 게시글에_좋아요를_성공적으로_생성한다() {
			// given
			int beforeLikeCount = SOCIAL_BOARD.getLikeCount();

			// when
			LikeCreateResponse response = socialUseCase.createLike(USER.getId(), SOCIAL_BOARD.getId());

			// then
			Like createdLike = likeRepository.findByUserAndSocial(USER, SOCIAL_BOARD).orElseThrow();
			Optional<Social> afterSocialBoard = socialRepository.findById(SOCIAL_BOARD.getId());
			assertSoftly(softly -> {
				assertThat(createdLike).isNotNull();
				assertThat(response.likeId()).isNotNull();
				afterSocialBoard.ifPresent(
					social -> softly.assertThat(social.getLikeCount()).isEqualTo(beforeLikeCount + 1)
				);

			});
		}

		@Test
		void 이미_좋아요를_누른_경우_예외를_발생시킨다() {
			// given
			LikeCreateResponse response = socialUseCase.createLike(USER.getId(), SOCIAL_BOARD.getId());

			// when & then
			assertThatThrownBy(() -> socialUseCase.createLike(USER.getId(), SOCIAL_BOARD.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.EXISTS_LIKE.getMessage());
		}

		@Test
		void 존재하지_않는_게시글에_좋아요를_누를_경우_예외를_발생시킨다() {
			// given
			Long nonExistentSocialId = -1L;

			// when & then
			assertThatThrownBy(() -> socialUseCase.createLike(USER.getId(), nonExistentSocialId))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}
	}

	@Nested
	@DisplayName("게시글 좋아요 취소시")
	class DeleteLikeTest {
		Social SOCIAL_BOARD;

		@BeforeEach
		void setUp() {
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, false));
		}

		@Test
		void 게시글에_좋아요를_성공적으로_취소한다() {
			// given
			Like LIKE = testFixtureBuilder.buildLike(LIKE(USER, SOCIAL_BOARD));

			// when
			socialUseCase.deleteLike(USER.getId(), SOCIAL_BOARD.getId());

			// then
			assertThat(likeRepository.findByUserAndSocial(USER, SOCIAL_BOARD)).isNotPresent();
			assertThat(SOCIAL_BOARD.getLikeCount()).isEqualTo(0);
		}

		@Test
		void 좋아요가_존재하지_않는_경우_예외를_발생시킨다() {

			// when & then
			assertThatThrownBy(() -> socialUseCase.deleteLike(USER.getId(), SOCIAL_BOARD.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_LIKE.getMessage());
		}

		@Test
		void 존재하지_않는_게시글에_좋아요_취소를_시도하면_예외를_발생시킨다() {
			// given
			Long nonExistentSocialId = -1L;

			// when & then
			assertThatThrownBy(() -> socialUseCase.deleteLike(USER.getId(), nonExistentSocialId))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}
	}

	@Nested
	@DisplayName("게시글 단건 조회시")
	class GetSocialDetail {
		Social SOCIAL_BOARD;
		Like LIKE;
		List<SocialImageFile> IMAGE_FILES;
		List<String> imageUrls = List.of("image1.jpg", "image2.jpg");

		@BeforeEach
		void setUp() {
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, false));
			LIKE = testFixtureBuilder.buildLike(LIKE(USER, SOCIAL_BOARD));
			IMAGE_FILES = testFixtureBuilder.buildSocialImageFiles(
				SocialImageFileFixtures.MULTIPLE_IMAGE_FILES(SOCIAL_BOARD, imageUrls)
			);

		}

		@Test
		void 게시글_단건_조회에_성공한다() {
			// when
			SocialDetailResponse response = socialUseCase.getSocialDetail(USER.getId(), SOCIAL_BOARD.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.id()).isEqualTo(SOCIAL_BOARD.getId());
				softly.assertThat(response.content()).isEqualTo(SOCIAL_BOARD.getContent());
				softly.assertThat(response.hasImages()).isTrue();
				softly.assertThat(response.likeInfo().isLiked()).isTrue();
				softly.assertThat(response.images())
					.hasSize(IMAGE_FILES.size())
					.extracting(SocialImageDto::url)
					.containsExactlyInAnyOrderElementsOf(
						IMAGE_FILES.stream().map(SocialImageFile::getUrl).toList()
					);
			});
		}

		@Test
		void 게시글이_없는_경우_단건_조회에_실패_한다() {
			// given
			Long nonExistentSocialId = -1L;

			// when & then
			assertThatThrownBy(() -> socialUseCase.getSocialDetail(USER.getId(), nonExistentSocialId))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 소유자를_찾을_수_없는_경우_게시글_단건_조회에_실패_한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(() -> socialUseCase.getSocialDetail(nonExistentUserId, SOCIAL_BOARD.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}
	}

	@Nested
	@DisplayName("게시글 목록 조회시")
	class GetSocials {

		@Test
		void 게시글_목록을_성공적으로_조회한다() {
			// given
			int numberOfPosts = 15;
			List<Social> socials = testFixtureBuilder.buildSocials(MULTIPLE_SOCIALS(USER, numberOfPosts));
			Long cursor = null;
			Integer limit = 10;

			// when
			CursorPaginationResponse<SocialResponse> response = socialUseCase.getSocials(USER.getId(), cursor, limit);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.results()).hasSize(limit);
				softly.assertThat(response.hasMore()).isTrue();
				softly.assertThat(response.nextCursor()).isNotNull();
			});
		}

		@Test
		void 존재하지_않는_사용자일_경우_조회에_실패한다() {
			// given
			Long nonExistentUserId = -1L;
			Long cursor = null;
			Integer limit = 10;

			// when & then
			assertThatThrownBy(() -> socialUseCase.getSocials(nonExistentUserId, cursor, limit))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}
	}

}
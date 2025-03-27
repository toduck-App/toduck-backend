package im.toduck.domain.social.domain.usecase;

import static im.toduck.fixtures.social.CommentFixtures.*;
import static im.toduck.fixtures.social.CommentLikeFixtures.*;
import static im.toduck.fixtures.social.LikeFixtures.*;
import static im.toduck.fixtures.social.SocialFixtures.*;
import static im.toduck.fixtures.user.BlockFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.ServiceTest;
import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.CommentImageFile;
import im.toduck.domain.social.persistence.entity.CommentLike;
import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.ReportType;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.repository.CommentImageFileRepository;
import im.toduck.domain.social.persistence.repository.CommentLikeRepository;
import im.toduck.domain.social.persistence.repository.CommentRepository;
import im.toduck.domain.social.persistence.repository.LikeRepository;
import im.toduck.domain.social.persistence.repository.SocialRepository;
import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.request.ReportCreateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentCreateResponse;
import im.toduck.domain.social.presentation.dto.response.CommentLikeCreateResponse;
import im.toduck.domain.social.presentation.dto.response.ReportCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialLikeCreateResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.fixtures.social.CommentFixtures;
import im.toduck.fixtures.user.UserFixtures;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.exception.VoException;

public class SocialInteractionUseCaseTest extends ServiceTest {

	private User USER;
	private Social SOCIAL_BOARD;
	private Comment PARENT_COMMENT;

	@Autowired
	private SocialInteractionUseCase socialInteractionUseCase;

	@Autowired
	private SocialRepository socialRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private CommentLikeRepository commentLikeRepository;

	@Autowired
	private CommentImageFileRepository commentImageFileRepository;

	@BeforeEach
	public void setUp() {
		USER = testFixtureBuilder.buildUser(GENERAL_USER());
		SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, false));
		PARENT_COMMENT = testFixtureBuilder.buildComment(SINGLE_COMMENT(USER, SOCIAL_BOARD));
	}

	@Nested
	@DisplayName("댓글 작성시")
	class CreateComment {
		Social SOCIAL_BOARD;
		String commentContent = "This is a test comment.";

		CommentCreateRequest request = new CommentCreateRequest(commentContent, null, null);

		@BeforeEach
		public void setUp() {
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, false));
		}

		@Test
		void 주어진_요청에_따라_댓글을_생성할_수_있다() {
			// when
			CommentCreateResponse response = socialInteractionUseCase.createComment(USER.getId(), SOCIAL_BOARD.getId(),
				request);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.commentId()).isNotNull();
			});
		}

		@Test
		void 주어진_요청에_따라_대댓글을_생성할_수_있다() {
			// given
			CommentCreateRequest replyCommentRequest = new CommentCreateRequest(
				commentContent,
				PARENT_COMMENT.getId(),
				null
			);

			// when
			CommentCreateResponse response = socialInteractionUseCase.createComment(
				USER.getId(),
				SOCIAL_BOARD.getId(),
				replyCommentRequest
			);

			// then
			Comment savedReplyComment = commentRepository.findById(response.commentId()).orElseThrow();
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.commentId()).isNotNull();
				softly.assertThat(savedReplyComment.getParent().getId()).isEqualTo(PARENT_COMMENT.getId());
			});
		}

		@Test
		void 사용자를_조회할_수_없는_경우_댓글_생성에_실패한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(
				() -> socialInteractionUseCase.createComment(nonExistentUserId, SOCIAL_BOARD.getId(), request))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}

		@Test
		void 소셜_게시글이_존재하지_않는_경우_댓글_생성에_실패한다() {
			// given
			Long nonExistentSocialBoardId = -1L;

			// when & then
			assertThatThrownBy(
				() -> socialInteractionUseCase.createComment(USER.getId(), nonExistentSocialBoardId, request))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 댓글_내용이_빈_경우_댓글_생성에_실패한다() {
			// given
			String emptyContent = "";
			CommentCreateRequest emptyRequest = new CommentCreateRequest(emptyContent, null, null);

			// when & then
			assertThatThrownBy(
				() -> socialInteractionUseCase.createComment(USER.getId(), SOCIAL_BOARD.getId(), emptyRequest))
				.isInstanceOf(VoException.class)
				.hasMessage("댓글 내용은 비어 있을 수 없습니다.");
		}

		@Test
		void 댓글_내용이_공백인_경우_댓글_생성에_실패한다() {
			// given
			String whitespaceContent = "   ";
			CommentCreateRequest whitespaceRequest = new CommentCreateRequest(whitespaceContent, null, null);

			// when & then
			assertThatThrownBy(
				() -> socialInteractionUseCase.createComment(USER.getId(), SOCIAL_BOARD.getId(), whitespaceRequest))
				.isInstanceOf(VoException.class)
				.hasMessage("댓글 내용은 비어 있을 수 없습니다.");
		}

		@Test
		void 대댓글을_부모_댓글로_사용하려는_경우_댓글_생성에_실패한다() {
			// given
			Comment parentComment = testFixtureBuilder.buildComment(
				CommentFixtures.SINGLE_COMMENT(USER, SOCIAL_BOARD));
			Comment replyToParent = testFixtureBuilder.buildComment(
				CommentFixtures.REPLY_COMMENT(USER, SOCIAL_BOARD, parentComment));

			CommentCreateRequest invalidParentRequest = new CommentCreateRequest(
				commentContent,
				replyToParent.getId(),
				null
			);

			// when & then
			assertThatThrownBy(() -> socialInteractionUseCase.createComment(
				USER.getId(),
				SOCIAL_BOARD.getId(),
				invalidParentRequest
			))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.INVALID_PARENT_COMMENT.getMessage());
		}

		@Test
		void 이미지가_포함된_댓글을_작성할_수_있다() {
			// given
			String imageUrl = "https://cdn.toduck.app/test-image.jpg";
			CommentCreateRequest requestWithImage = new CommentCreateRequest(
				commentContent,
				null,
				imageUrl
			);

			// when
			CommentCreateResponse response = socialInteractionUseCase.createComment(
				USER.getId(),
				SOCIAL_BOARD.getId(),
				requestWithImage
			);

			// then
			Comment savedComment = commentRepository.findById(response.commentId()).orElseThrow();
			CommentImageFile savedCommentImage = commentImageFileRepository.findByComment(savedComment).orElseThrow();

			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.commentId()).isNotNull();
				softly.assertThat(savedCommentImage.getUrl()).isEqualTo(imageUrl);
				softly.assertThat(savedComment.getContent().getValue()).isEqualTo(commentContent);
			});
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
		void 댓글이_직접_삭제되면_hard_delete_된다() {
			// when
			socialInteractionUseCase.deleteComment(USER.getId(), SOCIAL_BOARD.getId(), COMMENT.getId());

			// then
			assertThat(commentRepository.findById(COMMENT.getId())).isNotPresent();
		}

		@Test
		void 사용자를_조회할_수_없는_경우_댓글_삭제에_실패한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(
				() -> socialInteractionUseCase.deleteComment(nonExistentUserId, SOCIAL_BOARD.getId(), COMMENT.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}

		@Test
		void 게시글이_존재하지_않는_경우_댓글_삭제에_실패한다() {
			// given
			Long nonExistentSocialBoardId = -1L;

			// when & then
			assertThatThrownBy(
				() -> socialInteractionUseCase.deleteComment(USER.getId(), nonExistentSocialBoardId, COMMENT.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 댓글이_존재하지_않는_경우_댓글_삭제에_실패한다() {
			// given
			Long nonExistentCommentId = -1L;

			// when & then
			assertThatThrownBy(
				() -> socialInteractionUseCase.deleteComment(USER.getId(), SOCIAL_BOARD.getId(), nonExistentCommentId))
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
				() -> socialInteractionUseCase.deleteComment(USER.getId(), SOCIAL_BOARD.getId(),
					ANOTHER_USER_COMMENT.getId()))
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
				() -> socialInteractionUseCase.deleteComment(USER.getId(), SOCIAL_BOARD.getId(),
					ANOTHER_BOARD_COMMENT.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.INVALID_COMMENT_FOR_BOARD.getMessage());
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
			SocialLikeCreateResponse response = socialInteractionUseCase.createSocialLike(USER.getId(),
				SOCIAL_BOARD.getId());

			// then
			Like createdLike = likeRepository.findByUserAndSocial(USER, SOCIAL_BOARD).orElseThrow();
			Optional<Social> afterSocialBoard = socialRepository.findById(SOCIAL_BOARD.getId());
			assertSoftly(softly -> {
				assertThat(createdLike).isNotNull();
				assertThat(response.socialLikeId()).isNotNull();
				afterSocialBoard.ifPresent(
					social -> softly.assertThat(social.getLikeCount()).isEqualTo(beforeLikeCount + 1)
				);

			});
		}

		@Test
		void 이미_좋아요를_누른_경우_예외를_발생시킨다() {
			// given
			SocialLikeCreateResponse response = socialInteractionUseCase.createSocialLike(USER.getId(),
				SOCIAL_BOARD.getId());

			// when & then
			assertThatThrownBy(() -> socialInteractionUseCase.createSocialLike(USER.getId(), SOCIAL_BOARD.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.EXISTS_LIKE.getMessage());
		}

		@Test
		void 존재하지_않는_게시글에_좋아요를_누를_경우_예외를_발생시킨다() {
			// given
			Long nonExistentSocialId = -1L;

			// when & then
			assertThatThrownBy(() -> socialInteractionUseCase.createSocialLike(USER.getId(), nonExistentSocialId))
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
			socialInteractionUseCase.deleteSocialLike(USER.getId(), SOCIAL_BOARD.getId());

			// then
			assertThat(likeRepository.findByUserAndSocial(USER, SOCIAL_BOARD)).isNotPresent();
			assertThat(SOCIAL_BOARD.getLikeCount()).isEqualTo(0);
		}

		@Test
		void 좋아요가_존재하지_않는_경우_예외를_발생시킨다() {

			// when & then
			assertThatThrownBy(() -> socialInteractionUseCase.deleteSocialLike(USER.getId(), SOCIAL_BOARD.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_LIKE.getMessage());
		}

		@Test
		void 존재하지_않는_게시글에_좋아요_취소를_시도하면_예외를_발생시킨다() {
			// given
			Long nonExistentSocialId = -1L;

			// when & then
			assertThatThrownBy(() -> socialInteractionUseCase.deleteSocialLike(USER.getId(), nonExistentSocialId))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}
	}

	@Nested
	class ReportSocialTest {

		User OTHER_USER;
		Social SOCIAL_BOARD;

		@BeforeEach
		void setUp() {
			OTHER_USER = testFixtureBuilder.buildUser(GENERAL_USER());
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(OTHER_USER, false));
		}

		@Test
		void 게시글을_성공적으로_신고한다() {
			// given
			ReportCreateRequest request = new ReportCreateRequest(ReportType.OTHER, "부적절한 내용입니다.", true);

			// when
			ReportCreateResponse response = socialInteractionUseCase.reportSocial(USER.getId(), SOCIAL_BOARD.getId(),
				request);

			// then
			assertThat(response.reportId()).isNotNull();
		}

		@Test
		void 자신의_게시글을_신고하려고_하면_예외를_발생시킨다() {
			// given
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, true));

			ReportCreateRequest request = new ReportCreateRequest(ReportType.OTHER, "부적절한 내용입니다.", true);

			// when & then
			assertThatThrownBy(() -> socialInteractionUseCase.reportSocial(USER.getId(), SOCIAL_BOARD.getId(), request))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.CANNOT_REPORT_OWN_POST.getMessage());
		}

		@Test
		void 이미_신고된_게시글을_다시_신고하면_예외가_발생한다() {
			// given
			ReportCreateRequest request = new ReportCreateRequest(ReportType.OTHER, "부적절한 내용입니다.", true);
			socialInteractionUseCase.reportSocial(USER.getId(), SOCIAL_BOARD.getId(), request);

			// when & then
			assertThatThrownBy(() -> socialInteractionUseCase.reportSocial(USER.getId(), SOCIAL_BOARD.getId(), request))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.ALREADY_REPORTED.getMessage());
		}

		@Test
		void 존재하지_않는_게시글을_신고하면_예외가_발생한다() {
			// given
			Long nonExistentSocialId = -1L;
			ReportCreateRequest request = new ReportCreateRequest(ReportType.OTHER, "부적절한 내용입니다.", true);

			// when & then
			assertThatThrownBy(() -> socialInteractionUseCase.reportSocial(USER.getId(), nonExistentSocialId, request))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 게시글_작성자가_이미_차단된_경우_예외를_발생시키지_않는다() {
			// given
			User BLOCK_USER = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
			testFixtureBuilder.buildBlock(BLOCK_USER(USER, BLOCK_USER));
			Social BLOCKED_USER_SOCIAL = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(BLOCK_USER, false));

			ReportCreateRequest request = new ReportCreateRequest(ReportType.OTHER, "부적절한 내용입니다.", true);

			// when
			ReportCreateResponse response =
				socialInteractionUseCase.reportSocial(USER.getId(), BLOCKED_USER_SOCIAL.getId(), request);

			// then
			assertThat(response.reportId()).isNotNull();
		}
	}

	@Nested
	@DisplayName("댓글 좋아요 생성시")
	class CreateCommentLikeTest {

		Social SOCIAL_BOARD;
		Comment COMMENT;

		@BeforeEach
		void setUp() {
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, false));
			COMMENT = testFixtureBuilder.buildComment(SINGLE_COMMENT(USER, SOCIAL_BOARD));
		}

		@Test
		void 댓글에_좋아요를_성공적으로_생성한다() {
			// when
			CommentLikeCreateResponse response = socialInteractionUseCase.createCommentLike(USER.getId(),
				COMMENT.getId());

			// then
			CommentLike createdLike = commentLikeRepository.findCommentLikeByUserAndComment(USER, COMMENT)
				.orElseThrow();
			Comment afterComment = commentRepository.findById(COMMENT.getId()).orElseThrow();

			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.commentLikeId()).isNotNull();
				softly.assertThat(afterComment.getLikeCount()).isEqualTo(1);
				softly.assertThat(createdLike.getComment().getId()).isEqualTo(COMMENT.getId());
			});
		}

		@Test
		void 이미_댓글에_좋아요를_누른_경우_예외를_발생시킨다() {
			// given
			socialInteractionUseCase.createCommentLike(USER.getId(), COMMENT.getId());

			// when & then
			assertThatThrownBy(() -> socialInteractionUseCase.createCommentLike(USER.getId(), COMMENT.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.EXISTS_COMMENT_LIKE.getMessage());
		}

		@Test
		void 존재하지_않는_댓글에_좋아요를_누를_경우_예외를_발생시킨다() {
			// given
			Long nonExistentCommentId = -1L;

			// when & then
			assertThatThrownBy(() -> socialInteractionUseCase.createCommentLike(USER.getId(), nonExistentCommentId))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_COMMENT.getMessage());
		}

		@Test
		void 사용자를_조회할_수_없는_경우_댓글_좋아요에_실패한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(() -> socialInteractionUseCase.createCommentLike(nonExistentUserId, COMMENT.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}
	}

	@Nested
	@DisplayName("댓글 좋아요 삭제시")
	class DeleteCommentLikeTest {

		Social SOCIAL_BOARD;
		Comment COMMENT;

		@BeforeEach
		void setUp() {
			SOCIAL_BOARD = testFixtureBuilder.buildSocial(SINGLE_SOCIAL(USER, false));
			COMMENT = testFixtureBuilder.buildComment(SINGLE_COMMENT(USER, SOCIAL_BOARD));
		}

		@Test
		void 댓글에_좋아요를_성공적으로_삭제한다() {
			// given
			CommentLike commentLike = testFixtureBuilder.buildCommentLike(COMMENT_LIKE(USER, COMMENT));

			// when
			socialInteractionUseCase.deleteCommentLike(USER.getId(), COMMENT.getId());

			// then
			Comment afterComment = commentRepository.findById(COMMENT.getId()).orElseThrow();
			assertSoftly(softly -> {
				softly.assertThat(commentLikeRepository.findCommentLikeByUserAndComment(USER, COMMENT)).isNotPresent();
				softly.assertThat(afterComment.getLikeCount()).isEqualTo(0);
			});
		}

		@Test
		void 댓글에_좋아요가_존재하지_않는_경우_예외를_발생시킨다() {
			// when & then
			assertThatThrownBy(() -> socialInteractionUseCase.deleteCommentLike(USER.getId(), COMMENT.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_COMMENT_LIKE.getMessage());
		}

		@Test
		void 존재하지_않는_댓글에_좋아요를_삭제하려고_하면_예외를_발생시킨다() {
			// given
			Long nonExistentCommentId = -1L;

			// when & then
			assertThatThrownBy(() -> socialInteractionUseCase.deleteCommentLike(USER.getId(), nonExistentCommentId))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_COMMENT.getMessage());
		}

		@Test
		void 사용자를_조회할_수_없는_경우_댓글_좋아요_삭제에_실패한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(() -> socialInteractionUseCase.deleteCommentLike(nonExistentUserId, COMMENT.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}
	}

}

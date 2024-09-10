package im.toduck.domain.social.domain.usecase;

import static im.toduck.fixtures.UserFixtures.*;
import static im.toduck.fixtures.social.SocialCategoryFixtures.*;
import static im.toduck.fixtures.social.SocialFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.ServiceTest;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.repository.SocialRepository;
import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialCreateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialCreateResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.exception.VoException;

public class SocialUseCaseTest extends ServiceTest {

	@Autowired
	private SocialUseCase socialUseCase;

	@Autowired
	private SocialRepository socialRepository;

	private User user;
	private Social socialBoard;

	@BeforeEach
	public void setUp() {
		user = testFixtureBuilder.buildUser(GENERAL_USER());
		socialBoard = testFixtureBuilder.buildSocial(CREATE_SINGLE_SOCIAL(user, false));
	}

	@Nested
	@DisplayName("게시글 작성시")
	class CreateSocialBoard {
		String content = "Test Content";
		Boolean isAnonymous = false;
		List<String> imageUrls = List.of("image1.jpg", "image2.jpg");
		List<Long> categoryIds = testFixtureBuilder.buildCategories(CREATE_MULTIPLE_CATEGORIES(2))
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
			SocialCreateResponse response = socialUseCase.createSocialBoard(user.getId(), request);

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
			assertThatThrownBy(() -> socialUseCase.createSocialBoard(user.getId(), invalidRequest))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY.getMessage());
		}

	}

	@Nested
	@DisplayName("댓글 작성시")
	class CreateComment {
		String commentContent = "This is a test comment.";

		CommentCreateRequest request = new CommentCreateRequest(
			commentContent
		);

		@Test
		void 주어진_요청에_따라_댓글을_생성할_수_있다() {
			// when
			CommentCreateResponse response = socialUseCase.createComment(user.getId(), socialBoard.getId(), request);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.socialCommentId()).isNotNull();
			});
		}

		@Test
		void 사용자를_조회할_수_없는_경우_댓글_생성에_실패한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(() -> socialUseCase.createComment(nonExistentUserId, socialBoard.getId(), request))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}

		@Test
		void 소셜_게시글이_존재하지_않는_경우_댓글_생성에_실패한다() {
			// given
			Long nonExistentSocialBoardId = -1L;

			// when & then
			assertThatThrownBy(() -> socialUseCase.createComment(user.getId(), nonExistentSocialBoardId, request))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 댓글_내용이_빈_경우_댓글_생성에_실패한다() {
			// given
			String emptyContent = "";
			CommentCreateRequest emptyRequest = new CommentCreateRequest(emptyContent);

			// when & then
			assertThatThrownBy(() -> socialUseCase.createComment(user.getId(), socialBoard.getId(), emptyRequest))
				.isInstanceOf(VoException.class)
				.hasMessage("댓글 내용은 비어 있을 수 없습니다.");
		}

		@Test
		void 댓글_내용이_공백인_경우_댓글_생성에_실패한다() {
			// given
			String whitespaceContent = "   ";
			CommentCreateRequest whitespaceRequest = new CommentCreateRequest(whitespaceContent);

			// when & then
			assertThatThrownBy(() -> socialUseCase.createComment(user.getId(), socialBoard.getId(), whitespaceRequest))
				.isInstanceOf(VoException.class)
				.hasMessage("댓글 내용은 비어 있을 수 없습니다.");
		}
	}

	@Nested
	@DisplayName("게시글 삭제시")
	class DeleteSocialBoard {

		@Test
		void 주어진_요청에_따라_게시글을_삭제할_수_있다() {
			// when
			socialUseCase.deleteSocialBoard(user.getId(), socialBoard.getId());

			// then
			assertThat(socialRepository.findById(socialBoard.getId())).isEmpty();
		}

		@Test
		void 사용자를_조회할_수_없는_경우_게시글_삭제에_실패한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(() -> socialUseCase.deleteSocialBoard(nonExistentUserId, socialBoard.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_USER.getMessage());
		}

		@Test
		void 게시글이_존재하지_않는_경우_삭제에_실패한다() {
			// given
			Long nonExistentSocialBoardId = -1L;

			// when & then
			assertThatThrownBy(() -> socialUseCase.deleteSocialBoard(user.getId(), nonExistentSocialBoardId))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.NOT_FOUND_SOCIAL_BOARD.getMessage());
		}

		@Test
		void 게시글의_소유자가_아닌_경우_삭제에_실패한다() {
			// given
			User anotherUser = testFixtureBuilder.buildUser(GENERAL_USER());
			Long socialBoardId = socialBoard.getId();

			// when & then
			assertThatThrownBy(() -> socialUseCase.deleteSocialBoard(anotherUser.getId(), socialBoardId))
				.isInstanceOf(CommonException.class)
				.hasMessage(ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD.getMessage());
		}
	}

}

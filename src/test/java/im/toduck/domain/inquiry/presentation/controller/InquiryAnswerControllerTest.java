package im.toduck.domain.inquiry.presentation.controller;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.admin.domain.usecase.AdminUseCase;
import im.toduck.domain.admin.persistence.entity.Admin;
import im.toduck.domain.admin.persistence.repository.AdminRepository;
import im.toduck.domain.inquiry.domain.usecase.InquiryAnswerUseCase;
import im.toduck.domain.inquiry.domain.usecase.InquiryUseCase;
import im.toduck.domain.inquiry.persistence.entity.Inquiry;
import im.toduck.domain.inquiry.persistence.entity.InquiryAnswer;
import im.toduck.domain.inquiry.persistence.entity.Status;
import im.toduck.domain.inquiry.persistence.entity.Type;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryAnswerCreateRequest;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryAnswerUpdateRequest;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryCreateRequest;
import im.toduck.domain.inquiry.presentation.dto.response.InquiryListResponse;
import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.entity.UserRole;
import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;

class InquiryAnswerControllerTest extends ServiceTest {

	@Autowired
	InquiryUseCase inquiryUseCase;

	@Autowired
	InquiryAnswerUseCase inquiryAnswerUseCase;

	@Autowired
	AdminUseCase adminUseCase;

	@Autowired
	UserRepository userRepository;

	@Autowired
	AdminRepository adminRepository;

	@Transactional
	@Nested
	@DisplayName("문의 답변")
	class InquiryAnswerTest {
		private User savedAdminUser, savedGeneralUser;

		String content = "설정한 루틴의 순서를 바꾸고 싶은데 어떻게 하는지 모르겠어요 ㅠㅠ";
		String content2 = "루틴을 매 달 반복되도록 설정할 수 있나요?";

		String answer = "현재로써는 루틴을 반복할 기간을 따로 설정할 수 있는 기능은 없습니다!";
		String answer2 = "도움이 되셨기를 바라며, 좋은 하루 되세요 :)";

		InquiryCreateRequest inquiryCreateRequest =
			InquiryCreateRequest.builder()
				.type(Type.ERROR)
				.content(content)
				.inquiryImgs(Arrays.asList("asdf", "ㅁㄴㅇㄹ"))
				.build();

		InquiryCreateRequest inquiryCreateRequest2 =
			InquiryCreateRequest.builder()
				.type(Type.USAGE)
				.content(content2)
				.inquiryImgs(Arrays.asList("asdf", "ㅁㄴㅇㄹ"))
				.build();

		@BeforeEach
		void setUp() {
			savedAdminUser = userRepository.save(
				User.builder()
					.role(UserRole.ADMIN)
					.nickname("admin")
					.email("admin@naver.com")
					.provider(OAuthProvider.APPLE)
					.build()
			);

			savedGeneralUser = testFixtureBuilder.buildUser(GENERAL_USER());
		}

		@Transactional
		@Test
		void 성공적으로_생성_조회한다() {
			// given
			Inquiry inquiry = inquiryUseCase.createInquiry(inquiryCreateRequest, savedGeneralUser.getId());
			Inquiry inquiry2 = inquiryUseCase.createInquiry(inquiryCreateRequest2, savedGeneralUser.getId());

			Admin admin = adminRepository.save(
				Admin.builder()
					.user(savedAdminUser)
					.displayName("토덕 관리자")
					.build()
			);

			// when
			InquiryAnswerCreateRequest request = InquiryAnswerCreateRequest.builder()
				.inquiryId(inquiry.getId())
				.content(answer)
				.build();

			InquiryAnswerCreateRequest request2 = InquiryAnswerCreateRequest.builder()
				.inquiryId(inquiry2.getId())
				.content(answer2)
				.build();

			InquiryAnswer inquiryAnswer = inquiryAnswerUseCase.createInquiryAnswer(request,
				admin.getUser().getId());

			InquiryAnswer inquiryAnswer2 = inquiryAnswerUseCase.createInquiryAnswer(request2,
				admin.getUser().getId());

			// then
			InquiryListResponse response = inquiryUseCase.getInquiries(savedGeneralUser.getId());

			assertSoftly(softly -> {
				softly.assertThat(response.inquiryDtos().size()).isEqualTo(2L);
				softly.assertThat(response.inquiryDtos().get(1).type())
					.isEqualTo(inquiryCreateRequest.type());
				softly.assertThat(response.inquiryDtos().get(1).content()).isEqualTo(content);
				softly.assertThat(response.inquiryDtos().get(1).status()).isEqualTo(Status.ANSWERED);
				softly.assertThat(response.inquiryDtos().get(1).inquiryAnswerId())
					.isEqualTo(inquiryAnswer.getId());
				softly.assertThat(response.inquiryDtos().get(1).answerAdminName())
					.isEqualTo(admin.getDisplayName());
				softly.assertThat(response.inquiryDtos().get(1).answerContent()).isEqualTo(answer);
				softly.assertThat(response.inquiryDtos().get(1).answerCreatedAt())
					.isEqualTo(inquiryAnswer.getCreatedAt());

				softly.assertThat(response.inquiryDtos().get(0).type())
					.isEqualTo(inquiryCreateRequest2.type());
				softly.assertThat(response.inquiryDtos().get(0).content()).isEqualTo(content2);
				softly.assertThat(response.inquiryDtos().get(0).status()).isEqualTo(Status.ANSWERED);
				softly.assertThat(response.inquiryDtos().get(0).inquiryAnswerId())
					.isEqualTo(inquiryAnswer2.getId());
				softly.assertThat(response.inquiryDtos().get(0).answerAdminName())
					.isEqualTo(admin.getDisplayName());
				softly.assertThat(response.inquiryDtos().get(0).answerContent()).isEqualTo(answer2);
				softly.assertThat(response.inquiryDtos().get(0).answerCreatedAt())
					.isEqualTo(inquiryAnswer2.getCreatedAt());
			});
		}

		@Transactional
		@Test
		void 이미_답변이_달린_경우_에러가_발생한다() {
			// given
			Inquiry inquiry = inquiryUseCase.createInquiry(inquiryCreateRequest, savedGeneralUser.getId());

			Admin admin = adminRepository.save(
				Admin.builder()
					.user(savedAdminUser)
					.displayName("토덕 관리자")
					.build()
			);

			InquiryAnswerCreateRequest request = InquiryAnswerCreateRequest.builder()
				.inquiryId(inquiry.getId())
				.content(answer)
				.build();

			InquiryAnswer inquiryAnswer = inquiryAnswerUseCase.createInquiryAnswer(request,
				admin.getUser().getId());

			// when - then
			CommonException exception = assertThrows(CommonException.class, () ->
				inquiryAnswerUseCase.createInquiryAnswer(request, admin.getUser().getId())
			);

			assertSoftly(softly -> {
				softly.assertThat(exception.getErrorCode())
					.isEqualTo(ExceptionCode.ALREADY_ANSWERED_INQUIRY.getErrorCode());
			});
		}

		@Transactional
		@Test
		void 답변_작성자의_관리자_권한이_제거된_경우에도_답변_작성자가_표시된다() {
			// given
			Inquiry inquiry = inquiryUseCase.createInquiry(inquiryCreateRequest, savedGeneralUser.getId());

			Admin admin = adminRepository.save(
				Admin.builder()
					.user(savedAdminUser)
					.displayName("토덕 관리자")
					.build()
			);

			InquiryAnswerCreateRequest request = InquiryAnswerCreateRequest.builder()
				.inquiryId(inquiry.getId())
				.content(answer)
				.build();

			InquiryAnswer inquiryAnswer = inquiryAnswerUseCase.createInquiryAnswer(request,
				admin.getUser().getId());

			// when
			adminUseCase.deleteAdmin(admin.getUser().getId());

			// then
			InquiryListResponse response = inquiryUseCase.getInquiries(savedGeneralUser.getId());

			assertSoftly(softly -> {
				softly.assertThat(response.inquiryDtos().size()).isEqualTo(1L);
				softly.assertThat(response.inquiryDtos().get(0).type())
					.isEqualTo(inquiryCreateRequest.type());
				softly.assertThat(response.inquiryDtos().get(0).content()).isEqualTo(content);
				softly.assertThat(response.inquiryDtos().get(0).status()).isEqualTo(Status.ANSWERED);
				softly.assertThat(response.inquiryDtos().get(0).inquiryAnswerId())
					.isEqualTo(inquiryAnswer.getId());
				softly.assertThat(response.inquiryDtos().get(0).answerAdminName())
					.isEqualTo(admin.getDisplayName());
				softly.assertThat(response.inquiryDtos().get(0).answerContent()).isEqualTo(answer);
				softly.assertThat(response.inquiryDtos().get(0).answerCreatedAt())
					.isEqualTo(inquiryAnswer.getCreatedAt());

				softly.assertThat(savedAdminUser.getRole()).isEqualTo(UserRole.USER);
			});
		}

		@Transactional
		@Test
		void 성공적으로_답변을_수정한다() {
			// given
			Inquiry inquiry = inquiryUseCase.createInquiry(inquiryCreateRequest, savedGeneralUser.getId());

			Admin admin = adminRepository.save(
				Admin.builder()
					.user(savedAdminUser)
					.displayName("토덕 관리자")
					.build()
			);

			InquiryAnswerCreateRequest request = InquiryAnswerCreateRequest.builder()
				.inquiryId(inquiry.getId())
				.content(answer)
				.build();

			InquiryAnswer inquiryAnswer = inquiryAnswerUseCase.createInquiryAnswer(request,
				admin.getUser().getId());

			// when
			InquiryAnswerUpdateRequest updateRequest = InquiryAnswerUpdateRequest.builder()
				.content(answer2)
				.build();

			inquiryAnswerUseCase.updateInquiryAnswer(inquiry.getId(), updateRequest, admin.getUser().getId());

			// then
			InquiryListResponse response = inquiryUseCase.getInquiries(savedGeneralUser.getId());

			assertSoftly(softly -> {
				softly.assertThat(response.inquiryDtos().size()).isEqualTo(1L);
				softly.assertThat(response.inquiryDtos().get(0).type())
					.isEqualTo(inquiryCreateRequest.type());
				softly.assertThat(response.inquiryDtos().get(0).content()).isEqualTo(content);
				softly.assertThat(response.inquiryDtos().get(0).status()).isEqualTo(Status.ANSWERED);
				softly.assertThat(response.inquiryDtos().get(0).inquiryAnswerId())
					.isEqualTo(inquiryAnswer.getId());
				softly.assertThat(response.inquiryDtos().get(0).answerAdminName())
					.isEqualTo(admin.getDisplayName());
				softly.assertThat(response.inquiryDtos().get(0).answerContent()).isEqualTo(answer2);
				softly.assertThat(response.inquiryDtos().get(0).answerCreatedAt())
					.isEqualTo(inquiryAnswer.getCreatedAt());
				softly.assertThat(inquiry.getInquiryAnswer().getUpdatedAt()).isNotNull();
			});
		}

		@Transactional
		@Test
		void 성공적으로_답변을_삭제한다() {
			// given
			Inquiry inquiry = inquiryUseCase.createInquiry(inquiryCreateRequest, savedGeneralUser.getId());

			Admin admin = adminRepository.save(
				Admin.builder()
					.user(savedAdminUser)
					.displayName("토덕 관리자")
					.build()
			);

			InquiryAnswerCreateRequest request = InquiryAnswerCreateRequest.builder()
				.inquiryId(inquiry.getId())
				.content(answer)
				.build();

			InquiryAnswer inquiryAnswer = inquiryAnswerUseCase.createInquiryAnswer(request,
				admin.getUser().getId());

			// when
			inquiryAnswerUseCase.deleteInquiryAnswer(inquiry.getId());

			// then
			InquiryListResponse response = inquiryUseCase.getInquiries(savedGeneralUser.getId());

			assertSoftly(softly -> {
				softly.assertThat(response.inquiryDtos().size()).isEqualTo(1L);
				softly.assertThat(response.inquiryDtos().get(0).type())
					.isEqualTo(inquiryCreateRequest.type());
				softly.assertThat(response.inquiryDtos().get(0).content()).isEqualTo(content);
				softly.assertThat(response.inquiryDtos().get(0).status()).isEqualTo(Status.PENDING);
				softly.assertThat(response.inquiryDtos().get(0).inquiryAnswerId()).isNull();
				softly.assertThat(response.inquiryDtos().get(0).answerAdminName()).isNull();
				softly.assertThat(response.inquiryDtos().get(0).answerContent()).isNull();
				softly.assertThat(response.inquiryDtos().get(0).answerCreatedAt()).isNull();
			});
		}

		@Transactional
		@Test
		void 성공적으로_답변_삭제_후_다시_생성한다() {
			// given
			Inquiry inquiry = inquiryUseCase.createInquiry(inquiryCreateRequest, savedGeneralUser.getId());

			Admin admin = adminRepository.save(
				Admin.builder()
					.user(savedAdminUser)
					.displayName("토덕 관리자")
					.build()
			);

			InquiryAnswerCreateRequest request = InquiryAnswerCreateRequest.builder()
				.inquiryId(inquiry.getId())
				.content(answer)
				.build();

			InquiryAnswer inquiryAnswer = inquiryAnswerUseCase.createInquiryAnswer(request,
				admin.getUser().getId());

			inquiryAnswerUseCase.deleteInquiryAnswer(inquiry.getId());

			// when
			InquiryAnswer inquiryAnswer2 = inquiryAnswerUseCase.createInquiryAnswer(request, admin.getUser().getId());

			// then
			InquiryListResponse response = inquiryUseCase.getInquiries(savedGeneralUser.getId());

			assertSoftly(softly -> {
				softly.assertThat(response.inquiryDtos().size()).isEqualTo(1L);
				softly.assertThat(response.inquiryDtos().get(0).type())
					.isEqualTo(inquiryCreateRequest.type());
				softly.assertThat(response.inquiryDtos().get(0).content()).isEqualTo(content);
				softly.assertThat(response.inquiryDtos().get(0).status()).isEqualTo(Status.ANSWERED);
				softly.assertThat(response.inquiryDtos().get(0).inquiryAnswerId()).isEqualTo(inquiryAnswer2.getId());
				softly.assertThat(response.inquiryDtos().get(0).answerAdminName()).isEqualTo(admin.getDisplayName());
				softly.assertThat(response.inquiryDtos().get(0).answerContent()).isEqualTo(request.content());
				softly.assertThat(response.inquiryDtos().get(0).answerCreatedAt())
					.isEqualTo(inquiryAnswer2.getCreatedAt());
				softly.assertThat(inquiryAnswer2.getDeletedAt()).isNull();
			});
		}
	}
}

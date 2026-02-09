package im.toduck.domain.inquiry.presentation.controller;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.inquiry.domain.usecase.InquiryUseCase;
import im.toduck.domain.inquiry.persistence.entity.Inquiry;
import im.toduck.domain.inquiry.persistence.entity.Status;
import im.toduck.domain.inquiry.persistence.entity.Type;
import im.toduck.domain.inquiry.persistence.repository.InquiryRepository;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryCreateRequest;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryUpdateRequest;
import im.toduck.domain.inquiry.presentation.dto.response.InquiryListResponse;
import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.entity.UserRole;
import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;

class InquiryControllerTest extends ServiceTest {

	@Autowired
	InquiryUseCase inquiryUseCase;

	@Autowired
	InquiryRepository inquiryRepository;

	@Autowired
	UserRepository userRepository;

	@Transactional
	@Nested
	@DisplayName("문의")
	class InquiryTest {
		private User savedAdminUser, savedGeneralUser;

		String content = "설정한 루틴의 순서를 바꾸고 싶은데 어떻게 하는지 모르겠어요 ㅠㅠ";
		String content2 = "루틴을 매 달 반복되도록 설정할 수 있나요?";

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

		@Test
		void 성공적으로_생성한다() {
			// given - when
			InquiryCreateRequest inquiryCreateRequest =
				InquiryCreateRequest.builder()
					.type(Type.ERROR)
					.content(content)
					.inquiryImgs(Arrays.asList("asdf", "ㅁㄴㅇㄹ"))
					.build();

			Inquiry inquiry = inquiryUseCase.createInquiry(inquiryCreateRequest, savedGeneralUser.getId());

			// then
			Inquiry inquiry2 = inquiryRepository.findById(inquiry.getId())
				.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_INQUIRY));

			assertSoftly(softly -> {
				softly.assertThat(inquiry2.getUser().getId())
					.isEqualTo(inquiry.getUser().getId());
				softly.assertThat((inquiry2.getType()))
					.isEqualTo(inquiry.getType());
				softly.assertThat(inquiry2.getContent())
					.isEqualTo(inquiry.getContent());
				softly.assertThat(inquiry2.getStatus())
					.isEqualTo(inquiry.getStatus());
				softly.assertThat(inquiry2.getCreatedAt())
					.isEqualTo(inquiry.getCreatedAt());
			});
		}

		@Test
		void 성공적으로_사용자가_자신의_문의를_조회한다() {
			// given
			InquiryCreateRequest inquiryCreateRequest =
				InquiryCreateRequest.builder()
					.type(Type.ERROR)
					.content(content)
					.inquiryImgs(Arrays.asList("asdf", "ㅁㄴㅇㄹ"))
					.build();

			Inquiry inquiry = inquiryUseCase.createInquiry(inquiryCreateRequest, savedGeneralUser.getId());
			Inquiry inquiry2 = inquiryUseCase.createInquiry(inquiryCreateRequest, savedAdminUser.getId());

			// when
			InquiryListResponse inquiryListResponse = inquiryUseCase.getInquiries(savedGeneralUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(inquiryListResponse.inquiryDtos().size()).isEqualTo(1);
				softly.assertThat(inquiryListResponse.inquiryDtos().get(0).type()).isEqualTo(Type.ERROR);
				softly.assertThat(inquiryListResponse.inquiryDtos().get(0).content()).isEqualTo(content);
				softly.assertThat(inquiryListResponse.inquiryDtos().get(0).status()).isEqualTo(Status.PENDING);
			});
		}

		@Test
		void 성공적으로_문의를_수정한다() {
			// given
			InquiryCreateRequest inquiryCreateRequest =
				InquiryCreateRequest.builder()
					.type(Type.ERROR)
					.content(content)
					.inquiryImgs(Arrays.asList("asdf", "ㅁㄴㅇㄹ"))
					.build();

			Inquiry inquiry = inquiryUseCase.createInquiry(inquiryCreateRequest, savedGeneralUser.getId());

			// when
			InquiryUpdateRequest inquiryUpdateRequest =
				InquiryUpdateRequest.builder()
					.type(Type.USAGE)
					.content(content2)
					.inquiryImgs(Arrays.asList("asdf", "ㅁㄴㅇㄹ"))
					.build();

			Inquiry inquiry2 = inquiryUseCase.updateInquiry(inquiry.getId(), inquiryUpdateRequest,
				savedGeneralUser.getId());

			InquiryListResponse inquiryListResponse = inquiryUseCase.getInquiries(savedGeneralUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(inquiryListResponse.inquiryDtos().size()).isEqualTo(1);
				softly.assertThat(inquiryListResponse.inquiryDtos().get(0).type()).isEqualTo(Type.USAGE);
				softly.assertThat(inquiryListResponse.inquiryDtos().get(0).content()).isEqualTo(content2);
				softly.assertThat(inquiryListResponse.inquiryDtos().get(0).status()).isEqualTo(Status.PENDING);
			});
		}

		@Test
		void 성공적으로_삭제한다() {
			// given
			InquiryCreateRequest inquiryCreateRequest =
				InquiryCreateRequest.builder()
					.type(Type.ERROR)
					.content(content)
					.inquiryImgs(Arrays.asList("asdf", "ㅁㄴㅇㄹ"))
					.build();

			Inquiry inquiry = inquiryUseCase.createInquiry(inquiryCreateRequest, savedGeneralUser.getId());

			// when
			inquiryUseCase.deleteInquiry(inquiry.getId(), savedGeneralUser.getId());

			// then
			assertThat(inquiryRepository.findById(inquiry.getId())).isNotPresent();
		}

		@Test
		void 성공적으로_관리자가_모든_문의를_조회한다() throws Exception {
			// given
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

			Inquiry inquiry = inquiryUseCase.createInquiry(inquiryCreateRequest, savedGeneralUser.getId());
			Inquiry inquiry2 = inquiryUseCase.createInquiry(inquiryCreateRequest2, savedAdminUser.getId());

			// when
			InquiryListResponse inquiryListResponse = inquiryUseCase.getAllInquiries(savedAdminUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(inquiryListResponse.inquiryDtos().size()).isEqualTo(2);
				softly.assertThat(inquiryListResponse.inquiryDtos().get(1).type()).isEqualTo(Type.ERROR);
				softly.assertThat(inquiryListResponse.inquiryDtos().get(1).content()).isEqualTo(content);
				softly.assertThat(inquiryListResponse.inquiryDtos().get(1).status()).isEqualTo(Status.PENDING);

				softly.assertThat(inquiryListResponse.inquiryDtos().get(0).type()).isEqualTo(Type.USAGE);
				softly.assertThat(inquiryListResponse.inquiryDtos().get(0).content()).isEqualTo(content2);
				softly.assertThat(inquiryListResponse.inquiryDtos().get(0).status()).isEqualTo(Status.PENDING);
			});
		}
	}
}

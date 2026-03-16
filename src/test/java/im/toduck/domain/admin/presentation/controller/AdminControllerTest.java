package im.toduck.domain.admin.presentation.controller;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.SoftAssertions.*;

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
import im.toduck.domain.admin.presentation.dto.request.AdminCreateRequest;
import im.toduck.domain.admin.presentation.dto.request.AdminUpdateRequest;
import im.toduck.domain.admin.presentation.dto.response.AdminListResponse;
import im.toduck.domain.admin.presentation.dto.response.AdminResponse;
import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.entity.UserRole;
import im.toduck.domain.user.persistence.repository.UserRepository;

class AdminControllerTest extends ServiceTest {

	@Autowired
	AdminUseCase adminUseCase;

	@Autowired
	AdminRepository adminRepository;

	@Autowired
	UserRepository userRepository;

	@Transactional
	@Nested
	@DisplayName("관리자")
	class AdminTest {
		private User savedAdminUser, savedGeneralUser;

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
		void 성공적으로_생성_조회한다() {
			// given - when
			AdminCreateRequest adminCreateRequest =
				AdminCreateRequest.builder()
					.userId(savedAdminUser.getId())
					.displayName("토덕토덕")
					.build();

			AdminCreateRequest adminCreateRequest2 =
				AdminCreateRequest.builder()
					.userId(savedGeneralUser.getId())
					.displayName("토닥토닥")
					.build();

			Admin admin = adminUseCase.createAdmin(adminCreateRequest);
			Admin admin2 = adminUseCase.createAdmin(adminCreateRequest2);

			// then
			AdminResponse adminResponse = adminUseCase.getAdmin(savedAdminUser.getId());
			AdminResponse adminResponse2 = adminUseCase.getAdmin(savedGeneralUser.getId());
			AdminListResponse adminListResponse = adminUseCase.getAdmins();

			assertSoftly(softly -> {
				softly.assertThat(adminResponse.userId()).isEqualTo(admin.getUser().getId());
				softly.assertThat(adminResponse.displayName()).isEqualTo(admin.getDisplayName());
			});

			assertSoftly(softly -> {
				softly.assertThat(adminResponse2.userId()).isEqualTo(admin2.getUser().getId());
				softly.assertThat(adminResponse2.displayName()).isEqualTo(admin2.getDisplayName());
				softly.assertThat(savedGeneralUser.getRole()).isEqualTo(UserRole.ADMIN);
			});

			assertSoftly(softly -> {
				softly.assertThat(adminListResponse.adminDtos().size()).isEqualTo(2);
				softly.assertThat(adminListResponse.adminDtos().get(0).displayName()).isEqualTo(admin.getDisplayName());
				softly.assertThat(adminListResponse.adminDtos().get(1).displayName())
					.isEqualTo(admin2.getDisplayName());
			});
		}

		@Test
		void 성공적으로_수정한다() {
			// given
			AdminCreateRequest adminCreateRequest =
				AdminCreateRequest.builder()
					.userId(savedAdminUser.getId())
					.displayName("토덕토덕")
					.build();

			Admin admin = adminUseCase.createAdmin(adminCreateRequest);

			// when
			AdminUpdateRequest adminUpdateRequest =
				AdminUpdateRequest.builder()
					.displayName("토닥토닥")
					.build();

			adminUseCase.updateAdmin(savedAdminUser.getId(), adminUpdateRequest);

			// then
			assertSoftly(softly -> {
				softly.assertThat(admin.getDisplayName()).isEqualTo("토닥토닥");
			});
		}

		@Test
		void 성공적으로_삭제한다() {
			// given
			AdminCreateRequest adminCreateRequest =
				AdminCreateRequest.builder()
					.userId(savedAdminUser.getId())
					.displayName("토덕토덕")
					.build();

			Admin admin = adminUseCase.createAdmin(adminCreateRequest);

			// when
			adminUseCase.deleteAdmin(admin.getUser().getId());
			AdminListResponse adminListResponse = adminUseCase.getAdmins();

			// then
			assertSoftly(softly -> {
				softly.assertThat(adminListResponse.adminDtos().size()).isEqualTo(0);
				softly.assertThat(savedAdminUser.getRole()).isEqualTo(UserRole.USER);
			});
		}

		@Test
		void 삭제되지_않은_관리자만_조회한다() {
			// given - when
			AdminCreateRequest adminCreateRequest =
				AdminCreateRequest.builder()
					.userId(savedAdminUser.getId())
					.displayName("토덕토덕")
					.build();

			AdminCreateRequest adminCreateRequest2 =
				AdminCreateRequest.builder()
					.userId(savedGeneralUser.getId())
					.displayName("토닥토닥")
					.build();

			Admin admin = adminUseCase.createAdmin(adminCreateRequest);
			Admin admin2 = adminUseCase.createAdmin(adminCreateRequest2);

			adminUseCase.deleteAdmin(admin2.getUser().getId());

			// then
			AdminListResponse adminListResponse = adminUseCase.getAdmins();

			assertSoftly(softly -> {
				softly.assertThat(adminListResponse.adminDtos().size()).isEqualTo(1);
			});
		}
	}
}

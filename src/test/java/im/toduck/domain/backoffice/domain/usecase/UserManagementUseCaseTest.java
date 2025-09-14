package im.toduck.domain.backoffice.domain.usecase;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.backoffice.presentation.dto.request.UserSuspendRequest;
import im.toduck.domain.backoffice.presentation.dto.response.UserDetailResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UserListResponse;
import im.toduck.domain.mypage.persistence.entity.AccountDeletionLog;
import im.toduck.domain.mypage.persistence.entity.AccountDeletionReason;
import im.toduck.domain.mypage.persistence.repository.AccountDeletionLogRepository;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.entity.UserRole;
import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;

@Transactional
class UserManagementUseCaseTest extends ServiceTest {

    @Autowired
    private UserManagementUseCase userManagementUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountDeletionLogRepository accountDeletionLogRepository;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // 서로 다른 loginId를 가진 사용자들 생성
        testUser = testFixtureBuilder.buildUser(
            User.builder()
                .role(UserRole.USER)
                .nickname("testUser" + System.currentTimeMillis())
                .loginId("testUser" + System.currentTimeMillis())
                .password("password")
                .phoneNumber("010-1111-1111")
                .build()
        );

        adminUser = testFixtureBuilder.buildUser(
            User.builder()
                .role(UserRole.USER)
                .nickname("adminUser" + System.currentTimeMillis())
                .loginId("adminUser" + System.currentTimeMillis())
                .password("password")
                .phoneNumber("010-2222-2222")
                .build()
        );

        // 관리자 인증 설정 - adminUser의 실제 loginId로 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            adminUser.getLoginId(), null, null
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    @DisplayName("회원 탈퇴 로그 관련 테스트")
    class DeletionLogsTest {

        @Test
        void 탈퇴_로그가_있는_경우_정상적으로_조회된다() {
            // given
            User deletedUser = testFixtureBuilder.buildDeletedUser(
                GENERAL_USER(), LocalDateTime.now()
            );

            AccountDeletionLog log = AccountDeletionLog.builder()
                .user(deletedUser)
                .reasonCode(AccountDeletionReason.HARD_TO_USE)
                .reasonText("사용법이 어려워요")
                .build();
            accountDeletionLogRepository.save(log);

            // when
            var response = userManagementUseCase.getAllDeletionLogs();

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.deletionLogs()).isNotEmpty();
                softly.assertThat(response.totalCount()).isGreaterThan(0L);

                boolean hasOurLog = response.deletionLogs().stream()
                    .anyMatch(logResponse ->
                        logResponse.reasonCode() == AccountDeletionReason.HARD_TO_USE &&
                        "사용법이 어려워요".equals(logResponse.reasonText())
                    );
                softly.assertThat(hasOurLog).isTrue();
            });
        }

        @Test
        void 탈퇴_사유별_통계가_정상적으로_집계된다() {
            // given
            User deletedUser1 = testFixtureBuilder.buildDeletedUser(
                GENERAL_USER(), LocalDateTime.now()
            );
            User deletedUser2 = testFixtureBuilder.buildDeletedUser(
                GENERAL_USER(), LocalDateTime.now()
            );

            accountDeletionLogRepository.save(
                AccountDeletionLog.builder()
                    .user(deletedUser1)
                    .reasonCode(AccountDeletionReason.HARD_TO_USE)
                    .reasonText("사용법이 어려워요")
                    .build()
            );
            accountDeletionLogRepository.save(
                AccountDeletionLog.builder()
                    .user(deletedUser2)
                    .reasonCode(AccountDeletionReason.NO_FEATURES)
                    .reasonText("원하는 기능이 없어요")
                    .build()
            );

            // when
            var response = userManagementUseCase.getDeletionReasonStatistics();

            // then
            assertThat(response.totalCount()).isGreaterThanOrEqualTo(2L);
            assertThat(response.reasonCounts()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("사용자 목록 및 상세 조회시")
    class UserQueryTest {

        @Test
        void 전체_사용자_목록이_정상적으로_조회된다() {
            // when
            UserListResponse response = userManagementUseCase.getAllUsers();

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.getUsers()).isNotEmpty();
                softly.assertThat(response.getTotalCount()).isGreaterThan(0L);

                // 우리가 생성한 사용자들이 포함되어 있는지 확인
                boolean hasTestUser = response.getUsers().stream()
                    .anyMatch(user -> user.getId().equals(testUser.getId()));
                boolean hasAdminUser = response.getUsers().stream()
                    .anyMatch(user -> user.getId().equals(adminUser.getId()));

                softly.assertThat(hasTestUser).isTrue();
                softly.assertThat(hasAdminUser).isTrue();
            });
        }

        @Test
        void 사용자_상세_정보가_정상적으로_조회된다() {
            // when
            UserDetailResponse response = userManagementUseCase.getUserDetail(testUser.getId());

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.getId()).isEqualTo(testUser.getId());
                softly.assertThat(response.getNickname()).isEqualTo(testUser.getNickname());
                softly.assertThat(response.getPhoneNumber()).isEqualTo(testUser.getPhoneNumber());
                softly.assertThat(response.getLoginId()).isEqualTo(testUser.getLoginId());
                softly.assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
                softly.assertThat(response.getRole()).isEqualTo(testUser.getRole());
                softly.assertThat(response.getProvider()).isEqualTo(testUser.getProvider());
                softly.assertThat(response.isSuspended()).isFalse();
                softly.assertThat(response.getCreatedAt()).isNotNull();
            });
        }

        @Test
        void 존재하지_않는_사용자_조회시_예외가_발생한다() {
            // given
            Long nonExistentUserId = 999999L;

            // when & then
            assertThatThrownBy(() -> userManagementUseCase.getUserDetail(nonExistentUserId))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(ExceptionCode.NOT_FOUND_USER.getMessage());
        }
    }

    @Nested
    @DisplayName("사용자 정지 관리시")
    class UserSuspensionTest {

        @Test
        void 사용자를_정상적으로_정지할_수_있다() {
            // given
            LocalDateTime suspendedUntil = LocalDateTime.now().plusDays(7);
            String suspensionReason = "부적절한 게시물 작성";
            UserSuspendRequest request = UserSuspendRequest.builder()
                .suspendedUntil(suspendedUntil)
                .suspensionReason(suspensionReason)
                .build();

            // when
            userManagementUseCase.suspendUser(testUser.getId(), request);

            // then
            User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
            assertSoftly(softly -> {
                softly.assertThat(updatedUser.isSuspended()).isTrue();
                softly.assertThat(updatedUser.getSuspendedUntil()).isEqualTo(suspendedUntil);
                softly.assertThat(updatedUser.getSuspensionReason()).isEqualTo(suspensionReason);
            });
        }

        @Test
        void 자기_자신을_정지하려고_하면_예외가_발생한다() {
            // given
            UserSuspendRequest request = UserSuspendRequest.builder()
                .suspendedUntil(LocalDateTime.now().plusDays(7))
                .suspensionReason("자기 정지 시도")
                .build();

            // when & then
            assertThatThrownBy(() -> userManagementUseCase.suspendUser(adminUser.getId(), request))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(ExceptionCode.CANNOT_SUSPEND_SELF.getMessage());
        }

        @Test
        void 정지된_사용자를_정상적으로_해제할_수_있다() {
            // given
            testUser.suspend(LocalDateTime.now().plusDays(7), "정지 사유");
            userRepository.save(testUser);

            // when
            userManagementUseCase.unsuspendUser(testUser.getId());

            // then
            User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
            assertSoftly(softly -> {
                softly.assertThat(updatedUser.isSuspended()).isFalse();
                softly.assertThat(updatedUser.getSuspendedUntil()).isNull();
                softly.assertThat(updatedUser.getSuspensionReason()).isNull();
            });
        }

        @Test
        void 정지된_사용자_정보가_목록에서_정확히_표시된다() {
            // given
            LocalDateTime suspendedUntil = LocalDateTime.now().plusDays(7);
            String suspensionReason = "스팸 행위";
            testUser.suspend(suspendedUntil, suspensionReason);
            userRepository.save(testUser);

            // when
            UserListResponse response = userManagementUseCase.getAllUsers();

            // then
            UserListResponse.UserInfo suspendedUserInfo = response.getUsers().stream()
                .filter(user -> user.getId().equals(testUser.getId()))
                .findFirst()
                .orElseThrow();

            assertSoftly(softly -> {
                softly.assertThat(suspendedUserInfo.isSuspended()).isTrue();
                softly.assertThat(suspendedUserInfo.getSuspendedUntil()).isEqualTo(suspendedUntil);
                softly.assertThat(suspendedUserInfo.getSuspensionReason()).isEqualTo(suspensionReason);
            });
        }

        @Test
        void 존재하지_않는_사용자_정지시_예외가_발생한다() {
            // given
            Long nonExistentUserId = 999999L;
            UserSuspendRequest request = UserSuspendRequest.builder()
                .suspendedUntil(LocalDateTime.now().plusDays(7))
                .suspensionReason("정지 사유")
                .build();

            // when & then
            assertThatThrownBy(() -> userManagementUseCase.suspendUser(nonExistentUserId, request))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(ExceptionCode.NOT_FOUND_USER.getMessage());
        }
    }
}

package im.toduck.domain.social.domain.usecase;

import static im.toduck.fixtures.RoutineFixtures.*;
import static im.toduck.fixtures.RoutineFixtures.PRIVATE_ROUTINE;
import static im.toduck.fixtures.social.SocialFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static im.toduck.global.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.repository.RoutineRepository;
import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.presentation.dto.response.SocialProfileResponse;
import im.toduck.domain.social.presentation.dto.response.SocialResponse;
import im.toduck.domain.social.presentation.dto.response.UserProfileRoutineListResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.presentation.dto.response.CursorPaginationResponse;
import jakarta.persistence.EntityManager;

public class SocialProfileUseCaseTest extends ServiceTest {

	@Autowired
	private SocialProfileUseCase socialProfileUseCase;

	@Autowired
	private RoutineRepository routineRepository;

	@Autowired
	private EntityManager entityManager;

	private User PROFILE_USER;
	private User AUTH_USER;
	private User OHTER_USER;

	@BeforeEach
	void setUp() {
		PROFILE_USER = testFixtureBuilder.buildUser(GENERAL_USER());
		AUTH_USER = testFixtureBuilder.buildUser(GENERAL_USER());
		OHTER_USER = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Nested
	@DisplayName("SocialProfile 조회시")
	class GetUserProfileTests {

		@Test
		void 프로필_조회를_할_수_있다() {
			// given
			int followingCount = 3;
			int followerCount = 2;
			int postCount = 4;

			// profileUser가 팔로우한 수 생성 (followingCount)
			for (int i = 0; i < followingCount; i++) {
				User followed = testFixtureBuilder.buildUser(GENERAL_USER());
				testFixtureBuilder.buildFollow(PROFILE_USER, followed);
			}

			// profileUser를 팔로우하는 수 생성 (followerCount)
			for (int i = 0; i < followerCount; i++) {
				User follower = testFixtureBuilder.buildUser(GENERAL_USER());
				testFixtureBuilder.buildFollow(follower, PROFILE_USER);
			}

			// profileUser가 작성한 게시글 생성 (postCount)
			for (int i = 0; i < postCount; i++) {
				testFixtureBuilder.buildSocial(
					im.toduck.fixtures.social.SocialFixtures.SINGLE_SOCIAL(PROFILE_USER, false));
			}

			// when
			SocialProfileResponse response = socialProfileUseCase.getUserProfile(
				PROFILE_USER.getId(),
				AUTH_USER.getId()
			);

			// then
			assertThat(response).isNotNull();
			assertThat(response.nickname()).isEqualTo(PROFILE_USER.getNickname());
			assertThat(response.followingCount()).isEqualTo(followingCount);
			assertThat(response.followerCount()).isEqualTo(followerCount);
			assertThat(response.postCount()).isEqualTo(postCount);
			assertThat(response.isMe()).isFalse();
		}

		@Test
		void 자신의_프로필_조회시_isMe가_true이다() {
			// when
			SocialProfileResponse response = socialProfileUseCase.getUserProfile(
				PROFILE_USER.getId(),
				PROFILE_USER.getId()
			);

			// then
			assertThat(response.isMe()).isTrue();
		}

		@Test
		void 존재하지_않는_사용자_프로필_조회에_실패한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(() -> socialProfileUseCase.getUserProfile(nonExistentUserId, AUTH_USER.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(NOT_FOUND_USER.getMessage());
		}
	}

	@Nested
	@DisplayName("특정 유저의 Social 게시글 목록 조회시")
	class GetUserSocialsTests {

		@Test
		void 유저가_작성한_게시글_목록을_페이지네이션으로_조회한다() {
			// given
			int totalPosts = 15;
			int limit = 10;

			List<Social> profileUserSocials = testFixtureBuilder.buildSocials(
				MULTIPLE_SOCIALS(PROFILE_USER, totalPosts));

			testFixtureBuilder.buildSocial(SINGLE_SOCIAL(OHTER_USER, false));

			// when
			CursorPaginationResponse<SocialResponse> response = socialProfileUseCase.getUserSocials(
				PROFILE_USER.getId(),
				AUTH_USER.getId(), null, limit);

			// then
			Social firstExpectedSocial = profileUserSocials.get(totalPosts - 1);
			Social lastExpectedSocialOnPage = profileUserSocials.get(totalPosts - limit);

			assertSoftly(softly -> {
				softly.assertThat(response.hasMore()).isTrue();
				softly.assertThat(response.results()).hasSize(limit);
				softly.assertThat(response.nextCursor()).isEqualTo(lastExpectedSocialOnPage.getId());

				response.results().forEach(socialResponse ->
					softly.assertThat(socialResponse.owner().ownerId()).isEqualTo(PROFILE_USER.getId())
				);

				softly.assertThat(response.results().get(0).socialId()).isEqualTo(firstExpectedSocial.getId());
			});
		}

		@Test
		void 커서를_사용하여_다음_페이지를_조회한다() {
			// given
			int totalPosts = 15;
			int limit = 10;
			List<Social> profileUserSocials = testFixtureBuilder.buildSocials(
				MULTIPLE_SOCIALS(PROFILE_USER, totalPosts)
			);
			Long cursor = profileUserSocials.get(totalPosts - limit).getId();

			// when
			CursorPaginationResponse<SocialResponse> nextPage = socialProfileUseCase.getUserSocials(
				PROFILE_USER.getId(),
				AUTH_USER.getId(),
				cursor,
				limit
			);

			// then
			int remainingPosts = totalPosts - limit;
			Social oldestSocial = profileUserSocials.get(0);

			assertSoftly(softly -> {
				softly.assertThat(nextPage.hasMore()).isFalse();
				softly.assertThat(nextPage.results()).hasSize(remainingPosts);
				softly.assertThat(nextPage.nextCursor()).isNull();

				softly.assertThat(nextPage.results().get(remainingPosts - 1).socialId())
					.isEqualTo(oldestSocial.getId());
			});
		}

		@Test
		void 게시글이_없는_경우_빈_목록을_반환한다() {
			// when
			CursorPaginationResponse<SocialResponse> response = socialProfileUseCase.getUserSocials(
				PROFILE_USER.getId(),
				AUTH_USER.getId(),
				null,
				10
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response.hasMore()).isFalse();
				softly.assertThat(response.results()).isEmpty();
				softly.assertThat(response.nextCursor()).isNull();
			});
		}

		@Test
		void 존재하지_않는_사용자의_게시글_조회_시_예외가_발생한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(
				() -> socialProfileUseCase.getUserSocials(nonExistentUserId, AUTH_USER.getId(), null, 10))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(NOT_FOUND_USER.getMessage());
		}

		// TODO: 차단 로직 구현 시 차단된 사용자의 게시글 조회 테스트 추가
	}

	@Nested
	@DisplayName("유저의 공개 루틴 목록 조회시")
	class GetUserAvailableRoutinesTests {

		@Test
		void 유저의_공개_루틴_목록을_조회할_수_있다() {
			// given
			int routineCount = 5;
			List<Routine> routines = new ArrayList<>();

			for (int i = 0; i < routineCount; i++) {
				Routine routine = testFixtureBuilder.buildRoutine(WEEKEND_NOON_ROUTINE(PROFILE_USER));
				routines.add(routine);
			}

			// when
			UserProfileRoutineListResponse response = socialProfileUseCase.readUserAvailableRoutines(
				PROFILE_USER.getId(),
				AUTH_USER.getId()
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.routines()).hasSize(routineCount);

				List<Long> responseRoutineIds = response.routines().stream()
					.map(UserProfileRoutineListResponse.UserProfileRoutineResponse::routineId)
					.toList();

				List<Long> expectedRoutineIds = routines.stream()
					.map(Routine::getId)
					.toList();

				softly.assertThat(responseRoutineIds).containsExactlyInAnyOrderElementsOf(expectedRoutineIds);
			});
		}

		@Test
		void 존재하지_않는_사용자의_루틴_조회_시_예외가_발생한다() {
			// given
			Long nonExistentUserId = -1L;

			// when & then
			assertThatThrownBy(
				() -> socialProfileUseCase.readUserAvailableRoutines(nonExistentUserId, AUTH_USER.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(NOT_FOUND_USER.getMessage());
		}

		@Test
		@Transactional
		void 유저의_공개_루틴_목록_조회시_총_공유수가_정확히_계산된다() throws Exception {
			// given
			int routineCount = 3;
			List<Routine> routines = new ArrayList<>();

			// 리플렉션을 이용한 필드 값 변경
			Field sharedCountField = Routine.class.getDeclaredField("sharedCount");
			sharedCountField.setAccessible(true);

			for (int i = 0; i < routineCount; i++) {
				Routine routine = testFixtureBuilder.buildRoutine(WEEKEND_NOON_ROUTINE(PROFILE_USER));

				int sharedCount = i * 10;
				sharedCountField.set(routine, sharedCount);

				entityManager.merge(routine);
				entityManager.flush();

				routines.add(routine);
			}

			entityManager.clear();

			// when
			UserProfileRoutineListResponse response = socialProfileUseCase.readUserAvailableRoutines(
				PROFILE_USER.getId(),
				AUTH_USER.getId()
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.routines()).hasSize(routineCount);

				final Map<Long, Integer> routineIdToSharedCount = new HashMap<>();
				for (int i = 0; i < routineCount; i++) {
					routineIdToSharedCount.put(routines.get(i).getId(), i * 10);
				}

				response.routines().forEach(routineResponse -> {
					Long routineId = routineResponse.routineId();
					Integer expectedSharedCount = routineIdToSharedCount.get(routineId);
					softly.assertThat(routineResponse.sharedCount()).isEqualTo(expectedSharedCount);
				});
			});
		}

		@Test
		void 루틴이_없는_경우_빈_목록을_반환한다() {
			// when
			UserProfileRoutineListResponse response = socialProfileUseCase.readUserAvailableRoutines(
				PROFILE_USER.getId(),
				AUTH_USER.getId()
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.routines()).isEmpty();
			});
		}

	}

	@Nested
	class SaveSharedRoutineTests {
		private RoutineCreateRequest request;
		private Routine SOURCE_ROUTINE_IS_PUBLIC;
		private Routine SOURCE_ROUTINE_IS_PRIVATE;

		@BeforeEach
		void setUp() {
			request = new RoutineCreateRequest(
				"Morning Exercise",
				PlanCategory.COMPUTER,
				"#FF5733",
				LocalTime.of(7, 0),
				true,
				List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
				30,
				"30 minutes jogging"
			);

			SOURCE_ROUTINE_IS_PUBLIC = testFixtureBuilder.buildRoutine(WEEKEND_NOON_ROUTINE(PROFILE_USER));
			SOURCE_ROUTINE_IS_PRIVATE = testFixtureBuilder.buildRoutine(PRIVATE_ROUTINE(PROFILE_USER));
		}

		@Test
		void 공유_루틴_저장을_성공한다() {
			// given
			int initialSharedCount = SOURCE_ROUTINE_IS_PUBLIC.getSharedCount();

			// when
			RoutineCreateResponse response = socialProfileUseCase.saveSharedRoutine(
				AUTH_USER.getId(),
				SOURCE_ROUTINE_IS_PUBLIC.getId(),
				request
			);

			// then
			assertThat(response.routineId()).isNotNull();

			Routine newlyCreatedRoutine = routineRepository.findById(response.routineId()).orElseThrow();
			assertThat(newlyCreatedRoutine.getUser().getId()).isEqualTo(AUTH_USER.getId());
			assertThat(newlyCreatedRoutine.getSharedCount()).isZero();

			Routine updatedSourceRoutine = routineRepository.findById(SOURCE_ROUTINE_IS_PUBLIC.getId()).orElseThrow();

			assertThat(updatedSourceRoutine.getSharedCount()).isEqualTo(initialSharedCount + 1);
		}

		@Test
		void 저장하는_사용자를_찾지_못하면_루틴_저장에_실패한다() {
			// given
			Long nonExistentUserId = -1L;
			Long sourceRoutineId = SOURCE_ROUTINE_IS_PUBLIC.getId();

			// when & then
			assertThatThrownBy(
				() -> socialProfileUseCase.saveSharedRoutine(nonExistentUserId, sourceRoutineId, request))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(NOT_FOUND_USER.getMessage());
		}

		@Test
		void 원본_루틴을_찾지_못하면_루틴_저장에_실패한다() {
			// given
			Long nonExistentRoutineId = -99L;

			// when & then
			assertThatThrownBy(
				() -> socialProfileUseCase.saveSharedRoutine(AUTH_USER.getId(), nonExistentRoutineId, request))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(NOT_FOUND_ROUTINE.getMessage());
		}

		@Test
		void 원본_루틴이_비공개이면_루틴_저장에_실패한다() {
			// when & then
			assertThatThrownBy(
				() -> socialProfileUseCase.saveSharedRoutine(SOURCE_ROUTINE_IS_PRIVATE.getId(), AUTH_USER.getId(),
					request))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(NOT_FOUND_ROUTINE.getMessage());
		}
	}
}

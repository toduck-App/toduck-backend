package im.toduck.domain.events.events.domain.usecase;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.events.events.persistence.entity.Events;
import im.toduck.domain.events.events.persistence.repository.EventsRepository;
import im.toduck.domain.events.events.presentation.dto.EventsCreateRequest;
import im.toduck.domain.events.events.presentation.dto.EventsListResponse;
import im.toduck.domain.events.events.presentation.dto.EventsUpdateRequest;
import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.entity.UserRole;
import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;

class EventsUseCaseTest extends ServiceTest {

	@Autowired
	EventsUseCase eventsUseCase;

	@Autowired
	EventsRepository eventsRepository;

	@Autowired
	UserRepository userRepository;

	@Nested
	@DisplayName("이벤트")
	class EventsTest {
		private User savedAdminUser, savedGeneralUser;
		private final LocalDateTime st = LocalDateTime.of(2025, 9, 22, 0, 0, 0);
		private final LocalDateTime ed = LocalDateTime.of(2025, 9, 23, 23, 59, 59);

		private EventsCreateRequest eventsCreateRequest = EventsCreateRequest.builder()
			.eventName("테스트1")
			.startAt(st)
			.endAt(ed)
			.thumbUrl("https://userpic.codeforces.org/1306481/title/63d99453ed22e728.jpg")
			.appVersion("1.0.1")
			.build();

		@Transactional
		@Nested
		@DisplayName("이벤트 생성 시")
		class CreateEvents {
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
				eventsUseCase.createEvents(eventsCreateRequest, savedAdminUser.getId());
				List<Events> events = eventsRepository.findAllByOrderByIdAsc();

				// then
				assertSoftly(softly -> {
					softly.assertThat(events.size()).isEqualTo(1);
					softly.assertThat(events.get(0).getEventName()).isEqualTo("테스트1");
				});
			}

			@Test
			void 관리자가_아닐_경우_실패한다() {
				// given - when
				CommonException exception = assertThrows(CommonException.class, () ->
					eventsUseCase.createEvents(eventsCreateRequest, savedGeneralUser.getId())
				);

				// then
				assertThat(exception.getErrorCode()).isEqualTo(ExceptionCode.NOT_ADMIN.getErrorCode());
			}
		}

		@Transactional
		@Nested
		@DisplayName("이벤트 조회 시")
		class GetEvents {
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
			void 성공적으로_조회한다() {
				// given
				eventsUseCase.createEvents(eventsCreateRequest, savedAdminUser.getId());

				// when
				EventsListResponse events = eventsUseCase.getEvents(savedAdminUser.getId());

				// then
				assertSoftly(softly -> {
					softly.assertThat(events.eventsDtos().size()).isEqualTo(1);
					softly.assertThat(events.eventsDtos().get(0).eventName()).isEqualTo("테스트1");
				});
			}
		}

		@Transactional
		@Nested
		@DisplayName("이벤트 수정 시")
		class UpdateEvents {
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
			void 성공적으로_수정한다() {
				// given
				Events events = eventsUseCase.createEvents(eventsCreateRequest, savedAdminUser.getId());
				EventsUpdateRequest eventsUpdateRequest = EventsUpdateRequest.builder()
					.eventName("댓글 이벤트")
					.startAt(null)
					.endAt(null)
					.thumbUrl(null)
					.appVersion(null)
					.build();

				// when
				eventsUseCase.updateEvents(events.getId(), eventsUpdateRequest, savedAdminUser.getId());

				// then
				Events updatedEvent = eventsRepository.findById(events.getId())
					.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_EVENTS));

				assertSoftly(softly -> {
					softly.assertThat(updatedEvent.getEventName()).isEqualTo(eventsUpdateRequest.eventName());
					softly.assertThat(updatedEvent.getStartAt()).isEqualTo(updatedEvent.getStartAt());
					softly.assertThat(updatedEvent.getEndAt()).isEqualTo(updatedEvent.getEndAt());
					softly.assertThat(updatedEvent.getThumbUrl()).isEqualTo(updatedEvent.getThumbUrl());
					softly.assertThat(updatedEvent.getAppVersion()).isEqualTo(updatedEvent.getAppVersion());
				});
			}
		}

		@Transactional
		@Nested
		@DisplayName("이벤트 삭제 시")
		class DeleteEvents {
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
			void 성공적으로_삭제한다() {
				// given
				Events event = eventsUseCase.createEvents(eventsCreateRequest, savedAdminUser.getId());

				// when
				eventsUseCase.deleteEvents(event.getId(), savedAdminUser.getId());

				// then
				List<Events> events = eventsRepository.findAll();
				assertThat(events.size()).isEqualTo(0);
			}
		}
	}
}

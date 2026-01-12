package im.toduck.domain.events.detail.domain.usecase;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.events.detail.persistence.entity.EventsDetail;
import im.toduck.domain.events.detail.persistence.repository.EventsDetailRepository;
import im.toduck.domain.events.detail.presentation.dto.request.EventsDetailCreateRequest;
import im.toduck.domain.events.detail.presentation.dto.request.EventsDetailUpdateRequest;
import im.toduck.domain.events.detail.presentation.dto.response.EventsDetailListResponse;
import im.toduck.domain.events.events.persistence.entity.Events;
import im.toduck.domain.events.events.persistence.repository.EventsRepository;
import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.entity.UserRole;
import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;

class EventsDetailUseCaseTest extends ServiceTest {

	@Autowired
	EventsDetailUseCase eventsDetailUseCase;

	@Autowired
	EventsRepository eventsRepository;

	@Autowired
	EventsDetailRepository eventsDetailRepository;

	@Autowired
	UserRepository userRepository;

	@Transactional
	@Nested
	@DisplayName("이벤트 디테일")
	class EventsDetailTest {
		private User savedAdminUser, savedGeneralUser;
		private Events savedEvent;

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

			savedEvent = eventsRepository.save(
				Events.builder()
					.eventName("테스트 이벤트")
					.startAt(LocalDateTime.now())
					.endAt(LocalDateTime.now().plusDays(1))
					.thumbUrl("https://thumb.jpg")
					.appVersion("1.0.0")
					.buttonVisible(true)
					.buttonText("당첨 확인하기")
					.build()
			);
		}

		@Test
		void 성공적으로_생성한다() {
			// given - when
			EventsDetailCreateRequest eventsDetailCreateRequest =
				EventsDetailCreateRequest.builder()
					.eventsId(savedEvent.getId())
					.routingUrl("toduck://createPost")
					.buttonVisible(true)
					.buttonText("당첨 확인하기")
					.eventsDetailImgs(Arrays.asList("asdf", "ㅁㄴㅇㄹ"))
					.build();

			EventsDetail eventsDetail1 = eventsDetailUseCase.createEventsDetail(eventsDetailCreateRequest,
				savedAdminUser.getId());

			// then
			EventsDetail eventsDetail2 = eventsDetailRepository.findById(eventsDetail1.getId())
				.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_EVENTS_DETAIL));

			assertSoftly(softly -> {
				softly.assertThat(eventsDetail2.getEvents().getId())
					.isEqualTo(eventsDetailCreateRequest.eventsId());
				softly.assertThat(eventsDetail2.getRoutingUrl()).isEqualTo(eventsDetailCreateRequest.routingUrl());
			});
		}

		@Test
		void 성공적으로_조회한다() {
			// given
			EventsDetailCreateRequest eventsDetailCreateRequest =
				EventsDetailCreateRequest.builder()
					.eventsId(savedEvent.getId())
					.routingUrl("toduck://createPost")
					.buttonVisible(true)
					.buttonText("당첨 확인하기")
					.eventsDetailImgs(Arrays.asList("asdf", "ㅁㄴㅇㄹ"))
					.build();

			EventsDetailCreateRequest eventsDetailCreateRequest2 =
				EventsDetailCreateRequest.builder()
					.eventsId(savedEvent.getId())
					.routingUrl("toduck://anotherPost")
					.buttonVisible(true)
					.buttonText("당첨 확인하기")
					.eventsDetailImgs(Arrays.asList("xyz", "123"))
					.build();

			EventsDetail eventsDetail1 = eventsDetailUseCase.createEventsDetail(eventsDetailCreateRequest,
				savedAdminUser.getId());

			// when
			EventsDetailListResponse eventsDetailListResponse =
				eventsDetailUseCase.getEventsDetails(savedAdminUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(eventsDetailListResponse.eventsDetailDtos().size()).isEqualTo(1);
				softly.assertThat(eventsDetailListResponse.eventsDetailDtos().get(0).eventsDetailId())
					.isEqualTo(eventsDetail1.getId());
				softly.assertThat(eventsDetailListResponse.eventsDetailDtos().get(0).eventsId())
					.isEqualTo(eventsDetail1.getEvents().getId());
				softly.assertThat(eventsDetailListResponse.eventsDetailDtos().get(0).routingUrl())
					.isEqualTo(eventsDetail1.getRoutingUrl());
			});
		}

		@Test
		void 중복_생성_시_예외가_발생한다() {
			// given
			EventsDetailCreateRequest eventsDetailCreateRequest =
				EventsDetailCreateRequest.builder()
					.eventsId(savedEvent.getId())
					.routingUrl("toduck://createPost")
					.buttonVisible(true)
					.buttonText("당첨 확인하기")
					.eventsDetailImgs(
						Arrays.asList("https://cdn.toduck.app/test1.jpg", "https://cdn.toduck.app/test2.jpg"))
					.build();

			eventsDetailUseCase.createEventsDetail(eventsDetailCreateRequest, savedAdminUser.getId());

			EventsDetailCreateRequest duplicateRequest =
				EventsDetailCreateRequest.builder()
					.eventsId(savedEvent.getId())
					.routingUrl("toduck://anotherPost")
					.buttonVisible(true)
					.buttonText("당첨 확인하기")
					.eventsDetailImgs(
						Arrays.asList("https://cdn.toduck.app/test3.jpg", "https://cdn.toduck.app/test4.jpg"))
					.build();

			// when - then
			CommonException exception = assertThrows(CommonException.class, () ->
				eventsDetailUseCase.createEventsDetail(duplicateRequest, savedAdminUser.getId())
			);

			assertThat(exception.getErrorCode()).isEqualTo(ExceptionCode.DUPLICATE_EVENTS_DETAIL.getErrorCode());
		}

		@Test
		void 성공적으로_수정한다() {
			// given
			EventsDetailCreateRequest eventsDetailCreateRequest =
				EventsDetailCreateRequest.builder()
					.eventsId(savedEvent.getId())
					.routingUrl("toduck://createPost")
					.buttonVisible(true)
					.buttonText("당첨 확인하기")
					.eventsDetailImgs(Arrays.asList("asdf", "ㅁㄴㅇㄹ"))
					.build();

			EventsDetail eventsDetail1 = eventsDetailUseCase.createEventsDetail(eventsDetailCreateRequest,
				savedAdminUser.getId());

			EventsDetailUpdateRequest eventsDetailUpdateRequest =
				EventsDetailUpdateRequest.builder()
					.eventsId(null)
					.routingUrl("asas")
					.buttonVisible(true)
					.buttonText("당첨 확인하기")
					.eventsDetailImgs(Arrays.asList())
					.build();

			EventsDetailUpdateRequest eventsDetailUpdateRequest2 =
				EventsDetailUpdateRequest.builder()
					.eventsId(null)
					.routingUrl("asas")
					.buttonVisible(true)
					.buttonText("당첨 확인하기")
					.eventsDetailImgs(Arrays.asList())
					.build();

			// when - then
			// routingUrl 수정
			EventsDetail eventsDetail = eventsDetailUseCase.updateEventsDetail(eventsDetail1.getId(),
				eventsDetailUpdateRequest, savedAdminUser.getId());

			assertSoftly(softly -> {
				softly.assertThat(eventsDetail.getEvents().getId()).isEqualTo(savedEvent.getId());
				softly.assertThat(eventsDetail.getRoutingUrl()).isEqualTo(eventsDetailUpdateRequest.routingUrl());
			});

			// 수정 안함
			EventsDetail eventsDetail2 = eventsDetailUseCase.updateEventsDetail(eventsDetail1.getId(),
				eventsDetailUpdateRequest, savedAdminUser.getId());

			assertSoftly(softly -> {
				softly.assertThat(eventsDetail2.getEvents().getId()).isEqualTo(savedEvent.getId());
				softly.assertThat(eventsDetail2.getRoutingUrl()).isEqualTo(eventsDetail2.getRoutingUrl());
			});
		}

		@Test
		void 성공적으로_삭제한다() {
			// given
			EventsDetailCreateRequest eventsDetailCreateRequest =
				EventsDetailCreateRequest.builder()
					.eventsId(savedEvent.getId())
					.routingUrl("toduck://createPost")
					.buttonVisible(true)
					.buttonText("당첨 확인하기")
					.eventsDetailImgs(Arrays.asList("asdf", "ㅁㄴㅇㄹ"))
					.build();

			EventsDetail eventsDetail1 = eventsDetailUseCase.createEventsDetail(eventsDetailCreateRequest,
				savedAdminUser.getId());

			// when
			eventsDetailUseCase.deleteEventsDetail(eventsDetail1.getId(), savedAdminUser.getId());

			// then
			List<EventsDetail> eventsDetails = eventsDetailRepository.findAll();
			assertThat(eventsDetails.size()).isEqualTo(0);
		}
	}
}

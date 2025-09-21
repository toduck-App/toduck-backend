package im.toduck.domain.events.domain.usecase;

import static im.toduck.fixtures.social.SocialFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.events.social.domain.usecase.EventsSocialUseCase;
import im.toduck.domain.events.social.presentation.dto.request.EventsSocialRequest;
import im.toduck.domain.events.social.presentation.dto.response.EventsSocialCheckResponse;
import im.toduck.domain.social.domain.usecase.SocialBoardUseCase;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.repository.SocialRepository;
import im.toduck.domain.user.persistence.entity.User;

class EventsSocialUseCaseTest extends ServiceTest {

	@Autowired
	EventsSocialUseCase eventsSocialUseCase;

	@Autowired
	SocialBoardUseCase socialBoardUseCase;

	@Autowired
	SocialRepository socialRepository;

	@Transactional
	@Nested
	@DisplayName("소셜 이벤트 참여 여부 조회 시")
	class checkEventsSocial {
		private User savedUser;
		private final LocalDate today = LocalDate.now();

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
		}

		@Test
		void 성공적으로_반환한다1() {
			// given - when 해당 날짜에 참여하지 않은 경우
			EventsSocialCheckResponse eventsSocialCheckResponse =
				eventsSocialUseCase.checkEventsSocial(LocalDate.now(), savedUser.getId());

			boolean participated = eventsSocialCheckResponse.participated();

			// then
			assertSoftly(softly -> {
				softly.assertThat(participated).isEqualTo(false);
			});
		}

		@Test
		void 성공적으로_반환한다2() { //
			// given 해당 날짜에 참여 한 경우
			Social social = Social.builder()
				.user(savedUser)
				.content(DEFAULT_CONTENT + " " + 1)
				.isAnonymous(true)
				.build();
			socialRepository.save(social);

			EventsSocialRequest eventsSocialRequest = EventsSocialRequest.builder()
				.socialId(social.getId())
				.phone("01012345678")
				.date(LocalDate.now())
				.build();

			eventsSocialUseCase.saveEventsSocial(eventsSocialRequest, savedUser.getId());

			// when
			EventsSocialCheckResponse eventsSocialCheckResponse =
				eventsSocialUseCase.checkEventsSocial(LocalDate.now(), savedUser.getId());

			boolean participated = eventsSocialCheckResponse.participated();

			// then
			assertSoftly(softly -> {
				softly.assertThat(participated).isEqualTo(true);
			});
		}
	}
}

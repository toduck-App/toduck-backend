package im.toduck.domain.diary.domain.usecase;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import im.toduck.ServiceTest;
import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.diary.presentation.dto.response.DiaryStreakResponse;
import im.toduck.domain.user.persistence.entity.Emotion;
import im.toduck.domain.user.persistence.entity.User;

@Transactional
@Testcontainers
@ActiveProfiles("mysql-test")
class DiaryUseCaseTest extends ServiceTest {

	@Autowired
	DiaryUseCase diaryUseCase;

	@Autowired
	DiaryStreakUseCase diaryStreakUseCase;

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName("testdb")
		.withUsername("test")
		.withPassword("test")
		.withEnv("TZ", "Asia/Seoul");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		String jdbcUrl = mysql.getJdbcUrl() + "?serverTimezone=Asia/Seoul";
		registry.add("spring.datasource.url", () -> jdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
	}

	@Nested
	@DisplayName("일기 스트릭 조회 시")
	class diaryStreak {
		private User savedUser;
		private final LocalDate today = LocalDate.now();

		private final DiaryCreateRequest diaryCreateRequest0 = DiaryCreateRequest.builder()
			.date(today)
			.emotion(Emotion.ANGRY)
			.title("타이틀")
			.memo("메모")
			.build();

		private final DiaryCreateRequest diaryCreateRequest1 = DiaryCreateRequest.builder()
			.date(today.minusDays(1))
			.emotion(Emotion.ANGRY)
			.title("타이틀")
			.memo("메모")
			.build();

		private final DiaryCreateRequest diaryCreateRequest2 = DiaryCreateRequest.builder()
			.date(today.minusDays(2))
			.emotion(Emotion.ANGRY)
			.title("타이틀")
			.memo("메모")
			.build();

		private final DiaryCreateRequest diaryCreateRequest3 = DiaryCreateRequest.builder()
			.date(today.minusDays(3))
			.emotion(Emotion.ANGRY)
			.title("타이틀")
			.memo("메모")
			.build();

		private final DiaryCreateRequest diaryCreateRequest4 = DiaryCreateRequest.builder()
			.date(today.minusDays(4))
			.emotion(Emotion.ANGRY)
			.title("타이틀")
			.memo("메모")
			.build();

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
		}

		@Test
		void 성공적으로_반환한다1() {
			// given - 오늘 포함 5일 연속 작성
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest4);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest4.date(), diaryCreateRequest4.date());

			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest3);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest3.date(), diaryCreateRequest3.date());

			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest2);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest2.date(), diaryCreateRequest2.date());

			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest1);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest1.date(), diaryCreateRequest1.date());

			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest0);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest0.date(), diaryCreateRequest0.date());

			// when
			DiaryStreakResponse diaryStreakResponse = diaryStreakUseCase.getDiaryStreak(savedUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(diaryStreakResponse.streak()).isEqualTo(5);
				softly.assertThat(diaryStreakResponse.lastDiaryDate()).isEqualTo(today);
			});
		}

		@Test
		void 성공적으로_반환한다2() {
			// given - 오늘 미포함 4일 연속 작성
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest4);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest4.date(), diaryCreateRequest4.date());

			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest3);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest3.date(), diaryCreateRequest3.date());

			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest2);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest2.date(), diaryCreateRequest2.date());

			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest1);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest1.date(), diaryCreateRequest1.date());

			// when
			DiaryStreakResponse diaryStreakResponse = diaryStreakUseCase.getDiaryStreak(savedUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(diaryStreakResponse.streak()).isEqualTo(4);
				softly.assertThat(diaryStreakResponse.lastDiaryDate()).isEqualTo(today.minusDays(1));
			});
		}

		@Test
		void 성공적으로_반환한다3() {
			// given - 어제 오늘 2일 연속 작성
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest1);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest1.date(), diaryCreateRequest1.date());

			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest0);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest0.date(), diaryCreateRequest0.date());

			// when
			DiaryStreakResponse diaryStreakResponse = diaryStreakUseCase.getDiaryStreak(savedUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(diaryStreakResponse.streak()).isEqualTo(2);
				softly.assertThat(diaryStreakResponse.lastDiaryDate()).isEqualTo(today);
			});
		}

		@Test
		void 성공적으로_반환한다4() {
			// given - 오늘만 작성
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest0);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest0.date(), diaryCreateRequest0.date());

			// when
			DiaryStreakResponse diaryStreakResponse = diaryStreakUseCase.getDiaryStreak(savedUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(diaryStreakResponse.streak()).isEqualTo(1);
				softly.assertThat(diaryStreakResponse.lastDiaryDate()).isEqualTo(today);
			});
		}

		@Test
		void 성공적으로_반환한다5() {
			// given - 어제만 작성
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest1);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest1.date(), diaryCreateRequest1.date());

			// when
			DiaryStreakResponse diaryStreakResponse = diaryStreakUseCase.getDiaryStreak(savedUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(diaryStreakResponse.streak()).isEqualTo(1);
				softly.assertThat(diaryStreakResponse.lastDiaryDate()).isEqualTo(today.minusDays(1));
			});
		}

		@Test
		void 성공적으로_반환한다6() {
			// given - 그저께 이후로 작성 X
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest4);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest4.date(), diaryCreateRequest4.date());

			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest3);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest3.date(), diaryCreateRequest3.date());

			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest2);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest2.date(), diaryCreateRequest2.date());

			// when
			DiaryStreakResponse diaryStreakResponse = diaryStreakUseCase.getDiaryStreak(savedUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(diaryStreakResponse.streak()).isEqualTo(0);
				softly.assertThat(diaryStreakResponse.lastDiaryDate()).isEqualTo(today.minusDays(2));
			});
		}

		@Test
		void 성공적으로_반환한다7() {
			// given - 어제만 작성 안하고 오늘 작성
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest4);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest4.date(), diaryCreateRequest4.date());

			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest3);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest3.date(), diaryCreateRequest3.date());

			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest2);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest2.date(), diaryCreateRequest2.date());

			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest0);
			diaryStreakUseCase.updateStreak(savedUser.getId(), diaryCreateRequest0.date(), diaryCreateRequest0.date());

			// when
			DiaryStreakResponse diaryStreakResponse = diaryStreakUseCase.getDiaryStreak(savedUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(diaryStreakResponse.streak()).isEqualTo(1);
				softly.assertThat(diaryStreakResponse.lastDiaryDate()).isEqualTo(today);
			});
		}
	}
}

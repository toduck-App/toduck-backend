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
import im.toduck.domain.user.persistence.repository.UserRepository;

@Transactional
@Testcontainers
@ActiveProfiles("mysql-test")
class DiaryUseCaseTest extends ServiceTest {

	@Autowired
	DiaryUseCase diaryUseCase;

	@Autowired
	UserRepository userRepository;

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

		private final DiaryCreateRequest diaryCreateRequest5 = DiaryCreateRequest.builder()
			.date(today.minusDays(5))
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
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest0);
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest1);
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest2);
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest3);
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest4);

			// when
			DiaryStreakResponse diaryStreakResponse = diaryUseCase.getDiaryStreak(savedUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(diaryStreakResponse.consecutiveDays()).isEqualTo(5);
				softly.assertThat(diaryStreakResponse.lastDiaryDate()).isEqualTo(today);
			});
		}

		@Test
		void 성공적으로_반환한다2() {
			// given - 오늘 미포함 4일 연속 작성
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest1);
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest2);
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest3);
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest4);

			// when
			DiaryStreakResponse diaryStreakResponse = diaryUseCase.getDiaryStreak(savedUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(diaryStreakResponse.consecutiveDays()).isEqualTo(4);
				softly.assertThat(diaryStreakResponse.lastDiaryDate()).isEqualTo(today.minusDays(1));
			});
		}

		@Test
		void 성공적으로_반환한다3() {
			// given - 어제 오늘 2일 연속 작성
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest0);
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest1);

			// when
			DiaryStreakResponse diaryStreakResponse = diaryUseCase.getDiaryStreak(savedUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(diaryStreakResponse.consecutiveDays()).isEqualTo(2);
				softly.assertThat(diaryStreakResponse.lastDiaryDate()).isEqualTo(today);
			});
		}

		@Test
		void 성공적으로_반환한다4() {
			// given - 오늘만 작성
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest0);

			// when
			DiaryStreakResponse diaryStreakResponse = diaryUseCase.getDiaryStreak(savedUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(diaryStreakResponse.consecutiveDays()).isEqualTo(1);
				softly.assertThat(diaryStreakResponse.lastDiaryDate()).isEqualTo(today);
			});
		}

		@Test
		void 성공적으로_반환한다5() {
			// given - 어제만 작성
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest1);

			// when
			DiaryStreakResponse diaryStreakResponse = diaryUseCase.getDiaryStreak(savedUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(diaryStreakResponse.consecutiveDays()).isEqualTo(1);
				softly.assertThat(diaryStreakResponse.lastDiaryDate()).isEqualTo(today.minusDays(1));
			});
		}

		@Test
		void 성공적으로_반환한다6() {
			// given - 그저께 이후로 작성 X
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest2);
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest3);
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest4);

			// when
			DiaryStreakResponse diaryStreakResponse = diaryUseCase.getDiaryStreak(savedUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(diaryStreakResponse.consecutiveDays()).isEqualTo(0);
				softly.assertThat(diaryStreakResponse.lastDiaryDate()).isEqualTo(today.minusDays(2));
			});
		}

		@Test
		void 성공적으로_반환한다7() {
			// given - 어제만 작성 안하고 오늘 작성
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest0);
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest2);
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest3);
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest4);

			// when
			DiaryStreakResponse diaryStreakResponse = diaryUseCase.getDiaryStreak(savedUser.getId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(diaryStreakResponse.consecutiveDays()).isEqualTo(1);
				softly.assertThat(diaryStreakResponse.lastDiaryDate()).isEqualTo(today);
			});
		}
	}

	// @Nested
	// @DisplayName("대량 데이터 성능 테스트")
	// class LargeDatasetPerformanceTest {
	// 	private User savedUser;
	//
	// 	@BeforeEach
	// 	void setUp() {
	// 		savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
	//
	// 		IntStream.range(0, 1000)
	// 			.forEach(i -> {
	// 				User user =
	// 					testFixtureBuilder.buildUser(GENERAL_USER());
	//
	// 				LocalDate startDate = LocalDate.now().minusYears(3);
	// 				IntStream.range(0, 100)
	// 					.forEach(j -> {
	// 						diaryUseCase.createDiary(
	// 							user.getId(),
	// 							DiaryCreateRequest.builder()
	// 								.date(startDate.plusDays(j))
	// 								.emotion(Emotion.HAPPY)
	// 								.title("테스트 제목" + j)
	// 								.memo("테스트 내용" + j)
	// 								.build()
	// 						);
	// 					});
	// 			});
	// 	}
	//
	// 	@Test
	// 	@DisplayName("100명 사용자 데이터 생성 후 스트릭 조회 성능 테스트")
	// 	void testDiaryStreakPerformance() throws InterruptedException {
	// 		long totalTime = 0;
	// 		int iterations = 100;
	//
	// 		for (int i = 0; i < iterations; i++) {
	// 			long startTime = System.nanoTime();
	//
	// 			List<User> users = userRepository.findAll();
	// 			User randomUser = users.get(ThreadLocalRandom.current().nextInt(users.size()));
	//
	// 			diaryUseCase.getDiaryStreak(randomUser.getId());
	//
	// 			long duration = System.nanoTime() - startTime;
	// 			totalTime += duration;
	// 		}
	//
	// 		double avgMs = (totalTime / (double)iterations) / 1_000_000;
	// 		System.out.printf("평균 실행 시간 : %.2f ms%n", avgMs);
	// 	}
	// }
}

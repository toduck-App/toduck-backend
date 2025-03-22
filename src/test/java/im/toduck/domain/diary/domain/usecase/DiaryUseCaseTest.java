package im.toduck.domain.diary.domain.usecase;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.ServiceTest;
import im.toduck.domain.diary.persistence.repository.DiaryImageRepository;
import im.toduck.domain.diary.persistence.repository.DiaryRepository;
import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.diary.presentation.dto.response.DiaryCreateResponse;
import im.toduck.domain.user.persistence.entity.Emotion;
import im.toduck.domain.user.persistence.entity.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class DiaryUseCaseTest extends ServiceTest {
	private User USER;

	@Autowired
	private DiaryUseCase diaryUseCase;

	@Autowired
	private DiaryRepository diaryRepository;

	@Autowired
	private DiaryImageRepository diaryImageRepository;

	@BeforeEach
	public void setUp() {
		USER = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Nested
	@DisplayName("다이어리 생성시")
	class CreateDiary {
		String dateStr = "2025-03-21";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse(dateStr, formatter);
		Emotion emotion = Emotion.valueOf("HAPPY");

		private Validator validator;

		@BeforeEach
		void setUp() {
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			validator = factory.getValidator();
		}

		@Test
		void 성공() {
			// given
			DiaryCreateRequest DATE_EMOTION_NOT_NULL_REQUEST = new DiaryCreateRequest(
				date,
				emotion,
				null,
				null,
				null
			);

			// when
			DiaryCreateResponse response = diaryUseCase.createDiary(USER.getId(), DATE_EMOTION_NOT_NULL_REQUEST);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response.diaryId()).isNotNull();
			});
		}

		@Test
		void 실패_date_emotion이_null이면_에러를_반환한다() {
			// given
			DiaryCreateRequest DATE_EMOTION_NULL_REQUEST = new DiaryCreateRequest(
				null,
				null,
				null,
				null,
				null
			);

			// when
			Set<ConstraintViolation<DiaryCreateRequest>> violations = validator.validate(DATE_EMOTION_NULL_REQUEST);

			// then
			assertThat(violations)
				.extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder(
					"날짜는 비어있을 수 없습니다.",
					"감정은 비어있을 수 없습니다."
				);
		}
	}
}

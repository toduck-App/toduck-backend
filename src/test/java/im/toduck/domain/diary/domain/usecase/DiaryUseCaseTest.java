package im.toduck.domain.diary.domain.usecase;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.ServiceTest;
import im.toduck.domain.diary.persistence.repository.DiaryImageRepository;
import im.toduck.domain.diary.persistence.repository.DiaryRepository;
import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.user.persistence.entity.Emotion;
import im.toduck.domain.user.persistence.entity.User;
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
			DiaryCreateRequest 일기_생성_요청에_성공한다 = new DiaryCreateRequest(
				date,
				emotion,
				null,
				null,
				null
			);

			// when & then
			assertDoesNotThrow(() -> diaryUseCase.createDiary(USER.getId(), 일기_생성_요청에_성공한다));
		}
	}
}

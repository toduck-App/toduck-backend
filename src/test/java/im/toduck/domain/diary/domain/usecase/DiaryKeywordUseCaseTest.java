package im.toduck.domain.diary.domain.usecase;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.diary.domain.service.DiaryKeywordService;
import im.toduck.domain.diary.persistence.entity.DiaryKeyword;
import im.toduck.domain.diary.persistence.entity.KeywordCategory;
import im.toduck.domain.diary.persistence.entity.UserKeyword;
import im.toduck.domain.diary.persistence.repository.DiaryKeywordRepository;
import im.toduck.domain.diary.persistence.repository.UserKeywordRepository;
import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.diary.presentation.dto.request.DiaryKeywordCreateRequest;
import im.toduck.domain.diary.presentation.dto.request.UserKeywordRequest;
import im.toduck.domain.diary.presentation.dto.response.DiaryListResponse;
import im.toduck.domain.user.persistence.entity.Emotion;
import im.toduck.domain.user.persistence.entity.User;

@Transactional
class DiaryKeywordUseCaseTest extends ServiceTest {

	@Autowired
	private DiaryKeywordUseCase diaryKeywordUseCase;

	@Autowired
	private DiaryKeywordService diaryKeywordService;

	@Autowired
	private DiaryKeywordRepository diaryKeywordRepository;

	@Autowired
	private DiaryUseCase diaryUseCase;

	@Autowired
	private UserKeywordUseCase userKeywordUseCase;

	@Autowired
	private UserKeywordRepository userKeywordRepository;

	@Nested
	@DisplayName("일기-키워드 연결시")
	class diaryKeyword {
		private User savedUser;
		private DiaryCreateRequest diaryCreateRequest;
		private UserKeywordRequest userKeywordRequest1, userKeywordRequest2;
		private DiaryKeywordCreateRequest diaryKeywordCreateRequest;

		@BeforeEach
		void setUp() {
			savedUser = testFixtureBuilder.buildUser(GENERAL_USER());
			diaryCreateRequest = DiaryCreateRequest.builder()
				.date(LocalDate.of(2025, 8, 10))
				.emotion(Emotion.SAD)
				.title("슬픔")
				.memo("출근 전에 지갑을 두고 나오는 바람에 다시 돌아갔다")
				.diaryImageUrls(List.of("https://cdn.toduck.app/image1.jpg"))
				.build();
			userKeywordRequest1 = UserKeywordRequest.builder()
				.keywordCategory(KeywordCategory.PLACE)
				.keyword("회사")
				.build();
			userKeywordRequest2 = UserKeywordRequest.builder()
				.keywordCategory(KeywordCategory.PERSON)
				.keyword("상사")
				.build();

		}

		@Test
		void 성공적으로_일기와_키워드를_연결한다() {
			// given
			diaryUseCase.createDiary(savedUser.getId(), diaryCreateRequest);
			userKeywordUseCase.createKeyword(savedUser.getId(), userKeywordRequest1);
			userKeywordUseCase.createKeyword(savedUser.getId(), userKeywordRequest2);

			DiaryListResponse diaryListResponse = diaryUseCase.getDiariesByMonth(savedUser.getId(),
				YearMonth.of(2025, 8));
			Long diaryId = diaryListResponse.diaryDtos().get(0).diaryId();
			List<Long> keywordIds = userKeywordRepository.findByUserId(savedUser.getId())
				.stream()
				.map(UserKeyword::getId)
				.toList();
			diaryKeywordCreateRequest = DiaryKeywordCreateRequest.builder()
				.diaryId(diaryId)
				.keywordIds(keywordIds)
				.build();

			// when
			diaryKeywordUseCase.createDiaryKeyword(savedUser.getId(), diaryKeywordCreateRequest);

			// then
			List<DiaryKeyword> diaryKeywords = diaryKeywordRepository.findByDiaryId(diaryId);

			assertThat(diaryKeywords).hasSize(2);

			List<Long> savedKeywordIds = diaryKeywords.stream()
				.map(dk -> dk.getUserKeyword().getId())
				.toList();

			assertThat(savedKeywordIds).containsExactlyInAnyOrderElementsOf(keywordIds);
		}
	}
}

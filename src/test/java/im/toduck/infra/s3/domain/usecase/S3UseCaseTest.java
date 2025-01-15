package im.toduck.infra.s3.domain.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.ServiceTest;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.fixtures.user.UserFixtures;
import im.toduck.infra.s3.presentation.dto.request.FileNameDto;
import im.toduck.infra.s3.presentation.dto.request.PreSignedUrlRequest;
import im.toduck.infra.s3.presentation.dto.response.PreSignedUrlResponse;

class S3UseCaseTest extends ServiceTest {

	@Autowired
	private S3UseCase s3UseCase;

	private User USER;
	private LocalDate currentDate;
	private String fileName1;
	private String fileName2;

	@BeforeEach
	void setUp() {
		USER = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
		currentDate = LocalDate.now();
		fileName1 = "test1.jpg";
		fileName2 = "test2.png";

	}

	@Nested
	class GeneratePresignedUrlTest {

		@Test
		void 파일_이름으로_PreSigned_URL을_생성할_수_있다() {
			// given
			PreSignedUrlRequest request = new PreSignedUrlRequest(
				List.of(
					new FileNameDto(fileName1),
					new FileNameDto(fileName2)
				)
			);

			// when
			PreSignedUrlResponse response = s3UseCase.generatePresignedUrl(request, USER.getId(), currentDate);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response.fileUrlsDtos()).hasSize(2);
				response.fileUrlsDtos().forEach(fileUrlDto -> {
					softly.assertThat(fileUrlDto.presignedUrl()).isNotNull();
					softly.assertThat(fileUrlDto.fileUrl())
						.isNotNull()
						.startsWith("https://cdn.toduck.app");
				});
			});
		}

		@Test
		void 빈_파일_리스트에_대한_PreSigned_URL_생성시_빈_응답을_반환한다() {
			// given
			PreSignedUrlRequest request = new PreSignedUrlRequest(List.of());

			// when
			PreSignedUrlResponse response = s3UseCase.generatePresignedUrl(request, USER.getId(), currentDate);

			// then
			assertThat(response.fileUrlsDtos()).isEmpty();
		}
	}

}

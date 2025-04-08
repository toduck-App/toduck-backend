package im.toduck.infra.s3.domain.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import im.toduck.ServiceTest;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.fixtures.user.UserFixtures;
import im.toduck.infra.s3.domain.service.S3Service;
import im.toduck.infra.s3.presentation.dto.ImageExtension;
import im.toduck.infra.s3.presentation.dto.request.FileNameDto;
import im.toduck.infra.s3.presentation.dto.request.PreSignedUrlRequest;
import im.toduck.infra.s3.presentation.dto.response.PreSignedUrlResponse;

class S3UseCaseTest extends ServiceTest {
	@MockBean
	private S3Service s3Service;

	@Autowired
	private S3UseCase s3UseCase;

	private User USER;
	private LocalDate currentDate;
	private String fileName;
	private URL presignedUrl;

	@BeforeEach
	void setUp() {
		USER = testFixtureBuilder.buildUser(UserFixtures.GENERAL_USER());
		currentDate = LocalDate.now();
		fileName = "test.jpg";

		given(s3Service.createObjectKey(anyString(), anyLong(), any(LocalDate.class)))
			.willCallRealMethod();
		given(s3Service.generateFileUrl(anyString()))
			.willCallRealMethod();
	}

	@Nested
	class GeneratePresignedUrlTest {

		@BeforeEach
		void setUp() throws Exception {
			presignedUrl = new URL("https://www.testPresignedUrl.com");

			given(s3Service.generatePresignedUrl(anyString(), eq(ImageExtension.JPG.getExtension())))
				.willReturn(presignedUrl);
		}

		@Test
		void 파일_이름으로_PreSigned_URL을_생성할_수_있다() {
			// given
			PreSignedUrlRequest request = new PreSignedUrlRequest(
				List.of(new FileNameDto(fileName))
			);

			// when
			PreSignedUrlResponse response = s3UseCase.generatePresignedUrl(request, USER.getId(), currentDate);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response.fileUrlsDtos()).hasSize(1);
				softly.assertThat(response.fileUrlsDtos().get(0).presignedUrl()).isEqualTo(presignedUrl);
				softly.assertThat(response.fileUrlsDtos().get(0).fileUrl())
					.endsWith(fileName);
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
